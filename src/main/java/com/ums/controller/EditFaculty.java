package com.ums.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditFaculty {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    public void setFacultyData(String name, String email) {
        nameField.setText(name);
        emailField.setText(email);
    }
}
