package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PDFs_Activity extends AppCompatActivity {

    ImageView addNote;
    RecyclerView notesList;
    PDFAdapter notesAdapter;
    private String userID;
    private ShimmerFrameLayout ShimmerViewContainer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfs);

        addNote = findViewById(R.id.notes_fab);
        addNote.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Add_PDF_Activity.class)));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);
        ShimmerViewContainer.startShimmer();
        ShimmerViewContainer.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userID = user.getUid();

        notesList = findViewById(R.id.notes_list);
        notesList.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout layout = findViewById(R.id.emptyState);
        TextView error = layout.findViewById(R.id.error);

        FirebaseRecyclerOptions<PDFsModel> options =
                new FirebaseRecyclerOptions.Builder<PDFsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Notes"), PDFsModel.class)
                        .build();

        notesAdapter = new PDFAdapter(options);

        if (notesAdapter.getItemCount() == 0) {
            notesList.setVisibility(View.GONE);
            ShimmerViewContainer.startShimmer();
            layout.setVisibility(View.INVISIBLE);
            ShimmerViewContainer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                ShimmerViewContainer.setVisibility(View.INVISIBLE);

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    if (notesAdapter.getItemCount() == 0) {
                        layout.setVisibility(View.VISIBLE); //Checking again to handle slow internet or firebase issues
                        error.setText("No PDFs Found!");
                    }
                } else { //If no internet connection
                    notesList.setVisibility(View.INVISIBLE);
                    error.setText("Please check Internet!");
                    layout.setVisibility(View.VISIBLE);
                }
            }, 2000);

        } else {
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
                if (notesAdapter.getItemCount() == 0) {
                    notesList.setVisibility(View.GONE);
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        ShimmerViewContainer.setVisibility(View.INVISIBLE);
                        error.setText("No PDFs Found!");
                        layout.setVisibility(View.VISIBLE);
                    }, 2000);

                } else {
                    notesList.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.GONE);
                    notesList.setAdapter(notesAdapter);
                }
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        notesList.setVisibility(View.VISIBLE);
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (notesAdapter != null) {
            notesAdapter.stopListening();
            notesList.setVisibility(View.GONE);
        } else {
            LinearLayout layout = findViewById(R.id.emptyState);
            layout.setVisibility(View.INVISIBLE);
        }

    }
}