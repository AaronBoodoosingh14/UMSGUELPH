package com.ums.controller;

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

public class CourseController {

    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, String> courseNameColumn, courseCodeColumn, subjectNameColumn, sectionNumberColumn, teacherNameColumn, lectureTimeColumn, locationColumn;
    @FXML
    private Button btnAddCourse, btnEditCourse, btnDeleteCourse, btnAssignFaculty, btnManageEnrollments, btnImportExcel;

    // 🔹 New input fields for manual entry
    @FXML
    private TextField txtCourseName, txtCourseCode, txtSubjectName, txtSectionNumber, txtTeacherName, txtLectureTime, txtLocation;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        lectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        courseTable.setItems(courses);
    }

    /**
     * Handles adding a new course from user input fields.
     */
    @FXML
    private void handleAddCourse() {
        String courseName = txtCourseName.getText().trim();
        String courseCode = txtCourseCode.getText().trim();
        String subjectName = txtSubjectName.getText().trim();
        String sectionNumber = txtSectionNumber.getText().trim();
        String teacherName = txtTeacherName.getText().trim();
        String lectureTime = txtLectureTime.getText().trim();
        String location = txtLocation.getText().trim();

        if (courseName.isEmpty() || courseCode.isEmpty() || subjectName.isEmpty() ||
                sectionNumber.isEmpty() || teacherName.isEmpty() || lectureTime.isEmpty() || location.isEmpty()) {
            System.out.println("⚠️ ERROR: All fields must be filled before adding a course.");
            return;
        }

        Course newCourse = new Course(courseName, courseCode, subjectName, sectionNumber, teacherName, lectureTime, location);
        courses.add(newCourse);
        refreshTable();

        // Clear input fields after adding a course
        txtCourseName.clear();
        txtCourseCode.clear();
        txtSubjectName.clear();
        txtSectionNumber.clear();
        txtTeacherName.clear();
        txtLectureTime.clear();
        txtLocation.clear();
    }

    @FXML
    private void handleEditCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            selectedCourse.courseNameProperty().set(txtCourseName.getText());
            selectedCourse.courseCodeProperty().set(txtCourseCode.getText());
            selectedCourse.subjectNameProperty().set(txtSubjectName.getText());
            selectedCourse.sectionNumberProperty().set(txtSectionNumber.getText());
            selectedCourse.teacherNameProperty().set(txtTeacherName.getText());
            selectedCourse.lectureTimeProperty().set(txtLectureTime.getText());
            selectedCourse.locationProperty().set(txtLocation.getText());

            refreshTable();
        } else {
            System.out.println("⚠️ ERROR: No course selected for editing.");
        }
    }

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

    @FXML
    private void handleImportExcel() {
        String filePath = "src/main/resources/UMS_Data.xlsx"; // Ensure correct path

        System.out.println("✅ Import button clicked! Attempting to load: " + filePath);

        // Check if the file exists
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            System.out.println("❌ ERROR: Excel file not found at " + filePath);
            return;
        }

        System.out.println("📂 Excel file found! Attempting to read...");

        try (InputStream fileStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileStream)) {

            Sheet sheet = workbook.getSheet("Courses"); // Load the Courses sheet
            if (sheet == null) {
                System.out.println("❌ ERROR: 'Courses' sheet not found in the Excel file.");
                return;
            }

            System.out.println("📋 Courses sheet found! Reading data...");

            boolean headerSkipped = false;
            int rowCount = 0;

            for (Row row : sheet) {
                if (!headerSkipped) {
                    headerSkipped = true; // Skip header
                    continue;
                }

                if (row.getPhysicalNumberOfCells() < 8) {
                    System.out.println("⚠️ Skipping row " + row.getRowNum() + " due to missing data.");
                    continue;
                }

                String courseCode = getCellValue(row.getCell(0));
                String courseName = getCellValue(row.getCell(1));
                String subjectCode = getCellValue(row.getCell(2));
                String sectionNumber = getCellValue(row.getCell(3));
                String lectureTime = getCellValue(row.getCell(5));
                String location = getCellValue(row.getCell(7));
                String teacherName = getCellValue(row.getCell(8));

                if (courseName.isEmpty() || courseCode.isEmpty()) {
                    System.out.println("⚠️ Skipping row " + row.getRowNum() + " due to empty fields.");
                    continue;
                }

                courses.add(new Course(courseName, courseCode, subjectCode, sectionNumber, teacherName, lectureTime, location));
                rowCount++;
            }

            System.out.println("✅ Successfully imported " + rowCount + " courses!");
            refreshTable();

        } catch (IOException e) {
            System.out.println("❌ ERROR: Could not read the Excel file.");
            e.printStackTrace();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING -> { return cell.getStringCellValue().trim(); }
            case NUMERIC -> { return String.valueOf((int) cell.getNumericCellValue()); }
            case BOOLEAN -> { return String.valueOf(cell.getBooleanCellValue()); }
            case FORMULA -> { return cell.getCellFormula(); }
            default -> { return ""; }
        }
    }

    private void refreshTable() {
        courseTable.setItems(null);
        courseTable.setItems(courses);
        courseTable.refresh();
    }
}
