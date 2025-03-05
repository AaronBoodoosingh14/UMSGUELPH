package com.ums.controller;

import com.ums.data.Faculty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacultyviewController { // Fixed class name to match FXML

    @FXML private ImageView profileImage;
    @FXML private Label profileLetter;
    @FXML private Label facultyNameLabel;
    @FXML private Label degreeLabel;  // Fixed from degreetLabel
    @FXML private Label researchLabel;
    @FXML private Label emailLabel;
    @FXML private Label officeLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private ListView<String> coursesListView;
    @FXML private ListView<String> studentsListView;
    @FXML private VBox profileSection;
    @FXML private VBox coursesSection;
    @FXML private VBox studentsSection;

    private Faculty loggedFaculty;
    private final Map<String, List<String>> courseStudentsMap = new HashMap<>();

    public void setFacultyData(Faculty faculty) {
        this.loggedFaculty = faculty;
        initializeSampleData();
        updateUI();
    }

    private void initializeSampleData() {
        courseStudentsMap.put("CS101", List.of("Alice Johnson (S1001)", "Bob Smith (S1002)"));
        courseStudentsMap.put("MATH201", List.of("Charlie Brown (S1003)"));
    }

    private void updateUI() {
        // Basic Information
        facultyNameLabel.setText(loggedFaculty.getFacultyName());
        degreeLabel.setText(loggedFaculty.getDegree());
        researchLabel.setText(loggedFaculty.getResearch());
        emailLabel.setText(loggedFaculty.getEmail());
        officeLabel.setText(loggedFaculty.getOfficeLocation());

        // Courses List
        coursesListView.getItems().setAll(loggedFaculty.getCourses());

        // Set up course selection listener
        coursesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateStudentsList(newVal)
        );

        updateProfileDisplay();
    }

    private void updateProfileDisplay() {
        if (loggedFaculty.getProfilePhotoPath() != null && !loggedFaculty.getProfilePhotoPath().isEmpty()) {
            try {
                profileImage.setImage(new Image("file:" + loggedFaculty.getProfilePhotoPath()));
                profileImage.setVisible(true);
                profileLetter.setVisible(false);
            } catch (Exception e) {
                showFallbackProfile();
            }
        } else {
            showFallbackProfile();
        }
    }

    private void showFallbackProfile() {
        profileImage.setVisible(false);
        profileLetter.setVisible(true);
        profileLetter.setText(getInitialLetter());
        profileLetter.setStyle("-fx-background-color: " + generateColorFromName() + ";");
    }

    private String getInitialLetter() {
        return loggedFaculty.getFacultyName().isEmpty() ? "?"
                : loggedFaculty.getFacultyName().substring(0, 1).toUpperCase();
    }

    private String generateColorFromName() {
        int hash = loggedFaculty.getFacultyName().hashCode();
        return String.format("#%06x", (hash & 0x00FFFFFF));
    }

    private void updateStudentsList(String courseCode) {
        studentsListView.getItems().clear();
        if (courseCode != null && courseStudentsMap.containsKey(courseCode)) {
            studentsListView.getItems().addAll(courseStudentsMap.get(courseCode));
        }
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(profileImage.getScene().getWindow());

        if (file != null) {
            loggedFaculty.setProfilePhotoPath(file.getAbsolutePath());
            updateProfileDisplay();
            showAlert("Success", "Profile photo updated!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleUpdatePassword() {
        String newPassword = newPasswordField.getText().trim();

        if (newPassword.isEmpty()) {
            showAlert("Error", "Password cannot be empty", Alert.AlertType.ERROR);
            return;
        }

        if (newPassword.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters", Alert.AlertType.ERROR);
            return;
        }

        showAlert("Success", "Password updated successfully", Alert.AlertType.INFORMATION);
        newPasswordField.clear();
    }

    @FXML
    private void showProfile() {
        setVisibleSection(profileSection);
    }

    @FXML
    private void showCourses() {
        setVisibleSection(coursesSection);
    }

    @FXML
    private void showStudents() {
        setVisibleSection(studentsSection);
    }

    @FXML
    private void handleLogout() {
        showAlert("Logout", "Successfully logged out", Alert.AlertType.INFORMATION);
    }

    private void setVisibleSection(VBox section) {
        profileSection.setVisible(section == profileSection);
        coursesSection.setVisible(section == coursesSection);
        studentsSection.setVisible(section == studentsSection);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}