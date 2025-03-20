package com.ums.data;

import java.util.ArrayList;
import java.util.List;

public class Faculty {
    private String FacultyID;
    private String facultyName;
    private String degree;
    private String research;
    private String courses;
    private String email;
    private String officeLocation;
    private String profilePhotoPath;
    private String password;// New field

    // Default constructor
    public Faculty() {
        this.profilePhotoPath = "";  // Initialize as empty
    }

    // Updated parameterized constructor
    public Faculty(String password, String FacultyID, String facultyName, String degree, String research,
                   String courses, String email, String officeLocation) {
        this(password, facultyName,FacultyID, degree, research, courses, email, officeLocation, "");
    }

    // New full constructor
    public Faculty(String password, String FacultyID, String facultyName, String degree, String research,
                   String courses, String email, String officeLocation,
                   String profilePhotoPath) {
        this.FacultyID = FacultyID;
        this.facultyName = facultyName;
        this.degree = degree;
        this.research = research;
        this.courses = courses;
        this.email = email;
        this.officeLocation = officeLocation;
        this.profilePhotoPath = profilePhotoPath;
        this.password = password;
    }

    // New getter and setter
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getFacultyID() {return FacultyID;}
    public void setFacultyID(String FacultyID) {this.FacultyID = FacultyID;}

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    // Rest of existing getters/setters remain the same
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getResearch() { return research; }
    public void setResearch(String research) { this.research = research; }

    public String getCourses() { return courses; }
    public void setCourses(String courses) { this.courses = courses; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    // Updated toString()
    @Override
    public String toString() {
        return "Faculty{" +
                "FacultyID ='" + FacultyID + '\'' +
                "Name='" + facultyName + '\'' +
                ", Degree='" + degree + '\'' +
                ", Research='" + research + '\'' +
                ", Courses=" + courses +
                ", Email='" + email + '\'' +
                ", Office='" + officeLocation + '\'' +
                ", ProfilePhoto='" + profilePhotoPath + '\'' +
                '}';
    }
}