package com.ums.controller;

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
        btnDelete.setOnAction(e -> {
            try {
                deleteStudent();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
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
    private ArrayList<String> columnSelect() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        String studentID = selectedStudent.getStudentId();
        String studentName = selectedStudent.getName();
        String Address = selectedStudent.getAddress();
        String telephone = selectedStudent.getTelephone();
        String studentEmail = selectedStudent.getEmail();
        String Alevel =  selectedStudent.getAcademicLevel();
        String semester = selectedStudent.getCurrentSemester();
        String subjectsReg = selectedStudent.getSubjectsRegistered().toString();
        String thesis = selectedStudent.getThesisTitle();
        String progress = selectedStudent.getProgress();
        String tuition = selectedStudent.getTuition();

        ArrayList<String> StudentSelect =  new ArrayList<String>();
        StudentSelect.add(studentID);
        StudentSelect.add(studentName);
        StudentSelect.add(Address);
        StudentSelect.add(telephone);
        StudentSelect.add(studentEmail);
        StudentSelect.add(Alevel);
        StudentSelect.add(semester);
        StudentSelect.add(subjectsReg);
        StudentSelect.add(thesis);
        StudentSelect.add(progress);
        StudentSelect.add(tuition);



        System.out.println(StudentSelect);
        return StudentSelect;
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
                student.setSubjectsRegistered((rs.getString("SubjectsRegistered")));
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
        ArrayList<String> student = new ArrayList<>(columnSelect());
        String studentID = student.get(0);
        String studentName = student.get(1);
        String Address = student.get(2);
        String telephone = student.get(3);
        String studentEmail = student.get(4);
        String Alevel = student.get(5);
        String semester = student.get(6);
        String subjectsReg = student.get(7);
        String thesis = student.get(8);
        String progress = student.get(9);
        String tuition = student.get(10);

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/StudentPopup/EditStudent.fxml"));
            Parent root = loader.load();

            EditStudent editStudentcontroller = loader.getController();

            editStudentcontroller.setStudentData(studentID,studentName,Address,telephone,studentEmail,Alevel,semester,subjectsReg,thesis,progress,tuition);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Faculty");
            stage.show();

        }catch (Exception e){
            e.printStackTrace();
    }}

    /**
     * Deletes the selected student from the TableView.
     */
    public void deleteStudent() throws SQLException {
        ArrayList<String> selection = new ArrayList<String>(columnSelect());
        String studentID = selection.get(0);
        String studentName = selection.get(1);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete " + studentName + " from the database");
        alert.setContentText("This action cannot be reversed");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            String sql = "DELETE FROM students_info WHERE StudentId =  ?";
            String login = "DELETE FROM loginInfo WHERE username = ?";
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement stmt = connection.prepareStatement(login);
            PreparedStatement statement = connection.prepareStatement(sql);
            var ps = connection.prepareStatement(sql);
            var rs = connection.prepareStatement(login);
            rs.setString(1,studentID);
            ps.setString(1, studentID);
            rs.executeUpdate();
            ps.executeUpdate();
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Successful Delete");
            alert2.setHeaderText("user was successfully deleted");
            alert2.setContentText("");
            Optional<ButtonType> result2 = alert2.showAndWait();


        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Delete confirm");
            alert1.setHeaderText(studentName + " was not deleted");
            alert1.setContentText("Either the faculty was not found or the user cancelled");
            Optional<ButtonType> result1 = alert1.showAndWait();

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
