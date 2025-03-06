package com.ums.controller;

import com.ums.data.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Student Management.
 * Handles adding, editing, deleting students, and importing data from an Excel file.
 */
public class StudentController {

    // UI Elements for displaying and managing students
    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> colStudentID, colName, colEmail, colAcademicLevel;

    @FXML
    private TextField txtStudentID, txtName, txtEmail, txtAcademicLevel;

    @FXML
    private Button btnAdd, btnEdit, btnDelete, btnImportExcel;

    // ObservableList to hold student data and update TableView in real-time
    private final ObservableList<Student> students = FXCollections.observableArrayList();

    /**
     * Initializes the Student Management UI.
     * - Sets up TableView columns.
     * - Binds button actions to respective methods.
     */
    @FXML
    public void initialize() {
        // Link TableView columns to Student object properties
        colStudentID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAcademicLevel.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));

        // Bind the student list to the TableView
        studentTable.setItems(students);

        // Set button actions
        btnAdd.setOnAction(e -> addStudent());
        btnEdit.setOnAction(e -> editStudent());
        btnDelete.setOnAction(e -> deleteStudent());
        btnImportExcel.setOnAction(e -> importStudentsFromExcel());
    }

    /**
     * Imports students from an Excel file (UMS_Data.xlsx) into the TableView.
     * - Reads the "Students" sheet.
     * - Extracts Student ID, Name, Email, and Academic Level.
     * - Prevents duplicate Student IDs.
     */
    private void importStudentsFromExcel() {
        File file = new File("src/main/resources/UMS_Data.xlsx"); // Excel file path
        if (!file.exists()) {
            showAlert("File Not Found", "The Excel file (UMS_Data.xlsx) is missing!");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the "Students" sheet from the Excel file
            Sheet sheet = workbook.getSheet("Students");
            if (sheet == null) {
                showAlert("Sheet Not Found", "The 'Students' sheet is missing in UMS_Data.xlsx!");
                return;
            }

            // Iterate through each row in the Excel sheet
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                // Retrieve cell values from the row
                org.apache.poi.ss.usermodel.Cell idCell = row.getCell(0);
                org.apache.poi.ss.usermodel.Cell nameCell = row.getCell(1);
                org.apache.poi.ss.usermodel.Cell emailCell = row.getCell(4);
                org.apache.poi.ss.usermodel.Cell academicLevelCell = row.getCell(5);

                // Ensure that all required cells are present
                if (idCell != null && nameCell != null && emailCell != null && academicLevelCell != null) {
                    String studentID = idCell.getStringCellValue().trim();
                    String name = nameCell.getStringCellValue().trim();
                    String email = emailCell.getStringCellValue().trim();
                    String academicLevel = academicLevelCell.getStringCellValue().trim();

                    // Validate that no fields are empty and prevent duplicate entries
                    if (!studentID.isEmpty() && !name.isEmpty() && !email.isEmpty() && !academicLevel.isEmpty()) {
                        if (!isDuplicate(studentID)) {
                            students.add(new Student(studentID, name, "", "", email, academicLevel, "", "", new ArrayList<>(), "", "", "default123"));
                        }
                    }
                }
            }

            studentTable.refresh(); // Refresh the TableView to display imported data
            showAlert("Success", "Student data imported successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read UMS_Data.xlsx!");
        }
    }

    /**
     * Checks if a student with the given Student ID already exists in the list.
     */
    private boolean isDuplicate(String studentID) {
        return students.stream().anyMatch(student -> student.getStudentId().equalsIgnoreCase(studentID));
    }

    /**
     * Adds a new student to the TableView.
     * - Retrieves input values.
     * - Ensures all fields are filled.
     * - Checks for duplicate Student ID.
     */
    private void addStudent() {
        String studentID = txtStudentID.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String academicLevel = txtAcademicLevel.getText().trim();

        if (!studentID.isEmpty() && !name.isEmpty() && !email.isEmpty() && !academicLevel.isEmpty()) {
            if (!isDuplicate(studentID)) {
                students.add(new Student(studentID, name, "", "", email, academicLevel, "", "", new ArrayList<>(), "", "", "default123"));

                // Clear input fields after adding
                txtStudentID.clear();
                txtName.clear();
                txtEmail.clear();
                txtAcademicLevel.clear();
            } else {
                showAlert("Duplicate ID", "A student with this ID already exists.");
            }
        } else {
            showAlert("Invalid Input", "Please fill in all fields.");
        }
    }

    /**
     * Edits the selected student in the TableView.
     * - Only updates non-empty fields.
     * - Refreshes the TableView to reflect changes.
     */
    private void editStudent() {
        // Get selected student from the table
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            // Retrieve new values from text fields
            String newName = txtName.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newAcademicLevel = txtAcademicLevel.getText().trim();

            // Update only if the new values are not empty
            if (!newName.isEmpty()) selectedStudent.setName(newName);
            if (!newEmail.isEmpty()) selectedStudent.setEmail(newEmail);
            if (!newAcademicLevel.isEmpty()) selectedStudent.setAcademicLevel(newAcademicLevel);

            // Refresh TableView to reflect changes
            studentTable.refresh();

            // Clear input fields after editing
            txtName.clear();
            txtEmail.clear();
            txtAcademicLevel.clear();

            showAlert("Success", "Student details updated successfully!");
        } else {
            showAlert("No Selection", "Please select a student to edit.");
        }
    }

    /**
     * Deletes the selected student from the TableView.
     */
    private void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            students.remove(selected);
        } else {
            showAlert("No Selection", "Please select a student to delete.");
        }
    }

    /**
     * Displays an alert dialog with a given title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
