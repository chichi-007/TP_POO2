package com.example.Model;

import com.example.Service.EmailNotificationService;
import com.example.Service.NotificationServiceConsole;
import com.example.Service.Observer; // Importe l'interface Observer
import com.example.Service.NotificationService; // Importe l'interface NotificationService (pour les stratégies d'envoi)

import java.util.Objects;

public class Participant implements Observer { // <-- Participant implémente bien Observer
    protected String id;
    protected String nom;
    protected String email;
    // Marqué comme transient pour Jackson, car c'est un service et non une donnée à persister.
    private transient NotificationService notificationService;

    // Constructeur par défaut (sans arguments) : ESSENTIEL pour Jackson lors de la désérialisation
    public Participant() {}

    // Constructeur avec injection du service de notification (utilisé lors de la création initiale)
    public Participant(String id, String nom, String email, NotificationService notificationService) {
        this(id, nom, email); // Appelle le constructeur sans le service
        this.notificationService = notificationService;
    }

    // Constructeur pour initialiser les données du participant
    public Participant(String id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }



    // Setter pour le service de notification (utilisé pour "réhydrater" l'objet après désérialisation)
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // --- Implémentation de la méthode de l'interface Observer ---
    @Override // Cette annotation est maintenant valide si Service.Observer a 'notifier(String message)'
    public void notifier(String message) {
        // Cette ligne s'affiche toujours sur la console pour le débogage immédiat
        System.out.println("Notification pour " + this.nom + " (" + this.email + ") : " + message);

        if (notificationService != null) {
            // Logique pour envoyer la notification via la stratégie appropriée
            if (notificationService instanceof EmailNotificationService) {
                // Si le service est un EmailNotificationService, on appelle sa méthode spécifique pour l'email
                ((EmailNotificationService) notificationService).envoyerNotification(this.email, "Mise à jour de l'événement", message);
            } else if (notificationService instanceof NotificationServiceConsole) {
                // Si le service est un NotificationServiceConsole, on appelle sa méthode générique
                // (Assurez-vous que NotificationServiceConsole implémente bien envoyerNotification(String message))
                ((NotificationServiceConsole) notificationService).envoyerNotification(message); // <-- Correction ici
            } else {
                // Pour tout autre type de NotificationService non géré spécifiquement ci-dessus,
                // on utilise la méthode générique de l'interface NotificationService.
                notificationService.envoyerNotification(message); // <-- Correction ici
            }
        } else {
            System.out.println("Aucun service de notification configuré pour " + this.nom + ". La notification n'a pas été envoyée via un service externe.");
        }
    }

    // Getters et Setters (inchangés)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        // Si c'est la même instance, elles sont égales
        if (this == o) return true;
        // Si l'objet est null ou d'une classe différente, elles ne sont pas égales
        if (o == null || getClass() != o.getClass()) return false;
        // Caste l'objet en Participant
        Participant that = (Participant) o;
        // L'égalité est basée sur l'ID (qui doit être unique)
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // Le hashCode doit être cohérent avec equals.
        // Si equals est basé sur 'id', alors hashCode doit aussi être basé sur 'id'.
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "Participant{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}