package com.example.Model;



import com.example.Service.Observable;
import com.example.Service.Observer;
import com.example.exception.CapaciteMaxAtteinteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementObservable extends Evenement implements Observable {

    private List<Observer> observateurs;

    public EvenementObservable(String id, String nom, LocalDateTime date, String lieu, int capaciteMax) {
        super(id, nom, date, lieu, capaciteMax);
        this.observateurs = new ArrayList<>();
    }

    @Override


    public void ajouterParticipant(Participant p) throws CapaciteMaxAtteinteException {
        super.ajouterParticipant(p); // Cette méthode lève déjà l’exception
        if (p instanceof Observer) {
            ajouterObservateur((Observer) p);
        }
    }

    @Override
    public void annuler() {
        notifierObservateurs("⚠️ L’événement '" + nom + "' a été annulé.");
    }

    @Override
    public void ajouterObservateur(Observer o) {
        observateurs.add(o);
    }

    @Override
    public void supprimerObservateur(Observer o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateurs(String message) {
        for (Observer o : observateurs) {
            o.notifier(message);
        }
    }

    @Override
    public void afficherDetails() {
        System.out.println("Événement : " + nom + " | Lieu : " + lieu + " | Capacité : " + capaciteMax);
    }
}

