package com.example.Controller;

// src/main/java/com/yourcompany/yourapp/MainApp.java


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/premierpage.fxml"));
        Parent root = loader.load(); // Charge le FXML

        primaryStage.setTitle("Système de Gestion d'Événements");
        primaryStage.setScene(new Scene(root, 900, 600)); // Définir la taille de la scène
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}