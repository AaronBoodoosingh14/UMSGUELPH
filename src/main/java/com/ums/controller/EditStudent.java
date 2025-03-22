package com.ums.controller;

import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.function.Add;

import java.sql.Connection;
import java.util.Optional;

public class EditStudent extends Student {

    @FXML
    protected Button btncancel;
    @FXML
    protected Button btnsave;
    @FXML
    private TextField IDField;
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

    @FXML
    public void initialize(){
        setStudentData(getStudentId(),getName(),getAddress(),getTelephone(),getEmail(),getAcademicLevel(),getCurrentSemester(),getSubjectsRegistered(),getThesisTitle(),
                getProgress(),getTuition());
    }


    public void setStudentData(String studentID,String studentName,String Address,String telephone,String studentEmail,String
            Alevel,String semester,String subjectsReg,String thesis,String progress, String tuition ) {
        IDField.setText(studentID);
        nameField.setText(studentName);
        emailField.setText(Address);
        academicLevelField.setText(telephone);
        addressField.setText(studentEmail);
        telephoneField.setText(Alevel);
        currentSemesterField.setText(semester);
        subjectsRegisteredField.setText(subjectsReg);
        thesisTitleField.setText(thesis);
        progressField.setText(progress);
        tuitionField.setText(tuition);
        getStudentData(studentName,studentID);
    }
    public void getStudentData(String OGname, String permID){
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
            String ID = IDField.getText();
            String email = emailField.getText();
            String alevel = academicLevelField.getText();
            String address = addressField.getText();
            String telephone = telephoneField.getText();
            String semester = currentSemesterField.getText();
            String subjectsReg = subjectsRegisteredField.getText();
            String thesis = thesisTitleField.getText();
            String progress = progressField.getText();
            String tuition = tuitionField.getText();


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Are you sure you want to make changes to " + OGname);
            alert.setContentText("Action cannot be reversed");
            Optional<ButtonType> result = alert.showAndWait();


            if (result.isPresent() && result.get() == ButtonType.OK) {
                String sql = "UPDATE students_info SET StudentId = ?, Name = ?, Address = ?, Telephone = ?, Email = ?, AcademicLevel = ?, " +
                        "CurrentSemester = ?, SubjectsRegistered = ?, ThesisTitle=?, progress = ?, Tuition=? WHERE StudentId = ?;";
                String login = "UPDATE loginInfo SET username = ? WHERE username = ?;";
                try (Connection connection = DatabaseManager.getConnection();
                     var ps = connection.prepareStatement(sql);
                     var rs = connection.prepareStatement(login)) {

                    ps.setString(1, ID);
                    ps.setString(2, name);
                    ps.setString(3, address);
                    ps.setString(4, telephone);
                    ps.setString(5, email);
                    ps.setString(6, alevel);
                    ps.setString(7, semester);
                    ps.setString(8, subjectsReg);
                    ps.setString(9, thesis);
                    ps.setString(10, progress);
                    ps.setString(11, tuition);
                    ps.setString(12,permID);

                    rs.setString(1,ID);
                    rs.setString(2,permID);


                    rs.executeUpdate();
                    ps.executeUpdate();

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



