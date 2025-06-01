package com.example.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import pour @JsonIgnore
import com.example.exception.CapaciteMaxAtteinteException;
import com.example.Service.Observable;
import com.example.Service.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections; // Pour Collections.unmodifiableList
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Concert.class, name = "concert"),
        @JsonSubTypes.Type(value = Conference.class, name = "conference")
})
public abstract class Evenement implements Observable {
    protected String id;
    protected String nom;
    protected LocalDateTime date;
    protected String lieu;
    protected int capaciteMax;
    protected List<Participant> participants;
    // transient indique à Jackson de ne pas sérialiser/désérialiser ce champ
    private transient List<Observer> observateurs;

    // Constructeur par défaut (ESSENTIEL pour Jackson)
    public Evenement() {
        this.participants = new ArrayList<>();
        this.observateurs = new ArrayList<>();
    }

    // Constructeur principal
    public Evenement(String id, String nom, LocalDateTime date, String lieu, int capaciteMax) {
        this(); // Appelle le constructeur par défaut pour initialiser les listes
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
    }

    // --- Implémentation des méthodes de l'interface Observable ---
    @Override
    public void ajouterObservateur(Observer o) {
        if (this.observateurs == null) { // S'assurer que la liste est initialisée après désérialisation
            this.observateurs = new ArrayList<>();
        }
        if (!observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    @Override
    public void supprimerObservateur(Observer o) {
        if (observateurs != null) {
            observateurs.remove(o);
        }
    }

    @Override
    public void notifierObservateurs(String message) {
        if (observateurs != null) {
            // Crée une copie pour éviter ConcurrentModificationException
            List<Observer> observateursACopier = new ArrayList<>(observateurs);
            for (Observer o : observateursACopier) {
                o.notifier(message);
            }
        }
    }

    // --- Gestion des participants ---
    public void ajouterParticipant(Participant participant) throws CapaciteMaxAtteinteException {
        if (this.participants.size() >= this.capaciteMax) {
            throw new CapaciteMaxAtteinteException("La capacité maximale de l'événement '" + this.nom + "' est atteinte.");
        }

        if (!this.participants.contains(participant)) { // Utilise equals() de Participant
            this.participants.add(participant);
            ajouterObservateur(participant); // Les participants sont aussi des observateurs
            System.out.println(participant.getNom() + " s'est inscrit à l'événement " + this.nom);
        } else {
            System.out.println(participant.getNom() + " est déjà inscrit à l'événement " + this.nom);
        }
    }

    public boolean desinscrireParticipant(Participant participant) {
        if (this.participants.remove(participant)) { // Utilise equals() de Participant
            supprimerObservateur(participant);
            System.out.println(participant.getNom() + " s'est désinscrit de l'événement " + this.nom);
            return true;
        } else {
            System.out.println(participant.getNom() + " n'était pas inscrit à l'événement " + this.nom);
            return false;
        }
    }

    // --- Méthodes de l'événement ---
    public void annuler() {
        System.out.println("L'événement " + this.nom + " (ID: " + this.id + ") a été annulé.");
        notifierObservateurs("⚠️ L’événement '" + nom + "' a été annulé.");
    }

    public void modifierLieu(String nouveauLieu) {
        String ancienLieu = this.lieu;
        this.lieu = nouveauLieu;
        System.out.println("Lieu de l'événement " + this.nom + " modifié de " + ancienLieu + " à " + nouveauLieu + ".");
        notifierObservateurs("L'événement '" + this.nom + "' a été modifié. Nouveau lieu: " + nouveauLieu + ".");
    }

    // Méthode abstraite
    public abstract void afficherDetails();

    // --- Getters et Setters (TOUS nécessaires pour Jackson et la bonne pratique) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }

    // Pour Jackson, la liste des participants doit être gérée
    public List<Participant> getParticipants() {
        // Retourne une copie non modifiable pour éviter les modifications externes inattendues
        return Collections.unmodifiableList(participants);
    }
    @JsonProperty("participants") // Indique à Jackson d'utiliser cette méthode pour la propriété "participants"
    public void setParticipants(List<Participant> participants) {
        // Défensive copy : toujours créer une nouvelle liste pour éviter les problèmes de référence
        this.participants = new ArrayList<>(participants);
        // Important : Les observateurs sont transient, donc ils ne sont pas sérialisés.
        // Si vous les réutilisez après désérialisation, vous devez les re-ajouter.
        if (this.observateurs == null) { // Initialiser si null après désérialisation
            this.observateurs = new ArrayList<>();
        } else {
            this.observateurs.clear(); // Effacer les anciens si existent
        }
        for (Participant p : participants) {
            ajouterObservateur(p); // Ré-ajoute chaque participant comme observateur
        }
    }

    // Le champ observateurs est transient, donc Jackson ne le sérialisera pas.
    // L'annotation @JsonIgnore est bonne pour être explicite.
    @JsonIgnore
    public List<Observer> getObservateurs() {
        return Collections.unmodifiableList(observateurs);
    }

    // toString est toujours utile pour le débogage
    @Override
    public String toString() {
        return "Evenement{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", lieu='" + lieu + '\'' +
                ", capaciteMax=" + capaciteMax +
                ", participants=" + participants.size() +
                '}';
    }
}