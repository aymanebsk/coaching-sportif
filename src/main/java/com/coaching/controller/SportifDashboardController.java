package com.coaching.controller;

import com.coaching.dao.SeanceDAO;
import com.coaching.dao.SuiviNutritionDAO;
import com.coaching.dao.SuiviProgressionDAO;
import com.coaching.exception.BusinessException;
import com.coaching.model.Seance;
import com.coaching.model.SuiviNutrition;
import com.coaching.model.SuiviProgression;
import com.coaching.model.Utilisateur;
import com.coaching.service.CoachingService;
import com.coaching.service.ExportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SportifDashboardController {

    @FXML private Label lblBienvenue;
    @FXML private Label lblImc;
    @FXML private Label lblCaloriesCible;
    @FXML private Label lblCaloriesAujourdhui;
    @FXML private Label lblCompletion;
    @FXML private Label lblAlerteStagnation;

    @FXML private ComboBox<String> cmbFiltreStatut;
    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colSeanceTitre;
    @FXML private TableColumn<Seance, String> colSeanceJour;
    @FXML private TableColumn<Seance, Integer> colSeanceDuree;
    @FXML private TableColumn<Seance, Integer> colSeanceCalories;
    @FXML private TableColumn<Seance, String> colSeanceStatut;

    @FXML private LineChart<String, Number> chartPoids;
    @FXML private TextField txtPoids;
    @FXML private TextField txtTourTaille;
    @FXML private TextField txtCommentaire;

    @FXML private ComboBox<String> cmbTypeRepas;
    @FXML private TextField txtRepasCalories;
    @FXML private TextField txtRepasProteines;
    @FXML private TableView<SuiviNutrition> tableNutrition;
    @FXML private TableColumn<SuiviNutrition, LocalDate> colRepasDate;
    @FXML private TableColumn<SuiviNutrition, String> colRepasType;
    @FXML private TableColumn<SuiviNutrition, Integer> colRepasCal;
    @FXML private TableColumn<SuiviNutrition, Double> colRepasProt;

    private final CoachingService coachingService = new CoachingService();
    private final ExportService exportService = new ExportService();
    private final SeanceDAO seanceDAO = new SeanceDAO();
    private final SuiviProgressionDAO progressionDAO = new SuiviProgressionDAO();
    private final SuiviNutritionDAO nutritionDAO = new SuiviNutritionDAO();

    private Utilisateur sportifConnecte;
    private List<Seance> toutesLesSeances;

    @FXML
    public void initialize() {
        colSeanceTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colSeanceJour.setCellValueFactory(new PropertyValueFactory<>("jourSemaine"));
        colSeanceDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));
        colSeanceCalories.setCellValueFactory(new PropertyValueFactory<>("caloriesEstimees"));
        colSeanceStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colRepasDate.setCellValueFactory(new PropertyValueFactory<>("dateRepas"));
        colRepasType.setCellValueFactory(new PropertyValueFactory<>("typeRepas"));
        colRepasCal.setCellValueFactory(new PropertyValueFactory<>("calories"));
        colRepasProt.setCellValueFactory(new PropertyValueFactory<>("proteinesG"));

        cmbFiltreStatut.setItems(FXCollections.observableArrayList("TOUTES", "A_FAIRE", "TERMINEE"));
        cmbFiltreStatut.setValue("TOUTES");

        cmbTypeRepas.setItems(FXCollections.observableArrayList("PETIT_DEJEUNER", "DEJEUNER", "DINER", "COLLATION"));
        cmbTypeRepas.setValue("DEJEUNER");
    }

    public void initData(Utilisateur sportif) {
        this.sportifConnecte = sportif;
        lblBienvenue.setText("Espace Sportif — " + sportif.getNom() + " (Objectif : " + sportif.getObjectif() + ")");
        rafraichirTout();
    }

    private void rafraichirTout() {
        try {
            List<SuiviProgression> mesures = progressionDAO.findBySportifId(sportifConnecte.getId());
            toutesLesSeances = seanceDAO.findBySportifId(sportifConnecte.getId());
            List<SuiviNutrition> repas = nutritionDAO.findBySportifId(sportifConnecte.getId());

            if (!mesures.isEmpty()) {
                double dernierPoids = mesures.get(mesures.size() - 1).getPoidsKg();
                double imc = coachingService.calculerIMC(dernierPoids, sportifConnecte.getTailleCm());
                int cibleCal = coachingService.calculerObjectifCalorique(sportifConnecte, dernierPoids);
                lblImc.setText(imc + " kg/m²");
                lblCaloriesCible.setText(cibleCal + " kcal");
            } else {
                lblImc.setText("N/A");
                lblCaloriesCible.setText("N/A");
            }

            int calJour = coachingService.calculerCaloriesJour(repas, LocalDate.now());
            lblCaloriesAujourdhui.setText(calJour + " kcal");

            double taux = coachingService.calculerTauxCompletion(toutesLesSeances);
            lblCompletion.setText(taux + " %");

            boolean stagnation = coachingService.detecterStagnation(mesures, sportifConnecte.getObjectif());
            lblAlerteStagnation.setVisible(stagnation);
            lblAlerteStagnation.setText(stagnation ? "Alerte : stagnation du poids sur 3 mesures consécutives." : "");

            appliquerFiltreSeances();
            tableNutrition.getItems().setAll(repas);
            remplirGraphique(mesures);

        } catch (BusinessException e) {
            showAlert(Alert.AlertType.WARNING, "Attention", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void appliquerFiltreSeances() {
        if (toutesLesSeances == null) return;
        String filtre = cmbFiltreStatut.getValue();
        if ("TOUTES".equals(filtre)) {
            tableSeances.getItems().setAll(toutesLesSeances);
        } else {
            tableSeances.getItems().setAll(
                    toutesLesSeances.stream().filter(s -> filtre.equals(s.getStatut())).collect(Collectors.toList())
            );
        }
    }

    @FXML
    private void handleValiderSeance() {
        Seance selection = tableSeances.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Sélectionne une séance dans le tableau.");
            return;
        }
        try {
            seanceDAO.updateStatut(selection.getId(), "TERMINEE");
            rafraichirTout();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAjouterMesure() {
        try {
            double poids = Double.parseDouble(txtPoids.getText().trim().replace(",", "."));
            double taille = txtTourTaille.getText().isBlank() ? 0.0 : Double.parseDouble(txtTourTaille.getText().trim().replace(",", "."));
            coachingService.calculerIMC(poids, sportifConnecte.getTailleCm());

            SuiviProgression sp = new SuiviProgression();
            sp.setSportifId(sportifConnecte.getId());
            sp.setDateMesure(LocalDate.now());
            sp.setPoidsKg(poids);
            sp.setTourTailleCm(taille);
            sp.setCommentaire(txtCommentaire.getText().trim());

            progressionDAO.create(sp);
            txtPoids.clear();
            txtTourTaille.clear();
            txtCommentaire.clear();
            rafraichirTout();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format invalide", "Saisis des nombres valides pour le poids et le tour de taille.");
        } catch (BusinessException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAjouterRepas() {
        try {
            int calories = Integer.parseInt(txtRepasCalories.getText().trim());
            double proteines = Double.parseDouble(txtRepasProteines.getText().trim().replace(",", "."));
            if (calories <= 0 || proteines < 0) {
                throw new BusinessException("Les calories doivent être positives.");
            }
            SuiviNutrition sn = new SuiviNutrition();
            sn.setSportifId(sportifConnecte.getId());
            sn.setDateRepas(LocalDate.now());
            sn.setTypeRepas(cmbTypeRepas.getValue());
            sn.setCalories(calories);
            sn.setProteinesG(proteines);

            nutritionDAO.create(sn);
            txtRepasCalories.clear();
            txtRepasProteines.clear();
            rafraichirTout();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format invalide", "Saisis des valeurs numériques valides.");
        } catch (BusinessException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleExportCSV() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Exporter ma progression (CSV)");
            chooser.setInitialFileName("progression_" + sportifConnecte.getNom().replace(" ", "_") + ".csv");
            File file = chooser.showSaveDialog(lblBienvenue.getScene().getWindow());
            if (file != null) {
                exportService.exporterProgressionCSV(file, progressionDAO.findBySportifId(sportifConnecte.getId()));
                showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Fichier CSV enregistré avec succès.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur export", e.getMessage());
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

    private void remplirGraphique(List<SuiviProgression> mesures) {
        chartPoids.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Poids (kg)");
        for (SuiviProgression m : mesures) {
            serie.getData().add(new XYChart.Data<>(m.getDateMesure().toString(), m.getPoidsKg()));
        }
        chartPoids.getData().add(serie);
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}