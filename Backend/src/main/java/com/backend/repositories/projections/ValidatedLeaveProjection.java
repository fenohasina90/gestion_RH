package com.backend.repositories.projections;

import java.time.LocalDate;

public interface ValidatedLeaveProjection {
    Integer getIddemande();
    Integer getIdemploye();
    Integer getIdtypeconge();
    String getTypeconge();
    LocalDate getDatedebut();
    LocalDate getDatefin();
    Integer getNombrejours();
    String getStatut();
}
