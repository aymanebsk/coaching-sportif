package com.coaching.controller;

import com.coaching.dao.ProgrammeDAO;
import com.coaching.dao.SeanceDAO;
import com.coaching.dao.SuiviProgressionDAO;
import com.coaching.dao.UtilisateurDAO;
import com.coaching.model.Programme;
import com.coaching.model.Seance;
import com.coaching.model.SuiviProgression;
import com.coaching.model.Utilisateur;
import com.coaching.service.CoachingService;
import com.coaching.service.ExportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class CoachDashboardController {

    @FXML private Label lblBienvenue;
    @FXML private Label lblStatSportif;

    @FXML private ComboBox<Utilisateur> cmbSportifs;
    @FXML private TextField txtProgTitre;
    @FXML private TextField txtProgDesc;
    @FXML private ComboBox<String> cmbProgNiveau;
    @FXML private TextField txtProgDuree;

    @FXML private TableView<Programme> tableProgrammes;
    @FXML private TableColumn<Programme, String> colProgTitre;
    @FXML private TableColumn<Programme, String> colProgNiveau;
    @FXML private TableColumn<Programme, Integer> colProgDuree;
    @FXML private TableColumn<Programme, String> colProgDesc;

    @FXML private TextField txtSeanceTitre;
    @FXML private ComboBox<String> cmbSeanceJour;
    @FXML private TextField txtSeanceDuree;
    @FXML private TextField txtSeanceCalories;

    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colSeanceTitre;
    @FXML private TableColumn<Seance, String> colSeanceJour;
    @FXML private TableColumn<Seance, Integer> colSeanceDuree;
    @FXML private TableColumn<Seance, Integer> colSeanceCal;
    @FXML private TableColumn<Seance, String> colSeanceStatut;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProgrammeDAO programmeDAO = new ProgrammeDAO();
    private final SeanceDAO seanceDAO = new SeanceDAO();
    private final SuiviProgressionDAO progressionDAO = new SuiviProgressionDAO();
    private final CoachingService coachingService = new CoachingService();
    private final ExportService exportService = new ExportService();

    private Utilisateur coachConnecte;

    @FXML
    public void initialize() {
        colProgTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colProgNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colProgDuree.setCellValueFactory(new PropertyValueFactory<>("dureeSemaines"));
        colProgDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        colSeanceTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colSeanceJour.setCellValueFactory(new PropertyValueFactory<>("jourSemaine"));
        colSeanceDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));
        colSeanceCal.setCellValueFactory(new PropertyValueFactory<>("caloriesEstimees"));
        colSeanceStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        cmbProgNiveau.setItems(FXCollections.observableArrayList("DEBUTANT", "INTERMEDIAIRE", "AVANCE"));
        cmbProgNiveau.setValue("DEBUTANT");

        cmbSeanceJour.setItems(FXCollections.observableArrayList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"));
        cmbSeanceJour.setValue("Lundi");

        tableProgrammes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                chargerSeancesDuProgramme(newSel.getId());
            }
        });
    }

    public void initData(Utilisateur coach) {
        this.coachConnecte = coach;
        lblBienvenue.setText("Espace Coach — " + coach.getNom());
        try {
            List<Utilisateur> sportifs = utilisateurDAO.findAllSportifs();
            cmbSportifs.setItems(FXCollections.observableArrayList(sportifs));
            if (!sportifs.isEmpty()) {
                cmbSportifs.setValue(sportifs.get(0));
            }
            chargerProgrammes();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void chargerProgrammes() {
        try {
            List<Programme> programmes = programmeDAO.findByCoachId(coachConnecte.getId());
            tableProgrammes.getItems().setAll(programmes);
            if (!programmes.isEmpty()) {
                tableProgrammes.getSelectionModel().select(0);
            } else {
                tableSeances.getItems().clear();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void chargerSeancesDuProgramme(int programmeId) {
        try {
            tableSeances.getItems().setAll(seanceDAO.findByProgrammeId(programmeId));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleCreerProgramme() {
        Utilisateur sportif = cmbSportifs.getValue();
        if (sportif == null || txtProgTitre.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Sélectionne un sportif et saisis un titre de programme.");
            return;
        }
        try {
            int duree = Integer.parseInt(txtProgDuree.getText().trim());
            Programme p = new Programme();
            p.setTitre(txtProgTitre.getText().trim());
            p.setDescription(txtProgDesc.getText().trim());
            p.setNiveau(cmbProgNiveau.getValue());
            p.setDureeSemaines(duree);
            p.setCoachId(coachConnecte.getId());
            p.setSportifId(sportif.getId());

            programmeDAO.create(p);
            txtProgTitre.clear();
            txtProgDesc.clear();
            txtProgDuree.clear();
            chargerProgrammes();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format invalide", "La durée doit être un entier (en semaines).");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerProgramme() {
        Programme selected = tableProgrammes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Sélectionne un programme à supprimer.");
            return;
        }
        try {
            programmeDAO.delete(selected.getId());
            chargerProgrammes();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAjouterSeance() {
        Programme prog = tableProgrammes.getSelectionModel().getSelectedItem();
        if (prog == null || txtSeanceTitre.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Sélectionne un programme et saisis le titre de la séance.");
            return;
        }
        try {
            Seance s = new Seance();
            s.setProgrammeId(prog.getId());
            s.setTitre(txtSeanceTitre.getText().trim());
            s.setJourSemaine(cmbSeanceJour.getValue());
            s.setDureeMinutes(Integer.parseInt(txtSeanceDuree.getText().trim()));
            s.setCaloriesEstimees(Integer.parseInt(txtSeanceCalories.getText().trim()));
            s.setStatut("A_FAIRE");

            seanceDAO.create(s);
            txtSeanceTitre.clear();
            txtSeanceDuree.clear();
            txtSeanceCalories.clear();
            chargerSeancesDuProgramme(prog.getId());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format invalide", "Durée et calories doivent être des entiers.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerSeance() {
        Seance s = tableSeances.getSelectionModel().getSelectedItem();
        Programme prog = tableProgrammes.getSelectionModel().getSelectedItem();
        if (s == null || prog == null) return;
        try {
            seanceDAO.delete(s.getId());
            chargerSeancesDuProgramme(prog.getId());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAnalyserSportif() {
        Utilisateur sportif = cmbSportifs.getValue();
        if (sportif == null) return;
        try {
            List<SuiviProgression> mesures = progressionDAO.findBySportifId(sportif.getId());
            List<Seance> seances = seanceDAO.findBySportifId(sportif.getId());
            double taux = coachingService.calculerTauxCompletion(seances);
            boolean stagnation = coachingService.detecterStagnation(mesures, sportif.getObjectif());

            String dernierPoids = mesures.isEmpty() ? "N/A" : mesures.get(mesures.size() - 1).getPoidsKg() + " kg";
            String alerte = stagnation ? " | ALERTE STAGNATION !" : " | Progression OK";
            lblStatSportif.setText(sportif.getNom() + " : Poids = " + dernierPoids + " | Complétion = " + taux + "%" + alerte);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleExportSportifCSV() {
        Utilisateur sportif = cmbSportifs.getValue();
        if (sportif == null) return;
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Exporter le bilan du sportif");
            chooser.setInitialFileName("bilan_" + sportif.getNom().replace(" ", "_") + ".csv");
            File file = chooser.showSaveDialog(lblBienvenue.getScene().getWindow());
            if (file != null) {
                exportService.exporterProgressionCSV(file, progressionDAO.findBySportifId(sportif.getId()));
                showAlert(Alert.AlertType.INFORMATION, "Export", "Bilan CSV exporté.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleDeconnexion() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) lblBienvenue.getScene().getWindow();
            stage.setScene(new Scene(root, 420, 320));
            stage.setTitle("Coaching Sportif Personnalisé - Connexion");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}