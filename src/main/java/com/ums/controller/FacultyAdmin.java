package com.ums.controller;

import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;


public class FacultyAdmin {

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML private TableView<Faculty> facultyTable;
    @FXML private TableColumn<Faculty, String> IDcolumn;
    @FXML private TableColumn<Faculty, String> nameColumn;
    @FXML private TableColumn<Faculty, String> emailColumn;
    @FXML private TableColumn<Faculty, String> degreeColumn;
    @FXML private TableColumn<Faculty, String> researchColumn;
    @FXML private TableColumn<Faculty, String> officeColumn;
    @FXML private TableColumn<Faculty, String> coursesColumn;
    @FXML private TextField searchField;

    private ObservableList<Faculty> facultyList = FXCollections.observableArrayList();


    public void initialize() {
        IDcolumn.setCellValueFactory(new PropertyValueFactory<>("FacultyID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        researchColumn.setCellValueFactory(new PropertyValueFactory<>("research"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeLocation"));
        coursesColumn.setCellValueFactory(new  PropertyValueFactory<>("courses"));
        loadFacultyData();
    }


    @FXML
    private void handleEditFaculty(ActionEvent event) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/FacultyPopup/EditFaculty.fxml"));
            Parent root = loader.load();

            EditFaculty controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Faculty");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void insertFacultyIntoDatabase(String input) throws Exception {

        String[] arr = input.split(",");
        System.out.println(Arrays.toString(arr));
        String FacultyID = arr[0];
        String Name = arr[1];
        String Degree = arr[2];
        String ResearchInterest = arr[3];
        String Email = arr[4];
        String OfficeLocation = arr[5];



        String insertStatement = "INSERT INTO faculty_info (FacultyID,Name,Degree,ResearchInterest,Email,OfficeLocation) VALUES (?, ?, ?,?,?,?)";
        Connection connection  = DatabaseManager.getConnection();
        var ps  = connection.prepareStatement(insertStatement);



        ps.setString(1,FacultyID);
        ps.setString(2,Name);
        ps.setString(3,Degree);
        ps.setString(4,ResearchInterest);
        ps.setString(5,Email);
        ps.setString(6,OfficeLocation);


        ps.executeUpdate();

        facultyTable.refresh();

    }


    private void loadFacultyData() {
        facultyList.clear();

        String query = "SELECT * FROM faculty_info";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Faculty faculty = new Faculty();
                faculty.setFacultyID(rs.getString("FacultyID"));
                faculty.setFacultyName(rs.getString("Name"));
                faculty.setEmail(rs.getString("Email"));
                faculty.setDegree(rs.getString("Degree"));
                faculty.setResearch(rs.getString("ResearchInterest"));
                faculty.setOfficeLocation(rs.getString("OfficeLocation"));
                faculty.setCourses(rs.getString("courses"));


                facultyList.add(faculty);
            }

            System.out.println(facultyList);
            facultyTable.setItems(facultyList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load faculty data.");
        }
    }

    @FXML
    private void handleRefresh(){
        facultyTable.getItems().clear();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), event ->{loadFacultyData();}));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    private void columnSelect(){
        Faculty selectedFaculty = facultyTable.getSelectionModel().getSelectedItem();

        System.out.println(selectedFaculty);


    }





    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            facultyTable.setItems(facultyList);
        } else {
            ObservableList<Faculty> filteredList = facultyList.filtered(faculty ->
                    faculty.getFacultyName().toLowerCase().contains(searchTerm) ||
                            faculty.getEmail().toLowerCase().contains(searchTerm) ||
                            faculty.getDegree().toLowerCase().contains(searchTerm) ||
                            faculty.getResearch().toLowerCase().contains(searchTerm)
            );
            facultyTable.setItems(filteredList);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAssignCourses() {
    }



    public void handleViewProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController dashboard = loader.getController();
        dashboard.loadModule("Faculty");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}