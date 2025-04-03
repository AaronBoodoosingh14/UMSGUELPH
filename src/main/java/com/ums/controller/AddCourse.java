package com.ums.controller;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Controller class for handling the Add Course popup window.
 * This class manages user input validation, data insertion into the database, and UI actions like clearing or closing.
 */
public class AddCourse {

    // TextFields bound to the AddCourse.fxml file
    @FXML private TextField txtCourseCode;
    @FXML private TextField txtCourseName;
    @FXML private TextField txtSubjectCode;
    @FXML private TextField txtSection;
    @FXML private TextField txtCapacity;
    @FXML private TextField txtLectureTime;
    @FXML private TextField txtFinalExamDate;
    @FXML private TextField txtLocation;
    @FXML private TextField txtTeacherName;

    // Buttons for saving or canceling the operation
    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    /**
     * Initializes the AddCourse controller.
     * - Clears any preloaded data
     * - Binds button actions
     */
    @FXML
    public void initialize() {
        clearFields(); // Ensures form starts fresh

        // Closes the window when Cancel is clicked
        btnCancel.setOnAction(e -> closeWindow());

        // Validates and saves course details to the database when Save is clicked
        btnSave.setOnAction(e -> {
            if (!validateInputs()) return;

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO courses (CourseCode, CourseName, SubjectCode, SectionNumber, Capacity, LectureTime, FinalExamDate, Location, TeacherName) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                // Binding user inputs to SQL statement
                stmt.setInt(1, Integer.parseInt(txtCourseCode.getText().trim()));
                stmt.setString(2, txtCourseName.getText().trim());
                stmt.setString(3, txtSubjectCode.getText().trim());
                stmt.setString(4, txtSection.getText().trim());
                stmt.setInt(5, Integer.parseInt(txtCapacity.getText().trim()));
                stmt.setString(6, txtLectureTime.getText().trim());
                stmt.setString(7, txtFinalExamDate.getText().trim());
                stmt.setString(8, txtLocation.getText().trim());
                stmt.setString(9, txtTeacherName.getText().trim());

                stmt.executeUpdate(); // Save course in DB
                showAlert("Course added successfully!");
                clearFields(); // Reset form
                closeWindow(); // Close popup

            } catch (Exception ex) {
                ex.printStackTrace(); // Print stack trace for debugging
                showAlert("Failed to add course. Please check your input.");
            }
        });
    }

    /**
     * Validates all user input fields before saving.
     * @return true if all inputs are valid; false otherwise
     */
    private boolean validateInputs() {
        // Ensure no field is left blank
        if (
                txtCourseCode.getText().trim().isEmpty() ||
                        txtCourseName.getText().trim().isEmpty() ||
                        txtSubjectCode.getText().trim().isEmpty() ||
                        txtSection.getText().trim().isEmpty() ||
                        txtCapacity.getText().trim().isEmpty() ||
                        txtLectureTime.getText().trim().isEmpty() ||
                        txtFinalExamDate.getText().trim().isEmpty() ||
                        txtLocation.getText().trim().isEmpty() ||
                        txtTeacherName.getText().trim().isEmpty()
        ) {
            showAlert("All fields must be filled before saving.");
            return false;
        }

        // Ensure Course Code and Capacity are valid integers
        try {
            Integer.parseInt(txtCourseCode.getText().trim());
            Integer.parseInt(txtCapacity.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Course Code and Capacity must be numbers.");
            return false;
        }

        return true;
    }

    /**
     * Displays an informational alert to the user.
     * @param message The message to display.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        txtCourseCode.clear();
        txtCourseName.clear();
        txtSubjectCode.clear();
        txtSection.clear();
        txtCapacity.clear();
        txtLectureTime.clear();
        txtFinalExamDate.clear();
        txtLocation.clear();
        txtTeacherName.clear();
    }

    /**
     * Closes the current popup window.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
