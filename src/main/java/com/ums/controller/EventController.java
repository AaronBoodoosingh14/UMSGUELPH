package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Event;
import com.ums.data.Student;
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

import java.io.IOException;
import java.lang.String;


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
    private Button btnAdd; // Button to add subjects (REMOVED btnLoadData)



    // ObservableList to store subjects (TableView updates automatically)
    private ObservableList<Event> events = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        eventCode.setCellValueFactory(new PropertyValueFactory<>("eventCode"));
        eventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventDateAndTime.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));
        eventCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        eventHeaderImage.setCellValueFactory(new PropertyValueFactory<>("headerImage"));
        eventRegisteredStudents.setCellValueFactory(new PropertyValueFactory<>("registeredStudents"));


        //events.add(e); //later call excelUtil.getEvents and add to here (events.add (e))
        // Bind list to TableView
        eventTable.setItems(events);


        btnAdd.setOnAction(eventAction -> addEvent());
    }

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





}