package com.ums.data;

// Import JavaFX properties to allow automatic updates in UI components

/**
 * The Course class represents a course in the University Management System.
 * It contains properties related to course details such as name, code, subject, section, teacher, lecture time, and location.
 * The use of JavaFX StringProperty allows real-time binding to UI components.
 */
public class Course {
    private String courseName;
    private int courseCode;
    private String subjectName;
    private String sectionNumber;
    private String teacherName;
    private String lectureTime;
    private String location;

    public Course(String courseName, int courseCode, String subjectName, String sectionNumber, String teacherName, String lectureTime, String location){
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.subjectName = subjectName;
        this.sectionNumber = sectionNumber;
        this.teacherName = teacherName;
        this.lectureTime = lectureTime;
        this.location = location;
    }

    public Course(){}

    /**
     * Constructor to initialize a Course object with provided values.
     * Each parameter is wrapped in a SimpleStringProperty for UI binding.
     *
     */


    /**
     * Returns the property object for course name (for UI binding purposes).
     */
    public void courseNameProperty(String courseName) { this.courseName = courseName; }

    /**
     * Returns the property object for course code.
     */
    public void courseCodeProperty(int courseCode) { this.courseCode = courseCode; }

    /**
     * Returns the property object for subject name.
     */
    public void subjectNameProperty(String subjectName) { this.subjectName = subjectName; }

    /**
     * Returns the property object for section number.
     */
    public void sectionNumberProperty(String sectionNumber) { this.sectionNumber = sectionNumber; }

    /**
     * Returns the property object for teacher name.
     */
    public void teacherNameProperty(String teacherName) { this.teacherName = teacherName; }

    /**
     * Returns the property object for lecture time.
     */
    public void lectureTimeProperty(String lectureTime) { this.lectureTime = lectureTime; }

    /**
     * Returns the property object for location.
     */
    public void locationProperty(String location) { this.location = location; }

    /**
     * Retrieves the actual course name value as a String.
     */
    public String getCourseName() { return courseName; }

    /**
     * Retrieves the actual course code value as a String.
     */
    public int getCourseCode() { return courseCode; }

    /**
     * Retrieves the actual subject name value as a String.
     */
    public String getSubjectName() { return subjectName; }

    /**
     * Retrieves the actual section number value as a String.
     */
    public String getSectionNumber() { return sectionNumber; }

    /**
     * Retrieves the actual teacher name value as a String.
     */
    public String getTeacherName() { return teacherName; }

    /**
     * Retrieves the actual lecture time value as a String.
     */
    public String getLectureTime() { return lectureTime; }

    /**
     * Retrieves the actual location value as a String.
     */
    public String getLocation() { return location; }
}
