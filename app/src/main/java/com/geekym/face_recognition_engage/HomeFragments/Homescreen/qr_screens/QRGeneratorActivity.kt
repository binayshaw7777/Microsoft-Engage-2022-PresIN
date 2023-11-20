package com.geekym.face_recognition_engage.HomeFragments.Homescreen.qr_screens

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.geekym.face_recognition_engage.R
import com.geekym.face_recognition_engage.model.ClassPrompt
import com.geekym.face_recognition_engage.utils.UtilsKt.generateQR

class QRGeneratorActivity : AppCompatActivity() {

    private lateinit var classPrompt: ClassPrompt
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var iv: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)

        displayMetrics = DisplayMetrics()
        iv = findViewById(R.id.qr_preview)
        this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        val intent = intent
        classPrompt = intent.getSerializableExtra("classPrompt") as ClassPrompt
        iv.setImageBitmap(classPrompt.toString().generateQR(displayMetrics))
    }
}