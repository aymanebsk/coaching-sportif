package com.coaching.controller;

import com.coaching.exception.BusinessException;
import com.coaching.model.Utilisateur;
import com.coaching.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblErreur;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        lblErreur.setText("");
        try {
            Utilisateur user = authService.authenticate(txtEmail.getText(), txtPassword.getText());
            String fxmlPath = "COACH".equals(user.getRole())
                    ? "/fxml/coach_dashboard.fxml"
                    : "/fxml/sportif_dashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if ("COACH".equals(user.getRole())) {
                CoachDashboardController controller = loader.getController();
                controller.initData(user);
            } else {
                SportifDashboardController controller = loader.getController();
                controller.initData(user);
            }

            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root, 1020, 680));
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.setTitle("Coaching Sportif - " + user.getNom() + " (" + user.getRole() + ")");
        } catch (BusinessException e) {
            lblErreur.setText(e.getMessage());
        } catch (IOException e) {
            lblErreur.setText("Erreur interface : " + e.getMessage());
        }
    }
}