// Updated AddStudent.java: Email auto-fills based on name field when typing
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

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setText(generateEmailFromName(newVal));
            emailField.setEditable(false);
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
            String name = formatTextInput(nameField.getText());
            String ID = IDField.getText();
            String telephone = telephoneField.getText();
            String currentSemester = formatTextInput(currentSemesterField.getText());
            String Alevel = formatTextInput(academicLevelField.getText());
            String address = formatTextInput(addressField.getText());
            String subject = formatTextInput(subjectsRegisteredField.getText());
            String thesisTitle = formatTextInput(thesisTitleField.getText());
            String progressRaw = progressField.getText().trim();
            String tuition = calculateTuition(Alevel);
            String email = emailField.getText().trim();
            String password = "default123";

            if (!isValidName(name)) {
                showValidationAlert("Please enter the full name (first and last).");
                return;
            }
            if (!isValidPhone(telephone)) {
                showValidationAlert("Phone number must be in the format 123-4567.");
                return;
            }
            if (!isValidAcademicLevel(Alevel)) {
                showValidationAlert("Academic Level must be either 'Undergraduate' or 'Graduate'.");
                return;
            }
            if (!isValidSemester(currentSemester)) {
                showValidationAlert("Semester must be in format 'Fall YYYY' or 'Winter YYYY'.");
                return;
            }
            if (Alevel.equalsIgnoreCase("Graduate") && (thesisTitle == null || thesisTitle.trim().isEmpty() || thesisTitle.equals("-"))) {
                showValidationAlert("Graduate students must enter a thesis title.");
                return;
            }
            if (!isValidProgress(progressRaw)) {
                showValidationAlert("Progress must be a number between 0 and 100.");
                return;
            }

            String progress = progressRaw + "%";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to add student: " + name);
            alert.setContentText("This action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement("INSERT INTO students_info (StudentId, Name, Address, Telephone, Email, AcademicLevel, CurrentSemester, SubjectsRegistered, ThesisTitle, Progress, Password, Tuition) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

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
                     var login = connection.prepareStatement("INSERT INTO loginInfo (username, password, role) VALUES (?, ?, ?)") ) {
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

        return base + String.format("%04d", nextNumber);
    }

    private String formatTextInput(String input) {
        if (input == null || input.trim().isEmpty()) return "-";

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return capitalized.toString().trim();
    }

    private String calculateTuition(String level) {
        if ("Undergraduate".equalsIgnoreCase(level)) return "$5000";
        if ("Graduate".equalsIgnoreCase(level)) return "$4000";
        return "0";
    }

    private String generateEmailFromName(String nameInput) {
        if (nameInput == null || nameInput.trim().isEmpty()) return "-@example.edu";
        String[] parts = nameInput.trim().split("\\s+");
        String shorter = parts.length >= 2 ? (parts[0].length() <= parts[1].length() ? parts[0] : parts[1]) : parts[0];
        return shorter.toLowerCase() + "@example.edu";
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().split("\\s+").length >= 2;
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{3}-\\d{4}");
    }

    private boolean isValidAcademicLevel(String level) {
        return level.equalsIgnoreCase("Undergraduate") || level.equalsIgnoreCase("Graduate");
    }

    private boolean isValidSemester(String semester) {
        return semester.matches("(?i)(Fall|Winter)\\s\\d{4}");
    }

    private boolean isValidProgress(String progress) {
        try {
            int val = Integer.parseInt(progress);
            return val >= 0 && val <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showValidationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input Detected");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
