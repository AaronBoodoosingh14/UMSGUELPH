package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Event;

import com.ums.data.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.lang.String;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;


public class EventController {
    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> eventCode;
    @FXML
    private TableColumn<Event, String> eventName;
    @FXML
    private TableColumn<Event, String> eventDescription;
    @FXML
    private TableColumn<Event, String> eventLocation;
    @FXML
    private TableColumn<Event, String> eventDateAndTime;
    @FXML
    private TableColumn<Event, String> eventCapacity;
    @FXML
    private TableColumn<Event, String> eventCost;
    @FXML
    private TableColumn<Event, String> eventHeaderImage;
    @FXML
    private TableColumn<Event, String> eventRegisteredStudents;

    @FXML
    private TextField txtEventCode, txtEventName,txtEventDescription, txtEventLocation,  txtEventDateAndTime,txtEventCapacity,txtEventCost,txtEventHeaderImage,txtEventRegisteredStudents;


    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    // ObservableList to store subjects (TableView updates automatically)
    private ObservableList<Event> events = FXCollections.observableArrayList();

    private String userRole = "Student"; // Default role

    public void setUserRole(String role) {
        this.userRole = role;
    }

    @FXML
    public void initialize() {

        eventCode.setCellValueFactory(new PropertyValueFactory<>("eventCode"));
        eventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventDateAndTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        eventCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        eventHeaderImage.setCellValueFactory(new PropertyValueFactory<>("headerImage"));
        eventRegisteredStudents.setCellValueFactory(new PropertyValueFactory<>("registeredStudents"));

        //events.add(e); //later call excelUtil.getEvents and add to here (events.add (e))
        // Bind list to TableView
        eventTable.setItems(events);
        importEventsFromExcel();
        if(null != btnAdd ) {
            btnAdd.setOnAction(eventAction -> addEvent());
        }
        if(null != btnDelete ) {
            btnDelete.setOnAction(eventAction -> removeRow());
        }


    }


    /**
     * This method  will  validate the user input for duplicate code and
     * also for required fields ( must fill all fields), Also save the event.
     */
    private void addEvent() {
        String eventCode = txtEventCode.getText().trim();
        String eventName = txtEventName.getText().trim();
        String description = txtEventDescription.getText().trim();
        String location = txtEventLocation.getText().trim();
        String dateTime = txtEventDateAndTime.getText().trim();
        String capacity = txtEventCapacity.getText().trim();
        String cost = txtEventCost.getText().trim();
        String headerImage = txtEventHeaderImage.getText().trim();
        String registeredStudents = txtEventRegisteredStudents.getText().trim();
        if ( isDuplicate(eventCode)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Event code already exists, please enter new event code");
            errorAlert.showAndWait();

        }
        else if (eventCode.isEmpty() || eventName.isEmpty() || location.isEmpty() || dateTime.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Event code and Event name and Location and Date/Time, these fields are required");
            errorAlert.showAndWait();

        }
        else {
            events.add(new Event(eventCode, eventName,description,location,
                    dateTime, capacity, cost, headerImage, registeredStudents));
            txtEventCode.clear();
            txtEventName.clear();
            txtEventDescription.clear();
            txtEventLocation.clear();
            txtEventDateAndTime.clear();
            txtEventCapacity.clear();
            txtEventCost.clear();
            txtEventHeaderImage.clear();
            txtEventRegisteredStudents.clear();
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
            errorAlert.setHeaderText("Save");
            errorAlert.setContentText("Event saved successfully");
            errorAlert.showAndWait();
        }
    }
    /**
     * Checks if a event with the same code already exists in the list.
     * @param code Event code to check for duplicates
     * @return true if the event already exists, false otherwise
     */
    private boolean isDuplicate(String code) {
        for (Event event : events) {
            if (event.getEventCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    private void importEventsFromExcel() {
        try {
          //  InputStream file = getClass().getResourceAsStream("/UMS_Data.xlsx");
            InputStream file = new FileInputStream("C:/test/UMS_Data.xlsx");

            if (file == null) {
                System.out.println("Excel file not found!");
                return;
            }

            Workbook workbook = new XSSFWorkbook(file);
            Sheet eventSheet = workbook.getSheetAt(4);

            for (Row row : eventSheet) {
                if (row.getRowNum() == 0) continue;

                Cell eventCodeCell = row.getCell(0);
                Cell eventNameCell = row.getCell(1);
                Cell eventDescriptionCell = row.getCell(2);
                Cell eventLocationCell = row.getCell(3);
                Cell eventDateTimeCell = row.getCell(4);
                Cell eventCapacityCell = row.getCell(5);
                Cell eventCostCell = row.getCell(6);
                Cell eventHeaderImageCell = row.getCell(7);
                Cell eventRegisteredStudentsCell = row.getCell(8);

                if (eventCodeCell != null && eventNameCell != null && eventDescriptionCell != null && eventLocationCell != null && eventDateTimeCell != null
                && eventCapacityCell != null && eventCostCell != null && eventHeaderImageCell != null && eventRegisteredStudentsCell != null) {

                    String eventCode = eventCodeCell.getStringCellValue().trim();
                    String eventName = eventNameCell.getStringCellValue().trim();
                    String description = eventDescriptionCell.getStringCellValue().trim();
                    String location = eventLocationCell.getStringCellValue().trim();
                    DataFormatter formatter = new DataFormatter();
                    String dateTime = formatter.formatCellValue(eventDateTimeCell);
                    String capacity = formatter.formatCellValue(eventCapacityCell);

                    String cost = eventCostCell.getStringCellValue().trim();
                    String headerImage = eventHeaderImageCell.getStringCellValue().trim();
                    String registeredStudents =eventRegisteredStudentsCell.getStringCellValue().trim();

                    //if (!code.isEmpty() && !name.isEmpty() && !isDuplicate(code)) {
                        events.add(new Event(eventCode, eventName,description,location,
                                dateTime, capacity, cost, headerImage, registeredStudents));
                    //}
                }
            }
            file.close();
            workbook.close();

        } catch (IOException e) {
            System.out.println("Error reading Excel file.");
        }
    }

    private void removeRow() {
        try {

            int  rowIndex= -1;
            if (null !=eventTable && null!= eventTable.getSelectionModel() ){
                rowIndex=eventTable.getSelectionModel().getSelectedIndex();

                if (rowIndex ==-1) {
                    return;
                }
                rowIndex++; //first row is column row
            }
            // Define the file path
            InputStream fileInputStream = new FileInputStream("C:/test/UMS_Data.xlsx");
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(4); // Access the first sheet (index 0)
            // Specify the index of the row to delete (for example, delete the 3rd row, index 2)

            // Delete the row
            deleteRow(sheet, rowIndex);



            // Save the changes back to the same Excel file
            fileInputStream.close();
            OutputStream fileOutputStream = new FileOutputStream("C:/test/UMS_Data.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

            System.out.println("Row deleted and file updated successfully.");

            events.clear();
            importEventsFromExcel();
        } catch (Exception ex) {
            System.out.println("Error when removing Excel file." + ex.getMessage());
        }
    }

    private static void deleteRow(Sheet sheet, int rowIndex) {
        // Get the last row index
        int lastRowNum = sheet.getLastRowNum();

        // If the row to delete is not the last one, shift the rows up
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1); // Shift rows up
        }

        // If the row is the last one, just remove it
        Row removingRow = sheet.getRow(lastRowNum);
        if (removingRow != null) {
            sheet.removeRow(removingRow);
        }
    }
}

