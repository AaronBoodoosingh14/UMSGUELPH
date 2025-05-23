package com.ums.controller;

import com.ums.UMSApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * DashboardController manages the main dashboard.
 * Everyone sees the same layout, but Admins/Students load different modules.
 */
public class DashboardController {

    public ImageView logo;
    @FXML
    private StackPane mainContent;  // Main screen

    @FXML
    private Button btnSubjects, btnCourses, btnStudents, btnFaculty, btnEvents, btnLogout, btnView;  // Setting up modules

    private String userRole;
    private String username;
    private String permUser;
    private boolean visable = false;
    public String flocation;

    @FXML
    private TitledPane DropDown;

    /**
     * Sets the user role and applies role-based module selection.
     * @param role The user's role (Admin or Student).
     */
    public void setUserRole(String role) {
        this.userRole = role;
        System.out.println("Logged in as: " + role);
        if ("Student".equalsIgnoreCase(role)) {
            btnStudents.setVisible(false);
            btnStudents.setManaged(false); // Remove from layout space too
        }
    }

    public String getUserRole(){
        return userRole;
    }



    @FXML
    public void initialize() {

        btnSubjects.setOnAction(e -> loadModule("Subject"));
        btnCourses.setOnAction(e -> loadModule("Course"));
        btnStudents.setOnAction(e -> loadModule("Student"));
        btnFaculty.setOnAction(e -> loadModule("Faculty"));
        btnEvents.setOnAction(e -> loadModule("Events"));
        btnView.setOnAction(e -> {
            System.out.println("Button clicked, permUser is: " + permUser);
            username = UMSApplication.getLoggedInUsername();
            System.out.println("After assignment, username is: " + username);
            visable = true;
            loadModule("FacultyUser");
        });

        btnLogout.setOnAction(e -> logout());
    }

    public void setDropDown(boolean text) {
        DropDown.setExpanded(text);
    }

    private void setVisible(boolean visible) {
        this.visable = visible;
    }

    private boolean getVisible() {
        return visable;
    }

    public void setPermUser(String permUser) {
        System.out.println("Setting permUser to: " + permUser);
        this.permUser = permUser;
        System.out.println("After setting, permUser is: " + this.permUser);
    }

    public String getPermUser() {
        return permUser;
    }

    public void setUsername(String username){
        this.username = username;
    }

    private String getUsername(){
        return username;
    }

    /**
     * Loads the correct module FXML file based on module name.
     * @param moduleName The module name (Subjects, Courses, etc.).
     */
    public void loadModule(String moduleName) {
        String fxmlPath;

        if ("FacultyStudent".equals(moduleName)) {
            fxmlPath = "/com/ums/user/FacultyUser.fxml";
        }
        else if ("FacultyAdmin".equals(moduleName)) {
            fxmlPath = "/com/ums/admin/FacultyAdmin.fxml"; // Direct path for FacultyAdmin
        }
        else if ("Admin".equalsIgnoreCase(userRole)) {
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

            if ("Events".equals(moduleName)) {
                EventController controller = loader.getController();
                controller.setUserRole(userRole);
            } else if ("Subject".equals(moduleName)) {
                // Pass user role to SubjectController
                SubjectController controller = loader.getController();
                controller.setUserRole(userRole);
            } else if ("FacultyUser".equals(moduleName)) {
                System.out.println("Loading FacultyUser, current username: " + username);
                System.out.println("Current permUser: " + permUser);
                String temp = getUsername();
                System.out.println("getUsername() returned: " + temp);
                if (Objects.equals(userRole, "Admin")) {
                    FacultyUser controller = loader.getController();
                    controller.SQLhandling(temp);
                    controller.showPFP(temp);
                    controller.buttonVisable(visable);
                } else {
                    FacultyUserView controller = loader.getController();
                    controller.SQLhandling();
                }
            } else if ("FacultyStudent".equals(moduleName)) {
                // No additional setup needed, FacultyStudent initializes itself
                System.out.println("FacultyStudent module loaded");
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

    public void loadimage(String color){
        Image image = new Image(color);
        logo.setImage(image);
    }

    public static void updateDashboardLogo(String logoPath) {
        DashboardController currentController = UMSApplication.getCurrentDashboardController();
        if (currentController != null) {
            currentController.loadimage(logoPath);
        }
    }
}