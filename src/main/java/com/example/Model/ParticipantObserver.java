package com.example.Model;



import com.example.Service.Observer;
import com.example.Service.NotificationService;

public class ParticipantObserver extends Participant implements Observer {
    private NotificationService notificationService;

    public ParticipantObserver(String id, String nom, String email, NotificationService notificationService) {
        super(id, nom, email);
        this.notificationService = notificationService;
    }

    @Override
    public void notifier(String message) {
        notificationService.envoyerNotification("À " + nom + " (" + email + ") : " + message );
    }
}

