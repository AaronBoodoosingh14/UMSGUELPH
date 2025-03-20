package com.ums.controller.StudentControllers;

import com.ums.data.Student;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditStudent {

    @FXML
    private TextField studentIDField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField academicLevelField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField currentSemesterField;
    @FXML
    private TextField subjectsRegisteredField;
    @FXML
    private TextField thesisTitleField;
    @FXML
    private TextField progressField;
    @FXML
    private TextField tuitionField;

    /**
     * Populates the edit form with existing student data.
     */
    public void setStudentData(Student student) {
        studentIDField.setText(student.getStudentId());
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        academicLevelField.setText(student.getAcademicLevel());
        addressField.setText(student.getAddress());
        telephoneField.setText(student.getTelephone());
        currentSemesterField.setText(student.getCurrentSemester());
        subjectsRegisteredField.setText(String.join(", ", student.getSubjectsRegistered()));
        thesisTitleField.setText(student.getThesisTitle());
        progressField.setText(student.getProgress());
        tuitionField.setText(student.getTuition());
    }
}
