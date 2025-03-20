package com.ums.controller;

import com.ums.data.Faculty;
import com.ums.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    public FacultyUser() {}

    @FXML
    public void initialize() {
    }


    public void loadFacultyData(String ID) {
        String query = "SELECT * FROM faculty_info WHERE FacultyID = ?";
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

                Name.setText(faculty.getFacultyName());
                FacultyID.setText(faculty.getFacultyID());
                Degree.setText(faculty.getDegree());
                Email.setText(faculty.getEmail());
                Research.setText(faculty.getResearch());
                Office.setText(faculty.getOfficeLocation());
            } else {
                System.out.println("No faculty found with FacultyID: " + ID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}