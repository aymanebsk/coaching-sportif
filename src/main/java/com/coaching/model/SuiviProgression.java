package com.coaching.model;

import java.time.LocalDate;

public class SuiviProgression {
    private int id;
    private int sportifId;
    private LocalDate dateMesure;
    private double poidsKg;
    private double tourTailleCm;
    private String commentaire;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSportifId() { return sportifId; }
    public void setSportifId(int sportifId) { this.sportifId = sportifId; }
    public LocalDate getDateMesure() { return dateMesure; }
    public void setDateMesure(LocalDate dateMesure) { this.dateMesure = dateMesure; }
    public double getPoidsKg() { return poidsKg; }
    public void setPoidsKg(double poidsKg) { this.poidsKg = poidsKg; }
    public double getTourTailleCm() { return tourTailleCm; }
    public void setTourTailleCm(double tourTailleCm) { this.tourTailleCm = tourTailleCm; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}