package com.geekym.face_recognition_engage.HomeFragments.Status;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Status.Attendees.Attendees;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class status_Fragment extends Fragment {

    CardView attendeesList, userProgress;
    private DatabaseReference reference;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_, container, false);

        Initialization(view); //Function to initialize the variables

        //Starts the Attendees Activity
        attendeesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Users").child(userID).child("college").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String CollegeName = Objects.requireNonNull(snapshot.getValue()).toString();
                        Intent intent = new Intent(getContext(), Attendees.class);
                        intent.putExtra("CollegeName", CollegeName);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        userProgress.setOnClickListener(view1 -> intentNow(Progress_Activity.class));   //Starts the Progress Activity

        return view;
    }

    //Single Method to handle Intents in this activity
    private void intentNow(Class targetClass) {
        startActivity(new Intent(getContext(), targetClass));
    }

    //Function to initialize the variables
    private void Initialization(View view) {
        attendeesList = view.findViewById(R.id.attendees_card);
        userProgress = view.findViewById(R.id.progress_card);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
    }
}