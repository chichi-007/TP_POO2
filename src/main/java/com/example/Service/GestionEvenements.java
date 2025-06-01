package com.example.Service;



import com.example.Model.Evenement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.exception.EvenementDejaExistantException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionEvenements {

    private static GestionEvenements instance;
    private Map<String, Evenement> evenements;
    private ObjectMapper objectMapper;
;

    private GestionEvenements() {
        evenements = new HashMap<>();
    }

    public static GestionEvenements getInstance() {
        if (instance == null) {
            instance = new GestionEvenements();
        }
        return instance;
    }

    public void ajouterEvenement(Evenement e) throws EvenementDejaExistantException {
        if (evenements.containsKey(e.getId())) {
            throw new EvenementDejaExistantException("⚠️ Événement déjà existant avec l’ID : " + e.getId());
        }
        evenements.put(e.getId(), e);

    }

    public void supprimerEvenement(String id) {
        evenements.remove(id);
    }

    public Evenement rechercherEvenement(String id) {
        return evenements.get(id);
    }

    public Map<String, Evenement> getTousLesEvenements() {
        return evenements;
    }

    public void sauvegarderEvenementsJSON(String cheminFichier) throws IOException {
        // Écrit la liste de tous les événements dans le fichier.
        // On utilise new ArrayList<>(evenements.values()) pour convertir la Map en une List.
        objectMapper.writeValue(new File(cheminFichier), new ArrayList<>(evenements.values()));
    }

    /**
     * Charge les événements depuis un fichier JSON et les ajoute à la liste actuelle.
     * Les événements existants avec le même ID seront écrasés ou ignorés selon votre logique ajouterEvenement.
     * @param cheminFichier Le chemin du fichier JSON à charger.
     * @throws IOException Si une erreur d'entrée/sortie se produit ou si le fichier est introuvable/invalide.
     */
    public void chargerEvenementsJSON(String cheminFichier) throws IOException {
        File file = new File(cheminFichier);
        if (!file.exists()) {
            System.out.println("Fichier de sauvegarde non trouvé. Initialisation sans événements préchargés.");
            this.evenements.clear(); // S'assurer que la liste est vide si le fichier n'existe pas
            return;
        }

        // Lit une liste d'objets Evenement (qui peuvent être Conference ou Concert grâce à enableDefaultTyping)
        List<Evenement> listeChargee = objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Evenement.class));

        // Vider la map actuelle et ajouter les événements chargés
        // Cette étape est importante pour ne pas avoir de doublons si vous chargez plusieurs fois
        this.evenements.clear();
        for (Evenement evenement : listeChargee) {
            try {
                // Utilisez la méthode ajouterEvenement existante pour gérer la logique des doublons
                // et s'assurer que les événements sont bien ajoutés à la map interne.
                ajouterEvenement(evenement);
            } catch (EvenementDejaExistantException e) {
                System.err.println("Erreur (doublon) lors de l'ajout de l'événement chargé : " + evenement.getNom() + " - " + e.getMessage());
            }
            // Note : Les objets 'NotificationService' ne sont pas sérialisés par défaut
            // et devront être réinjectés ou gérés séparément si vos 'Participant'
            // en ont besoin après un chargement.
        }
    }
    //--- NOUVELLE MÉTHODE : ajouterEvenementJSON (selon votre demande) ---
    /**
     * Ajoute un événement à la gestion et le sauvegarde immédiatement dans un fichier JSON.
     * Cette méthode combine ajouterEvenement et sauvegarderEvenementsJSON.
     * @param evenement L'événement à ajouter.
     * @param cheminFichier Le chemin du fichier JSON où l'événement doit être sauvegardé.
     * @throws EvenementDejaExistantException Si un événement avec le même ID existe déjà.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la sauvegarde.
     */
    public void ajouterEvenementJSON(Evenement evenement, String cheminFichier) throws EvenementDejaExistantException, IOException {
        // 1. Ajouter l'événement à la map interne (utilise la logique de vérification des doublons)
        ajouterEvenement(evenement);

        // 2. Sauvegarder la liste mise à jour des événements dans le fichier JSON
        sauvegarderEvenementsJSON(cheminFichier);
    }

    // Méthode pour obtenir une liste des événements (utile pour l'UI si vous n'avez pas de getter Map direct)
    // public ObservableList<Evenement> getObservableEvenements() {
    //     return FXCollections.observableArrayList(evenements.values());
    // }
}

