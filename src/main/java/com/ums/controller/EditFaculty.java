package com.ums.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditFaculty {
    @FXML
    private TextField nameField;
    @FXML
    private TextField FacultyIDField;
    @FXML
    private TextField degreeField;
    @FXML
    private TextField ResearchInterestField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField coursesField;

    public void setFacultyData(String name, String FacultyID, String degree, String ResearchInterest, String email, String courses) {
        nameField.setText(name);
        FacultyIDField.setText(FacultyID);
        degreeField.setText(degree);
        ResearchInterestField.setText(ResearchInterest);
        emailField.setText(email);
        coursesField.setText(courses);

    }
}
