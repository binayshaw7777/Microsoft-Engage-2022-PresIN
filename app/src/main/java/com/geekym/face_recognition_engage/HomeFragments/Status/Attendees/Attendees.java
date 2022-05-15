package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Attendees extends AppCompatActivity {

    RecyclerView recyclerView;
    com.geekym.face_recognition_engage.HomeFragments.Status.Attendees.myAdapter myAdapter;

    private ShimmerFrameLayout ShimmerViewContainer;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        Initialization();

        ShimmerViewContainer.startShimmerAnimation();

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());

        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendance").child(year).child(month).child(date), ModelClass.class)
                        .build();

        myAdapter = new myAdapter(options);
        ShimmerViewContainer.setVisibility(View.GONE);
        ShimmerViewContainer.stopShimmerAnimation();
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(myAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myAdapter != null) {
            ShimmerViewContainer.stopShimmerAnimation();
            ShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            myAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myAdapter != null) {
            ShimmerViewContainer.setVisibility(View.VISIBLE);
            ShimmerViewContainer.startShimmerAnimation();
            myAdapter.stopListening();
            recyclerView.setVisibility(View.GONE);
        }
    }

    /*
    When Minimized, stop method causes the app to crash.
    The adapter stops loading data and after reopening it caused to crash.

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }
 */

    private void Initialization() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);

    }
}