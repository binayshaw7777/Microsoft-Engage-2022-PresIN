package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PDFs_Activity extends AppCompatActivity {

    FloatingActionButton addNote;
    RecyclerView notesList;
    PDFAdapter notesAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfs);

        addNote = findViewById(R.id.notes_fab);
        addNote.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Add_PDF_Activity.class)));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userID = user.getUid();

        notesList = findViewById(R.id.notes_list);
        notesList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<PDFsModel> options =
                new FirebaseRecyclerOptions.Builder<PDFsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Notes"), PDFsModel.class)
                        .build();

        notesAdapter=new PDFAdapter(options);
        notesList.setAdapter(notesAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }
}