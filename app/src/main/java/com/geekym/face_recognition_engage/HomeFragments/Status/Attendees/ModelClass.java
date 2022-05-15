package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

public class ModelClass {
    String Name, ID, Time;

    ModelClass() { }

    public ModelClass(String name, String id, String time) {
        Name = name;
        ID = id;
        Time = time;
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

}
