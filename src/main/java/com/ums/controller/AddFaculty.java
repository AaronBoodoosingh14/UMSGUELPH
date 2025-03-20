package com.ums.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.Connection;
import com.ums.database.DatabaseManager;
import java.util.Optional;

public class AddFaculty extends EditFaculty {

    public AddFaculty() {
        super();
    }

    @FXML
    @Override
    public void initialize() {
        getFacultyData("", "");
    }

    @Override
    public void getFacultyData(String OGname, String permID) {
        super.btncancel.setOnAction(e -> {
            Stage stage = (Stage) btncancel.getScene().getWindow();
            stage.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancelled");
            alert.setHeaderText("Are you sure you want to cancel? New faculty will not be added");
            alert.setContentText("This action cannot be reversed");
            alert.showAndWait();
            Optional<ButtonType> result = alert.showAndWait();
        });

        super.btnsave.setOnAction(e -> {
            String name = nameField.getText();
            String ID = facultyIDField.getText();
            String degree = degreeField.getText();
            String research = researchInterestField.getText();
            String email = emailField.getText();
            String office = officeLocationField.getText();
            String courses = coursesField.getText();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to add faculty: " + name);
            alert.setContentText("This action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();
            String password = "default123";

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "INSERT INTO faculty_info (FacultyID, Name, Degree, ResearchInterest, Email, OfficeLocation, courses,password) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql)){

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, degree);
                    ps.setString(4, research);
                    ps.setString(5, email);
                    ps.setString(6, office);
                    ps.setString(7, courses);
                    ps.setString(8, password);

                    ps.executeUpdate();

                    Stage stage = (Stage) btncancel.getScene().getWindow();
                    stage.close();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                String sql2 = "INSERT INTO loginInfo (username, password, role) VALUES (?, ?, ?);";
                String role = "Admin";
                try(Connection connection = DatabaseManager.getConnection();
                    var login = connection.prepareStatement(sql2)){
                    login.setString(1,ID);
                    login.setString(2,password);
                    login.setString(3,role);

                    login.executeUpdate();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                Stage stage = (Stage) btncancel.getScene().getWindow();
                stage.close();
            }
        });
    }
}