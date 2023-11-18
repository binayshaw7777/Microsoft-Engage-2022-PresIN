package com.geekym.face_recognition_engage.model;

import com.geekym.face_recognition_engage.Users;

import java.util.List;

//data class ClassPrompt(
//    val classID: String,
//    val userID: String,
//    val className: String,
//    val userName: String,
//    val timeStamp: String,
//    val latLong: LatLong,
//    val expectedStudents: Int,
//    val studentsList: List<Users>? = null
//)
//
//data class LatLong(
//    val lat: Long,
//    val long: Long
//)

public class ClassPrompt {
    private  String classID;
    private  String userID;
    private  String className;
    private  String userName;
    private  String timeStamp;
    private  LatLong latLong;
    private  int expectedStudents;
    private  List<Users> studentsList;

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

    public ClassPrompt(String classID, String userID, String className, String userName,
                       String timeStamp, LatLong latLong, int expectedStudents) {
        this(classID, userID, className, userName, timeStamp, latLong, expectedStudents, null);
    }

    public String getClassID() {
        return classID;
    }

    public String getUserID() {
        return userID;
    }

    public String getClassName() {
        return className;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public LatLong getLatLong() {
        return latLong;
    }

    public int getExpectedStudents() {
        return expectedStudents;
    }

    public List<Users> getStudentsList() {
        return studentsList;
    }
}
