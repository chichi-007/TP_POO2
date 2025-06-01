package com.example.Model;

import com.example.Service.SauvegardeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestSauvegarde {
    public static void main(String[] args) {
        Concert concert = new Concert("c1", "Afro Show", LocalDateTime.now(), "Douala", 100, "Daphné", "Afro");
        Conference conf = new Conference("conf1", "Tech Talk", LocalDateTime.now(), "Yaoundé", 80, "IA", Arrays.asList("Dr. John", "Prof. Diane"));

        List<Evenement> evenements = Arrays.asList(concert, conf);

        // Sauvegarder
        SauvegardeService.sauvegarderEvenements(evenements);

        // Recharger
        List<Evenement> charges = SauvegardeService.chargerEvenements();
        charges.forEach(e -> System.out.println("✔️ " + e.getNom() + " (" + e.getLieu() + ")"));
    }
}

