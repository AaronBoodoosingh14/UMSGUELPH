package com.ums.controller;

import com.ums.data.Subject;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Controller for Subject Management.
 * Handles user interactions, adding subjects, and importing from Excel.
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
    private Button btnAdd, btnImportExcel; // Buttons for adding and importing subjects

    private final ObservableList<Subject> subjects = FXCollections.observableArrayList(); // Stores subjects

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

        // Handle "Import from Excel" button click
        btnImportExcel.setOnAction(e -> importSubjectsFromExcel());
    }

    /**
     * Adds a new subject to the TableView when "Add Subject" is clicked.
     */
    private void addSubject() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

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

    /**
     * Reads Subject data from an Excel file and adds it to the TableView.
     */
    private void importSubjectsFromExcel() {
        try {
            // Ensure Excel file is inside "resources/"
            InputStream file = getClass().getResourceAsStream("/UMS_Data.xlsx");

            if (file == null) {
                System.out.println("Excel file not found in resources!");
                return;
            }

            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0); // Read first sheet

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                Cell codeCell = row.getCell(0); // Column 1: Subject Code
                Cell nameCell = row.getCell(1); // Column 2: Subject Name

                if (codeCell != null && nameCell != null) {
                    String code = codeCell.getStringCellValue().trim();
                    String name = nameCell.getStringCellValue().trim();

                    if (!code.isEmpty() && !name.isEmpty() && !isDuplicate(code)) {
                        subjects.add(new Subject(code, name)); // Add subject to table
                    }
                }
            }
            workbook.close();
            System.out.println("Excel imported successfully!");

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Error reading Excel file.");
        }
    }


}
