package com.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

public class TimesheetDetailDto {
    public LocalDate date;
    public LocalTime entree;
    public LocalTime sortie;
    public BigDecimal heuresTravaillees;
    public BigDecimal heuresSup;
    public Boolean estAbsent;
    public String commentaire;
}
