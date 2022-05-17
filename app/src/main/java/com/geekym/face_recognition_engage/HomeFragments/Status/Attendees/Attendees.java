package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    TextView dateDisplay;


    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        Initialization();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        ShimmerViewContainer.startShimmer();
        ShimmerViewContainer.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        String fullDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

        dateDisplay.setText(fullDate);
        LinearLayout layout = findViewById(R.id.emptyState);

        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendance").child(year).child(month).child(date), ModelClass.class)
                        .build();


        myAdapter = new myAdapter(options);

        if (myAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            ShimmerViewContainer.startShimmer();
            layout.setVisibility(View.INVISIBLE);
            ShimmerViewContainer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                ShimmerViewContainer.setVisibility(View.INVISIBLE);

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    if (myAdapter.getItemCount() == 0)
                        layout.setVisibility(View.VISIBLE); //Checking again to handle slow internet or firebase issues
                } else { //If no internet connection
                    recyclerView.setVisibility(View.INVISIBLE);
                    TextView error = layout.findViewById(R.id.error);
                    error.setText("Please check Internet!");
                    layout.setVisibility(View.VISIBLE);
                }
            }, 2000);

        } else {
            layout.setVisibility(View.INVISIBLE);
            ShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setAdapter(myAdapter);
        }

        myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                if (myAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        ShimmerViewContainer.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.VISIBLE);
                    }, 2000);

                } else {
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.GONE);
                    recyclerView.setAdapter(myAdapter);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setVisibility(View.VISIBLE);
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myAdapter != null) {
            myAdapter.stopListening();
            recyclerView.setVisibility(View.GONE);
        } else {
            LinearLayout layout = findViewById(R.id.emptyState);
            layout.setVisibility(View.INVISIBLE);
        }
    }

    private void Initialization() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);
        dateDisplay = findViewById(R.id.date);
    }
}