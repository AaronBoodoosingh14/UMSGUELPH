package com.ums.data; // Package location for Subject model

/**
 * Represents a Subject entity.
 * Stores subject details such as subject code and name.
 */
public class Subject {

    private String subjectCode; // Unique identifier for the subject (e.g., "MATH101")
    private String subjectName; // Name of the subject (e.g., "Calculus")
    private String description;

    public Subject() {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    /**
     * Gets the subject code.
     * @return subjectCode as a String
     */
    public String getSubjectCode() {
        return subjectCode;
    }

    /**
     * Gets the subject name.
     * @return subjectName as a String
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets a new subject name.
     * @param subjectName The new name of the subject
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Sets a new subject code.
     * @param subjectCode The new subject code
     */
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns a string representation of the Subject object.
     * This can be useful for debugging and displaying subject details.
     * @return Formatted subject details as a string
     */
    @Override
    public String toString() {
        return "Subject{" +
                "subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
