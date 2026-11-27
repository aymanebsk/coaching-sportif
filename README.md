# Coaching Sportif Personnalisé

Application desktop de coaching sportif et nutritionnel développée en **Java 17**, **JavaFX (FXML / MVC)** et **MySQL (JDBC)**.

## Fonctionnalités
- **Authentification & Rôles** : connexion avec hachage **SHA-256** et redirection vers l'**Espace Coach** ou l'**Espace Sportif**.
- **Espace Coach** : création et suppression de programmes, ajout et suppression de séances, analyse de la progression/stagnation des sportifs, export CSV.
- **Espace Sportif** : consultation et filtrage des séances (`TOUTES`, `A_FAIRE`, `TERMINEE`), validation d'une séance (`TERMINEE`), suivi du poids et de la courbe d'évolution, suivi nutritionnel quotidien et export CSV.
- **Calculs métier** : IMC, objectif calorique journalier selon l'objectif (`PERTE_POIDS`, `PRISE_MASSE`, `MAINTIEN`), taux de complétion des séances et détection de stagnation.

## Prérequis
- **JDK 17+**
- **Maven 3.8+**
- **MySQL 8.x**

## Installation et lancement
1. Créer et initialiser la base de données MySQL (`coaching_sportif`) avec le script SQL du projet.
2. Vérifier la configuration de connexion (`URL`, `utilisateur`, `mot de passe`) dans `DatabaseConnection.java`.
3. Depuis le dossier du projet, lancer l'application :
   ```bash
   mvn javafx:run
