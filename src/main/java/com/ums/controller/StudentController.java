package com.ums.controller;

import com.ums.controller.StudentControllers.AddStudent;
import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.ums.controller.StudentControllers.EditStudent;
import javafx.stage.Modality;


/**
 * Controller for Student Management.
 * Handles adding, editing, deleting students, and importing data from an SQL database.
 */
public class StudentController {

    // UI Elements for displaying and managing students
    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> colStudentID, colName, colEmail, colAcademicLevel, colAddress, colTelephone, colCurrentSemester, colProgress, colTuition, colThesisTitle, colSubjectsRegistered;

    @FXML
    private TextField txtStudentID, txtName, txtEmail, txtAcademicLevel, txtAddress, txtTelephone, txtCurrentSemester, txtProgress, txtTuition, txtThesisTitle, txtSubjectsRegistered, searchField;

    @FXML
    private Button btnAdd, btnEdit, btnDelete, btnRefresh;

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
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAcademicLevel.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));
        colCurrentSemester.setCellValueFactory(new PropertyValueFactory<>("currentSemester"));
        colSubjectsRegistered.setCellValueFactory(new PropertyValueFactory<>("subjectsRegistered"));
        colThesisTitle.setCellValueFactory(new PropertyValueFactory<>("thesisTitle"));
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        colTuition.setCellValueFactory(new PropertyValueFactory<>("tuition"));

        // Bind the student list to the TableView
        studentTable.setItems(students);

        // Set button actions to open Add and Edit dialogs
        btnAdd.setOnAction(e -> handleAddStudent()); //
        btnEdit.setOnAction(e -> handleEditStudent()); //
        btnDelete.setOnAction(e -> deleteStudent());
        btnRefresh.setOnAction(e -> handleRefresh());

        importStudentsFromSql();
    }

    /**
     * Refresh the student table with a small delay for better UI experience.
     */
    @FXML
    private void handleRefresh() {
        studentTable.getItems().clear();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), event -> {
            importStudentsFromSql();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Selects a student when a row in the table is clicked.
     */
    @FXML
    private void columnSelect() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        System.out.println(selectedStudent);
    }

    /**
     * Handles searching for a student by ID, Name, or Email.
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();

        if (searchTerm.isEmpty()) {
            studentTable.setItems(students);
        } else {
            ObservableList<Student> filteredList = students.filtered(student ->
                    student.getStudentId().toLowerCase().contains(searchTerm) ||
                            student.getName().toLowerCase().contains(searchTerm) ||
                            student.getEmail().toLowerCase().contains(searchTerm)
            );
            studentTable.setItems(filteredList);
        }
    }

    /**
     * Imports students from the SQL database into the TableView.
     */
    private void importStudentsFromSql() {
        students.clear();

        String query = "SELECT * FROM students_info";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("StudentId"));
                student.setName(rs.getString("Name"));
                student.setAddress(rs.getString("Address"));
                student.setTelephone(rs.getString("Telephone"));
                student.setEmail(rs.getString("Email"));
                student.setAcademicLevel(rs.getString("AcademicLevel"));
                student.setSubjectsRegistered(Collections.singletonList(rs.getString("SubjectsRegistered")));
                student.setThesisTitle(rs.getString("ThesisTitle"));
                student.setProgress(rs.getString("Progress"));
                student.setPassword(rs.getString("Password"));
                student.setCurrentSemester(rs.getString("CurrentSemester"));
                student.setTuition(rs.getString("Tuition"));

                students.add(student);
            }

            studentTable.setItems(students);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load student data.");
        }
    }

    /**
     * Adds a new student to the TableView and the database.
     */
    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/StudentPopup/AddStudent.fxml")); // ✅ Ensure the path is correct
            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle("Add Student");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            handleRefresh(); // Refresh table after adding
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Add Student window.");
        }
    }

    /**
     * Edits the selected student in the TableView and updates the database.
     */
    @FXML
    private void handleEditStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("No Selection", "Please select a student to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/StudentPopup/EditStudent.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the student data
            EditStudent controller = loader.getController();
            controller.setStudentData(selectedStudent);

            Stage stage = new Stage();
            stage.setTitle("Edit Student");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            handleRefresh(); // Refresh table after editing
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Edit Student window.");
        }
    }



    /**
     * Deletes the selected student from the TableView.
     */
    public void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            students.remove(selected);
        } else {
            showAlert("No Selection", "Please select a student to delete.");
        }
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
