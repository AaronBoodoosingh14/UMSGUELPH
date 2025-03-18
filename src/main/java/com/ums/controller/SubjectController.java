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

    @FXML
    private Button btnDelete;

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
        btnDelete.setOnAction(e -> deleteSubject());
    }


    private void configureUIForRole() {
        if ("Student".equalsIgnoreCase(userRole)) {
            txtCode.setDisable(true);
            txtCode.setVisible(false);

            txtName.setDisable(true);
            txtName.setVisible(false);

            btnAdd.setDisable(true);
            btnAdd.setVisible(false);

            btnEdit.setDisable(true);
            btnEdit.setVisible(false);

            btnDelete.setDisable(true);
            btnDelete.setVisible(false);

        }
    }

    @FXML
    private void addSubject() {

        String insertStatement = "INSERT INTO subjects VALUES(?,?)";
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

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertStatement)) {

            stmt.setString(1, code);
            stmt.setString(2, name);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Subject added successfully.");

                // Add subject to local list
                Subject subject = new Subject();
                subject.setSubjectCode(code);
                subject.setSubjectName(name);
                subjects.add(subject);

            }

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        txtCode.clear();
        txtName.clear();
    }

    @FXML
    private void deleteSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();

        if (selectedSubject == null) {
            System.out.println("No subject selected.");
            return;
        }

        String code = selectedSubject.getSubjectCode();

        // Confirm before deleting
        System.out.println("Deleting subject: " + code);

        String deleteStatement = "DELETE FROM subjects WHERE Code = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteStatement)) {

            stmt.setString(1, code);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Subject deleted successfully.");

                // Remove from observable list
                subjects.remove(selectedSubject);
                subjectTable.refresh();
            } else {
                System.out.println("Subject not found in the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    private boolean isDuplicate(String code) {
        return subjects.stream().anyMatch(s -> s.getSubjectCode().equalsIgnoreCase(code));
    }

    private void importSubjectsFromSQL() {
        subjects.clear();

        String query = "SELECT * FROM subjects";

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
            String newCode = txtCode.getText().trim();
            String newName = txtName.getText().trim();


            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE subjects SET Code = ?, Name = ? WHERE Code = ?")) {

                stmt.setString(1, newCode);
                stmt.setString(2, newName);
                stmt.setString(3, selectedSubject.getSubjectCode());

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Subject updated successfully.");

                    // Update the list directly
                    selectedSubject.setSubjectCode(newCode);
                    selectedSubject.setSubjectName(newName);

                    // Refresh the TableView
                    subjectTable.refresh();
                } else {
                    System.out.println("No subject found to update.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }

            // Clear input fields and re-enable the add button
            txtCode.clear();
            txtName.clear();
            btnAdd.setDisable(false);
        });
    }

}
