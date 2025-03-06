package com.ums.controller;

// Import required JavaFX and Apache POI libraries for UI components and Excel handling
import org.apache.poi.ss.usermodel.Cell;
import com.ums.data.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        handleImportExcel();
        // Binding the observable list to the TableView
        courseTable.setItems(courses);
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
        String courseCode = txtCourseCode.getText().trim();
        String subjectName = txtSubjectName.getText().trim();
        String sectionNumber = txtSectionNumber.getText().trim();
        String teacherName = txtTeacherName.getText().trim();
        String lectureTime = txtLectureTime.getText().trim();
        String location = txtLocation.getText().trim();

        // Validation: Ensure no fields are empty
        if (courseName.isEmpty() || courseCode.isEmpty() || subjectName.isEmpty() || sectionNumber.isEmpty() || teacherName.isEmpty() || lectureTime.isEmpty() || location.isEmpty()) {
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
        // Get the selected course from the table
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();

        if (selectedCourse != null) {
            // Retrieve new values from text fields
            String newCourseName = txtCourseName.getText().trim();
            String newCourseCode = txtCourseCode.getText().trim();
            String newSubjectName = txtSubjectName.getText().trim();
            String newSectionNumber = txtSectionNumber.getText().trim();
            String newTeacherName = txtTeacherName.getText().trim();
            String newLectureTime = txtLectureTime.getText().trim();
            String newLocation = txtLocation.getText().trim();

            // Update only if new values are provided (don't overwrite with empty values)
            if (!newCourseName.isEmpty()) selectedCourse.courseNameProperty().set(newCourseName);
            if (!newCourseCode.isEmpty()) selectedCourse.courseCodeProperty().set(newCourseCode);
            if (!newSubjectName.isEmpty()) selectedCourse.subjectNameProperty().set(newSubjectName);
            if (!newSectionNumber.isEmpty()) selectedCourse.sectionNumberProperty().set(newSectionNumber);
            if (!newTeacherName.isEmpty()) selectedCourse.teacherNameProperty().set(newTeacherName);
            if (!newLectureTime.isEmpty()) selectedCourse.lectureTimeProperty().set(newLectureTime);
            if (!newLocation.isEmpty()) selectedCourse.locationProperty().set(newLocation);

            // Force update TableView by resetting the list
            courseTable.refresh();

            // Clear input fields after editing
            txtCourseName.clear();
            txtCourseCode.clear();
            txtSubjectName.clear();
            txtSectionNumber.clear();
            txtTeacherName.clear();
            txtLectureTime.clear();
            txtLocation.clear();

            showAlert("Success", "Course details updated successfully!");
        } else {
            showAlert("No Selection", "Please select a course to edit.");
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
    @FXML
    private void handleImportExcel() {
        String filePath = "src/main/resources/UMS_Data.xlsx"; // Path to Excel file
        System.out.println("✅ Import button clicked! Attempting to load: " + filePath);

        try (InputStream fileStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileStream)) {

            Sheet sheet = workbook.getSheet("Courses"); // Get the "Courses" sheet
            if (sheet == null) {
                System.out.println("❌ ERROR: 'Courses' sheet not found in the Excel file.");
                return;
            }

            System.out.println("📋 Courses sheet found! Reading data...");
            courses.clear(); // Clear existing data before importing

            boolean headerSkipped = false;

            for (Row row : sheet) {
                if (!headerSkipped) {
                    headerSkipped = true; // Skip header row
                    continue;
                }
                // Ensure no empty values are added
                String courseName = getCellValue(row.getCell(1));
                String courseCode = getCellValue(row.getCell(0));
                String subjectName = getCellValue(row.getCell(2));
                String sectionNumber = getCellValue(row.getCell(3));
                String teacherName = getCellValue(row.getCell(8));
                String lectureTime = getCellValue(row.getCell(5));
                String location = getCellValue(row.getCell(7));

                if (!courseName.isEmpty() && !courseCode.isEmpty()) {  // Prevent blank rows
                    courses.add(new Course(courseName, courseCode, subjectName, sectionNumber, teacherName, lectureTime, location));
                }
            }
            System.out.println("✅ Successfully imported courses!");
            refreshTable();
        } catch (IOException e) {
            System.out.println("❌ ERROR: Could not read the Excel file.");
            e.printStackTrace();
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
