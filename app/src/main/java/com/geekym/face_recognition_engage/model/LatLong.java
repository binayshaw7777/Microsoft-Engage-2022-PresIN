package com.geekym.face_recognition_engage.model;


// LatLong class
public class LatLong {
    private double lat;
    private double lon;

    public LatLong(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "LatLong{" +
                "latitude=" + lat +
                ", longitude=" + lon +
                '}';
    }
}

