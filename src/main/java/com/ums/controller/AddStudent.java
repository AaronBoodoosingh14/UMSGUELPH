package com.ums.controller;

import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.util.Optional;

import javafx.scene.control.Alert;

public class AddStudent extends EditFaculty {
    @FXML
    private TextField nameField;
    @FXML
    private TextField IDField;
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
            alert.setHeaderText("Are you sure you want to cancel? New Student will not be added");
            alert.setContentText("This action cannot be reversed");
            alert.showAndWait();
            Optional<ButtonType> result = alert.showAndWait();
        });

        super.btnsave.setOnAction(e -> {
            String name = nameField.getText();
            String ID = IDField.getText();
            String email = emailField.getText();
            String telephone =  telephoneField.getText();
            String currentSemester = currentSemesterField.getText();
            String Alevel = academicLevelField.getText();
            String address = addressField.getText();
            String subject = subjectsRegisteredField.getText();
            String thesisTitle = thesisTitleField.getText();
            String progress = progressField.getText();
            String tuition = tuitionField.getText();


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to add student: " + name);
            alert.setContentText("This action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();
            String password = "default123";

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "INSERT INTO students_info (StudentId, Name, Address, Telephone, Email, AcademicLevel, CurrentSemester, " +
                        "SubjectsRegistered, ThesisTitle, Progress, Password, Tuition) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql)){

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, address);
                    ps.setString(4, telephone);
                    ps.setString(5, email);
                    ps.setString(6, Alevel);
                    ps.setString(7, currentSemester);
                    ps.setString(8, subject);
                    ps.setString(9, thesisTitle);
                    ps.setString(10, progress);
                    ps.setString(11, password);
                    ps.setString(12, tuition);

                    ps.executeUpdate();

                    Stage stage = (Stage) btncancel.getScene().getWindow();
                    stage.close();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                String sql2 = "INSERT INTO loginInfo (username, password, role) VALUES (?, ?, ?);";
                String role = "Student";
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
