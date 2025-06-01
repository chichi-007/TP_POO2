// src/main/java/com/yourcompany/yourapp/controller/MainController.java
package com.example.Controller;

import com.example.Model.*;
import com.example.Service.NotificationService;
import com.example.exception.CapaciteMaxAtteinteException;
import com.example.exception.EvenementDejaExistantException;
import com.example.Service.EmailNotificationService; // Notez l'importation
import com.example.Service.GestionEvenements;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID; // Pour générer des IDs uniques

public class MainController {

    // Services
    private GestionEvenements gestionEvenements;
    private NotificationService notificationService; // Interface

    // FXML elements for Event Management Tab
    @FXML private ComboBox<String> eventTypeComboBox;
    @FXML private TableView<Evenement> eventTable;
    @FXML private TableColumn<Evenement, String> colId;
    @FXML private TableColumn<Evenement, String> colNom;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, Integer> colCapacite;
    @FXML private TableColumn<Evenement, String> colType;
    @FXML private TableColumn<Evenement, Void> colAction; // Pour les boutons d'action

    // FXML elements for Event Details & Participants Tab
    @FXML private Label detailIdLabel;
    @FXML private Label detailNomLabel;
    @FXML private Label detailDateLabel;
    @FXML private Label detailLieuLabel;
    @FXML private Label detailCapaciteLabel;
    @FXML private Label detailTypeLabel;
    @FXML private Label detailSpecific1Label; // Thème/Artiste
    @FXML private Label detailSpecific2Label; // Intervenants/Genre musical

    @FXML private TableView<Participant> participantTable;
    @FXML private TableColumn<Participant, String> colParticipantId;
    @FXML private TableColumn<Participant, String> colParticipantNom;
    @FXML private TableColumn<Participant, String> colParticipantEmail;

    @FXML private TextField participantIdField;
    @FXML private TextField participantNomField;
    @FXML private TextField participantEmailField;
    @FXML private TextField newLocationField;


    private ObservableList<Evenement> observableEvents;
    private ObservableList<Participant> observableParticipants;


    private  Evenement evenement;

