package com.ums.data;
import java.util.List;

public class Student {
    private String studentId;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private String academicLevel;
    private String currentSemester;
    private String profilePhoto;
    private List<String> subjectsRegistered;
    private String thesisTitle;
    private String progress;
    private String password;

    // Constructors
    public Student() {}

    public Student(String studentId, String name, String address, String telephone, String email,
                   String academicLevel, String currentSemester, String profilePhoto, List<String> subjectsRegistered,
                   String thesisTitle, String progress, String password) {
        this.studentId = studentId;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.academicLevel = academicLevel;
        this.currentSemester = currentSemester;
        this.profilePhoto = profilePhoto;
        this.subjectsRegistered = subjectsRegistered;
        this.thesisTitle = thesisTitle;
        this.progress = progress;
        this.password = password;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(String academicLevel) { this.academicLevel = academicLevel; }

    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public List<String> getSubjectsRegistered() { return subjectsRegistered; }
    public void setSubjectsRegistered(List<String> subjectsRegistered) { this.subjectsRegistered = subjectsRegistered; }

    public String getThesisTitle() { return thesisTitle; }
    public void setThesisTitle(String thesisTitle) { this.thesisTitle = thesisTitle; }

    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", academicLevel='" + academicLevel + '\'' +
                ", currentSemester=" + currentSemester +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", subjectsRegistered=" + subjectsRegistered +
                ", thesisTitle='" + thesisTitle + '\'' +
                ", progress='" + progress + '\'' +
                '}';
    }
}
