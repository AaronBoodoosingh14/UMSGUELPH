package com.ums.controller;

import com.ums.UMSApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class LoginController {
    @FXML
 //   private Label welcomeText;
    public TextField studentId;
    public TextField studentPwd;
    public  Button login;

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String id = studentId.getText();
        String pwd = studentPwd.getText();

        Parent root = FXMLLoader.load(UMSApplication.class.getResource("/com/ums/Dashboard.fxml"));

       Stage window = (Stage) login.getScene().getWindow();
       window.setScene(new Scene(root, 750,500));



        //welcomeText.setText(studentId.getText());

     //   welcomeText.setText("Welcome to JavaFX Application!");
    }
}