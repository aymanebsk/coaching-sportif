package com.coaching.model;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String role;
    private int age;
    private double tailleCm;
    private String objectif;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getTailleCm() { return tailleCm; }
    public void setTailleCm(double tailleCm) { this.tailleCm = tailleCm; }
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    @Override
    public String toString() {
        return nom + " [" + objectif + "]";
    }
}