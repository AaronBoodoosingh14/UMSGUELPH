package com.ums.controller; // Package for the DashboardController class

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import java.io.IOException;

/**
 * DashboardController manages the main dashboard view.
 * It handles navigation between different sections (Subjects, Courses, etc.)
 * by dynamically loading FXML files into the right panel.
 */
public class DashboardController {

    @FXML
    private StackPane mainContent; // The area where different views will be loaded dynamically

    @FXML
    private Button btnSubjects, btnCourses, btnStudents, btnFaculty, btnEvents, btnLogout; // Sidebar buttons

    /**
     * This method is automatically called when the FXML file is loaded.
     * It sets up button actions to switch between views dynamically.
     */
    @FXML
    public void initialize() {
        // When a button is clicked, it loads the corresponding FXML file into mainContent
        btnSubjects.setOnAction(e -> loadView("/com/ums/Subject.fxml"));  // Load Subjects view
        btnCourses.setOnAction(e -> loadView("/com/ums/Courses.fxml"));  // Load Courses view
        btnStudents.setOnAction(e -> loadView("/com/ums/Students.fxml"));  // Load Students view
        btnFaculty.setOnAction(e -> loadView("/com/ums/Faculty.fxml"));  // Load Faculty view
        btnEvents.setOnAction(e -> loadView("/com/ums/Event.fxml"));  // Load Events view
        btnLogout.setOnAction(e -> logout());  // Handle logout
    }

    /**
     * Loads an FXML file and displays it inside the `mainContent` area.
     * This method dynamically switches between different sections.
     *
     * @param fxmlPath The path to the FXML file to be loaded.
     */
    private void loadView(String fxmlPath) {
        try {
            System.out.println("Trying to load: " + fxmlPath); // Debugging: Print which file is being loaded

            // Load the FXML file and create the view
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));

            // Clear the previous content and add the new view
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace(); // Print error details if loading fails
            System.out.println("Error loading FXML: " + fxmlPath);
        }
    }

    /**
     * Handles the logout action.
     * This method should be updated to redirect users to the login screen.
     */
    private void logout() {
        System.out.println("Logging out...");
        // TODO: Implement logout logic (redirect to login screen)
    }

}

