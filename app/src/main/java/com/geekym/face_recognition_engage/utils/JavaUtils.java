package com.geekym.face_recognition_engage.utils;

import android.annotation.SuppressLint;
import android.os.Build;

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

    public static Integer CARD_ICON_CLICKED = 8;
    public static Integer CARD_VIEW_CLICKED = 4;

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
