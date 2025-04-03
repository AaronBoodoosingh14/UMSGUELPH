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

    /**
     * Populates the text fields with the selected subject's data.
     * @param subject The subject to be edited.
     */
    public void setSubjectData(Subject subject) {
        if (subject == null) return;

        txtSubjectCode.setText(subject.getSubjectCode());
        txtSubjectName.setText(subject.getSubjectName());
        originalCode = subject.getSubjectCode();
    }

    /**
     * Initializes the Save and Cancel button actions.
     */
    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> confirmAndSaveChanges());
        btnCancel.setOnAction(e -> confirmCancel());
    }

    /**
     * Confirms with the user before applying changes to the subject.
     * Validates input and proceeds to update if confirmed.
     */
    @FXML
    private void confirmAndSaveChanges() {
        String newCode = formatSubjectCode(txtSubjectCode.getText());
        String newName = formatSubjectName(txtSubjectName.getText());

        if (!isValidInput(newCode, newName)) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Edit");
        confirm.setHeaderText("Are you sure you want to save changes?");
        confirm.setContentText("Changes to subject '" + originalCode + "' will be permanent.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateSubject(newCode, newName);
        }
    }

    /**
     * Updates the subject record in the database using the new values.
     * Shows a success or error alert based on the outcome.
     */
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

    /**
     * Asks user if they want to cancel the operation and closes the window if confirmed.
     */
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

    /**
     * Closes the edit subject popup window.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert with the given title, message, and type.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Validates the subject code and name fields.
     * @return true if input is valid, false otherwise.
     */
    private boolean isValidInput(String code, String name) {
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            showAlert("Please fill in all fields.", "Input Error", Alert.AlertType.WARNING);
            return false;
        }

        if (!code.matches("[A-Z]{2,10}\\d{1,4}")) {
            showAlert("Format Error", "Subject code must be in the format: ABCD123", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    /**
     * Converts the subject code to uppercase and trims whitespace.
     */
    private String formatSubjectCode(String input) {
        return input == null ? "" : input.trim().toUpperCase();
    }

    /**
     * Formats the subject name to Title Case.
     */
    private String formatSubjectName(String input) {
        if (input == null || input.isBlank()) return "";
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1)).append(" ");
        }
        return formatted.toString().trim();
    }
}
