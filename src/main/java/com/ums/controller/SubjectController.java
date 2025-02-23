package com.ums.controller; // Package location for SubjectController

import com.ums.data.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for Subject Management.
 * Handles user interactions, adding subjects, and displaying data in TableView.
 */
public class SubjectController {

    @FXML
    private TableView<Subject> subjectTable; // Table to display subjects

    @FXML
    private TableColumn<Subject, String> colCode; // Table column for Subject Code

    @FXML
    private TableColumn<Subject, String> colName; // Table column for Subject Name

    @FXML
    private TextField txtCode, txtName; // Input fields for subject details

    @FXML
    private Button btnAdd; // Button to add subjects (REMOVED btnLoadData)

    // ObservableList to store subjects (TableView updates automatically)
    private ObservableList<Subject> subjects = FXCollections.observableArrayList();

    /**
     * This method is automatically called when the FXML file is loaded.
     * It initializes the TableView and sets up button actions.
     */
    @FXML
    public void initialize() {
        // Link TableView columns to Subject properties using PropertyValueFactory
        colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        // Bind list to TableView
        subjectTable.setItems(subjects);

        // Handle "Add Subject" button click
        btnAdd.setOnAction(e -> addSubject());
    }

    /**
     * Adds a new subject to the TableView when "Add Subject" is clicked.
     */
    private void addSubject() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        // Check if input fields are not empty and subject is not a duplicate
        if (!code.isEmpty() && !name.isEmpty() && !isDuplicate(code)) {
            subjects.add(new Subject(code, name)); // Add to list
            txtCode.clear(); // Clear input fields
            txtName.clear();
        }
    }

    /**
     * Checks if a subject with the same code already exists in the list.
     * @param code Subject code to check for duplicates
     * @return true if the subject already exists, false otherwise
     */
    private boolean isDuplicate(String code) {
        for (Subject s : subjects) {
            if (s.getSubjectCode().equalsIgnoreCase(code)) {
                return true; // Duplicate found
            }
        }
        return false;
    }
}
