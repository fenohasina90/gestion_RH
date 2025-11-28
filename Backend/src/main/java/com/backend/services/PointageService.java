package com.backend.services;

import com.backend.entities.Employe;
import com.backend.entities.Pointage;
import com.backend.entities.Typepointage;
import com.backend.repositories.EmployeRepository;
import com.backend.repositories.FeuilletempRepository;
import com.backend.repositories.DetailfeuilletempRepository;
import com.backend.entities.Feuilletemp;
import com.backend.entities.Detailfeuilletemp;
import com.backend.dto.TimesheetDto;
import com.backend.dto.TimesheetDetailDto;
import com.backend.repositories.PointageRepository;
import com.backend.repositories.TypepointageRepository;
import com.backend.repositories.ValidatedLeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class PointageService {
    private final PointageRepository pointageRepository;
    private final EmployeRepository employeRepository;
    private final TypepointageRepository typepointageRepository;
    private final FeuilletempRepository feuilletempRepository;
    private final DetailfeuilletempRepository detailfeuilletempRepository;
    private final ValidatedLeaveRepository validatedLeaveRepository;

    public PointageService(PointageRepository pointageRepository,
                           EmployeRepository employeRepository,
                           TypepointageRepository typepointageRepository,
                           FeuilletempRepository feuilletempRepository,
                           DetailfeuilletempRepository detailfeuilletempRepository,
                           ValidatedLeaveRepository validatedLeaveRepository) {
        this.pointageRepository = pointageRepository;
        this.employeRepository = employeRepository;
        this.typepointageRepository = typepointageRepository;
        this.feuilletempRepository = feuilletempRepository;
        this.detailfeuilletempRepository = detailfeuilletempRepository;
        this.validatedLeaveRepository = validatedLeaveRepository;
    }

    public List<Pointage> getPointagesOfDay(LocalDate date) {
        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant();
        return pointageRepository.findValidBetween(start, end);
    }

    public List<Pointage> getPointagesOfEmployeeOn(LocalDate date, Integer empId) {
        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant();
        return pointageRepository.findValidByEmployeBetween(empId, start, end);
    }

    public List<Pointage> getPointagesOfEmployeeOnByMatricule(LocalDate date, String matricule) {
        Employe e = employeRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable pour le matricule: " + matricule));
        return getPointagesOfEmployeeOn(date, e.getId());
    }

    @Transactional
    public Pointage createManual(Integer employeId, LocalDate date, LocalTime time, Integer typeId, String commentaire) {
        Employe emp = employeRepository.findById(employeId)
                .orElseThrow(() -> new IllegalArgumentException("Employe introuvable"));
        Typepointage type = typepointageRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Type de pointage introuvable"));

        Instant dateHeure = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant();

        // Doublon dans les 5 dernières minutes
        Instant fiveMinBefore = dateHeure.minusSeconds(5 * 60);
        List<Pointage> recent = pointageRepository.findRecentValidAfter(employeId, fiveMinBefore);
        boolean duplicate = recent.stream().anyMatch(p ->
                p.getIdtypepointage().getId().equals(typeId) && Math.abs(p.getDateheure().getEpochSecond() - dateHeure.getEpochSecond()) <= 5 * 60
        );
        if (duplicate) {
            throw new IllegalStateException("Doublon détecté dans les 5 dernières minutes");
        }

        // Cohérence basique: pas de "Sortie" ou "Fin pause" sans entrée/début pause avant
        int ENTR = 1, SORT = 2, PAUSE_DEB = 3, PAUSE_FIN = 4;
        if (typeId == SORT || typeId == PAUSE_FIN) {
            List<Pointage> day = getPointagesOfEmployeeOn(date, employeId);
            boolean ok = false;
            if (typeId == SORT) {
                // doit exister une entrée avant cette heure sans sortie après
                ok = day.stream().anyMatch(p -> p.getIdtypepointage().getId() == ENTR && p.getDateheure().isBefore(dateHeure));
            } else if (typeId == PAUSE_FIN) {
                ok = day.stream().anyMatch(p -> p.getIdtypepointage().getId() == PAUSE_DEB && p.getDateheure().isBefore(dateHeure));
            }
            if (!ok) {
                throw new IllegalStateException("Incohérence: pointage de fin sans début correspondant");
            }
        }

        Pointage p = new Pointage();
        p.setIdemploye(emp);
        p.setIdtypepointage(type);
        p.setDateheure(dateHeure);
        p.setCommentaire(commentaire);
        p.setEstvalide(Boolean.TRUE);
        Pointage saved = pointageRepository.save(p);

        // Mise à jour feuille (détail du jour et agrégat mensuel)
        updateTimesheets(emp, date);
        return saved;
    }

    @Transactional
    public void invalidate(Integer id) {
        Pointage p = pointageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pointage introuvable"));
        p.setEstvalide(Boolean.FALSE);
        pointageRepository.save(p);
    }

    @Transactional
    public void addAbsence(Integer employeId, LocalDate date, String commentaire) {
        Employe emp = employeRepository.findById(employeId)
                .orElseThrow(() -> new IllegalArgumentException("Employe introuvable"));
        int mois = date.getMonthValue();
        int annee = date.getYear();
        // Upsert feuille mensuelle
        Feuilletemp feuille = feuilletempRepository
                .findByIdemploye_IdAndMoisAndAnnee(emp.getId(), mois, annee)
                .orElseGet(() -> {
                    Feuilletemp f = new Feuilletemp();
                    f.setIdemploye(emp);
                    f.setMois(mois);
                    f.setAnnee(annee);
                    return feuilletempRepository.save(f);
                });

        // Upsert détail du jour
        Detailfeuilletemp detail = detailfeuilletempRepository
                .findByIdfeuilletemps_IdAndDatejour(feuille.getId(), date)
                .orElseGet(() -> {
                    Detailfeuilletemp d = new Detailfeuilletemp();
                    d.setIdfeuilletemps(feuille);
                    d.setDatejour(date);
                    return d;
                });
        detail.setEstabsent(Boolean.TRUE);
        detail.setCommentaire(commentaire);
        detail.setHeuresentree(null);
        detail.setHeuressortie(null);
        detail.setHeurestravaillees(java.math.BigDecimal.valueOf(0.0));
        detail.setHeuressup(java.math.BigDecimal.valueOf(0.0));
        detailfeuilletempRepository.save(detail);

        // Recalculate monthly aggregates from all daily details for this feuille
        List<Detailfeuilletemp> details = detailfeuilletempRepository.findByIdfeuilletemps_Id(feuille.getId());
        double sumSup = details.stream()
                .map(Detailfeuilletemp::getHeuressup)
                .filter(Objects::nonNull)
                .mapToDouble(bd -> bd.doubleValue())
                .sum();
        feuille.setHeuressupplementaires(java.math.BigDecimal.valueOf(round2(sumSup)));

        double sumDays = details.stream()
                .map(Detailfeuilletemp::getHeurestravaillees)
                .filter(Objects::nonNull)
                .mapToDouble(bd -> bd.doubleValue() / 8.0)
                .sum();
        feuille.setJourstravailles(java.math.BigDecimal.valueOf(round2(sumDays)));

        long absCount = details.stream()
                .map(Detailfeuilletemp::getEstabsent)
                .filter(Boolean.TRUE::equals)
                .count();
        feuille.setAbsences(java.math.BigDecimal.valueOf(absCount));

        // Recompute monthly retards as well to keep consistent
        LocalDate monthStart = LocalDate.of(annee, mois, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        Instant mStart = monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant mEnd = monthEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant();
        List<Pointage> monthPts = pointageRepository.findValidByEmployeBetween(emp.getId(), mStart, mEnd);
        Map<LocalDate, List<Pointage>> byDay = new HashMap<>();
        for (Pointage pt : monthPts) {
            LocalDate d = LocalDateTime.ofInstant(pt.getDateheure(), ZoneId.systemDefault()).toLocalDate();
            byDay.computeIfAbsent(d, k -> new ArrayList<>()).add(pt);
        }
        int totalRetards = 0;
        for (Map.Entry<LocalDate, List<Pointage>> e : byDay.entrySet()) {
            List<Pointage> list = e.getValue();
            list.sort(Comparator.comparing(Pointage::getDateheure));
            // matin
            Instant firstInI = list.stream().filter(pp -> pp.getIdtypepointage().getId() == 1)
                    .map(Pointage::getDateheure).min(Comparator.naturalOrder()).orElse(null);
            LocalTime arr = firstInI != null ? LocalDateTime.ofInstant(firstInI, ZoneId.systemDefault()).toLocalTime() : null;
            int amLate = 0;
            if (arr != null) {
                amLate = Math.max(0, (int) Duration.between(LocalTime.of(8,0), arr).toMinutes());
                if (arr.isBefore(LocalTime.of(8,0))) amLate = 0;
            }
            // après-midi
            LocalTime retPm = null;
            for (Pointage ptd : list) {
                int t = ptd.getIdtypepointage().getId();
                LocalTime tt = LocalDateTime.ofInstant(ptd.getDateheure(), ZoneId.systemDefault()).toLocalTime();
                if ((t == 1 || t == 4) && !tt.isBefore(LocalTime.of(14,0))) { retPm = tt; break; }
            }
            int pmLate = 0;
            if (retPm != null) {
                pmLate = Math.max(0, (int) Duration.between(LocalTime.of(14,0), retPm).toMinutes());
                if (retPm.isBefore(LocalTime.of(14,0))) pmLate = 0;
            }
            if (amLate > 10) totalRetards += 1;
            if (pmLate > 10) totalRetards += 1;
        }
        feuille.setRetards(totalRetards);
        feuilletempRepository.save(feuille);
    }

    // === Timesheet sync helpers ===
    private void updateTimesheets(Employe emp, LocalDate date) {
        // Récupérer tous les pointages valides du jour
        List<Pointage> day = getPointagesOfEmployeeOn(date, emp.getId());
        if (day.isEmpty()) return;

        // Trier par date
        day.sort(Comparator.comparing(Pointage::getDateheure));

        // Calcul arrivée, départ, pauses et heures travaillées
        int ENTR = 1, SORT = 2, PDB = 3, PFIN = 4;
        Instant firstInTs = day.stream().filter(pp -> pp.getIdtypepointage().getId() == ENTR)
                .map(Pointage::getDateheure).min(Comparator.naturalOrder()).orElse(null);
        Instant lastOutTs = day.stream().filter(pp -> pp.getIdtypepointage().getId() == SORT)
                .map(Pointage::getDateheure).max(Comparator.naturalOrder()).orElse(null);

        if (firstInTs == null || lastOutTs == null) {
            // Pas d'intervalle complet
            return;
        }

        long pauseMs = 0L;
        Instant stack = null;
        for (Pointage pt : day) {
            if (pt.getIdtypepointage().getId() == PDB) stack = pt.getDateheure();
            if (pt.getIdtypepointage().getId() == PFIN && stack != null) {
                pauseMs += (pt.getDateheure().toEpochMilli() - stack.toEpochMilli());
                stack = null;
            }
        }

        long workMs = lastOutTs.toEpochMilli() - firstInTs.toEpochMilli() - pauseMs;
        double workHours = Math.max(0d, workMs / 3_600_000d);

        // on calcule "overtime" après détermination des heures stockées
        LocalDateTime outLocal = LocalDateTime.ofInstant(lastOutTs, ZoneId.systemDefault());
        double overtime = 0d;

        // Journée complète si retards matin et après-midi <= 10 min
        final LocalTime MORNING_START = LocalTime.of(8, 0);
        final LocalTime AFTERNOON_START = LocalTime.of(14, 0);
        final int LATE_LIMIT_MIN = 10;

        // Arrivée (heure locale)
        LocalTime arrivee = LocalDateTime.ofInstant(firstInTs, ZoneId.systemDefault()).toLocalTime();
        int latenessAm = Math.max(0, (int) Duration.between(MORNING_START, arrivee).toMinutes());
        if (arrivee.isBefore(MORNING_START)) latenessAm = 0;

        // Retour après-midi: premier ENTRÉE ou FIN PAUSE >= 14:00
        LocalTime returnPm = null;
        for (Pointage pt : day) {
            int t = pt.getIdtypepointage().getId();
            LocalTime tt = LocalDateTime.ofInstant(pt.getDateheure(), ZoneId.systemDefault()).toLocalTime();
            if ((t == 1 || t == 4) && !tt.isBefore(AFTERNOON_START)) { // ENTR or PFIN at/after 14:00
                returnPm = tt;
                break;
            }
        }
        int latenessPm = 0;
        if (returnPm != null) {
            latenessPm = Math.max(0, (int) Duration.between(AFTERNOON_START, returnPm).toMinutes());
            if (returnPm.isBefore(AFTERNOON_START)) latenessPm = 0;
        }

        boolean dayComplete = (arrivee != null && outLocal != null)
                && (latenessAm <= LATE_LIMIT_MIN)
                && (latenessPm <= LATE_LIMIT_MIN);

        // Upsert Feuilletemp (mois/annee)
        int mois = date.getMonthValue();
        int annee = date.getYear();
        Feuilletemp feuille = feuilletempRepository
                .findByIdemploye_IdAndMoisAndAnnee(emp.getId(), mois, annee)
                .orElseGet(() -> {
                    Feuilletemp f = new Feuilletemp();
                    f.setIdemploye(emp);
                    f.setMois(mois);
                    f.setAnnee(annee);
                    return feuilletempRepository.save(f);
                });

        // Upsert Detailfeuilletemp du jour
        Detailfeuilletemp detail = detailfeuilletempRepository
                .findByIdfeuilletemps_IdAndDatejour(feuille.getId(), date)
                .orElseGet(() -> {
                    Detailfeuilletemp d = new Detailfeuilletemp();
                    d.setIdfeuilletemps(feuille);
                    d.setDatejour(date);
                    return d;
                });

        LocalTime hin = LocalDateTime.ofInstant(firstInTs, ZoneId.systemDefault()).toLocalTime();
        LocalTime hout = LocalDateTime.ofInstant(lastOutTs, ZoneId.systemDefault()).toLocalTime();

        detail.setHeuresentree(hin);
        detail.setHeuressortie(hout);
        // Si journée complète, on force 8h de travail
        double storedHours = dayComplete ? 8.0 : workHours;
        detail.setHeurestravaillees(java.math.BigDecimal.valueOf(round2(storedHours)));
        // Heures supplémentaires: tout ce qui dépasse 8h réelles travaillées
        // Utilise workHours (réel) et non storedHours (qui peut être forcé à 8h pour une journée complète)
        overtime = Math.max(0d, workHours - 8.0);
        detail.setHeuressup(java.math.BigDecimal.valueOf(round2(overtime)));
        detailfeuilletempRepository.save(detail);

        // Recalcule agrégat mensuel des heures sup comme somme des détails
        List<Detailfeuilletemp> details = detailfeuilletempRepository.findByIdfeuilletemps_Id(feuille.getId());
        double sumSup = details.stream()
                .map(Detailfeuilletemp::getHeuressup)
                .filter(Objects::nonNull)
                .mapToDouble(bd -> bd.doubleValue())
                .sum();
        feuille.setHeuressupplementaires(java.math.BigDecimal.valueOf(round2(sumSup)));
        // jourstravailles: 1 jour = 8h de travail
        double sumDays = details.stream()
                .map(Detailfeuilletemp::getHeurestravaillees)
                .filter(Objects::nonNull)
                .mapToDouble(bd -> bd.doubleValue() / 8.0)
                .sum();
        feuille.setJourstravailles(java.math.BigDecimal.valueOf(round2(sumDays)));
        // absences: nombre de jours marqués absents dans le mois
        long absCount = details.stream()
                .map(Detailfeuilletemp::getEstabsent)
                .filter(Boolean.TRUE::equals)
                .count();
        feuille.setAbsences(java.math.BigDecimal.valueOf(absCount));

        // Recalcule le nombre de retards mensuels (matin/après-midi) en scannant tous les pointages du mois
        LocalDate monthStart = LocalDate.of(annee, mois, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        Instant mStart = monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant mEnd = monthEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant();
        List<Pointage> monthPts = pointageRepository.findValidByEmployeBetween(emp.getId(), mStart, mEnd);
        // Grouper par jour
        Map<LocalDate, List<Pointage>> byDay = new HashMap<>();
        for (Pointage pt : monthPts) {
            LocalDate d = LocalDateTime.ofInstant(pt.getDateheure(), ZoneId.systemDefault()).toLocalDate();
            byDay.computeIfAbsent(d, k -> new ArrayList<>()).add(pt);
        }
        int totalRetards = 0;
        for (Map.Entry<LocalDate, List<Pointage>> e : byDay.entrySet()) {
            List<Pointage> list = e.getValue();
            list.sort(Comparator.comparing(Pointage::getDateheure));
            // matin
            Instant firstInI = list.stream().filter(pp -> pp.getIdtypepointage().getId() == 1)
                    .map(Pointage::getDateheure).min(Comparator.naturalOrder()).orElse(null);
            LocalTime arr = firstInI != null ? LocalDateTime.ofInstant(firstInI, ZoneId.systemDefault()).toLocalTime() : null;
            int amLate = 0;
            if (arr != null) {
                amLate = Math.max(0, (int) Duration.between(LocalTime.of(8,0), arr).toMinutes());
                if (arr.isBefore(LocalTime.of(8,0))) amLate = 0;
            }
            // après-midi
            LocalTime retPm = null;
            for (Pointage ptd : list) {
                int t = ptd.getIdtypepointage().getId();
                LocalTime tt = LocalDateTime.ofInstant(ptd.getDateheure(), ZoneId.systemDefault()).toLocalTime();
                if ((t == 1 || t == 4) && !tt.isBefore(LocalTime.of(14,0))) { retPm = tt; break; }
            }
            int pmLate = 0;
            if (retPm != null) {
                pmLate = Math.max(0, (int) Duration.between(LocalTime.of(14,0), retPm).toMinutes());
                if (retPm.isBefore(LocalTime.of(14,0))) pmLate = 0;
            }
            if (amLate > 10) totalRetards += 1;
            if (pmLate > 10) totalRetards += 1;
        }
        feuille.setRetards(totalRetards);
        feuilletempRepository.save(feuille);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // === Timesheet query ===
    public TimesheetDto getMonthlyTimesheet(Integer employeId, Integer mois, Integer annee) {
        if (mois == null || annee == null) {
            LocalDate now = LocalDate.now();
            if (mois == null) mois = now.getMonthValue();
            if (annee == null) annee = now.getYear();
        }

        TimesheetDto dto = new TimesheetDto();
        dto.employeId = employeId;
        dto.mois = mois;
        dto.annee = annee;

        Feuilletemp feuille = feuilletempRepository
                .findByIdemploye_IdAndMoisAndAnnee(employeId, mois, annee)
                .orElse(null);

        java.util.ArrayList<TimesheetDetailDto> detailsDto = new java.util.ArrayList<>();
        if (feuille != null) {
            dto.joursTravailles = feuille.getJourstravailles();
            dto.heuresSupplementaires = feuille.getHeuressupplementaires();
            dto.absences = feuille.getAbsences();
            dto.retards = feuille.getRetards();

            java.util.List<Detailfeuilletemp> details = detailfeuilletempRepository.findByIdfeuilletemps_Id(feuille.getId());
            for (Detailfeuilletemp d : details) {
                TimesheetDetailDto it = new TimesheetDetailDto();
                it.date = d.getDatejour();
                it.entree = d.getHeuresentree();
                it.sortie = d.getHeuressortie();
                it.heuresTravaillees = d.getHeurestravaillees();
                it.heuresSup = d.getHeuressup();
                it.estAbsent = d.getEstabsent();
                it.commentaire = d.getCommentaire();
                detailsDto.add(it);
            }
            // Fusionner les congés validés (vue v_conges_valides)
            LocalDate monthStart = LocalDate.of(annee, mois, 1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            var leaves = validatedLeaveRepository.findInRange(monthStart, monthEnd);
            // Ne garder que ceux de l'employé
            var leavesOfEmp = new java.util.ArrayList<com.backend.entities.VCongeValide>();
            for (var lv : leaves) if (lv.getIdemploye() != null && lv.getIdemploye().equals(employeId)) leavesOfEmp.add(lv);

            // Indexer les détails existants par date
            java.util.Map<LocalDate, TimesheetDetailDto> byDate = new java.util.HashMap<>();
            for (TimesheetDetailDto it : detailsDto) byDate.put(it.date, it);

            // Pour chaque congé recouvrant des jours du mois, marquer absent/ajouter commentaire
            for (var lv : leavesOfEmp) {
                LocalDate d = lv.getDatedebut();
                LocalDate end = lv.getDatefin();
                if (d == null || end == null) continue;
                LocalDate cur = d.isBefore(monthStart) ? monthStart : d;
                LocalDate stop = end.isAfter(monthEnd) ? monthEnd : end;
                while (!cur.isAfter(stop)) {
                    TimesheetDetailDto line = byDate.get(cur);
                    if (line == null) {
                        line = new TimesheetDetailDto();
                        line.date = cur;
                        line.estAbsent = true;
                        line.commentaire = "Congé: " + (lv.getTypeconge() == null ? "" : lv.getTypeconge());
                        detailsDto.add(line);
                        byDate.put(cur, line);
                    } else {
                        // Conserver les heures si présentes mais signaler qu'il était en congé
                        line.estAbsent = (line.estAbsent != null && line.estAbsent) || true;
                        String note = "Congé: " + (lv.getTypeconge() == null ? "" : lv.getTypeconge());
                        if (line.commentaire == null || line.commentaire.isBlank()) line.commentaire = note; else if (!line.commentaire.contains("Congé:")) line.commentaire += " | " + note;
                    }
                    cur = cur.plusDays(1);
                }
            }
            // Sort by date
            detailsDto.sort(java.util.Comparator.comparing(t -> t.date));
        } else {
            // If no feuille yet, initialize aggregates to zero
            dto.joursTravailles = java.math.BigDecimal.valueOf(0);
            dto.heuresSupplementaires = java.math.BigDecimal.valueOf(0);
            dto.absences = java.math.BigDecimal.valueOf(0);
            dto.retards = 0;
            // Même si pas de feuille, on peut quand même afficher les congés du mois
            LocalDate monthStart = LocalDate.of(annee, mois, 1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            var leaves = validatedLeaveRepository.findInRange(monthStart, monthEnd);
            for (var lv : leaves) {
                if (lv.getIdemploye() == null || !lv.getIdemploye().equals(employeId)) continue;
                LocalDate cur = lv.getDatedebut();
                LocalDate stop = lv.getDatefin();
                if (cur == null || stop == null) continue;
                if (cur.isBefore(monthStart)) cur = monthStart;
                if (stop.isAfter(monthEnd)) stop = monthEnd;
                while (!cur.isAfter(stop)) {
                    TimesheetDetailDto it = new TimesheetDetailDto();
                    it.date = cur;
                    it.estAbsent = true;
                    it.commentaire = "Congé: " + (lv.getTypeconge() == null ? "" : lv.getTypeconge());
                    detailsDto.add(it);
                    cur = cur.plusDays(1);
                }
            }
            // Trier
            detailsDto.sort(java.util.Comparator.comparing(t -> t.date));
        }
        dto.details = detailsDto;
        return dto;
    }
}
