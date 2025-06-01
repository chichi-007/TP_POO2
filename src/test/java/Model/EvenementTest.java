package Model;

import com.example.Model.Concert;
import com.example.Model.Participant;
import com.example.Service.NotificationService;
import com.example.exception.CapaciteMaxAtteinteException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EvenementTest {

    @Test
    void testAjouterEtAnnulerEvenement() throws Exception {
        // Mock du service de notification
        NotificationService mockService = Mockito.mock(NotificationService.class);

        // Création d'un participant réel avec service simulé
        Participant p1 = new Participant("1", "Alice", "alice@email.com", mockService);

        // Création d’un événement concret
        Concert concert = new Concert("001", "Mega Show", LocalDateTime.now(), "Douala", 1, "Daphné", "Afrobeat");

        // Ajouter un participant
        concert.ajouterParticipant(p1);

        // Annuler l’événement (devrait notifier Alice)
        concert.annuler();

        // Vérification : la notification a été envoyée
        Mockito.verify(mockService).envoyerNotification("⚠️ L’événement 'Mega Show' a été annulé.");
    }

    @Test
    void testCapaciteMaxException() {
        Concert concert = new Concert("002", "Test Show", LocalDateTime.now(), "Yaoundé", 0, "Artiste", "Genre");

        Participant p = new Participant("1", "Bob", "bob@email.com");

        assertThrows(CapaciteMaxAtteinteException.class, () -> {
            concert.ajouterParticipant(p);
        });
    }
}
