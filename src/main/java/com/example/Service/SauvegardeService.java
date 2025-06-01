package com.example.Service;

// Assurez-vous que le package est correct



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.Model.Evenement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList; // Pour retourner une liste vide au lieu de null en cas d'erreur
import java.util.List;

public class SauvegardeService {

    // Nom du fichier par défaut. Rendu non-final si vous souhaitez le rendre configurable via une méthode.
    private static String FICHIER = "evenements.json";

    // L'ObjectMapper est créé et configuré une seule fois
    private static final ObjectMapper mapper;

    static { // Bloc d'initialisation statique, exécuté une seule fois au chargement de la classe
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Enregistre le module pour LocalDateTime
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Rend le JSON lisible avec indentations
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Sérialise LocalDateTime au format ISO 8601
    }

    // Constructeur privé pour empêcher l'instanciation de cette classe utilitaire
    private SauvegardeService() {
        // Classe utilitaire, ne doit pas être instanciée
    }

    // Vous pouvez ajouter une méthode pour changer le fichier de sauvegarde si nécessaire pour les tests
    public static void setFichier(String nouveauFichier) {
        FICHIER = nouveauFichier;
    }

    public static void sauvegarderEvenements(List<Evenement> evenements) {
        try {
            ObjectWriter writer = mapper.writerFor(mapper.getTypeFactory().constructCollectionType(List.class, Evenement.class));
            writer.writeValue(new File(FICHIER), evenements);
            System.out.println("✅ Événements sauvegardés dans " + FICHIER);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la sauvegarde des événements : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Evenement> chargerEvenements() {
        File file = new File(FICHIER);
        if (!file.exists() || file.length() == 0) { // Vérifie si le fichier existe et n'est pas vide
            System.out.println("ℹ️ Fichier de sauvegarde non trouvé ou vide. Retourne une liste d'événements vide.");
            return new ArrayList<>(); // Retourne une liste vide au lieu de null
        }
        try {
            ObjectReader reader = mapper.readerFor(mapper.getTypeFactory().constructCollectionType(List.class, Evenement.class));
            return reader.readValue(file);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des événements : " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur de chargement
        }
    }
}