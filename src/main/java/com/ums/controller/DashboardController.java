package com.ums.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Button btnSubjects, btnCourses, btnStudents, btnFaculty, btnEvents, btnLogout;  // Setting up modules

    private String userRole = "Student"; // Default to Student if no role is set

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
        btnLogout.setOnAction(e -> logout());
    }

    /**
     * Loads the correct module FXML file based on user role.
     * @param moduleName The module name (Subjects, Courses, etc.).
     */
    private void loadModule(String moduleName) {
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
}



            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Error loading FXML: " + fxmlPath);
        }
    }



    private void logout() {
        try {
            System.out.println("Logging out...");

            // Load the Login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage using any existing UI component
            Stage stage = (Stage) btnLogout.getScene().getWindow();

            // Replace scene with Login screen
            stage.setScene(new Scene(loginRoot));
            stage.sizeToScene();
            stage.centerOnScreen(); // Center the login window
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Error loading Login screen.");
        }
    }



}
