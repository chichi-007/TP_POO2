package com.example.Service;


// Service/EmailNotificationService.java

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class EmailNotificationService implements NotificationService {

    private final String username; // Votre adresse email d'envoi
    private final String password; // Votre mot de passe d'application Gmail

    public EmailNotificationService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void envoyerNotification(String message) {
        // Pour les notifications génériques sans cible email spécifiée,
        // vous pouvez décider de ne rien faire, de loguer, ou d'envoyer
        // à une adresse par défaut (ex: un administrateur).
        // Pour l'instant, nous pouvons juste loguer.
        System.out.println("[EMAIL NOTIFICATION - GÉNÉRIQUE] : Tentative d'envoi d'email générique: " + message);
        // Si vous voulez envoyer à une adresse par défaut:
        // envoyerNotification("admin@example.com", "Notification Générique", message);
    }

    @Override
    public void envoyerNotification(String toEmail, String subject, String messageText) {
        CompletableFuture.runAsync(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(messageText);

                Transport.send(message);

                System.out.println("Email envoyé AVEC SUCCÈS à " + toEmail + " pour le sujet: " + subject);

            } catch (MessagingException e) {
                System.err.println("ERREUR lors de l'envoi de l'email à " + toEmail + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}