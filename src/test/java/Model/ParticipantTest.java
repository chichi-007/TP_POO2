package Model;

import com.example.Model.Participant;
import com.example.Service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ParticipantTest {

    @Test
    void testNotifieAvecServiceMock() {
        // 1. Créer un mock de l'interface NotificationService
        NotificationService mockService = Mockito.mock(NotificationService.class);

        // 2. Créer un vrai Participant avec le service injecté
        Participant participant = new Participant("1", "Alice", "alice@email.com", mockService);

        // 3. Appeler notifier
        String message = "Événement annulé";
        participant.notifier(message);

        // 4. Vérifier que la méthode a bien été appelée
        Mockito.verify(mockService).envoyerNotification(message);
    }
}
