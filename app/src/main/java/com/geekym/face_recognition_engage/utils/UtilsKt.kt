package com.geekym.face_recognition_engage.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import com.geekym.face_recognition_engage.model.ClassPrompt
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Calendar


object UtilsKt {
    fun String.generateQR(
        displayMetrics: DisplayMetrics,
        color1: Int = Color.BLACK,
        color2: Int = Color.WHITE,
    ): Bitmap? {
        return try {

            val size = displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels)

            this.encodeAsQrCodeBitmap(size, color1, color2)

        } catch (e: Exception) {
            Log.d("", e.message.toString())
            null
        }
    }

    @Throws(WriterException::class)
    fun String.encodeAsQrCodeBitmap(
        dimension: Int,
        color1: Int,
        color2: Int,
    ): Bitmap? {

        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(
                this,
                BarcodeFormat.QR_CODE,
                dimension,
                dimension,
                hashMapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H)
            )
        } catch (e: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) color1 else color2
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, dimension, 0, 0, w, h)

        return bitmap

    }

    private val gson = Gson()

    fun ClassPrompt.classPromptToJson(): String {
        return gson.toJson(this)
    }

    fun String.jsonToClassPrompt(): ClassPrompt? {
        return try {
            gson.fromJson(this, ClassPrompt::class.java)
        } catch (e: Exception) {
            // Handle exceptions during deserialization
            null
        }
    }

    fun getWorkingDaysInCurrentMonth(): Int {
        val calendar = Calendar.getInstance()

        // Set the calendar to the first day of the current month
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get the number of days in the current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Initialize a counter for working days
        var workingDays = 0

        // Iterate through each day of the month
        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)

            // Check if the day is not Sunday (Calendar.SUNDAY) or Saturday (Calendar.SATURDAY)
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
            ) {
                workingDays++
            }
        }

        return workingDays
    }

    fun String.deserializeQRString(): ClassPrompt? {
        return try {
            // Extract the relevant part of the QR string
            val jsonString = substringAfter("{").substringBefore("}")

            // Use Gson to deserialize the JSON string into a ClassPrompt object
            val gson = Gson()
            gson.fromJson(jsonString, ClassPrompt::class.java)
        } catch (e: JsonSyntaxException) {
            // Handle invalid JSON syntax
            null
        } catch (e: Exception) {
            // Handle other exceptions
            null
        }
    }
}