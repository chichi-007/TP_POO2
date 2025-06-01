package com.example.Model;



import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeName("conference")
public class Conference extends Evenement {
    private String theme;
    private List<Intervenant> intervenants;


    public Conference() {
        // Constructeur par défaut (nécessaire pour Jackson)
        super(); // Appelle le constructeur par défaut de Evenement
        this.intervenants = new ArrayList<>();
    }
    public Conference(String id, String nom, LocalDateTime date, String lieu, int capaciteMax, String theme, List<String> intervenants) {
        super(id, nom, date, lieu, capaciteMax);
        this.theme = theme;
        this.intervenants = new ArrayList<>();
    }

    public void ajouterIntervenant(Intervenant intervenant) {
        if (this.intervenants == null) {
            this.intervenants = new ArrayList<>();
        }
        this.intervenants.add(intervenant);
    }


    @Override
    public void afficherDetails() {
        // Pour joindre, nous devons d'abord convertir la liste d'Intervenant en une liste de String
        String intervenantsJoined = intervenants.stream()
                .map(Intervenant::getNom) // Ou Intervenant::toString si toString est bien formaté
                .collect(Collectors.joining(", "));

        System.out.println("Conférence : " + nom + " | Thème : " + theme + " | Intervenants : " + intervenantsJoined);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    // Getter pour la liste d'intervenants : doit retourner List<Intervenant>
    public List<Intervenant> getIntervenants() {
        return intervenants;
    }

    // Setter pour la liste d'intervenants : doit prendre List<Intervenant>
    public void setIntervenants(List<Intervenant> intervenants) {
        this.intervenants = intervenants;
    }

}


