package com.coaching.model;

import java.time.LocalDate;

public class SuiviNutrition {
    private int id;
    private int sportifId;
    private LocalDate dateRepas;
    private String typeRepas;
    private int calories;
    private double proteinesG;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSportifId() { return sportifId; }
    public void setSportifId(int sportifId) { this.sportifId = sportifId; }
    public LocalDate getDateRepas() { return dateRepas; }
    public void setDateRepas(LocalDate dateRepas) { this.dateRepas = dateRepas; }
    public String getTypeRepas() { return typeRepas; }
    public void setTypeRepas(String typeRepas) { this.typeRepas = typeRepas; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    public double getProteinesG() { return proteinesG; }
    public void setProteinesG(double proteinesG) { this.proteinesG = proteinesG; }
}