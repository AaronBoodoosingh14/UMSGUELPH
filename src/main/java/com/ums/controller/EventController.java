package com.ums.controller;

import com.ums.data.Event;
import com.ums.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.Optional;

import atlantafx.base.controls.Calendar;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import static impl.org.controlsfx.ImplUtils.getChildren;

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
    private TextField txtEventCode, txtEventName, txtEventDescription, txtEventLocation, txtEventDateAndTime, txtEventCapacity, txtEventCost, txtEventHeaderImage, txtEventRegisteredStudents;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSaveEdit;

    @FXML
    private Button btnCancelEdit;
    @FXML
    private VBox vBoxCalendarGrid;

    @FXML
    private VBox vBoxListView;

    @FXML
    private Button btnListView;
    @FXML
    private Button btnCalView;

    private YearMonth currentMonth;
    private Calendar calendar;
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private ObservableList<LocalDate> eventDates = FXCollections.observableArrayList();
    private DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
    private double vBoxListViewOriginalHeight;


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

        importEventsFromSQL();

        if (null != btnAdd) {
            btnAdd.setOnAction(eventAction -> addEvent());
        }
        if (null != btnDelete) {
            btnDelete.setOnAction(eventAction -> removeRow());
        }
        if (null != btnEdit) {
            btnEdit.setOnAction(eventAction -> editEvent());
        }

        if (null != btnSaveEdit) {
            btnSaveEdit.setOnAction(eventAction -> saveEditEvent());
            btnSaveEdit.setVisible(false);
        }

        if (null != btnCancelEdit) {
            btnCancelEdit.setOnAction(eventAction -> cancelEditEvent());
            btnCancelEdit.setVisible(false);
        }

        if (null != btnListView) {
            btnListView.setOnAction(eventAction -> displayListView());
        }
        if (null != btnCalView) {
            btnCalView.setOnAction(eventAction -> displayCalView());
        }

        if (null != vBoxCalendarGrid) {
            vBoxCalendarGrid.setVisible(false);
        }

        if (null != vBoxListView) {
            vBoxListViewOriginalHeight = vBoxListView.getPrefHeight(); // Save original height
        }
    }

    private void displayCalView() {
        vBoxCalendarGrid.setVisible(true);
        vBoxListView.setVisible(false);

        vBoxCalendarGrid.getChildren().clear();

        Label calendarTitle = new Label("Events Calendar");
        calendarTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px 0;");

        HBox legendBox = new HBox(20);
        legendBox.setAlignment(Pos.CENTER);

        HBox eventDayLegend = new HBox(5);
        Circle eventMarker = new Circle(5);
        eventMarker.setFill(Color.valueOf("#3498db"));
        Label eventLabel = new Label("Day with events");
        eventDayLegend.getChildren().addAll(eventMarker, eventLabel);

        HBox todayLegend = new HBox(5);
        Rectangle todayMarker = new Rectangle(10, 10);
        todayMarker.setFill(Color.valueOf("#e8f4f8"));
        todayMarker.setStroke(Color.valueOf("#3498db"));
        Label todayLabel = new Label("Today");
        todayLegend.getChildren().addAll(todayMarker, todayLabel);

        legendBox.getChildren().addAll(eventDayLegend, todayLegend);

        Label instructionLabel = new Label("Click on a day to view events");
        instructionLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d;");

        vBoxCalendarGrid.getChildren().addAll(calendarTitle, legendBox, instructionLabel);

        setupCalendar();
    }

    private void setupCalendar() {
        calendar = new Calendar();
        calendar.setShowWeekNumbers(true);


        calendar.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                showEventsForDate(newDate);
            }
        });

        updateCalendarView();
        vBoxCalendarGrid.getChildren().add(calendar);


        vBoxCalendarGrid.setStyle("-fx-border-color: #3498db; -fx-border-width: 2px; -fx-padding: 10px;");
    }

    private void updateCalendarView() {
        eventDates.clear();
        for (Event event : events) {
            try {
                LocalDate date = LocalDate.parse(event.getDateTime());
                if (!eventDates.contains(date)) {
                    eventDates.add(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calendar.setDayCellFactory(day -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null) {
                    if (date.equals(LocalDate.now())) {
                        setStyle("-fx-background-color: #e8f4f8; -fx-border-color: #3498db; -fx-border-width: 1px; -fx-text-fill: #2c3e50;");
                    }

                    if (eventDates.contains(date)) {
                        Circle eventMarker = new Circle(5);
                        eventMarker.setFill(Color.valueOf("#3498db"));
                        eventMarker.setTranslateY(5);

                        if (getChildren().size() > 0) {
                            setGraphic(eventMarker);
                        }

                        setStyle("-fx-background-color: #d4edda; -fx-border-color: #28a745; -fx-border-width: 1px; -fx-text-fill: #155724;");

                        Tooltip tooltip = new Tooltip("Event(s) scheduled on " + date.toString());
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(this, tooltip);
                    }
                }
            }
        });
    }


    private void showEventsForDate(LocalDate date) {
        ObservableList<Event> eventsOnDate = FXCollections.observableArrayList();
        for (Event event : events) {
            try {
                LocalDate eventDate = LocalDate.parse(event.getDateTime());
                if (eventDate.equals(date)) {
                    eventsOnDate.add(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Events on " + date.toString());

        if (eventsOnDate.isEmpty()) {
            dialog.setHeaderText("No events scheduled for this day");
            Label noEventsLabel = new Label("There are no events scheduled for " + date.toString());
            noEventsLabel.setWrapText(true);
            dialog.getDialogPane().setContent(noEventsLabel);
        } else {
            dialog.setHeaderText("Events scheduled for " + date.toString());


            ListView<String> eventListView = new ListView<>();
            for (Event event : eventsOnDate) {
                eventListView.getItems().add(event.getEventName() + " - " + event.getLocation());
            }


            VBox contentBox = new VBox(10);
            contentBox.setPadding(new Insets(10));

            Label detailsLabel = new Label("Select an event to view details");
            TextArea eventDetails = new TextArea();
            eventDetails.setEditable(false);
            eventDetails.setPrefHeight(150);
            eventDetails.setWrapText(true);

            eventListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.intValue() >= 0) {
                    Event selectedEvent = eventsOnDate.get(newVal.intValue());
                    StringBuilder details = new StringBuilder();
                    details.append("Event Code: ").append(selectedEvent.getEventCode()).append("\n");
                    details.append("Name: ").append(selectedEvent.getEventName()).append("\n");
                    details.append("Description: ").append(selectedEvent.getDescription()).append("\n");
                    details.append("Location: ").append(selectedEvent.getLocation()).append("\n");
                    details.append("Date/Time: ").append(selectedEvent.getDateTime()).append("\n");
                    details.append("Capacity: ").append(selectedEvent.getCapacity()).append("\n");
                    details.append("Cost: ").append(selectedEvent.getCost());

                    eventDetails.setText(details.toString());
                }
            });

            contentBox.getChildren().addAll(eventListView, detailsLabel, eventDetails);
            dialog.getDialogPane().setContent(contentBox);
            dialog.getDialogPane().setPrefWidth(500);
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void cancelEditEvent() {
        btnAdd.setDisable(false);
        btnDelete.setDisable(false);
        btnEdit.setVisible(true);
        btnSaveEdit.setVisible(false);
        btnCancelEdit.setVisible(false);
        clearAllFields();
    }

    @FXML
    private void editEvent() {
        Event event = eventTable.getSelectionModel().getSelectedItem();

        if (event == null) {
            System.out.println("No event selected.");
            return;
        }

        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnEdit.setVisible(false);
        btnSaveEdit.setVisible(true);
        btnCancelEdit.setVisible(true);

        txtEventCode.setText(event.getEventCode());
        txtEventName.setText(event.getEventName());
        txtEventDescription.setText(event.getDescription());
        txtEventLocation.setText(event.getLocation());
        txtEventDateAndTime.setText(event.getDateTime());
        txtEventCapacity.setText(event.getCapacity());
        txtEventCost.setText(event.getCost());
        txtEventHeaderImage.setText(event.getHeaderImage());
        txtEventRegisteredStudents.setText(event.getRegisteredStudents());
    }

    private void saveEditEvent() {
        String eventName = txtEventName.getText().trim();
        String description = txtEventDescription.getText().trim();
        String location = txtEventLocation.getText().trim();
        String dateTime = txtEventDateAndTime.getText().trim();
        String capacity = txtEventCapacity.getText().trim();
        String cost = txtEventCost.getText().trim();
        String headerImage = txtEventHeaderImage.getText().trim();
        String registeredStudents = txtEventRegisteredStudents.getText().trim();

        if (eventName.isEmpty() || location.isEmpty() || dateTime.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Event name and Location and Date/Time, these fields are required");
            errorAlert.showAndWait();
        } else if (!isDateValid(dateTime)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Date");
            errorAlert.setContentText("Please enter valid date");
            errorAlert.showAndWait();
        } else {
            String query = "UPDATE events SET eventName = ?, description = ?, location = ?, dateTime = ?, " +
                    "capacity = ?, cost = ?, headerImage = ?, registeredStudents = ? WHERE eventCode = ?";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, eventName);
                pstmt.setString(2, description);
                pstmt.setString(3, location);
                java.sql.Date sqlDate = convertStringToSQLDate(dateTime);

                pstmt.setDate(4, sqlDate);
                pstmt.setString(5, capacity);
                pstmt.setString(6, cost);
                pstmt.setString(7, headerImage);
                pstmt.setString(8, registeredStudents);
                pstmt.setString(9, txtEventCode.getText().trim());

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    clearAllFields();
                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setHeaderText("Save");
                    errorAlert.setContentText("Event edited successfully");
                    errorAlert.showAndWait();
                    importEventsFromSQL();
                    loadEventDates();
                    if (calendar != null) {
                        updateCalendarView();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancelEditEvent();
        }
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

        if (isDuplicate(eventCode)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Event code already exists, please enter new event code");
            errorAlert.showAndWait();
        } else if (eventCode.isEmpty() || eventName.isEmpty() || location.isEmpty() || dateTime.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Event code and Event name and Location and Date/Time, these fields are required");
            errorAlert.showAndWait();
        } else if (!isDateValid(dateTime)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Date");
            errorAlert.setContentText("Please enter valid date");
            errorAlert.showAndWait();
        } else {
            String query = "INSERT INTO events (eventCode,eventName,description,location,dateTime,capacity,cost,headerImage,registeredStudents) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, eventCode);
                pstmt.setString(2, eventName);
                pstmt.setString(3, description);
                pstmt.setString(4, location);
                java.sql.Date sqlDate = convertStringToSQLDate(dateTime);

                pstmt.setDate(5, sqlDate);
                pstmt.setString(6, capacity);
                pstmt.setString(7, cost);
                pstmt.setString(8, headerImage);
                pstmt.setString(9, registeredStudents);

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    clearAllFields();
                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setHeaderText("Save");
                    errorAlert.setContentText("Event saved successfully");
                    errorAlert.showAndWait();
                    importEventsFromSQL();
                    loadEventDates();
                    if (calendar != null) {
                        updateCalendarView();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAllFields() {
        txtEventCode.clear();
        txtEventName.clear();
        txtEventDescription.clear();
        txtEventLocation.clear();
        txtEventDateAndTime.clear();
        txtEventCapacity.clear();
        txtEventCost.clear();
        txtEventHeaderImage.clear();
        txtEventRegisteredStudents.clear();
    }

    private java.sql.Date convertStringToSQLDate(String dateStr) {
        try {
            java.util.Date utilDate = dtFormat.parse(dateStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean isDuplicate(String code) {
        for (Event event : events) {
            if (event.getEventCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDateValid(String inputDate) {
        boolean returnFlag = false;
        try {
            Date parsedDate = dtFormat.parse(inputDate);
            returnFlag = true;
        } catch (Exception e) {
            System.out.println("Invalid date format. yyyy-MM-dd.");
        }
        return returnFlag;
    }

    private void importEventsFromSQL() {
        events.clear();

        String query = "SELECT * FROM events";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event();
                event.setEventCode(rs.getString("eventCode"));
                event.setEventName(rs.getString("eventName"));
                event.setDescription(rs.getString("description"));
                event.setLocation(rs.getString("location"));

                java.sql.Date date = rs.getDate("dateTime");
                String dateStr = dtFormat.format(date);
                event.setDateTime(dateStr);

                event.setCapacity(rs.getString("capacity"));
                event.setCost(rs.getString("cost"));
                event.setHeaderImage(rs.getString("headerImage"));
                event.setRegisteredStudents(rs.getString("registeredStudents"));
                events.add(event);
            }

            eventTable.setItems(events);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadEventDates() {
        eventDates.clear();
        for (Event event : events) {
            try {
                Date date = dtFormat.parse(event.getDateTime());
                LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
                if (!eventDates.contains(localDate)) {
                    eventDates.add(localDate);
                }
            } catch (ParseException e) {
                System.out.println("Error parsing date: " + e.getMessage());
            }
        }
    }

    private void removeRow() {
        try {
            Event event = eventTable.getSelectionModel().getSelectedItem();
            String code = event.getEventCode();

            boolean userResponse = showYesNoDialog("Confirmation", "Are you sure you want to delete code : " + code + " ?");

            if (!userResponse) {
                System.out.println("User selected NO or closed the dialog");
            } else {
                String query = "delete from events where eventCode = ?";
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, code);

                    int deletedRow = pstmt.executeUpdate();

                    if (deletedRow > 0) {
                        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                        errorAlert.setHeaderText("Deleted");
                        errorAlert.setContentText("Event deleted successfully");
                        errorAlert.showAndWait();
                        importEventsFromSQL();
                        loadEventDates();
                        if (calendar != null) {
                            updateCalendarView();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error" + ex.getMessage());
        }
    }

    private boolean showYesNoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void displayListView() {
        vBoxCalendarGrid.setVisible(false);
        vBoxListView.setVisible(true);
        vBoxListView.setPrefHeight(vBoxListViewOriginalHeight);
    }
}