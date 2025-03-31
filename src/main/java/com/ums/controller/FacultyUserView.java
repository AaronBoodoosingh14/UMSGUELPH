package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Faculty;
import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FacultyUserView {
    @FXML
    private ImageView profiledefault;

    @FXML
    private Label SID;

    @FXML
    private Label address;

    @FXML
    private Label telephone;

    @FXML
    private Label tuition;

    @FXML
    private Label email;

    @FXML
    private Label semester;

    @FXML
    private Label academic;

    @FXML
    private Label progress;

    @FXML
    private Label Name;

    private final String ID = UMSApplication.getLoggedInUsername();

    @FXML
    public void initialize() {
        //showPFP();

    }

    public void handleProfile(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/uploadpic.fxml"));
        Parent root = loader.load();

        Uploadpic uploadpic = loader.getController();


        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Upload Profile");
        stage.show();
    }
    public void SQLhandling() {
        System.out.println("Inside SQLhandling, received facultyID: " + ID);
        String query = "SELECT * FROM students_info WHERE StudentId=?";
        ArrayList<String> studentinfo = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();

                student.setStudentId(rs.getString("StudentId"));
                student.setName(rs.getString("Name"));
                student.setAddress(rs.getString("Address"));
                student.setTelephone(rs.getString("Telephone"));
                student.setEmail(rs.getString("Email"));
                student.setAcademicLevel(rs.getString("AcademicLevel"));
                student.setCurrentSemester((rs.getString("CurrentSemester")));
                student.setSubjectsRegistered(rs.getString("SubjectsRegistered"));
                student.setProgress(rs.getString("Progress"));

                studentinfo.add(student.getStudentId());
                studentinfo.add(student.getName());
                studentinfo.add(student.getAddress());
                studentinfo.add(student.getTelephone());
                studentinfo.add(student.getEmail());
                studentinfo.add(student.getAcademicLevel());
                studentinfo.add(student.getCurrentSemester());
                studentinfo.add(student.getSubjectsRegistered());
                studentinfo.add(student.getProgress());


                loadstudent(studentinfo);

                System.out.println("No student found with this ID: " + ID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadstudent(ArrayList<String> studentinfo) {
        SID.setText(studentinfo.get(0));
        address.setText(studentinfo.get(2));
        telephone.setText(studentinfo.get(3));
        email.setText(studentinfo.get(4));
        semester.setText(studentinfo.get(6));
        academic.setText(studentinfo.get(5));
        progress.setText(studentinfo.get(8));

        if (studentinfo.get(5).equals("Undergraduate")) {
            tuition.setText("$4000");
        }


        Name.setText(studentinfo.get(1));



    }
    /*public void showPFP(){
        Uploadpic uploadpic = new Uploadpic();
        String path = uploadpic.downloadPic(UMSApplication.getLoggedInUsername());
        System.out.println(path);

        if (path != null) {
            System.out.println(path);

            // Assuming path is a local file path
            try {
                File file = new File(path);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());  // Convert file path to URI
                    profiledefault.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
        else {
            Image image = new Image ("pic.png");
            profiledefault.setImage(image);
        }
    } */

}
