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
    private TableColumn<Course, String> courseNameColumn, subjectNameColumn, sectionNumberColumn,
            teacherNameColumn, lectureTimeColumn, locationColumn, finalexamColumn;
    @FXML
    private TableColumn<Course, Integer> coursecodeColumn, capacityColumn;

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
        coursecodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        lectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        finalexamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
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
    /*private void handleAddCourse() {
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
        Course newCourse = new Course(courseName, courseCode, subjectName, sectionNumber, teacherName, lectureTime, location,capacity,finalexam);
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


    private void addCourse() {
        String insertStatement = "INSERT INTO courses VALUES(?,?,?,?,?,?,?,?,?)";
        String CourseCode = txtCourseCode.getText().trim();
        String CourseName = txtCourseName.getText().trim();
        String SubjectName = txtSubjectName.getText().trim();
        String SectionNumber = txtSectionNumber.getText().trim();
        String TeacherName = txtTeacherName.getText().trim();
        String LectureTime = txtLectureTime.getText().trim();
        String Location = txtLocation.getText().trim();

        if (CourseCode.isEmpty() || CourseName.isEmpty() || SubjectName.isEmpty() || SectionNumber.isEmpty()){
            System.out.println("All fields must be filled before adding a course.");
            return;
        }

        //Add funtion later
        //if (isDuplicate(code)){
            //System.out.println("Course code already exists.");
        //}

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertStatement)) {

            stmt.setString(1, CourseCode);
            stmt.setString(2,CourseName);
            stmt.setString(3,SubjectName);
            stmt.setString(4,SectionNumber);
            stmt.setString(5,TeacherName);
            stmt.setString(6,LectureTime);
            stmt.setString(7,Location);


            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Course added successfully.");

                Course course = new Course();
                course.setCourseCode(Integer.parseInt(CourseCode));
                course.setCourseName(CourseName);
                course.setSubjectName(SubjectName);
                course.setSectionNumber(SectionNumber);
                course.setTeacherName(TeacherName);
                course.setLectureTime(LectureTime);
                course.setLocation(Location);
                courses.add(course);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        txtCourseCode.clear();
        txtCourseName.clear();
        txtSubjectName.clear();
        txtSectionNumber.clear();
        txtTeacherName.clear();
        txtLectureTime.clear();
        txtLocation.clear();

    }


    //private void handleEditCourse() {

    //}

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
        String query = "SELECT * FROM courses"; // Ensure 'courses' is the correct table name

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseCode(rs.getInt("Course Code")); // Adjust column names as needed
                course.setCourseName(rs.getString("Course Name"));
                course.setSubjectName(rs.getString("Subject Code"));
                course.setSectionNumber(rs.getString("Section Number"));
                course.setCapacity(rs.getInt("Capacity"));
                course.setLectureTime(rs.getString("Lecture Time"));
                course.setFinalExam(rs.getString("Final Exam Date/Time"));
                course.setLocation(rs.getString("Location"));
                course.setTeacherName(rs.getString("Teacher Name"));

                courses.add(course);
            }

            courseTable.setItems(courses); // Ensure the table is updated with new data
        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL exception
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
