 Description

Ce projet est une application Java permettant de gérer des événements (concerts, conférences), d'inscrire des participants et de les notifier en cas d'annulation ou de modification. Il intègre JavaFX pour l'interface graphique, Jackson pour la sérialisation JSON, et Maven pour la gestion des dépendances.

 Technologies utilisées

Java 21

JavaFX 17 (FXML, SceneBuilder)

Maven 3.6+

Jackson (databind, jsr310)

JUnit 5 & Mockito
jackata
**
** Structure du projet

TP_POO2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Model/              # Classes métier (Evenement, Participant, etc.)
│   │   │   ├── Service/            # Interfaces & stratégies de notification
│   │   │   ├── Controller/         # Contrôleurs JavaFX
│   │   │   └── Utils/              # Classes utilitaires (JSON, fichiers...)
│   │   └── resources/
│   │       ├── fxml/               # Fichiers FXML des vues
│   │       └── data.json           # Fichier de persistance des événements
│   └── test/
│       └── java/                   # Tests unitaires
├── pom.xml                         # Fichier Maven
└── README.md

**Fonctionnalités principales**

Création d’événements (Concert, Conférence)

Inscription/désinscription de participants

Notification automatique des participants (via console ou simulation email)

Annulation ou modification du lieu d’un événement

Interface graphique moderne avec JavaFX

Sauvegarde automatique des données dans un fichier JSON

 Exécution

# Compilation et exécution
mvn clean javafx:run

Assurez-vous que JavaFX est bien configuré dans votre IDE ou en ligne de commande.

 Tests

mvn test

Les tests utilisent JUnit 5 et Mockito pour simuler les services de notification.

** Personnalisation**

Vous pouvez modifier les types d'événements (ajouter des sous-classes d’Evenement)

Ajouter des services de notification réels (envoi d’emails via SMTP, etc.)

Ajouter une base de données ou une API REST si nécessaire

 Fichier JSON (exemple)

{
  "type": "concert",
  "id": "E001",
  "nom": "Jazz Night",
  "date": "2025-07-01T20:00:00",
  "lieu": "Yaoundé",
  "capaciteMax": 50,
  "participants": [ { "id": "P001", "nom": "Alice", "email": "alice@mail.com" } ]
}

👨‍💻 Auteur

MOGOU LYBAWO ULRICH — Étudiant 
