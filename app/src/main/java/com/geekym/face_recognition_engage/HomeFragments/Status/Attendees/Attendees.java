package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Attendees extends AppCompatActivity {

    RecyclerView recyclerView;
    com.geekym.face_recognition_engage.HomeFragments.Status.Attendees.myAdapter myAdapter;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        Initialization();

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());

        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendance").child(year).child(month).child(date), ModelClass.class)
                        .build();

        myAdapter = new myAdapter(options);
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }

    private void Initialization() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}