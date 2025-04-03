package com.ums.controller;

import com.ums.UMSApplication;
import com.ums.data.Course;
import com.ums.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.*;

public class CourseUserController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn, subjectNameColumn, sectionNumberColumn,
            lectureTimeColumn, finalExamColumn, locationColumn, teacherNameColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;

    private final ObservableList<Course> registeredCourses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        lectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        finalExamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        loadRegisteredCourses();
    }

    private void loadRegisteredCourses() {
        String email = UMSApplication.getLoggedInUsername();

        Set<String> addedCourseNames = new HashSet<>();

        if (email == null || email.isEmpty()) {
            System.out.println("No user logged in.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            String studentQuery = "SELECT SubjectsRegistered FROM students_info WHERE StudentId = ?";
            PreparedStatement studentStmt = conn.prepareStatement(studentQuery);
            studentStmt.setString(1, email);
            ResultSet studentRs = studentStmt.executeQuery();

            if (studentRs.next()) {
                String subjectsStr = studentRs.getString("SubjectsRegistered");
                if (subjectsStr == null || subjectsStr.trim().isEmpty()) {
                    System.out.println("No registered subjects found.");
                    return;
                }
                List<String> subjectCodes = Arrays.stream(subjectsStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (!subjectCodes.isEmpty()) {
                    String placeholders = String.join(",", Collections.nCopies(subjectCodes.size(), "?"));
                    String courseQuery = "SELECT * FROM courses WHERE SubjectCode IN (" + placeholders + ")";
                    PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
                    for (int i = 0; i < subjectCodes.size(); i++) {
                        courseStmt.setString(i + 1, subjectCodes.get(i));
                    }

                    ResultSet courseRs = courseStmt.executeQuery();

                    while (courseRs.next()) {
                        String courseName = courseRs.getString("CourseName");

                        // Check if we've already added this course name
                        if (!addedCourseNames.contains(courseName)) {
                            Course course = new Course();
                            course.setCourseCode(courseRs.getInt("CourseCode"));
                            course.setCourseName(courseName);
                            course.setSubjectName(courseRs.getString("SubjectCode"));
                            course.setSectionNumber(courseRs.getString("SectionNumber"));
                            course.setCapacity(courseRs.getInt("Capacity"));
                            course.setLectureTime(courseRs.getString("LectureTime"));
                            course.setFinalExam(courseRs.getString("FinalExamDate"));
                            course.setLocation(courseRs.getString("Location"));
                            course.setTeacherName(courseRs.getString("TeacherName"));
                            registeredCourses.add(course);

                            // Add the course name to our tracking set
                            addedCourseNames.add(courseName);
                        } else {
                            System.out.println("Skipping duplicate course: " + courseName);
                        }
                    }

                    courseTable.setItems(registeredCourses);
                    courseTable.refresh();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
