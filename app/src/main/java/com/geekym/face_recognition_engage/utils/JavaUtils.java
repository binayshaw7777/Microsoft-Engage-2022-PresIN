package com.geekym.face_recognition_engage.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.model.LatLong;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)

public class JavaUtils {

    public static LatLong aritralatlong = new LatLong(22.6644468, 88.3945738);
    public static LatLong binaylatlong = new LatLong(22.7331228, 88.3643103);
    public static LatLong JISCElatlong = new LatLong(22.9595792, 88.4477127);
    // Binay's Logged LatLong: Longitude: 88.3642841 and latitude: 22.7332499

    public static Integer CARD_ICON_CLICKED = 8;
    public static Integer CARD_VIEW_CLICKED = 4;

    public static boolean isWithinGivenMinutes(long timestamp, long minutes) {
        // Convert the timestamp to a Date object
        Date timestampDate = new Date(timestamp);

        // Get the current timestamp
        long currentTimestamp = System.currentTimeMillis();

        // Convert the current timestamp to a Date object
        Date currentDate = new Date(currentTimestamp);

        // Calculate the difference in milliseconds
        long differenceInMillis = currentDate.getTime() - timestampDate.getTime();

        // Convert minutes to milliseconds
        long minutesInMillis = minutes * 60 * 1000;

        // Compare the difference with the specified time limit
        return differenceInMillis <= minutesInMillis;
    }

    public static boolean distance(float lat_a, float lng_a, float lat_b, float lng_b, float isWithinMeters) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        float distanceInMeters = (float) distance * meterConversion;

        Log.d("", "Distance in meters: " + distanceInMeters + " isWithin: " + isWithinMeters + " value: " + (distanceInMeters <= isWithinMeters));

        return distanceInMeters <= isWithinMeters;
    }

    public static String generateRandomId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomId = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomId.append(characters.charAt(randomIndex));
        }

        return randomId.toString();
    }

    public static String generateRandomId() {
        // Default length is 20
        return generateRandomId(20);
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static boolean shouldMeetTimeConstraints(Long classTimesStamp) {
        long currentTimestamp = System.currentTimeMillis();
        long tenMinutesInMillis = 10 * 60 * 1000; // 10 minutes in milliseconds

        long timeDifference = currentTimestamp - classTimesStamp;

        return timeDifference <= tenMinutesInMillis;
    }

    public static String formatTimestamp(long timestamp) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static int getYearFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getYear();
    }

    public static int getMonthFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getMonthValue();
    }

    public static int getDayFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getDayOfMonth();
    }

    public static int getHourFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getHour();
    }

    public static int getMinuteFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getMinute();
    }

    public static int getSecondFromTimestamp(long timestamp) {
        LocalDateTime dateTime = convertTimestampToLocalDateTime(timestamp);
        return dateTime.getSecond();
    }

    private static LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    // Convert a ClassPrompt object to a string using serialization
    public static String serializeToString(ClassPrompt classPrompt) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(classPrompt);
            objectOutputStream.close();
            return byteArrayOutputStream.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert a string to a ClassPrompt object using deserialization
    public static ClassPrompt deserializeFromString(String serializedString) {
        try {
            byte[] bytes = serializedString.getBytes(StandardCharsets.ISO_8859_1);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return (ClassPrompt) object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
