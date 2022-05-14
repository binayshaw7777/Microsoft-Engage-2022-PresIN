package com.geekym.face_recognition_engage.HomeFragments;

public class ModelClass {
    String Name, ID, Time, Status;

    ModelClass() { }

    public ModelClass(String name, String id, String time, String status) {
        Name = name;
        ID = id;
        Time = time;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
