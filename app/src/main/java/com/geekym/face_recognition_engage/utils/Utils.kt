package com.geekym.face_recognition_engage.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Random

@RequiresApi(Build.VERSION_CODES.O)

object Utils {

    val BINAY_LATLONG: Pair<Long, Long> = Pair(22.7331228.toLong(), 88.3643103.toLong())

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun isWithinTenMinutes(timestamp: Long): Boolean {
        // Get current timestamp
        val currentTimestamp = System.currentTimeMillis()

        // Calculate the difference in milliseconds
        val difference = currentTimestamp - timestamp

        // Check if the difference is less than or equal to 10 minutes (600,000 milliseconds)
        return difference <= 600000
    }

    private fun timestampToLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    }

    fun getYearFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).year
    }

    fun getMonthFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).monthValue
    }

    fun getDayFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).dayOfMonth
    }

    fun getHourFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).hour
    }

    fun getMinuteFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).minute
    }

    fun getSecondFromTimestamp(timestamp: Long): Int {
        return timestampToLocalDateTime(timestamp).second
    }

    fun getFormattedDateTimeFromTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return timestampToLocalDateTime(timestamp).format(formatter)
    }

    fun generateRandomId(length: Int = 20): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val id = StringBuilder()

        for (i in 0 until length) {
            val randomChar = characters[random.nextInt(characters.length)]
            id.append(randomChar)
        }

        return id.toString()
    }

}