// Keep all existing import statements
package com.ums.controller;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.PrimerLight;
import com.ums.UMSApplication;
import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class FacultyUserView {

    @FXML private ImageView profiledefault;
    @FXML private Label SID, address, telephone, tuition, email, semester, academic, progress, Name;
    @FXML private ToggleSwitch themeToggle;

    private final String ID = UMSApplication.getLoggedInUsername();
    private ArrayList<String> studentinfo = new ArrayList<>();

    @FXML
    public void initialize() {
        showPFP();
        SQLhandling();
        if (themeToggle != null) {
            themeToggle.setSelected(UMSApplication.isDarkMode());
            themeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                UMSApplication.setDarkMode(newValue);
                Application.setUserAgentStylesheet(newValue ? new NordDark().getUserAgentStylesheet() : new PrimerLight().getUserAgentStylesheet());
            });
        }
    }

    public void handleProfile(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/uploadpic.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Upload Profile");
        stage.show();
    }

    public void SQLhandling() {
        String query = "SELECT * FROM students_info WHERE StudentId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("StudentId"));
                student.setName(rs.getString("Name"));
                student.setAddress(rs.getString("Address"));
                student.setTelephone(rs.getString("Telephone"));
                student.setEmail(rs.getString("Email"));
                student.setAcademicLevel(rs.getString("AcademicLevel"));
                student.setCurrentSemester(rs.getString("CurrentSemester"));
                student.setSubjectsRegistered(rs.getString("SubjectsRegistered"));
                student.setProgress(rs.getString("Progress"));

                studentinfo.clear();
                studentinfo.add(student.getStudentId());
                studentinfo.add(student.getName());
                studentinfo.add(student.getAddress());
                studentinfo.add(student.getTelephone());
                studentinfo.add(student.getEmail());
                studentinfo.add(student.getAcademicLevel());
                studentinfo.add(student.getCurrentSemester());
                studentinfo.add(student.getSubjectsRegistered());
                studentinfo.add(student.getProgress());

                loadstudent(studentinfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadstudent(ArrayList<String> studentinfo) {
        SID.setText(studentinfo.get(0));
        Name.setText(studentinfo.get(1));
        address.setText(studentinfo.get(2));
        telephone.setText(studentinfo.get(3));
        email.setText(studentinfo.get(4));
        academic.setText(studentinfo.get(5));
        semester.setText(studentinfo.get(6));
        progress.setText(studentinfo.get(8));

        if (studentinfo.get(5).equalsIgnoreCase("Undergraduate")) {
            tuition.setText("$4000");
        } else {
            tuition.setText("$5000");
        }
    }

    public void showPFP() {
        String query = "SELECT profilepic FROM students_info WHERE StudentId = ?";
        String path = "src/main/resources/tempPic/";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String img = rs.getString("profilepic");
                if (img != null) {
                    File file = new File(path + img);
                    profiledefault.setImage(file.exists() ? new Image(file.toURI().toString()) : new Image("pic.png"));
                } else {
                    profiledefault.setImage(new Image("pic.png"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleEditPassword(ActionEvent e) {
        String query = "SELECT password FROM loginInfo WHERE username = ?";
        String update = "UPDATE loginInfo SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             PreparedStatement ps = conn.prepareStatement(update)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();
            String currentPass = rs.next() ? rs.getString("password") : "";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Change Password");
            alert.setHeaderText("Edit your password");

            TextField inputField = new TextField(currentPass);
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.add(new Label("Password:"), 0, 0);
            grid.add(inputField, 1, 0);
            alert.getDialogPane().setContent(grid);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ps.setString(1, inputField.getText());
                ps.setString(2, ID);
                ps.executeUpdate();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleViewAssignedCourses(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assigned Courses");
        alert.setHeaderText("Your Registered Courses");
        alert.setContentText(studentinfo.get(7));
        alert.showAndWait();
    }

    public void handleViewGrades(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Grades");
        alert.setHeaderText("Semester Grades");

        StringBuilder grades = new StringBuilder();
        String query = "SELECT Semester, SubjectName, Grade FROM grades WHERE StudentId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();

            boolean hasGrades = false;
            while (rs.next()) {
                hasGrades = true;
                grades.append(rs.getString("Semester"))
                        .append(" - ")
                        .append(rs.getString("SubjectName"))
                        .append(" - ")
                        .append(rs.getString("Grade"))
                        .append("\n");
            }

            if (!hasGrades) {
                grades.append("No grades available.");
            }

        } catch (SQLException e) {
            grades.append("Unable to retrieve grades.");
            e.printStackTrace();
        }

        alert.setContentText(grades.toString());
        alert.showAndWait();
    }

    public void handleViewAllCourses(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Courses");
        alert.setHeaderText("Courses Available");

        StringBuilder courseList = new StringBuilder();
        String query = "SELECT courseName FROM course";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courseList.append("- ").append(rs.getString("courseName")).append("\n");
            }

        } catch (SQLException e) {
            courseList.append("Unable to retrieve courses.");
        }

        alert.setContentText(courseList.toString());
        alert.showAndWait();
    }
}
