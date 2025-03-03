package com.ums.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the login screen.
 * Reads user credentials from "Students " and "Faculties " sheets in Excel.
 */
public class LoginController {

    @FXML
    private TextField txtUsername, txtPassword;

    @FXML
    private Button btnLogin;

    private final Map<String, String[]> userDatabase = new HashMap<>();

    /**
     * Initializes the login system by reading credentials from Excel.
     */
    @FXML
    public void initialize() {
        loadLoginsFromExcel();  // ✅ Load credentials from Excel

        btnLogin.setOnAction(e -> handleLogin());

        // Enable Enter key login
        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) txtPassword.requestFocus();
        });

        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) handleLogin();
        });
    }

    /**
     * Reads login credentials from "Students " and "Faculties " sheets in Excel.
     * Role is determined based on the sheet it comes from.
     */
    private void loadLoginsFromExcel() {
        try {
            InputStream file = getClass().getClassLoader().getResourceAsStream("UMS_Data.xlsx");

            if (file == null) {
                System.out.println("❌ Excel file not found! Check if 'UMS_Data.xlsx' is in 'src/main/resources/'");
                return;
            }

            Workbook workbook = new XSSFWorkbook(file);

            // ✅ Read "Students " sheet (Assigns "Student" role)
            readLoginsFromSheet(workbook, "Students ", "Student", 0, 11);

            // ✅ Read "Faculties " sheet (Assigns "Admin" role)
            readLoginsFromSheet(workbook, "Faculties ", "Admin", 0, 7);

            workbook.close();
            System.out.println("✅ Logins loaded successfully from Excel!");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error reading Excel file.");
        }
    }

    /**
     * Reads a specific sheet and extracts login credentials.
     * @param workbook The Excel workbook.
     * @param sheetName The name of the sheet (Students or Faculties).
     * @param role The assigned role (Student or Admin).
     * @param usernameCol The column index for Username.
     * @param passwordCol The column index for Password.
     */
    private void readLoginsFromSheet(Workbook workbook, String sheetName, String role, int usernameCol, int passwordCol) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            System.out.println("❌ Sheet '" + sheetName + "' not found!");
            return;
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            Cell usernameCell = row.getCell(usernameCol);
            Cell passwordCell = row.getCell(passwordCol);

            if (usernameCell != null && passwordCell != null) {
                String username = usernameCell.getStringCellValue().trim();
                String password = passwordCell.getStringCellValue().trim();
                userDatabase.put(username, new String[]{password, role});
            }
        }
    }

    /**
     * Handles the login attempt by validating credentials.
     */
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (userDatabase.containsKey(username)) {
            String[] userInfo = userDatabase.get(username);
            String storedPassword = userInfo[0];
            String role = userInfo[1];

            if (password.equals(storedPassword)) {
                loadDashboard(role); // Redirect based on role
            } else {
                showAlert("Login Failed", "Incorrect password.");
            }
        } else {
            showAlert("Login Failed", "User not found.");
        }
    }

    /**
     * Loads the dashboard and passes the user's role.
     */
    private void loadDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserRole(role);

            Stage window = (Stage) btnLogin.getScene().getWindow();
            window.setScene(new Scene(root));
            window.setMaximized(true);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard.");
        }
    }

    /**
     * Displays an alert message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
