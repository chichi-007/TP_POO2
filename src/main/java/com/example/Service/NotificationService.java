package com.example.Service;





public interface NotificationService {
    // Méthode générique pour une notification simple (ex: pour la console, ou un log)
    void envoyerNotification(String message);

    // Méthode spécifique pour les notifications par email (avec destinataire, sujet et corps du message)
    void envoyerNotification(String toEmail, String subject, String messageText);
}
