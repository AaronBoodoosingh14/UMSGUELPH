package com.ums.controller;

// Import required JavaFX and Apache POI libraries for UI components and Excel handling
import com.ums.data.Subject;
import com.ums.database.DatabaseManager;
import org.apache.poi.ss.usermodel.Cell;
import com.ums.data.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private TableColumn<Course, String> courseNameColumn, subjectNameColumn, sectionNumberColumn,
            teacherNameColumn, lectureTimeColumn, locationColumn, finalexamColumn;
    @FXML
    private TableColumn<Course, Integer> coursecodeColumn, capacityColumn;

    // Buttons for handling various actions
    @FXML
    private Button btnAddCourse, btnEditCourse, btnDeleteCourse, btnAssignFaculty, btnManageEnrollments;

    // Text fields for user input to add/edit courses
    @FXML
    private TextField txtCourseName, txtCourseCode, txtSubjectName, txtSectionNumber, txtTeacherName, txtLectureTime, txtLocation, txtFinalExamDate, txtCapacity;

    // List to store course data dynamically
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();


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
        coursecodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        lectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        finalexamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        handleImportSQL();
        btnAddCourse.setOnAction(e -> {handleAddCourse();});

    }


    private void configureUIForRole() {
        if ("Student".equalsIgnoreCase(userRole)){
            txtCourseName.setVisible(true);
            txtCourseCode.setVisible(true);
            txtSubjectName.setVisible(true);
            txtSectionNumber.setVisible(true);
            txtTeacherName.setVisible(true);
            txtLectureTime.setVisible(true);
            txtLocation.setVisible(true);
            txtFinalExamDate.setVisible(true);
            txtCapacity.setVisible(true);


        }
    }
    /**
     * Handles adding a new course based on user input from text fields.
     */

    @FXML
    private void handleAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/CoursesPopup/EditCourses.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Course");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            handleImportSQL(); // refresh after add

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void editCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("No Selection", "Please select a course to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/CoursesPopup/EditCourses.fxml"));
            Parent root = loader.load();

            // Get controller and pass selected course to pre-fill
            EditCourse editController = loader.getController();
            editController.setCourse(selectedCourse);

            // Show modal popup
            Stage stage = new Stage();
            stage.setTitle("Edit Course");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            handleImportSQL(); // Refresh table after popup closes

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open edit popup.");
        }
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
        String query = "SELECT * FROM courses";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int courseCode = rs.getInt("CourseCode");
                String courseName = rs.getString("CourseName");
                String subjectCode = rs.getString("SubjectCode");
                String section = rs.getString("SectionNumber");
                int capacity = rs.getInt("Capacity");
                String lectureTime = rs.getString("LectureTime");
                String finalExam = rs.getString("FinalExamDate");
                String location = rs.getString("Location");
                String teacherName = rs.getString("TeacherName");

                // 🧹 Skip rows with empty or junk data
                if (courseCode <= 0 ||
                        courseName == null || courseName.trim().isEmpty() ||
                        subjectCode == null || subjectCode.trim().isEmpty() ||
                        section == null || section.trim().isEmpty()) {
                    System.out.println("Skipping invalid row: " + courseCode + " | " + courseName);
                    continue;
                }

                Course course = new Course();
                course.setCourseCode(courseCode);
                course.setCourseName(courseName);
                course.setSubjectName(subjectCode);
                course.setSectionNumber(section);
                course.setCapacity(capacity);
                course.setLectureTime(lectureTime);
                course.setFinalExam(finalExam);
                course.setLocation(location);
                course.setTeacherName(teacherName);

                courses.add(course);
            }

            courseTable.setItems(courses);
            courseTable.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load courses from database.");
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
