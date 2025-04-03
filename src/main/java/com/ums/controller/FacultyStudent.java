package com.ums.controller;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.ums.UMSApplication;
import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyStudent {

    @FXML
    private FlowPane facultyContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private List<Faculty> facultyList = new ArrayList<>();
    private Scene currentScene;

    @FXML
    public void initialize() {
        Platform.runLater(this::applyThemeToCurrentScene);

        loadFacultyData();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFaculty(newValue);
        });

        searchButton.setOnAction(event -> {
            filterFaculty(searchField.getText());
        });
    }

    private void applyThemeToCurrentScene() {
        if (facultyContainer != null) {
            currentScene = facultyContainer.getScene();
            if (currentScene != null) {
                currentScene.getStylesheets().clear();
                if (UMSApplication.isDarkMode()) {
                    currentScene.getStylesheets().add(getClass().getResource("/com/ums/styles/primer-dark.css").toExternalForm());
                } else {
                    currentScene.getStylesheets().add(getClass().getResource("/com/ums/styles/primer-light.css").toExternalForm());
                }
            }
        }
    }

    private void filterFaculty(String searchText) {
        facultyContainer.getChildren().clear();

        if (searchText == null || searchText.trim().isEmpty()) {
            displayFacultyCards(facultyList);
        } else {
            String searchLower = searchText.toLowerCase();
            List<Faculty> filteredList = facultyList.stream()
                    .filter(faculty ->
                            faculty.getFacultyName().toLowerCase().contains(searchLower) ||
                                    (faculty.getResearch() != null && faculty.getResearch().toLowerCase().contains(searchLower)) ||
                                    (faculty.getDegree() != null && faculty.getDegree().toLowerCase().contains(searchLower)) ||
                                    (faculty.getCourses() != null && faculty.getCourses().toLowerCase().contains(searchLower))
                    )
                    .toList();

            displayFacultyCards(filteredList);
        }
    }

    public void loadFacultyData() {
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

            displayFacultyCards(facultyList);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private void displayFacultyCards(List<Faculty> faculties) {
        facultyContainer.getChildren().clear();

        for (Faculty faculty : faculties) {
            VBox card = createFacultyCard(faculty);
            facultyContainer.getChildren().add(card);
        }
    }

    private VBox createFacultyCard(Faculty faculty) {
        VBox card = new VBox(10);
        card.setPrefWidth(400);
        card.setPrefHeight(300);
        card.setPadding(new Insets(15));

        //more bs
        if (UMSApplication.isDarkMode()) {
            card.getStyleClass().add("faculty-card-dark");
            card.setStyle("-fx-background-color: #2d333b; -fx-border-color: #444c56; -fx-border-radius: 8; -fx-background-radius: 8;");
        } else {
            card.getStyleClass().add("faculty-card-light");
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d7de; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);");
        }

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView profileImageView = new ImageView();
        profileImageView.setFitHeight(80);
        profileImageView.setFitWidth(80);
        profileImageView.setPreserveRatio(true);

        profileImageView.getStyleClass().add("profile-image");
        // sum chatgpt bullshit
        if (UMSApplication.isDarkMode()) {
            profileImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");
        } else {
            profileImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0, 0, 1);");
        }

        loadProfilePicture(profileImageView, faculty.getFacultyID());

        VBox nameBox = new VBox(5);
        Label nameLabel = new Label(faculty.getFacultyName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.getStyleClass().add("faculty-name");

        Label idLabel = new Label("ID: " + faculty.getFacultyID());
        idLabel.setFont(Font.font("System", 12));
        idLabel.getStyleClass().add("faculty-id");

        if (UMSApplication.isDarkMode()) {
            nameLabel.setTextFill(Color.web("#f0f6fc"));
            idLabel.setTextFill(Color.web("#8b949e"));
        } else {
            nameLabel.setTextFill(Color.web("#24292f"));
            idLabel.setTextFill(Color.web("#57606a"));
        }

        nameBox.getChildren().addAll(nameLabel, idLabel);

        header.getChildren().addAll(profileImageView, nameBox);

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setPrefWidth(Double.MAX_VALUE);

        if (UMSApplication.isDarkMode()) {
            separator.setStyle("-fx-background-color: #444c56;");
        } else {
            separator.setStyle("-fx-background-color: #d0d7de;");
        }

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(8);
        detailsGrid.setPadding(new Insets(10, 0, 0, 0));

        String labelStyle = UMSApplication.isDarkMode() ?
                "faculty-label-dark" : "faculty-label-light";
        String valueStyle = UMSApplication.isDarkMode() ?
                "faculty-value-dark" : "faculty-value-light";

        Color labelColor = UMSApplication.isDarkMode() ?
                Color.web("#8b949e") : Color.web("#57606a");
        Color valueColor = UMSApplication.isDarkMode() ?
                Color.web("#f0f6fc") : Color.web("#24292f");

        Label emailLabel = createStyledLabel("Email:", labelColor, labelStyle);
        Label emailValueLabel = createStyledLabel(faculty.getEmail(), valueColor, valueStyle);
        detailsGrid.add(emailLabel, 0, 0);
        detailsGrid.add(emailValueLabel, 1, 0);

        Label degreeLabel = createStyledLabel("Degree:", labelColor, labelStyle);
        Label degreeValueLabel = createStyledLabel(faculty.getDegree(), valueColor, valueStyle);
        detailsGrid.add(degreeLabel, 0, 1);
        detailsGrid.add(degreeValueLabel, 1, 1);

        Label officeLabel = createStyledLabel("Office:", labelColor, labelStyle);
        Label officeValueLabel = createStyledLabel(faculty.getOfficeLocation(), valueColor, valueStyle);
        detailsGrid.add(officeLabel, 0, 2);
        detailsGrid.add(officeValueLabel, 1, 2);

        Label researchLabel = createStyledLabel("Research:", labelColor, labelStyle);
        Label researchValueLabel = createStyledLabel(faculty.getResearch(), valueColor, valueStyle);
        researchValueLabel.setWrapText(true);
        detailsGrid.add(researchLabel, 0, 3);
        detailsGrid.add(researchValueLabel, 1, 3);


        Label coursesLabel = createStyledLabel("Courses:", labelColor, labelStyle);
        Label coursesValueLabel = createStyledLabel(faculty.getCourses(), valueColor, valueStyle);
        coursesValueLabel.setWrapText(true);
        detailsGrid.add(coursesLabel, 0, 4);
        detailsGrid.add(coursesValueLabel, 1, 4);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(70);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        detailsGrid.getColumnConstraints().addAll(col1, col2);

        card.getChildren().addAll(header, separator, detailsGrid);
        return card;
    }

    private Label createStyledLabel(String text, Color textColor, String styleClass) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        label.setTextFill(textColor);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private void loadProfilePicture(ImageView profileImageView, String facultyID) {
        String query = "SELECT profilepic FROM faculty_info WHERE FACULTYID = ?";
        String path = "src/main/resources/tempPic/";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, facultyID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getString("profilepic") != null) {
                String finalPath = path + rs.getString("profilepic");
                File imgFile = new File(finalPath);
                if (imgFile.exists()) {
                    profileImageView.setImage(new Image(imgFile.toURI().toString()));
                } else {
                    String resourcePath = "/tempPic/" + facultyID + ".png";
                    if (getClass().getResource(resourcePath) != null) {
                        profileImageView.setImage(new Image(getClass().getResourceAsStream(resourcePath)));
                    } else {
                        profileImageView.setImage(new Image(getClass().getResourceAsStream("/pic.png")));
                    }
                }
            } else {
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/pic.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/defaultProfilePic.png")));
        }
    }
}