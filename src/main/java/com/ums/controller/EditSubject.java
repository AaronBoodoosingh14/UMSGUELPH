package com.ums.controller;

import com.ums.data.Subject;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

/**
 * Controller for editing a Subject.
 * Opens in a popup window from SubjectController.
 */
public class EditSubject {

    @FXML private TextField txtSubjectCode;
    @FXML private TextField txtSubjectName;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private String originalCode;

    /** Loads the selected subject data into the form fields. */
    public void setSubjectData(Subject subject) {
        if (subject == null) return;

        txtSubjectCode.setText(subject.getSubjectCode());
        txtSubjectName.setText(subject.getSubjectName());
        originalCode = subject.getSubjectCode();
    }

    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> confirmAndSaveChanges());
        btnCancel.setOnAction(e -> confirmCancel());
    }

    /** Asks for confirmation before saving subject changes. */
    private void confirmAndSaveChanges() {
        String newCode = txtSubjectCode.getText().trim();
        String newName = txtSubjectName.getText().trim();

        if (newCode.isEmpty() || newName.isEmpty()) {
            showAlert("Validation Error", "Both Subject Code and Name are required.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Edit");
        confirm.setHeaderText("Are you sure you want to save changes?");
        confirm.setContentText("Changes to subject '" + originalCode + "' will be permanent.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateSubject(newCode, newName);
        }
    }

    /** Executes SQL update to save the subject changes. */
    private void updateSubject(String newCode, String newName) {
        String sql = "UPDATE subjects SET Code = ?, Name = ? WHERE Code = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newCode);
            stmt.setString(2, newName);
            stmt.setString(3, originalCode);
            stmt.executeUpdate();

            showAlert("Success", "Subject updated successfully.", Alert.AlertType.INFORMATION);
            closeWindow();

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            showAlert("Error", "Failed to update subject.", Alert.AlertType.ERROR);
        }
    }

    /** Confirms whether the user really wants to cancel editing. */
    private void confirmCancel() {
        Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cancelAlert.setTitle("Cancel Confirmation");
        cancelAlert.setHeaderText("Are you sure you want to cancel editing?");
        cancelAlert.setContentText("Any unsaved changes will be lost.");

        Optional<ButtonType> result = cancelAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            closeWindow();
        }
    }

    /** Closes the edit subject popup window. */
    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /** Utility method for showing alert dialogs. */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
