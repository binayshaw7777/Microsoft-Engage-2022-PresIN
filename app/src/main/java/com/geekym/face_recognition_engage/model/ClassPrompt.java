package com.geekym.face_recognition_engage.model;

import com.geekym.face_recognition_engage.Users;

import java.io.Serializable;
import java.util.List;

public class ClassPrompt implements Serializable {
    private String classID;
    private String userID;
    private String className;
    private String userName;
    private String timeStamp;
    private LatLong latLong;
    private int expectedStudents;
    private List<Users> studentsList;

    public ClassPrompt(String classID, String userID, String className, String userName,
                       String timeStamp, LatLong latLong, int expectedStudents,
                       List<Users> studentsList) {
        this.classID = classID;
        this.userID = userID;
        this.className = className;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.latLong = latLong;
        this.expectedStudents = expectedStudents;
        this.studentsList = studentsList;
    }

    public ClassPrompt() {
    }

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

}
