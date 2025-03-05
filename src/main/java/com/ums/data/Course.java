package com.ums.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Course {
    private final StringProperty courseName;
    private final StringProperty courseCode;
    private final StringProperty subjectName;
    private final StringProperty sectionNumber;
    private final StringProperty teacherName;
    private final StringProperty lectureTime;
    private final StringProperty location;

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

    public StringProperty courseNameProperty() { return courseName; }
    public StringProperty courseCodeProperty() { return courseCode; }
    public StringProperty subjectNameProperty() { return subjectName; }
    public StringProperty sectionNumberProperty() { return sectionNumber; }
    public StringProperty teacherNameProperty() { return teacherName; }
    public StringProperty lectureTimeProperty() { return lectureTime; }
    public StringProperty locationProperty() { return location; }

    public String getCourseName() { return courseName.get(); }
    public String getCourseCode() { return courseCode.get(); }
    public String getSubjectName() { return subjectName.get(); }
    public String getSectionNumber() { return sectionNumber.get(); }
    public String getTeacherName() { return teacherName.get(); }
    public String getLectureTime() { return lectureTime.get(); }
    public String getLocation() { return location.get(); }
}