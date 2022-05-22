package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

        Initialization(); //Function to initialize the variables

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Check internet connection

        Intent intent = getIntent();
        String CollegeName = intent.getStringExtra("CollegeName");


        ShimmerViewContainer.startShimmer(); //start shimmer animation
        ShimmerViewContainer.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance(); //get calendar instance

        //Getting the values of year, month name, date and full date format in the form of string.
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        String fullDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

        dateDisplay.setText(fullDate);

        LinearLayout layout = findViewById(R.id.emptyState); //Initializing (Empty state illustration)

        //Firebase data -> RecyclerView
        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendees").child(CollegeName).child(year).child(month).child(date), ModelClass.class)
                        .build();


        //Setting up the adapter with the Firebase UI variable -> 'options'
        myAdapter = new myAdapter(options);

        if (myAdapter.getItemCount() == 0) {        //If no item is found in the recycler view
            recyclerView.setVisibility(View.GONE);      //Disable recycler view
            ShimmerViewContainer.startShimmer();
            layout.setVisibility(View.INVISIBLE);       //Disable Empty State Illustration
            ShimmerViewContainer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> { //Perform some task after some delay

                ShimmerViewContainer.setVisibility(View.INVISIBLE); //Disable shimmering effect after the delay time

                //If connected to internet
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    if (myAdapter.getItemCount() == 0) //If still the adapter is empty (we give time to the app to fetch data from firebase and check here)
                        layout.setVisibility(View.VISIBLE); //Checking again to handle slow internet or firebase issues, show Empty State Illustration
                } else {
                    //If no internet connection found
                    recyclerView.setVisibility(View.INVISIBLE);
                    TextView error = layout.findViewById(R.id.error);
                    error.setText("Please check Internet!");
                    layout.setVisibility(View.VISIBLE);
                }
            }, 2000); //Delay Time

        } else {
            //If data/item is present in Adapter
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

            //Handle the condition when there's any change in item count of the adapter
            void checkEmpty() {
                if (myAdapter.getItemCount() == 0) { //If no item found in adapter
                    recyclerView.setVisibility(View.GONE); //Disable recyclerView
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.VISIBLE); //Show shimmering effect

                    new Handler().postDelayed(() -> {
                        ShimmerViewContainer.setVisibility(View.INVISIBLE); //Disable shimmering effect after the delay time
                        layout.setVisibility(View.VISIBLE); //Show Empty State Illustration
                    }, 2000); //Delay time

                } else { //If item is found in adapter
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.GONE);
                    recyclerView.setAdapter(myAdapter);
                }
            }
        });
    }

    //When the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setVisibility(View.VISIBLE);
        myAdapter.startListening();
    }

    //When the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        if (myAdapter != null) { //checking if adapter is not empty
            myAdapter.stopListening();
            recyclerView.setVisibility(View.GONE);
        } else {
            LinearLayout layout = findViewById(R.id.emptyState);
            layout.setVisibility(View.INVISIBLE);
        }
    }

    //Function to initialize the variables
    private void Initialization() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);
        dateDisplay = findViewById(R.id.date);
    }
}