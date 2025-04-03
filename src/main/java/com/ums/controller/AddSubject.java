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

    /**
     * Initializes button actions when the FXML loads.
     * - btnSave triggers subject validation and insertion
     * - btnCancel confirms and closes the popup
     */
    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> confirmAndSaveSubject());
        btnCancel.setOnAction(e -> confirmCancel());
    }

    /**
     * Validates input fields, confirms with user, and saves subject if confirmed.
     */
    private void confirmAndSaveSubject() {
        String code = formatSubjectCode(txtSubjectCode.getText());
        String name = formatSubjectName(txtSubjectName.getText());

        if (!isValidInput(code, name)) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Add");
        confirm.setHeaderText("Add Subject?");
        confirm.setContentText("Code: " + code + "\nName: " + name + "\nThis action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveSubject(code, name);
        }
    }

    /**
     * Executes the SQL insert statement to save a new subject to the database.
     * Displays alerts for success or failure (including duplicates).
     */
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
            if (ex.getMessage().toLowerCase().contains("duplicate")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Error", "A subject with this code already exists.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving the subject.");
            }
        }
    }

    /**
     * Confirms cancellation with user before closing the window.
     * Protects against accidental dismissal with unsaved data.
     */
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

    /**
     * Closes the current popup window.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert with the given type, title, and message content.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Validates subject code and name fields.
     * Ensures both are non-empty and that code follows the format like "CS101".
     */
    private boolean isValidInput(String code, String name) {
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return false;
        }

        if (!code.matches("[A-Z]{2,10}\\d{1,4}")) {
            showAlert(Alert.AlertType.WARNING, "Format Error", "Subject code must be in the format: ABCD123");
            return false;
        }

        return true;
    }

    /**
     * Formats subject code: trims whitespace and converts to uppercase.
     */
    private String formatSubjectCode(String input) {
        return input == null ? "" : input.trim().toUpperCase();
    }

    /**
     * Capitalizes the first letter of each word in the subject name.
     * E.g., "computer science" becomes "Computer Science"
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
