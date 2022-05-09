package com.geekym.face_recognition_engage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Attendance_Result_Activity extends AppCompatActivity {

    Button Home;
    TextView Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);
        Initialize();

        Name.setText("USERNAME HERE");

        Home.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeScreen.class)));

    }

    private void Initialize() {
        Home = findViewById(R.id.back_home);
        Name = findViewById(R.id.name_display);
    }
}