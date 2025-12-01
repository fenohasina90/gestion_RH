-- Payroll setup: parameters, IRSA function, and full payslip view for Madagascar context
-- Safe to run multiple times

-- 1) Cotisation parameters table
CREATE TABLE IF NOT EXISTS parametrecotisation (
    id               SERIAL PRIMARY KEY,
    libelle          VARCHAR(100) NOT NULL,
    taux             NUMERIC(6,2),              -- percentage, e.g. 1.00 for 1%
    plafondsalarial  NUMERIC(12,2),             -- monthly salary cap for contributions
    dateeffet        DATE NOT NULL,
    datefin          DATE
);

-- 1.b) IRSA brackets parameters (progressive tax)
CREATE TABLE IF NOT EXISTS parametre_irsa (
    id        SERIAL PRIMARY KEY,
    borne_min NUMERIC(12,2) NOT NULL,
    borne_max NUMERIC(12,2),      -- NULL = no upper bound
    taux      NUMERIC(5,2) NOT NULL, -- percentage
    dateeffet DATE NOT NULL,
    datefin   DATE
);

-- 1.c) Overtime coefficients parameters
CREATE TABLE IF NOT EXISTS parametre_heures_sup (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50) UNIQUE NOT NULL,  -- HS_NORMALE, HS_DIMANCHE, etc.
    libelle     VARCHAR(100) NOT NULL,
    coefficient NUMERIC(6,2) NOT NULL,        -- e.g. 1.30, 1.50
    dateeffet   DATE NOT NULL,
    datefin     DATE
);

-- 2) Seed parameters (CNAPS/OSTIE) with current effective rates
-- Plafond salarial demandé: 8 * 3_500_000 Ar = 28_000_000 Ar
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM parametrecotisation WHERE libelle = 'CNAPS_SALARIE' AND datefin IS NULL
    ) THEN
        INSERT INTO parametrecotisation(libelle, taux, plafondsalarial, dateeffet, datefin)
        VALUES ('CNAPS_SALARIE', 1.00, 2800000.00, CURRENT_DATE, NULL);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM parametrecotisation WHERE libelle = 'CNAPS_EMPLOYEUR' AND datefin IS NULL
    ) THEN
        INSERT INTO parametrecotisation(libelle, taux, plafondsalarial, dateeffet, datefin)
        VALUES ('CNAPS_EMPLOYEUR', 13.00, 2800000.00, CURRENT_DATE, NULL);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM parametrecotisation WHERE libelle = 'OSTIE_SALARIE' AND datefin IS NULL
    ) THEN
        INSERT INTO parametrecotisation(libelle, taux, plafondsalarial, dateeffet, datefin)
        VALUES ('OSTIE_SALARIE', 1.00, 2800000.00, CURRENT_DATE, NULL);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM parametrecotisation WHERE libelle = 'OSTIE_EMPLOYEUR' AND datefin IS NULL
    ) THEN
        INSERT INTO parametrecotisation(libelle, taux, plafondsalarial, dateeffet, datefin)
        VALUES ('OSTIE_EMPLOYEUR', 5.00, 2800000.00, CURRENT_DATE, NULL);
    END IF;
END $$;

-- 2.b) Seed default IRSA brackets if none active
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM parametre_irsa WHERE datefin IS NULL) THEN
        INSERT INTO parametre_irsa (borne_min, borne_max, taux, dateeffet, datefin) VALUES
        (     0, 350000,  0.00, CURRENT_DATE, NULL),
        (350001, 400000,  5.00, CURRENT_DATE, NULL),
        (400001, 500000, 10.00, CURRENT_DATE, NULL),
        (500001, 600000, 15.00, CURRENT_DATE, NULL),
        (600001,   NULL, 20.00, CURRENT_DATE, NULL);
    END IF;
END $$;

-- 2.c) Seed default overtime coefficient if none active
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM parametre_heures_sup WHERE code = 'HS_NORMALE' AND datefin IS NULL) THEN
        INSERT INTO parametre_heures_sup(code, libelle, coefficient, dateeffet, datefin)
        VALUES ('HS_NORMALE', 'Heures supplémentaires normales', 1.30, CURRENT_DATE, NULL);
    END IF;
END $$;

