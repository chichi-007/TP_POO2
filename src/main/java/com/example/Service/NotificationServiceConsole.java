package com.example.Service;


// Service/NotificationServiceConsole.java


public class NotificationServiceConsole implements NotificationService {

    @Override
    public void envoyerNotification(String message) {
        System.out.println("[CONSOLE NOTIFICATION] : " + message);
    }

    @Override // Cette méthode manquait ou n'était pas correctement implémentée !
    public void envoyerNotification(String toEmail, String subject, String messageText) {
        // Pour la console, nous allons simplement afficher les détails
        // qui auraient été envoyés par e-mail.
        System.out.println("[CONSOLE NOTIFICATION - DÉTAILS EMAIL SIMULÉS] :");
        System.out.println("  À: " + toEmail);
        System.out.println("  Sujet: " + subject);
        System.out.println("  Message: " + messageText);
    }
}