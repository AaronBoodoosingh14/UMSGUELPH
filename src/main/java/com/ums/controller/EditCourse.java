package com.ums.controller;

import com.ums.data.Course;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EditCourse {

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

    private Course course;

    public void setCourse(Course course) {
        this.course = course;
        // Pre-fill form
        txtCourseCode.setText(String.valueOf(course.getCourseCode()));
        txtCourseName.setText(course.getCourseName());
        txtSubjectCode.setText(course.getSubjectName());
        txtSection.setText(course.getSectionNumber());
        txtCapacity.setText(String.valueOf(course.getCapacity()));
        txtLectureTime.setText(course.getLectureTime());
        txtFinalExamDate.setText(course.getFinalExam());
        txtLocation.setText(course.getLocation());
        txtTeacherName.setText(course.getTeacherName());
    }

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> closeWindow());

        btnSave.setOnAction(e -> {
            if (!validateInputs()) return;

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE courses SET CourseCode=?, CourseName=?, SubjectCode=?, SectionNumber=?, Capacity=?, LectureTime=?, FinalExamDate=?, Location=?, TeacherName=? WHERE CourseCode=?")) {

                int newCourseCode = Integer.parseInt(txtCourseCode.getText().trim());

                stmt.setInt(1, newCourseCode); // NEW course code
                stmt.setString(2, txtCourseName.getText().trim());
                stmt.setString(3, txtSubjectCode.getText().trim());
                stmt.setString(4, txtSection.getText().trim());
                stmt.setInt(5, Integer.parseInt(txtCapacity.getText().trim()));
                stmt.setString(6, txtLectureTime.getText().trim());
                stmt.setString(7, txtFinalExamDate.getText().trim());
                stmt.setString(8, txtLocation.getText().trim());
                stmt.setString(9, txtTeacherName.getText().trim());
                stmt.setInt(10, course.getCourseCode()); // OLD course code to locate row

                stmt.executeUpdate();

                // Update in-memory object
                course.setCourseCode(newCourseCode);
                course.setCourseName(txtCourseName.getText().trim());
                course.setSubjectName(txtSubjectCode.getText().trim());
                course.setSectionNumber(txtSection.getText().trim());
                course.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
                course.setLectureTime(txtLectureTime.getText().trim());
                course.setFinalExam(txtFinalExamDate.getText().trim());
                course.setLocation(txtLocation.getText().trim());
                course.setTeacherName(txtTeacherName.getText().trim());

                showAlert("Course updated successfully!");
                closeWindow();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to update course.");
            }
        });
    }

    private boolean validateInputs() {
        if (txtCourseCode.getText().isEmpty() || txtCourseName.getText().isEmpty() ||
                txtSubjectCode.getText().isEmpty() || txtSection.getText().isEmpty() ||
                txtCapacity.getText().isEmpty() || txtLectureTime.getText().isEmpty() ||
                txtFinalExamDate.getText().isEmpty() || txtLocation.getText().isEmpty() ||
                txtTeacherName.getText().isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }

        try {
            Integer.parseInt(txtCourseCode.getText());
            Integer.parseInt(txtCapacity.getText());
        } catch (NumberFormatException e) {
            showAlert("Course Code and Capacity must be numbers.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Edit");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
