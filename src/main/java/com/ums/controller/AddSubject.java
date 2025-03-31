package com.ums.controller;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

/**
 * Controller for adding a new Subject.
 * Launched from the SubjectController as a popup window.
 */
public class AddSubject {

    @FXML private TextField txtSubjectCode;
    @FXML private TextField txtSubjectName;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> confirmAndSaveSubject());
        btnCancel.setOnAction(e -> confirmCancel());
    }

    /** Prompts user to confirm and saves subject if confirmed. */
    private void confirmAndSaveSubject() {
        String code = txtSubjectCode.getText().trim();
        String name = txtSubjectName.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter both subject code and name.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Add");
        confirm.setHeaderText("Add Subject?");
        confirm.setContentText("Code: " + code + "\nName: " + name + "\nThis action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveSubject(code, name);
        }
    }

    /** Executes SQL to insert subject into database. */
    private void saveSubject(String code, String name) {
        String sql = "INSERT INTO subjects (Code, Name) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Subject added successfully!");
            closeWindow();

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not add subject.");
        }
    }

    /** Asks for confirmation before canceling the add operation. */
    private void confirmCancel() {
        Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cancelAlert.setTitle("Cancel Confirmation");
        cancelAlert.setHeaderText("Are you sure you want to cancel?");
        cancelAlert.setContentText("Any unsaved data will be lost.");

        Optional<ButtonType> result = cancelAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            closeWindow();
        }
    }

    /** Closes the popup window. */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /** Utility method for showing user-friendly alerts. */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
