package com.geekym.face_recognition_engage.model;

import java.io.Serializable;

public class ClassPrompt implements Serializable {
    private String classID;
    private String userID;
    private String className;
    private String userName;
    private String timeStamp;
    private String latLong;
    private int expectedStudents;

    public ClassPrompt(String classID, String userID, String className, String userName,
                       String timeStamp, String latLong, int expectedStudents) {
        this.classID = classID;
        this.userID = userID;
        this.className = className;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.latLong = latLong;
        this.expectedStudents = expectedStudents;
    }

    public ClassPrompt() {}

    public String getUserID() {
        return userID;
    }

    public String getClassName() {
        return className;
    }

    public String getClassID() {
        return classID;
    }

    public int getExpectedStudents() {
        return expectedStudents;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    @Override
    public String toString() {
        return "ClassPrompt{" +
                "classID='" + classID + '\'' +
                ", userID='" + userID + '\'' +
                ", className='" + className + '\'' +
                ", userName='" + userName + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", latLong=" + latLong +
                ", expectedStudents=" + expectedStudents +
                '}';
    }
}