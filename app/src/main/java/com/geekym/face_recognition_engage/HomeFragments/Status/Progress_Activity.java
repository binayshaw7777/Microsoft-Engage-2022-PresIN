package com.geekym.face_recognition_engage.HomeFragments.Status;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Progress_Activity extends AppCompatActivity {

    TextView counter;
    private DatabaseReference reference;
    private String userID;
    int c = 0;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
    Date today = new Date();
    String date1 = dateFormat.format(today);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Initialization();

        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MMM").format(cal.getTime());
        @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd").format(cal.getTime());

    }

    private void Initialization() {
        counter = findViewById(R.id.counter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
    }
}