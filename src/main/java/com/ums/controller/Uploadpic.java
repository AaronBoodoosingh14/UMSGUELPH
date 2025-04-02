package com.ums.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class Uploadpic {

    private static final String PROJECTID = "premium-portal-453304-m8";
    private static final String BUCKET_NAME = "ums_profilepic";

    @FXML
    private Button btnsave;

    private String selectedFilePath;
    private String selectedFileName;
    private String userRole;

    public Uploadpic() {
        this.userRole = UMSApplication.getLoggedInUserRole();
        System.out.println(" User role retrieved in Uploadpic: " + userRole);
    }

    private static String temp = "src/main/resources/tempPic/";

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
        System.out.println("Before SQLtrack, userRole is: " + userRole);
        SQLtrack(newFileName);
        Uploadimage(selectedFilePath, "FacultyProfilePic", newFileName);
        downloadPic();
    }

    public void Uploadimage(String filepath, String folderName, String fileName) throws IOException {
        GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
        Storage storage = StorageOptions.getDefaultInstance().getService();

        String object = folderName + "/" + fileName;
        BlobId blobId = BlobId.of(BUCKET_NAME, object);

        if (storage.get(blobId) != null) {
            storage.delete(blobId);
            System.out.println("deleted goonar file " + object);
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.create(blobInfo, Files.readAllBytes(Paths.get(filepath)));
            System.out.println("file uploaded  " + object);
        } catch (Exception e) {
            System.err.println("error uploading file" + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleclose() {
        Stage stage = (Stage) btnsave.getScene().getWindow();
        stage.close();
    }

    public String generateName(String file) {
        String fileExtension = file.substring(file.lastIndexOf("."));
        String user = UMSApplication.getLoggedInUsername();
        return user + fileExtension;
    }

    private void SQLtrack(String filename) {
        System.out.println(userRole);
        if (Objects.equals(userRole, "Admin")) {
            String query = "UPDATE faculty_info SET profilepic = ? WHERE FacultyID = ?";
            String ID = UMSApplication.getLoggedInUsername();

            try (Connection conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(query)) {
                ps.setString(1, filename);
                ps.setString(2, ID);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
       else if (Objects.equals(userRole, "Student")) {
            String query = "UPDATE students_info SET profilepic = ? WHERE StudentId = ?";
            String ID = UMSApplication.getLoggedInUsername();

            try (Connection conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(query)) {
                ps.setString(1, filename);
                ps.setString(2, ID);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public void downloadPic() {
        System.out.println("downloadPic() started");
        String folderName = "FacultyProfilePic/";
        String destinationFolder = "src/main/resources/tempPic/";

        GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            System.err.println("error loading creds: " + e.getMessage());
            return;
        }

        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Page<Blob> blobs = storage.list(BUCKET_NAME, Storage.BlobListOption.prefix(folderName));

        for (Blob blob : blobs.iterateAll()) {
            String fileName = blob.getName().substring(folderName.length());
            File file = new File(destinationFolder + fileName);

            try {
                Files.write(Paths.get(file.getAbsolutePath()), blob.getContent());
                System.out.println("download: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("failed to download " + fileName + ": " + e.getMessage());
            }
        }
    }

}
