package com.ums.controller;

import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class EditFaculty extends Faculty {

    @FXML
    protected Button btnsave;
    @FXML
    protected Button btncancel;
    @FXML
    protected TextField nameField;
    @FXML
    protected TextField facultyIDField;
    @FXML
    protected TextField degreeField;
    @FXML
    protected TextField researchInterestField;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField officeLocationField;
    @FXML
    protected TextField coursesField;


    public EditFaculty() {
    }

    // have to initialize after the file is loaded?
    @FXML
    public void initialize() {
        setFacultyData(getFacultyName(), getFacultyID(), getDegree(), getResearch(), getEmail(), getCourses(), getOfficeLocation());
    }

    public void setFacultyData(String facultyname, String facultyID, String degree, String researchInterest, String email, String courses, String office) {
        Platform.runLater(() -> {
            nameField.setText(facultyname);
            facultyIDField.setText(facultyID);
            degreeField.setText(degree);
            researchInterestField.setText(researchInterest);
            emailField.setText(email);
            officeLocationField.setText(office);
            coursesField.setText(courses);
            getFacultyData(facultyname,facultyID);
        });
    }

    public void getFacultyData(String OGname, String permID) {
        btncancel.setOnAction(e -> {
            Stage stage = (Stage) btncancel.getScene().getWindow();
            stage.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancelled");
            alert.setHeaderText("Are you sure you want to cancel changes will not be saved");
            alert.setContentText("This action cannot be reversed");
            alert.showAndWait();
            Optional<ButtonType> result = alert.showAndWait();
        });

        btnsave.setOnAction(e -> {
            String name = nameField.getText();
            String ID = facultyIDField.getText();
            String degree = degreeField.getText();
            String research = researchInterestField.getText();
            String email = emailField.getText();
            String office = officeLocationField.getText();
            String courses = coursesField.getText();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to make changes to " + OGname);
            alert.setContentText("Action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();


            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "UPDATE faculty_info SET FacultyID = ?, Name = ?, Degree = ?, ResearchInterest = ?, Email = ?, OfficeLocation = ?, courses = ? WHERE FacultyID = ?;";
                String login = "UPDATE loginInfo SET username = ? WHERE username = ?;";
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql);
                     var rs = connection.prepareStatement(login)) {

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, degree);
                    ps.setString(4, research);
                    ps.setString(5, email);
                    ps.setString(6, office);
                    ps.setString(7, courses);
                    ps.setString(8,permID);

                    rs.setString(1,ID);
                    rs.setString(2,permID);

                    ps.executeUpdate();
                    rs.executeUpdate();

                    Stage stage = (Stage) btncancel.getScene().getWindow();
                    stage.close();




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









