// src/test/java/Model/EvenementTest.java
package Model; // Assurez-vous que le package correspond à celui de votre classe Evenement

import com.example.Model.Conference;
import com.example.Model.Evenement;
import com.example.Model.Participant;
import com.example.exception.CapaciteMaxAtteinteException; // Assurez-vous du package correct de votre exception
import com.example.Service.NotificationService; // Nous allons "simuler" ce service pour le test
import com.example.Service.NotificationServiceConsole; // Peut-être utiliser ceci pour les tests

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime; // Pour la date de l'événement
import java.util.UUID; // Si vous utilisez UUID pour les IDs

public class EvenementTest {

    private Evenement evenement;
    private NotificationService mockNotificationService; // Un "mock" ou une simple console pour les tests

    @BeforeEach // Cette méthode s'exécute avant chaque test
    void setUp() {
        // Initialiser un service de notification de console pour les tests.
        // On ne veut pas envoyer de vrais emails lors des tests unitaires !
        mockNotificationService = new NotificationServiceConsole();

        // Crée un événement de type Conference pour les tests
        // S'adapte à votre constructeur de Conference/Evenement
        evenement = new Conference(
                UUID.randomUUID().toString(), // id
                "Conférence Agile",         // nom
                LocalDateTime.now().plusDays(7), // date (dans le futur)
                "Centre de Conférences",    // lieu
                2,                          // capaciteMax (petite capacité pour tester facilement)
                "Méthodes Agiles",          // theme spécifique à Conference
                null                        // intervenants (peut être null pour ce test)
        );
    }

    @Test
    @DisplayName("Devrait ajouter un participant si la capacité n'est pas atteinte")
    void devraitAjouterParticipant() {
        Participant participant1 = new Participant("P001", "Alice", "alice@example.com", mockNotificationService);
        Participant participant2 = new Participant("P002", "Bob", "bob@example.com", mockNotificationService);

        try {
            evenement.ajouterParticipant(participant1);
            assertEquals(1, evenement.getParticipants().size(), "Le nombre de participants devrait être 1.");
            assertTrue(evenement.getParticipants().contains(participant1), "Alice devrait être inscrite.");

            evenement.ajouterParticipant(participant2);
            assertEquals(2, evenement.getParticipants().size(), "Le nombre de participants devrait être 2.");
            assertTrue(evenement.getParticipants().contains(participant2), "Bob devrait être inscrit.");

        } catch (CapaciteMaxAtteinteException e) {
            fail("Ne devrait pas lancer CapaciteMaxAtteinteException ici : " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Ne devrait pas ajouter un participant si la capacité maximale est atteinte")
    void neDevraitPasAjouterParticipantSiCapaciteMaxAtteinte() {
        Participant participant1 = new Participant("P001", "Alice", "alice@example.com", mockNotificationService);
        Participant participant2 = new Participant("P002", "Bob", "bob@example.com", mockNotificationService);
        Participant participant3 = new Participant("P003", "Charlie", "charlie@example.com", mockNotificationService);

        try {
            evenement.ajouterParticipant(participant1); // Capacité: 1/2
            evenement.ajouterParticipant(participant2); // Capacité: 2/2

            // Cette inscription devrait échouer
            assertThrows(CapaciteMaxAtteinteException.class, () -> {
                evenement.ajouterParticipant(participant3);
            }, "Devrait lancer CapaciteMaxAtteinteException lorsque la capacité est atteinte.");

            assertEquals(2, evenement.getParticipants().size(), "Le nombre de participants devrait rester à 2.");

        } catch (CapaciteMaxAtteinteException e) {
            // C'est attendu pour participant3, mais le fail() ne serait pas appelé si assertThrows est utilisé correctement
        }
    }

    @Test
    @DisplayName("Devrait désinscrire un participant existant")
    void devraitDesinscrireParticipantExistant() {
        Participant participant = new Participant("P001", "Alice", "alice@example.com", mockNotificationService);
        try {
            evenement.ajouterParticipant(participant);
            assertTrue(evenement.getParticipants().contains(participant), "Alice devrait être inscrite initialement.");

            evenement.desinscrireParticipant(participant); // Supposons que vous ayez cette méthode
            assertFalse(evenement.getParticipants().contains(participant), "Alice ne devrait plus être inscrite.");
            assertEquals(0, evenement.getParticipants().size(), "Le nombre de participants devrait être 0.");

        } catch (CapaciteMaxAtteinteException e) {
            fail("Ne devrait pas lancer d'exception ici : " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Ne devrait pas désinscrire un participant non existant")
    void neDevraitPasDesinscrireParticipantNonExistant() {
        Participant participantExistant = new Participant("P001", "Alice", "alice@example.com", mockNotificationService);
        Participant participantNonExistant = new Participant("P002", "Bob", "bob@example.com", mockNotificationService);

        try {
            evenement.ajouterParticipant(participantExistant);
            assertEquals(1, evenement.getParticipants().size(), "Le nombre de participants devrait être 1.");

            evenement.desinscrireParticipant(participantNonExistant); // Supposons que vous ayez cette méthode
            assertEquals(1, evenement.getParticipants().size(), "Le nombre de participants devrait rester à 1.");
            assertTrue(evenement.getParticipants().contains(participantExistant), "Alice devrait toujours être là.");

        } catch (CapaciteMaxAtteinteException e) {
            fail("Ne devrait pas lancer d'exception ici : " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Un participant ne devrait pas pouvoir s'inscrire plusieurs fois")
    void neDevraitPasPermettreInscriptionMultipleDuMemeParticipant() {
        Participant participant = new Participant("P001", "Alice", "alice@example.com", mockNotificationService);

        try {
            evenement.ajouterParticipant(participant);
            assertEquals(1, evenement.getParticipants().size(), "Le participant devrait être ajouté une fois.");

            // Tenter d'ajouter le même participant à nouveau
            evenement.ajouterParticipant(participant); // Votre logique ajouterParticipant devrait gérer cela
            assertEquals(1, evenement.getParticipants().size(), "Le participant ne devrait pas être ajouté deux fois.");

        } catch (CapaciteMaxAtteinteException e) {
            fail("Ne devrait pas lancer d'exception ici : " + e.getMessage());
        }
    }
}