    @FXML
    public void initialize() {
        gestionEvenements = GestionEvenements.getInstance();
        // Initialiser le service de notification avec vos informations de compte Gmail
        // REMPLACEZ CES VALEURS PAR VOS VRAIES INFOS GMAIL (Email et Mot de Passe d'Application)
        notificationService = new EmailNotificationService("votre_email@gmail.com", "votre_mot_de_passe_application_gmail");

        observableEvents = FXCollections.observableArrayList();
        eventTable.setItems(observableEvents);

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        colLieu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieu()));

        colType.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Conference) {
                return new SimpleStringProperty("Conférence");
            } else if (cellData.getValue() instanceof Concert) {
                return new SimpleStringProperty("Concert");
            }
            return new SimpleStringProperty("Inconnu");
        });

        // Cell Factory pour la colonne Action (bouton de suppression)
        Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Evenement, Void> call(final TableColumn<Evenement, Void> param) {
                final TableCell<Evenement, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Supprimer");

                    {
                        btn.setOnAction(event -> {
                            Evenement eventToDelete = getTableView().getItems().get(getIndex());
                            gestionEvenements.supprimerEvenement(eventToDelete.getId());
                            loadEvents(); // Recharger la liste après suppression
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé", "L'événement '" + eventToDelete.getNom() + "' a été supprimé.");
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colAction.setCellFactory(cellFactory);


        // Listen to event table selection changes
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayEventDetails(newSelection);
            } else {
                clearEventDetails();
            }
        });

        // Initialize participant table
        observableParticipants = FXCollections.observableArrayList();
        participantTable.setItems(observableParticipants);
        colParticipantId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colParticipantNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colParticipantEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        loadEvents(); // Charger les événements au démarrage
    }

    private void displayEventDetails(Evenement event) {
        detailIdLabel.setText(event.getId());
        detailNomLabel.setText(event.getNom());
        detailDateLabel.setText(event.getDate().toString());
        detailLieuLabel.setText(event.getLieu());
        detailCapaciteLabel.setText(String.valueOf(event.getCapaciteMax()));

        if (event instanceof Conference) {
            Conference conference = (Conference) event;
            detailTypeLabel.setText("Conférence");
            detailSpecific1Label.setText("Thème: " + conference.getTheme());
            detailSpecific2Label.setText("Intervenants: " + conference.getIntervenants().size());
        } else if (event instanceof Concert) {
            Concert concert = (Concert) event;
            detailTypeLabel.setText("Concert");
            detailSpecific1Label.setText("Artiste: " + concert.getArtiste());
            detailSpecific2Label.setText("Genre: " + concert.getGenreMusical());
        } else {
            detailTypeLabel.setText("Inconnu");
            detailSpecific1Label.setText("");
            detailSpecific2Label.setText("");
        }

        observableParticipants.setAll(event.getParticipants());
    }

    private void clearEventDetails() {
        detailIdLabel.setText("");
        detailNomLabel.setText("");
        detailDateLabel.setText("");
        detailLieuLabel.setText("");
        detailCapaciteLabel.setText("");
        detailTypeLabel.setText("");
        detailSpecific1Label.setText("");
        detailSpecific2Label.setText("");
        observableParticipants.clear();
    }

    @FXML
    private void handleCreateNewEvent() {
        String selectedType = eventTypeComboBox.getValue();
        if (selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélection requise", "Veuillez sélectionner un type d'événement.");
            return;
        }

        // Créer une boîte de dialogue pour la saisie des informations de l'événement
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Créer un nouvel événement");
        dialog.setHeaderText("Entrez les détails du nouvel événement.");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de l'événement");
        TextField dateField = new TextField();
        dateField.setPromptText("AAAA-MM-JJTHH:MM (ex: 2025-12-31T18:00)");
        TextField locationField = new TextField();
        locationField.setPromptText("Lieu");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacité maximale");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Date et Heure:"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Lieu:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Capacité Max:"), 0, 3);
        grid.add(capacityField, 1, 3);

        TextField specific1Field = new TextField(); // Thème ou Artiste
        TextField specific2Field = new TextField(); // Intervenants ou Genre musical

        if ("Conférence".equals(selectedType)) {
            specific1Field.setPromptText("Thème de la conférence");
            specific2Field.setPromptText("Liste des intervenants (séparés par ';')");
            grid.add(new Label("Thème:"), 0, 4);
            grid.add(specific1Field, 1, 4);
            grid.add(new Label("Intervenants:"), 0, 5);
            grid.add(specific2Field, 1, 5);
        } else if ("Concert".equals(selectedType)) {
            specific1Field.setPromptText("Nom de l'artiste");
            specific2Field.setPromptText("Genre musical");
            grid.add(new Label("Artiste:"), 0, 4);
            grid.add(specific1Field, 1, 4);
            grid.add(new Label("Genre Musical:"), 0, 5);
            grid.add(specific2Field, 1, 5);
        }

        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat en Evenement
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String id = UUID.randomUUID().toString(); // Générer un ID unique
                    String nom = nameField.getText();
                    LocalDateTime date = LocalDateTime.parse(dateField.getText());
                    String lieu = locationField.getText();
                    int capaciteMax = Integer.parseInt(capacityField.getText());
                    String intervenants=specific1Field.getText();

                    if ("Conférence".equals(selectedType)) {
                        String theme = specific1Field.getText();
                        // Pour les intervenants, on simplifie pour l'UI, mais la logique métier est plus complexe
                        Conference conference = new Conference(id, nom, date, lieu, capaciteMax,theme, Collections.singletonList(intervenants));
                        // Ajouter les intervenants (simulation simple)
                        if (!specific2Field.getText().isEmpty()) {
                            String[] intervenantNames = specific2Field.getText().split(";");
                            for (String iName : intervenantNames) {
                                conference.ajouterIntervenant(new Intervenant(nom, "Spécialité")); // Spécialité fictive
                            }
                        }
                        return conference;
                    } else if ("Concert".equals(selectedType)) {
                        String artist = specific1Field.getText();
                        String genre = specific2Field.getText();
                        return new Concert(id, nom, date, lieu, capaciteMax, artist, genre);
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Capacité invalide", "Veuillez entrer un nombre pour la capacité.");
                } catch (java.time.format.DateTimeParseException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Format de date/heure invalide", "Veuillez utiliser le format AAAA-MM-JJTHH:MM.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création", e.getMessage());
                }
            }
            return null;
        });

        Optional<Evenement> result = dialog.showAndWait();
        result.ifPresent(event -> {
            try {
                gestionEvenements.ajouterEvenement(event);
                loadEvents(); // Recharger la table après ajout
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement créé", "L'événement '" + event.getNom() + "' a été ajouté.");
            } catch (EvenementDejaExistantException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'ajout", e.getMessage());
            }
        });
    }

    @FXML
    private void handleInscrireParticipant() {
        Evenement selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélection requise", "Veuillez sélectionner un événement pour inscrire un participant.");
            return;
        }

        String id = participantIdField.getText();
        String nom = participantNomField.getText();
        String email = participantEmailField.getText();

        if (id.isEmpty() || nom.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Champs manquants", "Tous les champs du participant doivent être remplis.");
            return;
        }

        try {
            // Créer le participant en lui passant le service de notification
            Participant participant = new Participant(id, nom, email, notificationService);
            selectedEvent.ajouterParticipant(participant);
            displayEventDetails(selectedEvent); // Rafraîchir les détails de l'événement
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie", participant.getNom() + " a été inscrit à " + selectedEvent.getNom());
        } catch (CapaciteMaxAtteinteException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Capacité maximale atteinte", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'inscription", e.getMessage());
        }
    }

    @FXML
    private void handleAnnulerEvenement() {
        Evenement selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélection requise", "Veuillez sélectionner un événement à annuler.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation d'annulation");
        confirmAlert.setHeaderText("Annuler l'événement '" + selectedEvent.getNom() + "' ?");
        confirmAlert.setContentText("Tous les participants inscrits seront notifiés.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedEvent.annuler(); // Ceci déclenchera la notification via l'Observer
            gestionEvenements.supprimerEvenement(selectedEvent.getId()); // Supprime de la liste des événements
            loadEvents(); // Recharger la table
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement annulé", "L'événement '" + selectedEvent.getNom() + "' a été annulé et les participants notifiés.");
        }
    }

    @FXML
    private void handleModifierLieu() {
        Evenement selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélection requise", "Veuillez sélectionner un événement à modifier.");
            return;
        }

        String newLocation = newLocationField.getText();
        if (newLocation.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Champ vide", "Veuillez entrer le nouveau lieu.");
            return;
        }

        selectedEvent.modifierLieu(newLocation); // Déclenchera la notification via l'Observer
        // Pas besoin de recharger loadEvents() si seulement un attribut est modifié et non l'ID
        // Mais il faut rafraîchir l'affichage des détails
        displayEventDetails(selectedEvent);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Lieu modifié", "Le lieu de l'événement '" + selectedEvent.getNom() + "' a été mis à jour.");
    }


    @FXML
    private void saveEvents() {
        try {
            gestionEvenements.sauvegarderEvenementsJSON("events.json");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de sauvegarde", "Échec de la sauvegarde", "Impossible de sauvegarder les événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void loadEvents() {
        try {
            gestionEvenements.ajouterEvenementJSON(evenement,"events.json");
            observableEvents.setAll(gestionEvenements.getTousLesEvenements().values()); // Mettre à jour l'ObservableList
            eventTable.refresh(); // Rafraîchir le TableView
            clearEventDetails(); // Effacer les détails pour éviter d'afficher des infos périmées
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Chargement terminé", "Les événements ont été chargés depuis events.json.");
        } catch (IOException | EvenementDejaExistantException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Échec du chargement", "Impossible de charger les événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}