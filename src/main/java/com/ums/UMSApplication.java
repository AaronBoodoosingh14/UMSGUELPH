package com.ums;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class








UMSApplication extends javafx.application.Application {
    private static String loggedInUsername;
    private static final String TEMP_DIR = "src/main/resources/tempPic";

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
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

}








