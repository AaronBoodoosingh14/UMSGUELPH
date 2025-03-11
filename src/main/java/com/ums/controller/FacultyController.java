package com.ums.controller;

import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.sql.SQLException;
import java.util.Scanner;


public class FacultyController {

    @FXML private TableView<Faculty> facultyTable;
    @FXML private TableColumn<Faculty, String> nameColumn;
    @FXML private TableColumn<Faculty, String> emailColumn;
    @FXML private TableColumn<Faculty, String> degreeColumn;
    @FXML private TableColumn<Faculty, String> researchColumn;
    @FXML private TableColumn<Faculty, String> officeColumn;
    @FXML private TableColumn<Faculty, String> coursesColumn;
    @FXML private TextField searchField;

    private ObservableList<Faculty> facultyList = FXCollections.observableArrayList();


    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        researchColumn.setCellValueFactory(new PropertyValueFactory<>("research"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeLocation"));
        coursesColumn.setCellValueFactory(cellData -> {
            return null;
        });
        loadFacultyData();
    }

    private void insertFacultyIntoDatabase(Faculty newFaculty) throws Exception {
        String insertStatement = "INSERT INTO faculty_info (id, name, goon) VALUES (?, ?, ?)";
        Connection connection  = DatabaseManager.getConnection();
        var ps  = connection.prepareStatement(insertStatement);
        Scanner sc = new Scanner(System.in);



        //ps.setInt(1,FacultyID);
        //ps.setString(2,Name);
        //ps.setString(3,Degree);
        //ps.setString(4,ResearchInterest);
        //ps.setString(5,Email);
        //ps.setString(6,OfficeLocation);
        //ps.setString(7,password);

        ps.executeUpdate();
    }


    private void loadFacultyData() {
        facultyList.clear(); // Clear existing list before fetching data

        String query = "SELECT * FROM faculty_info";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Faculty faculty = new Faculty();
                faculty.setFacultyName(rs.getString("Name"));
                faculty.setEmail(rs.getString("Email"));
                faculty.setDegree(rs.getString("Degree"));
                faculty.setResearch(rs.getString("ResearchInterest"));
                faculty.setOfficeLocation(rs.getString("OfficeLocation"));


                facultyList.add(faculty);
            }

            facultyTable.setItems(facultyList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load faculty data.");
        }
    }




    @FXML
    private void handleEditFaculty() {
        Faculty selectedFaculty = facultyTable.getSelectionModel().getSelectedItem();
        if (selectedFaculty != null) {
            showFacultyDialog(selectedFaculty, "Edit Faculty Member");
        } else {
            showAlert("No Selection", "Please select a faculty member to edit.");
        }
    }

    // Other handlers remain similar with Faculty type adjustments
    // ... [keep other handlers mostly same, adjusting for Faculty properties]

    private String generateDefaultEmail() {
        // Implement email generation logic based on faculty name
        return "new.faculty@uni.edu";
    }

    private void showFacultyDialog(Faculty faculty, String title) {
        // Implementation for add/edit dialog
        // Would need to create a form for all Faculty properties
    }

    private void showCourseAssignmentDialog(Faculty faculty) {
        // Implementation for course assignment
        // Should modify the courses list directly
    }

    // Updated search handler
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            facultyTable.setItems(facultyList);
        } else {
            ObservableList<Faculty> filteredList = facultyList.filtered(faculty ->
                    faculty.getFacultyName().toLowerCase().contains(searchTerm) ||
                            faculty.getEmail().toLowerCase().contains(searchTerm) ||
                            faculty.getDegree().toLowerCase().contains(searchTerm) ||
                            faculty.getResearch().toLowerCase().contains(searchTerm)
            );
            facultyTable.setItems(filteredList);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAssignCourses() {
    }



    public void handleViewProfile() {

    }
}