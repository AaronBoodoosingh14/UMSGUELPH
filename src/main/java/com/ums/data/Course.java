package com.ums.data;


public class Course {
    private String courseName;
    private int courseCode;
    private String subjectName;
    private String sectionNumber;
    private String teacherName;
    private String lectureTime;
    private String location;
    private int capacity;
    private String finalExam;

    public Course(String courseName, int courseCode, String subjectName, String sectionNumber, String teacherName,
                  String lectureTime, String location, int capacity, String finalExam) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.subjectName = subjectName;
        this.sectionNumber = sectionNumber;
        this.teacherName = teacherName;
        this.lectureTime = lectureTime;
        this.location = location;
        this.capacity = capacity;
        this.finalExam = finalExam;
    }

    public Course() {}

    // Corrected Getters and Setters
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getFinalExam() { return finalExam; }
    public void setFinalExam(String finalExam) { this.finalExam = finalExam; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCourseCode() { return courseCode; }
    public void setCourseCode(int courseCode) { this.courseCode = courseCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getLectureTime() { return lectureTime; }
    public void setLectureTime(String lectureTime) { this.lectureTime = lectureTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
