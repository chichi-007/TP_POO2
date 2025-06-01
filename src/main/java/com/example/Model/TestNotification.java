
package com.example.Model;

import com.example.Service.GestionEvenements;
import com.example.Service.NotificationServiceConsole;
import com.example.exception.CapaciteMaxAtteinteException;
import com.example.exception.EvenementDejaExistantException;

import java.time.LocalDateTime;

public class TestNotification {

        // Créer un service de notification (par console ici)
        public static void main(String[] args) {
            NotificationServiceConsole notificationService = new NotificationServiceConsole();
            ParticipantObserver p1 = new ParticipantObserver("p1", "Alice", "alice@email.com", notificationService);
            ParticipantObserver p2 = new ParticipantObserver("p2", "Bob", "bob@email.com", notificationService);
            ParticipantObserver p3 = new ParticipantObserver("p3", "Charlie", "charlie@email.com", notificationService);

            EvenementObservable concert = new EvenementObservable(
                    "001", "Mega Concert", LocalDateTime.now(), "Douala", 2);

            // ➤ TEST CAPACITÉ
            try {
                concert.ajouterParticipant(p1);
                concert.ajouterParticipant(p2);
                concert.ajouterParticipant(p3); // ➤ doit déclencher l’exception
            } catch (CapaciteMaxAtteinteException e) {
                System.out.println(e.getMessage());
            }

            // ➤ TEST DOUBLON D’ÉVÉNEMENT
            GestionEvenements gestion = GestionEvenements.getInstance();
            try {
                gestion.ajouterEvenement(concert);
                gestion.ajouterEvenement(concert); // ➤ doit déclencher l’exception
            } catch (EvenementDejaExistantException e) {
                System.out.println(e.getMessage());
            }
        }
}
