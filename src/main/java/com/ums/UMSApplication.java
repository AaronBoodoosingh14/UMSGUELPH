package com.ums;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.ums.controller.DashboardController;
import com.ums.controller.Uploadpic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class UMSApplication extends javafx.application.Application {

    private static String loggedInUsername;
    private static String loggedInUserRole;
    private static boolean darkMode = true;

    private static final String TEMP_DIR = "src/main/resources/tempPic";

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUserRole(String role) {
        loggedInUserRole = role;
    }
    public static String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean isDarkMode) {
        darkMode = isDarkMode;
    }


    private static DashboardController currentDashboardController;

    public static void setCurrentDashboardController(DashboardController controller) {
        currentDashboardController = controller;
    }

    public static DashboardController getCurrentDashboardController() {
        return currentDashboardController;
    }

    public static void clearTempDirectory() {
        Path tempPath = Paths.get(TEMP_DIR);
        if (Files.exists(tempPath)) {
            try {
                Files.walkFileTree(tempPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Temp directory cleared successfully.");
            } catch (IOException e) {
                System.err.println("Failed to clear temp directory: " + e.getMessage());
            }
        } else {
            System.out.println("Temp directory does not exist, skipping cleanup.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (darkMode) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(UMSApplication.class.getResource("/com/ums/Login.fxml"));
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/createCategory.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Image Icon = new Image(getClass().getResourceAsStream("/image.png"));
        stage.getIcons().add(Icon);
        stage.setTitle("UMS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        clearTempDirectory();
        loadprofilepic();
        launch();
    }

    public static void restart(){
        Platform.runLater(()->{
            try{
                new UMSApplication().start(new Stage());
            }
            catch(Exception e){
                System.out.println("error");
            }
        });
    }

    public static void loadprofilepic(){
        Uploadpic uploadpic = new Uploadpic();
        uploadpic.downloadPic();
    }
}