package com.ums.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the login screen.
 * Uses hardcoded credentials and redirects all users to the same Dashboard.
 * The role (Admin/Student) determines which module views are loaded.
 */
public class LoginController {

    @FXML
    private TextField txtUsername, txtPassword;

    @FXML
    private Button btnLogin;

    private final Map<String, String[]> userDatabase = new HashMap<>();

    @FXML
    public void initialize() {
        // Hardcoded users with roles
        userDatabase.put("admin", new String[]{"admin123", "Admin"});
        userDatabase.put("student1", new String[]{"student123", "Student"});
        userDatabase.put("student2", new String[]{"student456", "Student"});

        btnLogin.setOnAction(e -> handleLogin());

        // Enable Enter key login
        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) txtPassword.requestFocus();
        });

        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) handleLogin();
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (userDatabase.containsKey(username)) {
            String[] userInfo = userDatabase.get(username);
            String storedPassword = userInfo[0];
            String role = userInfo[1];

            if (password.equals(storedPassword)) {
                loadDashboard(role);  // ✅ Pass role to Dashboard
            } else {
                showAlert("Login Failed", "Incorrect password.");
            }
        } else {
            showAlert("Login Failed", "User not found.");
        }
    }

    private void loadDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Dashboard.fxml"));
            Parent root = loader.load();

            // Pass role to DashboardController
            DashboardController dashboardController = loader.getController();
            dashboardController.setUserRole(role);

            Stage window = (Stage) btnLogin.getScene().getWindow();
            window.setScene(new Scene(root));
            window.setMaximized(true);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
