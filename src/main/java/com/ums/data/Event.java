package com.ums.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private String eventCode;
    private String eventName;
    private String description;
    private String location;
    private Date dateTime;
    private String capacity;
    private String cost;
    private String headerImage;
    private List<String> registeredStudents;

    public Event() {
        this.registeredStudents = new ArrayList<>();
    }

    public Event(String eventCode, String eventName, String description, String location,
                 Date dateTime, String capacity, String cost, String headerImage) {
        this(eventCode, eventName, description, location, dateTime, capacity, cost, headerImage, new ArrayList<>());
    }

    public Event(String eventCode, String eventName, String description, String location,
                 Date dateTime, String capacity, String cost, String headerImage,
                 List<String> registeredStudents) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.cost = cost;
        this.headerImage = headerImage;
        this.registeredStudents = new ArrayList<>(registeredStudents);
    }

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }

    public String getCapacity() { return capacity; }
    public void setCapacity(String  capacity) { this.capacity = capacity; }

    public String getCost() { return cost; }
    public void setCost(String cost) { this.cost = cost; }

    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }

    public List<String> getRegisteredStudents() { return registeredStudents; }
    public void setRegisteredStudents(List<String> registeredStudents) { this.registeredStudents = new ArrayList<>(registeredStudents); }
    public void addRegisteredStudent(String studentId) { this.registeredStudents.add(studentId); }

    @Override
    public String toString() {
        return "Event{" +
                "eventCode='" + eventCode + '\'' +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", capacity=" + capacity +
                ", cost=" + cost +
                ", headerImage='" + headerImage + '\'' +
                ", registeredStudents=" + registeredStudents +
                '}';
    }
}
