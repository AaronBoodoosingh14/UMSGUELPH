package com.ums.data;


import java.util.List;

public class Event {
    private String eventCode;
    private String eventName;
    private String description;
    private String location;
    private String dateTime;
    private String capacity;
    private String cost;  // Changed from double to String
    private String headerImage;
    private String registeredStudents;

    // Constructors
    public Event() {
    }

    public Event(String eventCode, String eventName, String description, String location,
                 String dateTime, String capacity, String cost, String headerImage,
                 String registeredStudents) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.cost = cost;
        this.headerImage = headerImage;
        this.registeredStudents = registeredStudents;
    }

    // Getters and Setters
    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(String registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventCode='" + eventCode + '\'' +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", dateTime=" + dateTime +
                ", capacity=" + capacity +
                ", cost='" + cost + '\'' +
                ", headerImage='" + headerImage + '\'' +
                ", registeredStudents=" + registeredStudents +
                '}';
    }
}
