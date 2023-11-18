package com.geekym.face_recognition_engage.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

object Utils {

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
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

}