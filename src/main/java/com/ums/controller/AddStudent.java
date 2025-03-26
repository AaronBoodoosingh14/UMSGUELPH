package com.ums.controller;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Optional;

public class AddStudent extends EditFaculty {

    @FXML private TextField nameField;
    @FXML private TextField IDField;
    @FXML private TextField emailField;
    @FXML private TextField academicLevelField;
    @FXML private TextField addressField;
    @FXML private TextField telephoneField;
    @FXML private TextField currentSemesterField;
    @FXML private TextField subjectsRegisteredField;
    @FXML private TextField thesisTitleField;
    @FXML private TextField progressField;
    @FXML private TextField tuitionField;

    public void initialize() {
        String generatedId = generateNextStudentId();
        IDField.setText(generatedId);
        IDField.setEditable(false);

        academicLevelField.textProperty().addListener((obs, oldVal, newVal) -> {
            tuitionField.setText(calculateTuition(newVal));
        });

        getFacultyData("", "");
    }

    @Override
    public void getFacultyData(String OGname, String permID) {
        super.btncancel.setOnAction(e -> {
            Stage stage = (Stage) btncancel.getScene().getWindow();
            stage.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancelled");
            alert.setHeaderText("Are you sure you want to cancel? New Student will not be added");
            alert.setContentText("This action cannot be reversed");
            alert.showAndWait();
        });

        super.btnsave.setOnAction(e -> {
            String name = nameField.getText();
            String ID = IDField.getText();
            String email = formatEmail(emailField.getText());
            String telephone = telephoneField.getText();
            String currentSemester = currentSemesterField.getText();
            String Alevel = academicLevelField.getText();
            String address = addressField.getText();
            String subject = subjectsRegisteredField.getText();
            String thesisTitle = thesisTitleField.getText();
            String progress = progressField.getText();
            String tuition = calculateTuition(Alevel);
            String password = "default123";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to add student: " + name);
            alert.setContentText("This action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "INSERT INTO students_info (StudentId, Name, Address, Telephone, Email, AcademicLevel, " +
                        "CurrentSemester, SubjectsRegistered, ThesisTitle, Progress, Password, Tuition) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql)) {

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, address);
                    ps.setString(4, telephone);
                    ps.setString(5, email);
                    ps.setString(6, Alevel);
                    ps.setString(7, currentSemester);
                    ps.setString(8, subject);
                    ps.setString(9, thesisTitle);
                    ps.setString(10, progress);
                    ps.setString(11, password);
                    ps.setString(12, tuition);
                    ps.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try (Connection connection = DatabaseManager.getConnection();
                     var login = connection.prepareStatement(
                             "INSERT INTO loginInfo (username, password, role) VALUES (?, ?, ?)")) {

                    login.setString(1, ID);
                    login.setString(2, password);
                    login.setString(3, "Student");
                    login.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Stage stage = (Stage) btncancel.getScene().getWindow();
                stage.close();
            } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                Stage stage = (Stage) btncancel.getScene().getWindow();
                stage.close();
            }
        });
    }

    private String generateNextStudentId() {
        String base = "S2025";
        int nextNumber = 1;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("SELECT MAX(StudentId) FROM students_info");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null && lastId.startsWith(base)) {
                    String number = lastId.substring(base.length());
                    nextNumber = Integer.parseInt(number) + 1;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return base + String.format("%03d", nextNumber); // e.g., S2025001
    }

    private String calculateTuition(String level) {
        if ("Undergraduate".equalsIgnoreCase(level)) return "$5000";
        if ("Graduate".equalsIgnoreCase(level)) return "$4000";
        return "0";
    }
    private String formatEmail(String emailInput) {
        if (emailInput == null || emailInput.trim().isEmpty()) return "";

        emailInput = emailInput.trim();

        // Check if it already ends with "@example.edu"
        if (!emailInput.toLowerCase().endsWith("@example.edu")) {
            // Remove any existing domain part and append
            if (emailInput.contains("@")) {
                emailInput = emailInput.substring(0, emailInput.indexOf("@"));
            }
            emailInput += "@example.edu";
        }

        return emailInput;
    }
}
