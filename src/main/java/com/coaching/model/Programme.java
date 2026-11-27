package com.coaching.model;

public class Programme {
    private int id;
    private String titre;
    private String description;
    private String niveau;
    private int dureeSemaines;
    private int coachId;
    private int sportifId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public int getDureeSemaines() { return dureeSemaines; }
    public void setDureeSemaines(int dureeSemaines) { this.dureeSemaines = dureeSemaines; }
    public int getCoachId() { return coachId; }
    public void setCoachId(int coachId) { this.coachId = coachId; }
    public int getSportifId() { return sportifId; }
    public void setSportifId(int sportifId) { this.sportifId = sportifId; }
}