package com.ums.controller;

import com.ums.data.Subject;
import com.ums.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for Subject Management.
 * Students can only view subjects, while admins can edit.
 */
public class SubjectController {

    @FXML
    private TableView<Subject> subjectTable;

    @FXML
    private TableColumn<Subject, String> colCode;

    @FXML
    private TableColumn<Subject, String> colName;

    @FXML
    private TextField txtCode, txtName;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();

    private String userRole = "Student"; // Default role

    public void setUserRole(String role) {
        this.userRole = role;
        configureUIForRole();
    }

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        subjectTable.setItems(subjects);
        importSubjectsFromSQL();
        btnAdd.setOnAction(e -> addSubject());
        btnEdit.setOnAction(e -> editSubject()); // Bind edit button action
    }


    private void configureUIForRole() {
        if ("Student".equalsIgnoreCase(userRole)) {
            txtCode.setDisable(true);
            txtName.setDisable(true);
            btnAdd.setDisable(true);
        }
    }

    @FXML
    private void addSubject() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            System.out.println("Subject Code and Name cannot be empty.");
            return;
        }

        if (isDuplicate(code)) {
            System.out.println("Duplicate subject code.");
            return;
        }

        Subject newSubject = new Subject();
        subjects.add(newSubject); // Add new subject to the list

        txtCode.clear();
        txtName.clear();
    }


    private boolean isDuplicate(String code) {
        return subjects.stream().anyMatch(s -> s.getSubjectCode().equalsIgnoreCase(code));
    }

    private void importSubjectsFromSQL() {
        subjects.clear();

        String query = "SELECT * FROM Subjects";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setSubjectCode(rs.getString("Code"));
                subject.setSubjectName(rs.getString("Name"));


                subjects.add(subject);
            }

            subjectTable.setItems(subjects);

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }

    @FXML
    private void editSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();

        if (selectedSubject == null) {
            System.out.println("No subject selected.");
            return;
        }

        // Update the text fields with the selected subject’s values
        txtCode.setText(selectedSubject.getSubjectCode());
        txtName.setText(selectedSubject.getSubjectName());

        // Disable the add button to prevent duplicates
        btnAdd.setDisable(true);

        // When the user presses "Edit Subject", update the subject instead of adding a new one
        btnEdit.setOnAction(e -> {
            selectedSubject.setSubjectCode(txtCode.getText().trim());
            selectedSubject.setSubjectName(txtName.getText().trim());

            // Refresh the TableView
            subjectTable.refresh();

            // Clear input fields and re-enable the add button
            txtCode.clear();
            txtName.clear();
            btnAdd.setDisable(false);
        });
    }

}
