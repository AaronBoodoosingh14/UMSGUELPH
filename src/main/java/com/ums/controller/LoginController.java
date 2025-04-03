package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;



public class LoginController{

    @FXML
    private TextField txtUsername, txtPassword;

    @FXML
    private Button btnLogin;



    /**
     * Initializes the login system by reading credentials from Excel.
     */
    public void initialize() {
        btnLogin.setOnAction(e -> {
            try {
                handleLogin();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("Database error occurred!");
            }
        });

        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) txtPassword.requestFocus();
        });

        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try {
                    handleLogin();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("Database error occurred!");
                }
            }
        });
    }



    private void handleLogin() throws SQLException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();


        try {
            String insert = "SELECT * FROM loginInfo WHERE username = ? AND password = ?";

            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insert);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");

                System.out.println(role);


                loadDashboard(role);  // Only call loadDashboard after successful login
            } else {
                showAlert("Username or password is incorrect!");
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Add this to see any SQL errors
            showAlert("Database error occurred: " + e.getMessage());
        }
    }


    /**
      Loads the dashboard and passes the user's role.
     */
    private void loadDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Dashboard.fxml"));
            Parent root = loader.load();


            DashboardController dashboardController = loader.getController();

            UMSApplication.setCurrentDashboardController(dashboardController);
            dashboardController.setDropDown(false);
            dashboardController.setUserRole(role);
            dashboardController.setPermUser(txtUsername.getText());
            if (UMSApplication.isDarkMode()) {
                dashboardController.updateDashboardLogo("/logo.png");
            } else {
                dashboardController.updateDashboardLogo("/uofgb.png");
            }
            UMSApplication.setLoggedInUsername(txtUsername.getText());
            UMSApplication.setLoggedInUserRole(role);
            String temp = dashboardController.getPermUser();
            System.out.println(temp);


            Scene currentScene = btnLogin.getScene();
            if (currentScene != null) {
                Stage window = (Stage) currentScene.getWindow();
                if (window != null) {
                    window.setScene(new Scene(root));
                    window.setMaximized(true);
                    window.show();
                } else {
                    System.err.println("Window is null");
                }
            } else {
                System.err.println("Scene is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load dashboard: " + e.getMessage());
        }
    }

    /**
     * Displays an alert message.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
