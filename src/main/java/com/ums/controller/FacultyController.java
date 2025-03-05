package com.ums.controller;

import com.ums.data.Faculty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Arrays;

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

    @FXML
    public void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        researchColumn.setCellValueFactory(new PropertyValueFactory<>("research"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeLocation"));

        // Custom cell factory for courses list
        coursesColumn.setCellValueFactory(cellData -> {
            Faculty faculty = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    String.join(", ", faculty.getCourses())
            );
        });

        facultyTable.setItems(facultyList);

        // Add sample data
        facultyList.add(new Faculty(
                "Dr. Sarah Johnson",
                "PhD in Computer Science",
                "Machine Learning",
                Arrays.asList("CS501", "AI201"),
                "s.johnson@uni.edu",
                "Building A, Room 305"
        ));
    }

    // Updated event handlers
    @FXML
    private void handleAddFaculty() {
        Faculty newFaculty = new Faculty();
        newFaculty.setFacultyName("New Faculty Member");
        newFaculty.setEmail(generateDefaultEmail());
        showFacultyDialog(newFaculty, "Add New Faculty Member");
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

    public void handleDeleteFaculty(ActionEvent actionEvent) {
    }

    public void handleViewProfile(ActionEvent actionEvent) {
    }
}