package com.ums;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.Formatter;

public class








 UMSApplication extends javafx.application.Application {
    private static String loggedInUsername;

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
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








