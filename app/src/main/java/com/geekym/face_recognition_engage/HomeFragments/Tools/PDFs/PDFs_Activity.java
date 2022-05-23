package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.FirebaseDatabase;

public class PDFs_Activity extends AppCompatActivity {

    ImageView addNote;
    RecyclerView notesList;
    PDFAdapter notesAdapter;
    private ShimmerFrameLayout ShimmerViewContainer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfs);

        Initialization();    //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = PDFs_Activity.this.getSharedPreferences("userData", 0);
        String CollegeName = userDataSP.getString("collegeName", "0");

        addNote.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Add_PDF_Activity.class)));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        ShimmerViewContainer.startShimmer();
        ShimmerViewContainer.setVisibility(View.VISIBLE);

        notesList.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout layout = findViewById(R.id.emptyState);
        TextView error = layout.findViewById(R.id.error);

        FirebaseRecyclerOptions<PDFsModel> options =
                new FirebaseRecyclerOptions.Builder<PDFsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("PDFs").child(CollegeName), PDFsModel.class)
                        .build();

        //Setting up the adapter with the Firebase UI variable -> 'options'
        notesAdapter = new PDFAdapter(options);

        if (notesAdapter.getItemCount() == 0) { //If no item is found in the recycler view
            notesList.setVisibility(View.GONE); //Disable recycler view
            ShimmerViewContainer.startShimmer();
            layout.setVisibility(View.INVISIBLE); //Disable Empty State Illustration
            ShimmerViewContainer.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> { //Perform some task after some delay

                ShimmerViewContainer.setVisibility(View.INVISIBLE);  //Disable shimmering effect after the delay time

                //If connected to internet
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    if (notesAdapter.getItemCount() == 0) { //If still the adapter is empty (we give time to the app to fetch data from firebase and check here)
                        layout.setVisibility(View.VISIBLE); //Checking again to handle slow internet or firebase issues
                        error.setText("No PDFs Found!");
                    }
                } else {
                    //If no internet connection
                    notesList.setVisibility(View.INVISIBLE);
                    error.setText("Please check Internet!");
                    layout.setVisibility(View.VISIBLE);
                }
            }, 2000);   //Delay Time

        } else {
            //If data/item is present in Adapter
            notesList.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            ShimmerViewContainer.setVisibility(View.GONE);
            notesList.setAdapter(notesAdapter);
        }

        notesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                //Handle the condition when there's any change in item count of the adapter
                if (notesAdapter.getItemCount() == 0) {     //If no item found in adapter
                    notesList.setVisibility(View.GONE);     //Disable recyclerView
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.VISIBLE);   //Show shimmering effect

                    new Handler().postDelayed(() -> {
                        ShimmerViewContainer.setVisibility(View.INVISIBLE); //Disable shimmering effect after the delay time
                        error.setText("No PDFs Found!");
                        layout.setVisibility(View.VISIBLE); //Show Empty State Illustration

                    }, 2000); //Delay time

                } else {    //If item is found in adapter
                    notesList.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.GONE);
                    notesList.setAdapter(notesAdapter);
                }
            }

        });

    }

    //When the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        notesList.setVisibility(View.VISIBLE);
        notesAdapter.startListening();
    }

    //When the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        if (notesAdapter != null) {     //checking if adapter is not empty
            notesAdapter.stopListening();
            notesList.setVisibility(View.GONE);
        } else {
            LinearLayout layout = findViewById(R.id.emptyState);
            layout.setVisibility(View.INVISIBLE);
        }
    }

    //Function to initialize the variables
    private void Initialization() {
        addNote = findViewById(R.id.notes_fab);
        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);
        notesList = findViewById(R.id.notes_list);
    }
}