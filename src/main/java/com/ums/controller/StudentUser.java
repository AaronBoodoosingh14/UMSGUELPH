package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class StudentUser extends Student {

    @FXML private TextField nameLabel, studentIdLabel, addressLabel, emailLabel, phoneLabel;
    @FXML private TextField academicLevelLabel, semesterLabel, thesisLabel, progressLabel, tuitionLabel;
    @FXML private TextArea registeredSubjectsArea, enrolledCoursesArea, gradesArea;
    @FXML private Button btnEditPassword;

    // Called when the FXML loads
    @FXML
    public void initialize() {
        String loggedInId = UMSApplication.getLoggedInUsername();
        if (loggedInId != null && !loggedInId.isEmpty()) {
            System.out.println("🔍 Logged-in student ID (from session): " + loggedInId);
            loadStudentFromDatabase(loggedInId);
        } else {
            System.out.println("⚠ No logged-in student ID found.");
        }
    }

    // For DashboardController to call directly
    public void setStudentIdFromLogin(String studentId) {
        System.out.println("🔁 setStudentIdFromLogin called with: " + studentId);
        loadStudentFromDatabase(studentId);
    }

    private void loadStudentFromDatabase(String studentId) {
        String query = "SELECT * FROM students_info WHERE StudentId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentId);
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
                student.setThesisTitle(rs.getString("ThesisTitle"));
                student.setProgress(rs.getString("Progress"));
                student.setTuition(rs.getString("Tuition"));
                student.setGrades(rs.getString("Grades"));
                student.setCourses(rs.getString("RegisteredCourses"));

                System.out.println("✅ Student record found: " + student.getName());
                updateUIWithStudentData(student);
            } else {
                System.out.println("❌ No student found in database for ID: " + studentId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUIWithStudentData(Student student) {
        nameLabel.setText(student.getName());
        studentIdLabel.setText(student.getStudentId());
        addressLabel.setText(student.getAddress());
        phoneLabel.setText(student.getTelephone());
        emailLabel.setText(student.getEmail());
        academicLevelLabel.setText(student.getAcademicLevel());
        semesterLabel.setText(student.getCurrentSemester());
        thesisLabel.setText(student.getThesisTitle());
        progressLabel.setText(student.getProgress());
        tuitionLabel.setText(student.getTuition());

        registeredSubjectsArea.setText(student.getSubjectsRegistered());
        enrolledCoursesArea.setText(student.getCourses());
        gradesArea.setText(student.getGrades());
    }

    @FXML
    public void handleEditPassword() {
        String studentID = studentIdLabel.getText();
        String currentPassword = "";

        String query = "SELECT password FROM loginInfo WHERE username = ?";
        String update = "UPDATE loginInfo SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             PreparedStatement ps = conn.prepareStatement(update)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentPassword = rs.getString("password");
            }

            TextField passField = new TextField(currentPassword);
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.add(new Label("New Password:"), 0, 0);
            grid.add(passField, 1, 0);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Password");
            alert.setHeaderText("Update your password");
            alert.getDialogPane().setContent(grid);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ps.setString(1, passField.getText());
                ps.setString(2, studentID);
                ps.executeUpdate();
                System.out.println("✅ Password updated for: " + studentID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
