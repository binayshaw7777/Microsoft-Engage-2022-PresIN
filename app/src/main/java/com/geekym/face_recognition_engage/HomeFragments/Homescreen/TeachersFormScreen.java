package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.model.LatLong;
import com.geekym.face_recognition_engage.utils.JavaUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TeachersFormScreen extends AppCompatActivity {

    private DatabaseReference ref;
    EditText className, expectedStudentNo;
    Button create_Form;

    @SuppressLint({"SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_form_screen);

        initialization();

        SharedPreferences userData = getSharedPreferences("userData", Context.MODE_PRIVATE);

        String userID = userData.getString("userID", "0");
        String userName = userData.getString("name", "0");
        String CollegeName = userData.getString("collegeName", "0");
        Calendar cal = Calendar.getInstance(); //Creating a calendar instance
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());   //Storing year as string
        String month = new SimpleDateFormat("MMM").format(cal.getTime());   //Storing month name as string
        String date = new SimpleDateFormat("dd").format(cal.getTime()); //CollegeName from SharedPreference


        create_Form.setOnClickListener(view -> {

            String classNameTextField = className.getText().toString().trim();
            Integer expectedStudentTextField = Integer.parseInt(expectedStudentNo.getText().toString().trim());
            String classID = JavaUtils.generateRandomId(10);
            String timeStamp = String.valueOf(JavaUtils.getCurrentTimestamp());
            LatLong latLong = JavaUtils.aritralatlong;


            ClassPrompt classPrompt = new ClassPrompt(classID,userID,classNameTextField,userName,timeStamp, latLong, expectedStudentTextField,null);

            // Pushing the Class Prompt to Attendees
            ref.child("Attendees").child(CollegeName).child(year).child(month).child(date).child(classID).setValue(classPrompt);

//            ref.child("Users").child(userID).child("Attendance").child(year).child(month).child(date).setValue(classPrompt);

            onBackPressed();

        });
    }

    private void initialization() {
        ref = FirebaseDatabase.getInstance().getReference();
        className = findViewById(R.id.className);
        create_Form = findViewById(R.id.createForm);
        expectedStudentNo = findViewById(R.id.expectedStudentNo);
    }
}