package com.ums.data;

public class Student {
    private String studentId;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private String academicLevel;
    private String currentSemester;
    private String profilePhotoPath;
    private String subjectsRegistered;
    private String thesisTitle;
    private String progress;
    private String password;
    private String tuition;

    public Student() {
        this.profilePhotoPath = "";
    }

    public Student(String tuition, String studentId, String name, String address, String telephone, String email,
                   String academicLevel, String currentSemester, String subjectsRegistered,
                   String thesisTitle, String progress, String password) {
        this.studentId = studentId;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.academicLevel = academicLevel;
        this.currentSemester = currentSemester;
        this.subjectsRegistered = subjectsRegistered;
        this.thesisTitle = thesisTitle;
        this.progress = progress;
        this.password = password;
        this.tuition = tuition;
        this.profilePhotoPath = "";
    }

    public Student(String studentId, String name, String address, String telephone, String email,
                   String academicLevel, String currentSemester, String subjectsRegistered,
                   String thesisTitle, String progress, String password, String tuition, String profilePhotoPath) {
        this.studentId = studentId;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.academicLevel = academicLevel;
        this.currentSemester = currentSemester;
        this.subjectsRegistered = subjectsRegistered;
        this.thesisTitle = thesisTitle;
        this.progress = progress;
        this.password = password;
        this.tuition = tuition;
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

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

    public String getSubjectsRegistered() { return subjectsRegistered; }
    public void setSubjectsRegistered(String subjectsRegistered) { this.subjectsRegistered = subjectsRegistered; }

    public String getThesisTitle() { return thesisTitle; }
    public void setThesisTitle(String thesisTitle) { this.thesisTitle = thesisTitle; }

    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTuition() { return tuition; }
    public void setTuition(String tuition) { this.tuition = tuition; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", academicLevel='" + academicLevel + '\'' +
                ", currentSemester='" + currentSemester + '\'' +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", subjectsRegistered='" + subjectsRegistered + '\'' +
                ", thesisTitle='" + thesisTitle + '\'' +
                ", progress='" + progress + '\'' +
                ", tuition='" + tuition + '\'' +
                '}';
    }
}
