package com.geekym.face_recognition_engage.utils;

import com.geekym.face_recognition_engage.model.LatLong;

import java.security.SecureRandom;

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
}
