package com.backend.dto;

public class CreatePointageRequest {
    public Integer employeId;
    public String date; // yyyy-MM-dd
    public String time; // HH:mm
    public Integer typeId; // 1=Entree,2=Sortie,3=Debut pause,4=Fin pause
    public String commentaire;
}
