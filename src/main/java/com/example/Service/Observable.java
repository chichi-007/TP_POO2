package com.example.Service;


public interface Observable {
    void ajouterObservateur(Observer o);
    void supprimerObservateur(Observer o);
    void notifierObservateurs(String message);
}
