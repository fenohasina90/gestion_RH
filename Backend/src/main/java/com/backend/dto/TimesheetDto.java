package com.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class TimesheetDto {
    public Integer employeId;
    public Integer mois;
    public Integer annee;
    public BigDecimal joursTravailles; // sum(hours/8)
    public BigDecimal heuresSupplementaires;
    public BigDecimal absences; // count of absent days
    public Integer retards; // count of lateness occurrences
    public List<TimesheetDetailDto> details;
}
