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


    double vBoxListViewOriginalHeight;


    private GridPane calendarGrid;
    private ComboBox<Integer> yearSelector;
    private ComboBox<String> monthSelector;
    private YearMonth currentMonth;


    // ObservableList to store subjects (TableView updates automatically)
    private ObservableList<Event> events = FXCollections.observableArrayList();

    private String userRole = "Student"; // Default role

    private DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                // Execute the INSERT statement
                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    clearAllFields();
                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setHeaderText("Save");
                    errorAlert.setContentText("Event edited successfully");
                    errorAlert.showAndWait();
                    importEventsFromSQL();
                }

            } catch (Exception e) {
                e.printStackTrace(); // Print SQL error details
            }
            cancelEditEvent();
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

                // Execute the INSERT statement
                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    clearAllFields();
                    Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                    errorAlert.setHeaderText("Save");
                    errorAlert.setContentText("Event saved successfully");
                    errorAlert.showAndWait();
                    importEventsFromSQL();
                }

            } catch (Exception e) {
                e.printStackTrace(); // Print SQL error details
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
            java.util.Date utilDate = dtFormat.parse(dateStr); // Convert to java.util.Date
            return new java.sql.Date(utilDate.getTime()); // Convert to java.sql.Date
        } catch (ParseException e) {
            return null; // Return null if parsing fails
        }
    }

    /**
     * Checks if a event with the same code already exists in the list.
     *
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

    private boolean isDateValid(String inputDate) {
        boolean returnFlag = false;

        // set lenient to false to apply strict date parsing

        try {
            // parse the user input into a Date object
            Date parsedDate = dtFormat.parse(inputDate);
            returnFlag = true;

        } catch (Exception e) {
            System.out.println(
                    "Invalid date format. yyyy-MM-dd.");
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
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // Print SQL error details
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

        // Customize buttons (Yes/No)
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Show dialog and wait for user response
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void displayListView() {
        vBoxCalendarGrid.setVisible(false);
        vBoxListView.setVisible(true);
        vBoxListView.setPrefHeight(vBoxListViewOriginalHeight);
    }

    private void displayCalView() {
        vBoxCalendarGrid.setVisible(true);
        vBoxListView.setVisible(false);
        btnCalView.setText("Loading..");
        btnCalView.setDisable(true);
        btnListView.setDisable(true);
        vBoxListView.setPrefHeight(0);

        BorderPane root = new BorderPane();
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        yearSelector = new ComboBox<>();
        for (int i = 2024; i <= 2030; i++) {
            yearSelector.getItems().add(i);
        }
        yearSelector.setValue(LocalDate.now().getYear());

        monthSelector = new ComboBox<>();
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for (String month : months) {
            monthSelector.getItems().add(month);
        }
        monthSelector.setValue(months[LocalDate.now().getMonthValue() - 1]);

        Button updateButton = new Button("Update Calendar");
        updateButton.setOnAction(e -> updateCalendar());

        controls.getChildren().addAll(new Label("Year:"), yearSelector, new Label("Month:"), monthSelector, updateButton);

        calendarGrid = new GridPane();
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        root.setTop(controls);
        root.setCenter(calendarGrid);

        currentMonth = YearMonth.now();
        updateCalendar();
        vBoxCalendarGrid.getChildren().clear();
        vBoxCalendarGrid.getChildren().add(root);

        btnCalView.setText("Calender View");
        btnCalView.setDisable(false);
        btnListView.setDisable(false);

        //  Scene scene = new Scene(root, 600, 400);
        //    primaryStage.setScene(scene);
        //   primaryStage.show();


    }

    private void updateCalendar() {
        showOkMessage("Loading please wait");
        calendarGrid.getChildren().clear();
        int selectedYear = yearSelector.getValue();
        int selectedMonth = monthSelector.getSelectionModel().getSelectedIndex() + 1;
        currentMonth = YearMonth.of(selectedYear, selectedMonth);

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();


        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = firstDayOfMonth.plusDays(day - 1);
                VBox dayBox = new VBox();
                dayBox.setPadding(new Insets(5));
                dayBox.setStyle("-fx-border-color: black; -fx-padding: 10px;");

                Label dayLabel = new Label(String.valueOf(day));
                dayBox.getChildren().add(dayLabel);
                String event = getEventForDate(date, conn, dayBox);


                calendarGrid.add(dayBox, (day - 1) % 7, (day - 1) / 7);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        showOkMessage("Calendar Updated");
    }

    private String getEventForDate(LocalDate date, Connection conn, VBox dayBox) throws SQLException {
        String event = null;
        String query = "SELECT eventName FROM events where dateTime = '" + date + "'";
        try (
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                event = rs.getString("eventName");
                if (event != null) {
                    Label eventLabel = new Label(event);
                    dayBox.getChildren().add(eventLabel);
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return event;
    }

    private void showOkMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);

        alert.showAndWait(); // Show the alert and wait for the user to close it
    }
}