-- 3) Helper: get active rate row by libelle
CREATE OR REPLACE FUNCTION get_active_param(lib TEXT)
RETURNS parametrecotisation AS $$
DECLARE r parametrecotisation;
BEGIN
  SELECT * INTO r
  FROM parametrecotisation
  WHERE libelle = lib AND (datefin IS NULL OR datefin >= CURRENT_DATE)
  ORDER BY dateeffet DESC
  LIMIT 1;
  RETURN r;
END;
$$ LANGUAGE plpgsql;

-- 3.b) Helper: get active overtime coefficient by code
CREATE OR REPLACE FUNCTION get_coef_heures_sup(p_code TEXT)
RETURNS NUMERIC AS $$
DECLARE
    coef NUMERIC;
BEGIN
  SELECT coefficient INTO coef
  FROM parametre_heures_sup
  WHERE code = p_code
    AND dateeffet <= CURRENT_DATE
    AND (datefin IS NULL OR datefin >= CURRENT_DATE)
  ORDER BY dateeffet DESC
  LIMIT 1;

  RETURN COALESCE(coef, 1.30);
END;
$$ LANGUAGE plpgsql;

-- 4) IRSA progressive tax function (amount in Ariary per month), based on parametre_irsa
CREATE OR REPLACE FUNCTION calcul_irsa(montant NUMERIC)
RETURNS NUMERIC AS $$
DECLARE
    base NUMERIC := GREATEST(montant, 0);
    total_impot NUMERIC := 0;
    r RECORD;
    tranche_montant NUMERIC;
BEGIN
    IF base <= 0 THEN
        RETURN 0;
    END IF;

    FOR r IN
        SELECT *
        FROM parametre_irsa
        WHERE dateeffet <= CURRENT_DATE
          AND (datefin IS NULL OR datefin >= CURRENT_DATE)
        ORDER BY borne_min
    LOOP
        IF r.borne_max IS NULL THEN
            tranche_montant := GREATEST(base - r.borne_min + 1, 0);
        ELSE
            tranche_montant := GREATEST(LEAST(base, r.borne_max) - r.borne_min + 1, 0);
        END IF;

        IF tranche_montant > 0 THEN
            total_impot := total_impot + tranche_montant * (r.taux / 100.0);
        END IF;
    END LOOP;

    RETURN GREATEST(total_impot, 0);
END;
$$ LANGUAGE plpgsql;

