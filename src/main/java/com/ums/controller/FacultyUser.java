package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Faculty;
import com.ums.data.Student;
import com.ums.database.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;

public class FacultyUser extends Faculty {
    @FXML
    private Label Name;
    @FXML
    private Label FacultyID;
    @FXML
    private Label Degree;
    @FXML
    private Label Email;
    @FXML
    private Label Research;
    @FXML
    private Label Office;
    @FXML
    private Button btnpass;
    @FXML
    private Button btnpfp;
    @FXML
    private Button btnstudent;
    @FXML
    private ImageView profiledefault;

    private ArrayList<String> facultyInfo;

    public FacultyUser() {}

    @FXML
    public void initialize() {
        showPFP();

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


    public void SQLhandling(String ID) {
        System.out.println("Inside SQLhandling, received facultyID: " + ID);
        String query = "SELECT * FROM faculty_info WHERE FacultyID = ?";
        ArrayList<String> facultyInfo = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Faculty faculty = new Faculty();

                faculty.setFacultyID(rs.getString("FacultyID"));
                faculty.setFacultyName(rs.getString("Name"));
                faculty.setDegree(rs.getString("Degree"));
                faculty.setEmail(rs.getString("Email"));
                faculty.setResearch(rs.getString("ResearchInterest"));
                faculty.setOfficeLocation(rs.getString("OfficeLocation"));
                faculty.setCourses(rs.getString("Courses"));

                facultyInfo.add(faculty.getFacultyID());
                facultyInfo.add(faculty.getFacultyName());
                facultyInfo.add(faculty.getDegree());
                facultyInfo.add(faculty.getEmail());
                facultyInfo.add(faculty.getResearch());
                facultyInfo.add(faculty.getOfficeLocation());
                facultyInfo.add(faculty.getCourses());

                loadFaculty(facultyInfo);
            } else {
                System.out.println("No faculty found with FacultyID: " + ID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonVisable(boolean visible) {
        btnpass.setVisible(visible);
        btnpfp.setVisible(visible);
        btnstudent.setVisible(visible);
    }

    public void loadFaculty(ArrayList<String> facultyInfo) {
        this.facultyInfo = facultyInfo;

        Name.setText(facultyInfo.get(1));
        FacultyID.setText(facultyInfo.get(0));
        Degree.setText(facultyInfo.get(2));
        Email.setText(facultyInfo.get(3));
        Research.setText(facultyInfo.get(4));
        Office.setText(facultyInfo.get(5));
    }

    public void handleViewStudents(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Information");
        alert.setHeaderText("Students in "+ facultyInfo.get(6));

        TableView<Student> table = new TableView<>();


        TableColumn<Student, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        table.getColumns().addAll(nameColumn, idColumn);

        table.setPrefHeight(200);
        table.setPrefWidth(300);

        VBox vbox = new VBox(table);
        alert.getDialogPane().setContent(vbox);


        alert.showAndWait();



    }

    @FXML
    public void handleEditpass()throws Exception{
        String facultyID = facultyInfo.get(0);

        String query = "SELECT password FROM loginInfo WHERE username = ?";
        String new_query = "UPDATE loginInfo SET password = ? WHERE username = ?";
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        PreparedStatement ps =  conn.prepareStatement(new_query);
        stmt.setString(1,facultyID);
        ResultSet rs = stmt.executeQuery();

        Faculty faculty = new Faculty();
        if (rs.next()) {
            String password = rs.getString("password");
            faculty.setPassword(password);
        }
        String pass =  faculty.getPassword();


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change Password");
        alert.setHeaderText("Change password");

        TextField inputField = new TextField();
        inputField.setText(pass);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(10);
        grid.add(new Label("Password;"), 0, 0);
        grid.add(inputField, 1, 0);

        alert.getDialogPane().setContent(grid);


        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String userInput = inputField.getText();
            ps.setString(1, userInput);
            ps.setString(2, facultyID);
            ps.executeUpdate();
        }
    }

    @FXML
    public void handleViewCourses(ActionEvent event) {

        String courses = facultyInfo.get(6);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View Courses");
        alert.setHeaderText(courses);
        alert.setContentText("Active Courses");
        alert.showAndWait();
    }
    public void showPFP(){
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
    }

}
