package com.ums.controller;

import com.google.api.gax.paging.Page;
import com.ums.UMSApplication;
import com.ums.database.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Uploadpic {

    private static final String PROJECTID = "premium-portal-453304-m8";
    private static final String BUCKET_NAME = "ums_profilepic";

    @FXML
    private Button btnsave;


    private String selectedFilePath;
    private String selectedFileName;

    private static String temp = "src/main/resources/tempPic";


    public void handleUpload(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedFilePath = file.getAbsolutePath();
            selectedFileName = file.getName();

        }
        String newFileName = generateName(selectedFileName);
        System.out.println(newFileName);
        SQLtrack(newFileName);
        Uploadimage(selectedFilePath, "FacultyProfilePic",newFileName);
        downloadPic(UMSApplication.getLoggedInUsername());
    }

    public void Uploadimage(String filepath, String folderName, String fileName) throws IOException {
        GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
        Storage storage = StorageOptions.getDefaultInstance().getService();

        String object = folderName + "/" + fileName;
        BlobId blobId = BlobId.of(BUCKET_NAME, object);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try {
            // Attempt to upload the file
            storage.create(blobInfo, Files.readAllBytes(Paths.get(filepath)));
            System.out.println("File uploaded successfully: " + object);
        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    private void handleclose(){
        Stage stage = (Stage) btnsave.getScene().getWindow();
        stage.close();
    }

    public String generateName(String file){
        String fileExtension = file.substring(file.lastIndexOf("."));
        String user =  UMSApplication.getLoggedInUsername();

        return user +  fileExtension;


    }

    private void SQLtrack(String filename) {
            String query = "UPDATE faculty_info SET profilepic = ? WHERE FacultyID = ?";
            String ID = UMSApplication.getLoggedInUsername();


            try(Connection conn = DatabaseManager.getConnection();
                var ps = conn.prepareStatement(query)
            ) {

                ps.setString(1,filename);
                ps.setString(2,ID);
                ps.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String downloadPic(String ID) {
        String sql = "SELECT profilepic FROM faculty_info WHERE FacultyID = ?";
        String profilepic = "";
        String finalpath = null;
        try (Connection conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                profilepic = rs.getString("profilepic");

            }

            if (profilepic == null) {
                return null;
            }
            else {
                System.out.println("THis is profile pic" + profilepic);
                String objectname = "FacultyProfilePic/" + profilepic.trim();
                System.out.println(objectname);
                System.out.println(temp);

                Storage storage = StorageOptions.getDefaultInstance().getService();
                Page<Blob> blobs = storage.list(BUCKET_NAME, Storage.BlobListOption.prefix("FacultyProfilePic/"));

                System.out.println("Listing objects in FacultyProfilePic/:");
                for (Blob b : blobs.iterateAll()) {
                    System.out.println("Found file: " + b.getName());
                }
                Blob blob = storage.get(BUCKET_NAME, objectname);
                if (blob == null) {
                    System.out.println("Error: Blob not found for path: " + objectname);
                    return null;
                }
                finalpath = Paths.get(temp, profilepic).toString();
                System.out.println((finalpath));

                Files.write(Paths.get(finalpath), blob.getContent());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return finalpath;

    }
}


