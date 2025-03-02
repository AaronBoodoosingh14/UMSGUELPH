package com.ums.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the login screen.
 * Uses hardcoded credentials and redirects users to the default dashboard.
 */
public class LoginController {

    @FXML
    private TextField txtUsername, txtPassword; // Username and password fields

    @FXML
    private Button btnLogin; // Login button

    // Hardcoded user database (Username -> Password)
    private final Map<String, String> userDatabase = new HashMap<>();

    /**
     * Initializes the login system with hardcoded credentials and key listeners.
     */
    @FXML
    public void initialize() {
        // Predefined users (temporary)
        userDatabase.put("admin", "admin123");
        userDatabase.put("student1", "student123");
        userDatabase.put("faculty1", "faculty123");

        // Handle login button click
        btnLogin.setOnAction(e -> handleLogin());

        // Enable pressing "Enter" to log in
        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                txtPassword.requestFocus(); // Move to password field when Enter is pressed
            }
        });

        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin(); // Attempt login when Enter is pressed in password field
            }
        });
    }

    /**
     * Handles the login attempt by validating credentials.
     */
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
            // Redirect to the default dashboard
            loadDashboard("/com/ums/Dashboard.fxml");
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    /**
     * Loads the default dashboard after login.
     * @param fxmlPath The path to the Dashboard.fxml file.
     */
    private void loadDashboard(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage window = (Stage) btnLogin.getScene().getWindow();
            window.setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard.");
        }
    }

    /**
     * Displays an alert message.
     * @param title Title of the alert.
     * @param message Message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
