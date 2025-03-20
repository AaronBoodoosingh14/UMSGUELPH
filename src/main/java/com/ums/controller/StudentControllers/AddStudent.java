package com.ums.controller.StudentControllers;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javafx.scene.control.Alert;

public class AddStudent {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField academicLevelField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField currentSemesterField;
    @FXML
    private TextField subjectsRegisteredField;
    @FXML
    private TextField thesisTitleField;
    @FXML
    private TextField progressField;
    @FXML
    private TextField tuitionField;


    /**
     * Handles adding a new student to the database.
     */
    @FXML
    private void handleSaveStudent() {
        String studentID = generateStudentID();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String academicLevel = academicLevelField.getText().trim();
        String address = addressField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String currentSemester = currentSemesterField.getText().trim();
        String subjectsRegistered = subjectsRegisteredField.getText().trim();
        String thesisTitle = thesisTitleField.getText().trim();
        String progress = progressField.getText().trim();
        String tuition = tuitionField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || academicLevel.isEmpty()) {
            showAlert("Invalid Input", "Please fill in all required fields.");
            return;
        }

        String query = "INSERT INTO students_info (StudentId, Name, Email, AcademicLevel, Address, Telephone, CurrentSemester, SubjectsRegistered, ThesisTitle, Progress, Tuition) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentID);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, academicLevel);
            pstmt.setString(5, address);
            pstmt.setString(6, telephone);
            pstmt.setString(7, currentSemester);
            pstmt.setString(8, subjectsRegistered);
            pstmt.setString(9, thesisTitle);
            pstmt.setString(10, progress);
            pstmt.setString(11, tuition);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Student added successfully! Generated ID: " + studentID);
                closeWindow();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add student.");
        }
    }

    /**
     * Generates a unique Student ID in the format: "SXXXXXXXXX" (S + 9 random digits).
     */
    private String generateStudentID() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder("S");

        for (int i = 0; i < 9; i++) {
            idBuilder.append(random.nextInt(10));
        }

        return idBuilder.toString();
    }

    /**
     * Displays an alert message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Closes the Add Student window.
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
