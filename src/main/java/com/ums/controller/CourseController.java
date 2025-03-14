package com.ums.controller;

// Import required JavaFX and Apache POI libraries for UI components and Excel handling
import com.ums.database.DatabaseManager;
import org.apache.poi.ss.usermodel.Cell;
import com.ums.data.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

/**
 * Controller class for managing courses in the University Management System (UMS).
 * This class handles user interactions with the Course Management UI, including
 * adding, editing, deleting, and importing courses from an Excel file.
 */
public class CourseController {



    // TableView UI component for displaying courses
    @FXML
    private TableView<Course> courseTable;

    // Table columns representing course details
    @FXML
    private TableColumn<Course, String> courseNameColumn, courseCodeColumn, subjectNameColumn, sectionNumberColumn, teacherNameColumn, lectureTimeColumn, locationColumn;

    // Buttons for handling various actions
    @FXML
    private Button btnAddCourse, btnEditCourse, btnDeleteCourse, btnAssignFaculty, btnManageEnrollments;

    // Text fields for user input to add/edit courses
    @FXML
    private TextField txtCourseName, txtCourseCode, txtSubjectName, txtSectionNumber, txtTeacherName, txtLectureTime, txtLocation;

    // List to store course data dynamically
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    private String userRole = "Student";

    public void setUserRole(String role) {
        this.userRole = role;
        configureUIForRole();
    }
    /**
     * Initializes the controller. Sets up column-cell mappings and binds data to the TableView.
     */
    @FXML
    public void initialize() {
        // Mapping TableColumn to corresponding Course object properties
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        lectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        handleImportSQL();

    }

    private void configureUIForRole() {
        if ("Student".equalsIgnoreCase(userRole)){
            txtCourseName.setDisable(true);
            txtCourseCode.setDisable(true);
            txtSubjectName.setDisable(true);
            txtSectionNumber.setDisable(true);
            txtTeacherName.setDisable(true);
            txtLectureTime.setDisable(true);
            txtLocation.setDisable(true);

        }
    }
    /**
     * Handles adding a new course based on user input from text fields.
     */
    @FXML
    private void handleAddCourse() {
        // Retrieving input from user fields and trimming extra spaces
        String courseName = txtCourseName.getText().trim();
        int courseCode = Integer.parseInt(txtCourseCode.getText().trim());
        String subjectName = txtSubjectName.getText().trim();
        String sectionNumber = txtSectionNumber.getText().trim();
        String teacherName = txtTeacherName.getText().trim();
        String lectureTime = txtLectureTime.getText().trim();
        String location = txtLocation.getText().trim();

        // Validation: Ensure no fields are empty
        if (courseName.isEmpty() || subjectName.isEmpty() || sectionNumber.isEmpty() || teacherName.isEmpty() || lectureTime.isEmpty() || location.isEmpty()) {
            System.out.println("⚠️ ERROR: All fields must be filled before adding a course.");
            return;
        }

        // Creating a new Course object and adding it to the list
        Course newCourse = new Course(courseName, courseCode, subjectName, sectionNumber, teacherName, lectureTime, location);
        courses.add(newCourse);
        refreshTable(); // Refresh the table to reflect changes

        // Clearing input fields after adding
        txtCourseName.clear();
        txtCourseCode.clear();
        txtSubjectName.clear();
        txtSectionNumber.clear();
        txtTeacherName.clear();
        txtLectureTime.clear();
        txtLocation.clear();
    }

    /**
     * Handles editing an existing course selected from the TableView.
     */
    @FXML
    private void handleEditCourse() {

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Handles deleting a selected course from the TableView.
     */
    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courses.remove(selectedCourse);
            refreshTable();
        } else {
            System.out.println("⚠️ ERROR: No course selected for deletion.");
        }
    }

    /**
     * Handles importing course data from an Excel file.
     */


    private void handleImportSQL() {
        courses.clear();
        String insert = "SELECT * FROM courses";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(insert);
        ResultSet rs = stmt.executeQuery()){

            while (rs.next()) {
            Course course = new Course();
            course.courseNameProperty(rs.getString("Course Name"));
            course.courseCodeProperty(rs.getInt("CourseCode"));
            course.subjectNameProperty(rs.getString("SubjectName"));
            course.sectionNumberProperty(rs.getString("Section Number"));
            course.teacherNameProperty(rs.getString("Teacher Name"));
            course.lectureTimeProperty(rs.getString("Lecture Time"));
            course.locationProperty(rs.getString("Location"));


            courses.add(course); }
            courseTable.setItems(courses);
        }

        catch (SQLException e){
            System.out.println("no good");

        }
    }


    /**
     * Retrieves the string value from an Excel cell, handling different data types.
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * Refreshes the TableView to reflect changes.
     */
    private void refreshTable() {
        courseTable.setItems(null);  // Clear the table first
        courseTable.setItems(courses);  // Reassign list to trigger UI update
        courseTable.refresh();
    }

}
