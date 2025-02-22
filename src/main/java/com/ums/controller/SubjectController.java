package com.ums.controller;

import com.ums.data.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.util.ArrayList;
import java.util.List;

public class SubjectController {


    @FXML
    private Label welcomeText;
    public TextField studentId;
    public TextField studentPwd;

    List<Subject> subjectList = new ArrayList<>();


    @FXML
    public void initialize() {
    }



}