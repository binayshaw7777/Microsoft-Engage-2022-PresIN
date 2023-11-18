package com.geekym.face_recognition_engage.model;

public class LatLong {
    private final long lat;
    private final long lon;

    public LatLong(long lat, long lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public long getLat() {
        return lat;
    }

    public long getLon() {
        return lon;
    }
}
