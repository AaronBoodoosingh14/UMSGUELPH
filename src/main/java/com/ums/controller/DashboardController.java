package com.ums.controller;

import com.ums.UMSApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * DashboardController manages the main dashboard.
 * Everyone sees the same layout, but Admins/Students load different modules.
 */
public class DashboardController {

    @FXML
    private StackPane mainContent;  // Main screen

    @FXML
    private Button btnSubjects, btnCourses, btnStudents, btnFaculty, btnEvents, btnLogout, btnView;  // Setting up modules

    private String userRole;// Default to Student if no role is set
    private String username;


    /**
     * Sets the user role and applies role-based module selection.
     * @param role The user's role (Admin or Student).
     */

    public void setUserRole(String role) {
        this.userRole = role;
        System.out.println("Logged in as: " + role);
    }

    @FXML
    public void initialize() {
        btnSubjects.setOnAction(e -> loadModule("Subject"));
        btnCourses.setOnAction(e -> loadModule("Course"));
        btnStudents.setOnAction(e -> loadModule("Student"));
        btnFaculty.setOnAction(e -> loadModule("Faculty"));
        btnEvents.setOnAction(e -> loadModule("Events"));
        btnView.setOnAction(e -> loadModule("FacultyUser"));
        btnLogout.setOnAction(e -> logout());
    }

    public void setUsername(String username){
        this.username = username;
    }
    private String getUsername(){
        return username;
    }


    /**
     * Loads the correct module FXML file based on user role.
     * @param moduleName The module name (Subjects, Courses, etc.).
     */
    public void loadModule(String moduleName) {
        String fxmlPath;

        if ("Admin".equalsIgnoreCase(userRole)) {
            fxmlPath = "/com/ums/admin/" + moduleName + "Admin.fxml";
        } else {
            fxmlPath = "/com/ums/user/" + moduleName + "User.fxml";
        }

        try {
            System.out.println("Loading: " + fxmlPath);

            if (getClass().getResource(fxmlPath) == null) {
                System.out.println("ERROR: FXML file not found at: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
if("Events".equals(moduleName)) {

    EventController controller = loader.getController();
    controller.setUserRole(userRole);
}else if("Subject".equals(moduleName)) {
    // Pass user role to SubjectController
    SubjectController controller = loader.getController();
    controller.setUserRole(userRole);
}else if ("FacultyUser".equals(moduleName)) {
    String temp = getUsername();
    System.out.println(temp);
    FacultyUser controller = loader.getController();
    controller.SQLhandling(temp);

}



            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Error loading FXML: " + fxmlPath);
        }
    }



    private void logout() {
        System.out.println("Logging out...");
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();

        UMSApplication.restart();


    }





}
