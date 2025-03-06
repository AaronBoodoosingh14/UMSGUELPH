package com.ums.data;

// Import JavaFX properties to allow automatic updates in UI components
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Course class represents a course in the University Management System.
 * It contains properties related to course details such as name, code, subject, section, teacher, lecture time, and location.
 * The use of JavaFX StringProperty allows real-time binding to UI components.
 */
public class Course {

    // Private properties for storing course details
    private final StringProperty courseName;
    private final StringProperty courseCode;
    private final StringProperty subjectName;
    private final StringProperty sectionNumber;
    private final StringProperty teacherName;
    private final StringProperty lectureTime;
    private final StringProperty location;

    /**
     * Constructor to initialize a Course object with provided values.
     * Each parameter is wrapped in a SimpleStringProperty for UI binding.
     *
     * @param courseName Name of the course
     * @param courseCode Unique course code
     * @param subjectName Related subject name
     * @param sectionNumber Section number of the course
     * @param teacherName Name of the teacher assigned
     * @param lectureTime Scheduled lecture time
     * @param location Location of the lecture
     */
    public Course(String courseName, String courseCode, String subjectName, String sectionNumber,
                  String teacherName, String lectureTime, String location) {
        this.courseName = new SimpleStringProperty(courseName);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.sectionNumber = new SimpleStringProperty(sectionNumber);
        this.teacherName = new SimpleStringProperty(teacherName);
        this.lectureTime = new SimpleStringProperty(lectureTime);
        this.location = new SimpleStringProperty(location);
    }

    /**
     * Returns the property object for course name (for UI binding purposes).
     */
    public StringProperty courseNameProperty() { return courseName; }

    /**
     * Returns the property object for course code.
     */
    public StringProperty courseCodeProperty() { return courseCode; }

    /**
     * Returns the property object for subject name.
     */
    public StringProperty subjectNameProperty() { return subjectName; }

    /**
     * Returns the property object for section number.
     */
    public StringProperty sectionNumberProperty() { return sectionNumber; }

    /**
     * Returns the property object for teacher name.
     */
    public StringProperty teacherNameProperty() { return teacherName; }

    /**
     * Returns the property object for lecture time.
     */
    public StringProperty lectureTimeProperty() { return lectureTime; }

    /**
     * Returns the property object for location.
     */
    public StringProperty locationProperty() { return location; }

    /**
     * Retrieves the actual course name value as a String.
     */
    public String getCourseName() { return courseName.get(); }

    /**
     * Retrieves the actual course code value as a String.
     */
    public String getCourseCode() { return courseCode.get(); }

    /**
     * Retrieves the actual subject name value as a String.
     */
    public String getSubjectName() { return subjectName.get(); }

    /**
     * Retrieves the actual section number value as a String.
     */
    public String getSectionNumber() { return sectionNumber.get(); }

    /**
     * Retrieves the actual teacher name value as a String.
     */
    public String getTeacherName() { return teacherName.get(); }

    /**
     * Retrieves the actual lecture time value as a String.
     */
    public String getLectureTime() { return lectureTime.get(); }

    /**
     * Retrieves the actual location value as a String.
     */
    public String getLocation() { return location.get(); }
}
