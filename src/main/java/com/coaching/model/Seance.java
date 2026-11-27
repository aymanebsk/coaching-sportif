package com.coaching.model;

public class Seance {
    private int id;
    private int programmeId;
    private String titre;
    private String jourSemaine;
    private int dureeMinutes;
    private int caloriesEstimees;
    private String statut;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProgrammeId() { return programmeId; }
    public void setProgrammeId(int programmeId) { this.programmeId = programmeId; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }
    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public int getCaloriesEstimees() { return caloriesEstimees; }
    public void setCaloriesEstimees(int caloriesEstimees) { this.caloriesEstimees = caloriesEstimees; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}