-- 5) Full payslip view v_bulletin_paie_complet
-- Notes:
-- - Salaire de base pris depuis employe.salaire
-- - Jours/Heures depuis feuilletemps/detailfeuilletemps
-- - Congés payés (justifiés) depuis v_conges_valides
-- - Minutes de retard: ici on expose feuilletemps.retards (attention: selon votre modèle actuel c'est un compteur; adaptez si vous stockez les minutes)
-- - Certaines primes/indemnités mises à 0 par défaut (à adapter selon vos tables)

CREATE OR REPLACE VIEW v_bulletin_paie_complet AS
WITH base AS (
    SELECT
        e.id                                    AS employe_id,
        e.matricule                             AS matricule,
        (e.prenom || ' ' || e.nom)             AS nom_complet,
        e.cin                                    AS cin,
        e.numerocnaps                            AS numero_cnaps,
        e.numeroostie                            AS numero_ostie,
        COALESCE(cph.nom, cp.nom, '')::varchar(50)            AS categorie,
        COALESCE(d.nom, '')                     AS departement,
        f.mois,
        f.annee,
        COALESCE(sb.salairebrut, 0)::numeric(12,2)   AS salaire_base,
        (COALESCE(sb.salairebrut,0) / 173.33)::numeric(12,2) AS taux_horaire_base,
        0::int                                  AS anciennete_annees,
        COALESCE(f.jourstravailles, 0)::numeric(12,2) AS jours_travailles,
        COALESCE(f.heuressupplementaires, 0)::numeric(12,2) AS heures_supplementaires,
        (
          SELECT COALESCE(SUM(
              COALESCE(GREATEST(0, EXTRACT(EPOCH FROM (
                  am_first - (g.d::timestamp + time '08:10')
              )) / 60), 0)
            + COALESCE(GREATEST(0, EXTRACT(EPOCH FROM (
                  pm_first - (g.d::timestamp + time '14:10')
              )) / 60), 0)
          ), 0)::int
          FROM generate_series(
                   make_date(f.annee, f.mois, 1),
                   (make_date(f.annee, f.mois, 1) + INTERVAL '1 month - 1 day')::date,
                   interval '1 day'
               ) AS g(d)
          LEFT JOIN LATERAL (
              SELECT MIN(p.dateheure + interval '3 hours') AS am_first
              FROM pointage p
              WHERE p.idemploye = e.id
                AND p.idtypepointage = 1
                AND (p.dateheure + interval '3 hours')::date = g.d
          ) am ON TRUE
          LEFT JOIN LATERAL (
              SELECT MIN(p.dateheure + interval '3 hours') AS pm_first
              FROM pointage p
              WHERE p.idemploye = e.id
                AND p.idtypepointage IN (1, 4)
                AND (p.dateheure + interval '3 hours')::date = g.d
                AND (p.dateheure + interval '3 hours')::time >= time '14:00'
          ) pm ON TRUE
        )                                        AS minutes_retard_total,
        COALESCE(f.absences, 0)::numeric(12,2)  AS jours_absences_total
    FROM feuilletemps f
    JOIN employe e ON e.id = f.idemploye
    -- Latest salary from salairebase
    LEFT JOIN LATERAL (
        SELECT s.*
        FROM salairebase s
        WHERE s.idemploye = e.id
        ORDER BY COALESCE(s.datefin, DATE '9999-12-31') DESC, s.dateeffet DESC
        LIMIT 1
    ) sb ON TRUE
    -- Derive employee department from latest historiqueposte
    LEFT JOIN LATERAL (
        SELECT hp.*
        FROM historiqueposte hp
        WHERE hp.idemploye = e.id
        ORDER BY COALESCE(hp.datefin, DATE '9999-12-31') DESC, hp.datedebut DESC
        LIMIT 1
    ) hp ON TRUE
    LEFT JOIN departement d ON d.id = hp.iddepartement
    -- Categorie depuis histo poste ou fallback employe.idcategorie
    LEFT JOIN categoriepersonnel cph ON cph.id = hp.idcategorie
    LEFT JOIN categoriepersonnel cp ON cp.id = e.idcategorie
)
,
-- Congés valides du mois, ramenés en jours (intersection avec mois)
conges AS (
    SELECT
        b.employe_id,
        b.mois,
        b.annee,
        SUM(
            GREATEST(0,
                (LEAST(v.datefin, (make_date(b.annee, b.mois, 1) + INTERVAL '1 month' - INTERVAL '1 day')::date)
                - GREATEST(v.datedebut, make_date(b.annee, b.mois, 1)) + 1)
            )
        )::numeric(12,2) AS jours_conges_payes
    FROM base b
    JOIN v_conges_valides v ON v.idemploye = b.employe_id
     AND v.datefin >= make_date(b.annee, b.mois, 1)
     AND v.datedebut <= (make_date(b.annee, b.mois, 1) + INTERVAL '1 month' - INTERVAL '1 day')::date
    GROUP BY b.employe_id, b.mois, b.annee
),
-- Absences non justifiées = total absences - congés payés (>=0)
presence AS (
    SELECT
        b.*,
        COALESCE(c.jours_conges_payes, 0) AS jours_absences_justifiees,
        GREATEST(COALESCE(b.jours_absences_total,0) - COALESCE(c.jours_conges_payes,0), 0)::numeric(12,2) AS jours_absences_non_justifiees
    FROM base b
    LEFT JOIN conges c ON c.employe_id = b.employe_id AND c.mois = b.mois AND c.annee = b.annee
),
-- Agrégation des éléments variables de paie par employé / mois / année
elem_vars AS (
    SELECT
        ev.idemploye AS employe_id,
        ev.mois,
        ev.annee,
        SUM(CASE WHEN te.libelle = 'Prime de rendement' THEN ev.montant ELSE 0 END) AS prime_rendement,
        SUM(CASE WHEN te.libelle = 'Prime d''ancienneté' THEN ev.montant ELSE 0 END) AS prime_anciennete,
        SUM(CASE WHEN te.libelle = 'Indemnité transport' THEN ev.montant ELSE 0 END) AS indemnite_transport,
        SUM(CASE WHEN te.libelle = 'Indemnité logement' THEN ev.montant ELSE 0 END) AS indemnite_logement,
        SUM(CASE WHEN te.libelle = 'Avance sur salaire' THEN ev.montant ELSE 0 END) AS avance_sur_salaire,
        SUM(CASE WHEN te.libelle = 'Retenue absence' THEN ev.montant ELSE 0 END) AS retenue_absence
    FROM elementvariable ev
    JOIN typeelementpaie te ON te.id = ev.idtypeelementpaie
    GROUP BY ev.idemploye, ev.mois, ev.annee
),
-- Heures sup breakdown (utilise coefficient paramétrable HS_NORMALE)
heures_sup AS (
    SELECT
        p.*,
        COALESCE(p.heures_supplementaires, 0) AS heures_sup_normales,
        0::numeric(12,2) AS heures_sup_majorees,
        (COALESCE(p.heures_supplementaires,0) * p.taux_horaire_base * get_coef_heures_sup('HS_NORMALE'))::numeric(12,2) AS montant_heures_sup
    FROM presence p
),
-- Primes & indemnités (alimentées par elementvariable)
primes AS (
    SELECT
        h.*,
        COALESCE(v.prime_rendement, 0)::numeric(12,2) AS prime_rendement,
        COALESCE(v.prime_anciennete, 0)::numeric(12,2) AS prime_anciennete,
        0::numeric(12,2) AS prime_assiduite,
        COALESCE(v.indemnite_transport, 0)::numeric(12,2) AS indemnite_transport,
        COALESCE(v.indemnite_logement, 0)::numeric(12,2) AS indemnite_logement,
        COALESCE(v.avance_sur_salaire, 0)::numeric(12,2) AS avance_sur_salaire,
        COALESCE(v.retenue_absence, 0)::numeric(12,2) AS retenue_absence
    FROM heures_sup h
    LEFT JOIN elem_vars v
      ON v.employe_id = h.employe_id
     AND v.mois = h.mois
     AND v.annee = h.annee
),
-- Deductions présence
deductions AS (
    SELECT
        pr.*,
        ((COALESCE(pr.minutes_retard_total,0)::numeric / 60.0) * pr.taux_horaire_base)::numeric(12,2) AS deduction_retards,
        ((COALESCE(pr.jours_absences_non_justifiees,0) / 22.0) * pr.salaire_base)::numeric(12,2) AS deduction_absences
    FROM primes pr
),
-- Brut & cotisations
brut AS (
    SELECT
        d.*,
        -- Gains
        (d.salaire_base + d.prime_rendement + d.prime_anciennete + d.prime_assiduite + d.indemnite_transport + d.indemnite_logement + d.montant_heures_sup)::numeric(12,2) AS total_gains,
        -- Deductions présence
        (d.deduction_retards + d.deduction_absences)::numeric(12,2) AS total_deductions_presence,
        -- Salaire brut
        (d.salaire_base + d.prime_rendement + d.prime_anciennete + d.prime_assiduite + d.indemnite_transport + d.indemnite_logement + d.montant_heures_sup
         - (d.deduction_retards + d.deduction_absences))::numeric(12,2) AS salaire_brut
    FROM deductions d
),
-- Cotisations sociales (utilise paramétres actifs & plafond)
cotis AS (
    SELECT
        b.*,
        (get_active_param('CNAPS_SALARIE')).taux    AS cnaps_sal_taux,
        (get_active_param('CNAPS_SALARIE')).plafondsalarial AS cnaps_plaf,
        (get_active_param('OSTIE_SALARIE')).taux    AS ostie_sal_taux,
        (get_active_param('OSTIE_SALARIE')).plafondsalarial AS ostie_plaf,
        LEAST(b.salaire_brut, COALESCE((get_active_param('CNAPS_SALARIE')).plafondsalarial, b.salaire_brut)) AS base_cnaps,
        LEAST(b.salaire_brut, COALESCE((get_active_param('OSTIE_SALARIE')).plafondsalarial, b.salaire_brut)) AS base_ostie
    FROM brut b
),
net AS (
    SELECT
        c.*,
        ROUND(c.base_cnaps * c.cnaps_sal_taux / 100.0, 2) AS cnaps_salarie,
        ROUND(c.base_ostie * c.ostie_sal_taux / 100.0, 2) AS ostie_salarie,
        -- Salaire imposable
        (c.salaire_brut - (ROUND(c.base_cnaps * c.cnaps_sal_taux / 100.0, 2) + ROUND(c.base_ostie * c.ostie_sal_taux / 100.0, 2)))::numeric(12,2) AS salaire_imposable,
        COALESCE(c.avance_sur_salaire, 0)::numeric(12,2) AS avance_sur_salaire_calc,
        0::numeric(12,2) AS pret_en_cours_calc,
        COALESCE(c.retenue_absence, 0)::numeric(12,2) AS autres_retenues_calc
    FROM cotis c
),
irsa_calc AS (
    SELECT
        n.*,
        ROUND(calcul_irsa(n.salaire_imposable), 2) AS irsa,
        -- Charges employeur informatives
        (get_active_param('CNAPS_EMPLOYEUR')).taux AS cnaps_emp_taux,
        (get_active_param('OSTIE_EMPLOYEUR')).taux AS ostie_emp_taux,
        ROUND(LEAST(n.salaire_brut, COALESCE((get_active_param('CNAPS_EMPLOYEUR')).plafondsalarial, n.salaire_brut)) * (get_active_param('CNAPS_EMPLOYEUR')).taux / 100.0, 2) AS cnaps_employeur,
        ROUND(LEAST(n.salaire_brut, COALESCE((get_active_param('OSTIE_EMPLOYEUR')).plafondsalarial, n.salaire_brut)) * (get_active_param('OSTIE_EMPLOYEUR')).taux / 100.0, 2) AS ostie_employeur
    FROM net n
)
SELECT
    -- SECTION 1: IDENTIFICATION
    employe_id,
    matricule,
    nom_complet,
    cin,
    numero_cnaps,
    numero_ostie,
    categorie,
    departement,
    mois,
    annee,
    -- SECTION 2: ELEMENTS DE BASE
    salaire_base,
    taux_horaire_base,
    anciennete_annees,
    -- SECTION 3: PRESENCE
    jours_travailles,
    COALESCE(jours_absences_justifiees, 0) AS jours_absences_justifiees,
    COALESCE(jours_absences_non_justifiees, 0) AS jours_absences_non_justifiees,
    heures_supplementaires,
    minutes_retard_total,
    -- SECTION 4: HEURES SUP
    heures_sup_normales,
    heures_sup_majorees,
    montant_heures_sup,
    -- SECTION 5: PRIMES
    prime_rendement,
    prime_anciennete,
    prime_assiduite,
    indemnite_transport,
    indemnite_logement,
    -- SECTION 6: DEDUCTIONS PRESENCE
    deduction_retards,
    deduction_absences,
    -- SECTION 7: BRUT
    total_gains,
    total_deductions_presence,
    salaire_brut,
    -- SECTION 8: COTISATIONS
    cnaps_salarie,
    ostie_salarie,
    (cnaps_salarie + ostie_salarie) AS total_cotisations,
    -- SECTION 9: IRSA
    salaire_imposable,
    irsa,
    -- SECTION 10: AUTRES RETENUES
    COALESCE(avance_sur_salaire_calc, 0)::numeric(12,2) AS avance_sur_salaire,
    COALESCE(pret_en_cours_calc, 0)::numeric(12,2) AS pret_en_cours,
    COALESCE(autres_retenues_calc, 0)::numeric(12,2) AS autres_retenues,
    -- SECTION 11: NET A PAYER
    (cnaps_salarie + ostie_salarie + irsa + COALESCE(avance_sur_salaire_calc,0) + COALESCE(pret_en_cours_calc,0) + COALESCE(autres_retenues_calc,0)) AS total_retenues,
    (salaire_brut - (cnaps_salarie + ostie_salarie + irsa + COALESCE(avance_sur_salaire_calc,0) + COALESCE(pret_en_cours_calc,0) + COALESCE(autres_retenues_calc,0))) AS salaire_net,
    -- SECTION 12: CHARGES EMPLOYEUR (info)
    COALESCE(cnaps_emp_taux, 0::numeric(6,2)) AS cnaps_emp_taux,
    COALESCE(ostie_emp_taux, 0::numeric(6,2)) AS ostie_emp_taux,
    COALESCE(cnaps_employeur, 0) AS cnaps_employeur,
    COALESCE(ostie_employeur, 0) AS ostie_employeur,
    (salaire_brut + COALESCE(cnaps_employeur,0) + COALESCE(ostie_employeur,0)) AS cout_total_employeur
FROM irsa_calc;
