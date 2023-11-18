package com.geekym.face_recognition_engage.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.geekym.face_recognition_engage.model.LatLong;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiresApi(api = Build.VERSION_CODES.O)

public class JavaUtils {

    public static LatLong aritralatlong = new LatLong((long) 22.6644468, (long) 88.3945738);
    public static LatLong binaylatlong = new LatLong((long) 22.7331228, (long) 88.3643103);

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
}
