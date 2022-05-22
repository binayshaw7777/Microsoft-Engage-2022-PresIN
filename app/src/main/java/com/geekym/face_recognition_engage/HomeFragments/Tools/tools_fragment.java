package com.geekym.face_recognition_engage.HomeFragments.Tools;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs.PDFs_Activity;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tools_fragment extends Fragment {

    CardView userPDFs, dictionary, studyTimer;
    private DatabaseReference reference;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools_fragment, container, false);

        Initialization(view);

        userPDFs.setOnClickListener(v -> {
            reference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String CollegeName = snapshot.child("college").getValue().toString();
                    Intent intent = new Intent(getContext(), PDFs_Activity.class);
                    intent.putExtra("CollegeName", CollegeName);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        return view;
    }

    private void Initialization(View view) {
        userPDFs = view.findViewById(R.id.pdf_card);
        dictionary = view.findViewById(R.id.dictionary_card);
        studyTimer = view.findViewById(R.id.timer_card);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
    }
}