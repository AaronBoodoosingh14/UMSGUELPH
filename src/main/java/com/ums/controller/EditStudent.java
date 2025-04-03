// Updated EditStudent.java with full validation and formatting
package com.ums.controller;

import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Optional;

public class EditStudent extends Student {

    @FXML
    protected Button btncancel;
    @FXML
    protected Button btnsave;
    @FXML
    private TextField IDField;
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

    @FXML
    public void initialize(){
        setStudentData(getStudentId(), getName(), getAddress(), getTelephone(), getEmail(), getAcademicLevel(), getCurrentSemester(), getSubjectsRegistered(), getThesisTitle(), getProgress(), getTuition());
    }

    public void setStudentData(String studentID, String studentName, String Address, String telephone, String studentEmail, String Alevel, String semester, String subjectsReg, String thesis, String progress, String tuition) {
        IDField.setText(studentID);
        nameField.setText(studentName);
        emailField.setText(studentEmail);
        academicLevelField.setText(Alevel);
        addressField.setText(Address);
        telephoneField.setText(telephone);
        currentSemesterField.setText(semester);
        subjectsRegisteredField.setText(subjectsReg);
        thesisTitleField.setText(thesis);
        progressField.setText(progress);
        tuitionField.setText(tuition);
        getStudentData(studentName, studentID);
    }

    private String formatTextInput(String input) {
        if (input == null || input.trim().isEmpty()) return "-";
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    public void getStudentData(String OGname, String permID){
        // Auto-generate email on name change
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            emailField.setText(generateEmailFromName(newVal));
            emailField.setEditable(false);
        });

        btncancel.setOnAction(e -> {
            Stage stage = (Stage) btncancel.getScene().getWindow();
            stage.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancelled");
            alert.setHeaderText("Are you sure you want to cancel changes?");
            alert.setContentText("Changes will not be saved.");
            alert.showAndWait();
        });

        btnsave.setOnAction(e -> {
            String name = formatTextInput(nameField.getText());
            String ID = IDField.getText();
            String email = emailField.getText(); // Already generated
            String alevel = formatTextInput(academicLevelField.getText());
            String address = formatTextInput(addressField.getText());
            String telephone = formatTextInput(telephoneField.getText());
            String semester = formatTextInput(currentSemesterField.getText());
            String subjectsReg = formatTextInput(subjectsRegisteredField.getText());
            String thesis = formatTextInput(thesisTitleField.getText());
            String progressRaw = progressField.getText().trim();
            String tuition = tuitionField.getText();

            // Validation section
            if (!isValidName(name)) {
                showAlert("Please enter the full name (first and last).", "Invalid Name");
                return;
            }
            if (!isValidPhone(telephone)) {
                showAlert("Phone number must be in the format 123-4567.", "Invalid Phone Number");
                return;
            }
            if (!isValidAcademicLevel(alevel)) {
                showAlert("Academic Level must be 'Undergraduate' or 'Graduate'.", "Invalid Academic Level");
                return;
            }
            if (!isValidSemester(semester)) {
                showAlert("Semester must be 'Fall YYYY' or 'Winter YYYY'.", "Invalid Semester");
                return;
            }
            if (!isValidProgress(progressRaw.replace("%", ""))) {
                showAlert("Progress must be a number between 0 and 100.", "Invalid Progress");
                return;
            }
            if (alevel.equalsIgnoreCase("Graduate") && (thesis == null || thesis.equals("-"))) {
                showAlert("Graduate students must enter a thesis title.", "Missing Thesis Title");
                return;
            }

            String progress = progressRaw.endsWith("%") ? progressRaw : progressRaw + "%";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to make changes to " + OGname);
            alert.setContentText("Action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "UPDATE students_info SET StudentId = ?, Name = ?, Address = ?, Telephone = ?, Email = ?, AcademicLevel = ?, CurrentSemester = ?, SubjectsRegistered = ?, ThesisTitle = ?, Progress = ?, Tuition = ? WHERE StudentId = ?;";
                String login = "UPDATE loginInfo SET username = ? WHERE username = ?;";

                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql);
                     var rs = connection.prepareStatement(login)) {

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, address);
                    ps.setString(4, telephone);
                    ps.setString(5, email);
                    ps.setString(6, alevel);
                    ps.setString(7, semester);
                    ps.setString(8, subjectsReg);
                    ps.setString(9, thesis);
                    ps.setString(10, progress);
                    ps.setString(11, tuition);
                    ps.setString(12, permID);

                    rs.setString(1, ID);
                    rs.setString(2, permID);

                    rs.executeUpdate();
                    ps.executeUpdate();

                    Stage stage = (Stage) btncancel.getScene().getWindow();
                    stage.close();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
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

    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Input Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateEmailFromName(String nameInput) {
        if (nameInput == null || nameInput.trim().isEmpty()) return "-@example.edu";
        String[] parts = nameInput.trim().split("\\s+");
        String shorter = parts.length >= 2 ? (parts[0].length() <= parts[1].length() ? parts[0] : parts[1]) : parts[0];
        return shorter.toLowerCase() + "@example.edu";
    }
}
