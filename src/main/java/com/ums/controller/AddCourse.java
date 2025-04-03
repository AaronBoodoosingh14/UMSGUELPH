package com.ums.controller;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddCourse {

    @FXML private TextField txtCourseCode;
    @FXML private TextField txtCourseName;
    @FXML private TextField txtSubjectCode;
    @FXML private TextField txtSection;
    @FXML private TextField txtCapacity;
    @FXML private TextField txtLectureTime;
    @FXML private TextField txtFinalExamDate;
    @FXML private TextField txtLocation;
    @FXML private TextField txtTeacherName;

    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    @FXML
    public void initialize() {
        clearFields(); // Optional: ensures no stale data on load

        btnCancel.setOnAction(e -> closeWindow());

        btnSave.setOnAction(e -> {
            if (!validateInputs()) return;

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO courses (CourseCode, CourseName, SubjectCode, SectionNumber, Capacity, LectureTime, FinalExamDate, Location, TeacherName) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                stmt.setInt(1, Integer.parseInt(txtCourseCode.getText().trim()));
                stmt.setString(2, txtCourseName.getText().trim());
                stmt.setString(3, txtSubjectCode.getText().trim());
                stmt.setString(4, txtSection.getText().trim());
                stmt.setInt(5, Integer.parseInt(txtCapacity.getText().trim()));
                stmt.setString(6, txtLectureTime.getText().trim());
                stmt.setString(7, txtFinalExamDate.getText().trim());
                stmt.setString(8, txtLocation.getText().trim());
                stmt.setString(9, txtTeacherName.getText().trim());

                stmt.executeUpdate();
                showAlert("Course added successfully!");
                clearFields();
                closeWindow();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to add course. Please check your input.");
            }
        });
    }

    private boolean validateInputs() {
        // Check all fields are filled
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

        // Check Course Code and Capacity are numbers
        try {
            Integer.parseInt(txtCourseCode.getText().trim());
            Integer.parseInt(txtCapacity.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Course Code and Capacity must be numbers.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
