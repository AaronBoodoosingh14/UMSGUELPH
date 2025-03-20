package com.ums.controller;

import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.*;


public class FacultyAdmin {


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
    private String title;


    public void initialize() {
        IDcolumn.setCellValueFactory(new PropertyValueFactory<>("FacultyID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        researchColumn.setCellValueFactory(new PropertyValueFactory<>("research"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeLocation"));
        coursesColumn.setCellValueFactory(new  PropertyValueFactory<>("courses"));
        searchField.setOnKeyReleased(e -> {handleSearch();});

        loadFacultyData();
    }


    @FXML
    private void handleEditFaculty(ActionEvent event) throws Exception {
        ArrayList<String> selection = new ArrayList<>(columnSelect());
        String facultyID = selection.get(0);
        String facultyName = selection.get(1);
        String degree = selection.get(2);
        String researchInterest = selection.get(3);
        String email = selection.get(4);
        String office = selection.get(5);
        String courses = selection.get(6);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/FacultyPopup/EditFaculty.fxml"));
                Parent root = loader.load();

                EditFaculty editFacultycontroller = loader.getController();

                editFacultycontroller.setFacultyData(facultyName,facultyID,degree,researchInterest,email,office,courses);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Faculty");
                stage.show();



            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    public void handleAddFaculty(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/FacultyPopup/AddFaculty.fxml"));
            Parent root = loader.load();

            AddFaculty addFacultyController = loader.getController();


            Stage stage = new Stage();
            stage.setTitle("Add Faculty");
            stage.setScene(new Scene(root));
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

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
            showAlert("Database Error");
        }
    }

    @FXML
    public void handleRefresh(){
        facultyTable.getItems().clear();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.15), event ->{loadFacultyData();}));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    private void  handleDeleteFaculty(ActionEvent event) throws Exception {
        ArrayList<String> selection = new ArrayList<String>(columnSelect());
        String FacultyID = selection.get(0);
        String FacultyName = selection.get(1);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete " + FacultyName + " from the database");
        alert.setContentText("This action cannot be reversed");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            String sql = "DELETE FROM faculty_info WHERE FacultyID = ?";
            String login = "DELETE FROM loginInfo WHERE username = ?";
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement stmt = connection.prepareStatement(login);
            PreparedStatement statement = connection.prepareStatement(sql);
            var ps = connection.prepareStatement(sql);
            var rs = connection.prepareStatement(login);
            rs.setString(1,FacultyID);
            ps.setString(1, FacultyID);
            rs.executeUpdate();
            ps.executeUpdate();
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Successful Delete");
            alert2.setHeaderText(FacultyName + " was deleted from the database");
            alert2.setContentText("");


        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Delete confirm");
            alert1.setHeaderText(FacultyName + " was not deleted");
            alert1.setContentText("Either the faculty was not found or the user cancelled");
            Optional<ButtonType> result1 = alert1.showAndWait();

        }

    }

    @FXML
    private ArrayList<String> columnSelect(){
        Faculty selectedFaculty = facultyTable.getSelectionModel().getSelectedItem();
        String FacultyID = selectedFaculty.getFacultyID();
        String Facultyname = selectedFaculty.getFacultyName();
        String degree = selectedFaculty.getDegree();
        String research = selectedFaculty.getResearch();
        String officeLocation = selectedFaculty.getCourses();
        String courses = selectedFaculty.getOfficeLocation();
        String email = selectedFaculty.getEmail();


        System.out.println(selectedFaculty);
        System.out.println(FacultyID);

        ArrayList<String> selection = new ArrayList<String>();

        selection.add(FacultyID);
        selection.add(Facultyname);
        selection.add(degree);
        selection.add(research);
        selection.add(email);
        selection.add(officeLocation);
        selection.add(courses);


        return selection;


    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            facultyTable.setItems(facultyList);
        } else {
            ObservableList<Faculty> filteredList = facultyList.filtered(faculty ->
                    faculty.getFacultyID().toLowerCase().contains(searchTerm)||
                    faculty.getFacultyName().toLowerCase().contains(searchTerm) ||
                            faculty.getEmail().toLowerCase().contains(searchTerm) ||
                            faculty.getDegree().toLowerCase().contains(searchTerm) ||
                            faculty.getResearch().toLowerCase().contains(searchTerm)
            );
            facultyTable.setItems(filteredList);
        }
    }

    private void showAlert(String title) {
        this.title = title;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ar");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load faculty data.");
        alert.showAndWait();
    }

    public void handleAssignCourses() {
    }



    public void handleViewProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dashboard = loader.getController();
        dashboard.setUserRole("Admin");

        ArrayList<String> selection = new ArrayList<String>(columnSelect());
        String facultyID = selection.getFirst();
        System.out.println(facultyID);
        dashboard.setUsername(facultyID);
        dashboard.loadModule("FacultyUser");
        dashboard.setDropDown(true);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
    }

}