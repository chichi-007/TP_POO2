package com.example.Model;

// Dans src/main/java/Model/Intervenant.java

public class Intervenant {
    private String nom;
    private String specialite; // Ou "domaine", "rôle", "genreMusical" etc.

    public Intervenant(String nom, String specialite) {
        this.nom = nom;
        this.specialite = specialite;
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public String getSpecialite() {
        return specialite;
    }

    // Vous pouvez ajouter un toString() pour un affichage facile
    @Override
    public String toString() {
        return nom + " (" + specialite + ")";
    }
}