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
 * Allows Admin to Add, Edit, Delete students & import from Excel.
 */
public class StudentController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> colStudentID, colName, colEmail, colAcademicLevel;

    @FXML
    private TextField txtStudentID, txtName, txtEmail, txtAcademicLevel;

    @FXML
    private Button btnAdd, btnEdit, btnDelete, btnImportExcel;

    private final ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colStudentID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAcademicLevel.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));

        studentTable.setItems(students);

        btnAdd.setOnAction(e -> addStudent());
        btnEdit.setOnAction(e -> editStudent());
        btnDelete.setOnAction(e -> deleteStudent());
        btnImportExcel.setOnAction(e -> importStudentsFromExcel());
    }

    /**
     * Imports students from an Excel file (UMS_Data.xlsx) into the TableView.
     */
    private void importStudentsFromExcel() {
        File file = new File("src/main/resources/UMS_Data.xlsx");
        if (!file.exists()) {
            showAlert("File Not Found", "The Excel file (UMS_Data.xlsx) is missing!");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Students ");
            if (sheet == null) {
                showAlert("Sheet Not Found", "The 'Students' sheet is missing in UMS_Data.xlsx!");
                return;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                org.apache.poi.ss.usermodel.Cell idCell = row.getCell(0);
                org.apache.poi.ss.usermodel.Cell nameCell = row.getCell(1);
                org.apache.poi.ss.usermodel.Cell emailCell = row.getCell(4);
                org.apache.poi.ss.usermodel.Cell academicLevelCell = row.getCell(5);

                if (idCell != null && nameCell != null && emailCell != null && academicLevelCell != null) {
                    String studentID = idCell.getStringCellValue().trim();
                    String name = nameCell.getStringCellValue().trim();
                    String email = emailCell.getStringCellValue().trim();
                    String academicLevel = academicLevelCell.getStringCellValue().trim();

                    if (!studentID.isEmpty() && !name.isEmpty() && !email.isEmpty() && !academicLevel.isEmpty()) {
                        if (!isDuplicate(studentID)) {
                            students.add(new Student(studentID, name, "", "", email, academicLevel, "", "", new ArrayList<>(), "", "", "default123"));
                        }
                    }
                }
            }

            studentTable.refresh();
            showAlert("Success", "Student data imported successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read UMS_Data.xlsx!");
        }
    }

    private boolean isDuplicate(String studentID) {
        return students.stream().anyMatch(student -> student.getStudentId().equalsIgnoreCase(studentID));
    }

    private void addStudent() {
        String studentID = txtStudentID.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String academicLevel = txtAcademicLevel.getText().trim();

        if (!studentID.isEmpty() && !name.isEmpty() && !email.isEmpty() && !academicLevel.isEmpty()) {
            if (!isDuplicate(studentID)) {
                students.add(new Student(studentID, name, "", "", email, academicLevel, "", "", new ArrayList<>(), "", "", "default123"));
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
     * Allows editing of the selected student in the TableView.
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

    private void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            students.remove(selected);
        } else {
            showAlert("No Selection", "Please select a student to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
