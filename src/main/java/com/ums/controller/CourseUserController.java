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

/**
 * Controller for the user-facing (student) course module.
 * Displays only the courses a student is registered for based on their SubjectsRegistered field.
 */
public class CourseUserController {

    // TableView and columns for displaying course details
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn, subjectNameColumn, sectionNumberColumn,
            lectureTimeColumn, finalExamColumn, locationColumn, teacherNameColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;

    // List to hold the filtered list of courses registered to the logged-in student
    private final ObservableList<Course> registeredCourses = FXCollections.observableArrayList();

    /**
     * Initializes the controller:
     * - Maps each TableColumn to the corresponding Course field
     * - Loads the current student's registered courses from the database
     */
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

        loadRegisteredCourses();  // Load only the courses this student is registered for
    }

    /**
     * Queries the `students_info` table for the logged-in student's registered subject codes,
     * then fetches matching courses from the `courses` table and displays them in the table.
     */
    private void loadRegisteredCourses() {
        String email = UMSApplication.getLoggedInUsername(); // Student's ID used as username

        Set<String> addedCourseNames = new HashSet<>(); // To avoid duplicate course entries

        if (email == null || email.isEmpty()) {
            System.out.println("No user logged in.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            // Fetch the student's registered subject codes from the students_info table
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

                // Split the subjects string (comma-separated codes) into a list
                List<String> subjectCodes = Arrays.stream(subjectsStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (!subjectCodes.isEmpty()) {
                    // Build a SQL query with variable number of placeholders (?, ?, ?, ...)
                    String placeholders = String.join(",", Collections.nCopies(subjectCodes.size(), "?"));
                    String courseQuery = "SELECT * FROM courses WHERE SubjectCode IN (" + placeholders + ")";
                    PreparedStatement courseStmt = conn.prepareStatement(courseQuery);

                    // Assign values to the placeholders
                    for (int i = 0; i < subjectCodes.size(); i++) {
                        courseStmt.setString(i + 1, subjectCodes.get(i));
                    }

                    ResultSet courseRs = courseStmt.executeQuery();

                    // For each matching course, add it to the table if not already added
                    while (courseRs.next()) {
                        String courseName = courseRs.getString("CourseName");

                        // Avoid displaying duplicates if a course with the same name appears multiple times
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
                            addedCourseNames.add(courseName); // Mark as added
                        } else {
                            System.out.println("Skipping duplicate course: " + courseName);
                        }
                    }

                    // Bind the final list of courses to the table
                    courseTable.setItems(registeredCourses);
                    courseTable.refresh();
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log any SQL or connection errors
        }
    }
}
