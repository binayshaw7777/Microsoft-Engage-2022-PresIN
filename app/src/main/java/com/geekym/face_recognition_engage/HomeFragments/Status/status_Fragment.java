package com.geekym.face_recognition_engage.HomeFragments.Status;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Status.Attendees.Attendees;
import com.geekym.face_recognition_engage.R;

public class status_Fragment extends Fragment {

    CardView attendeesList, userProgress, userNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_, container, false);

        Initialization(view);

        attendeesList.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Attendees.class)));

        userProgress.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Progress_Activity.class)));

        userNotes.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Notes_Activity.class)));

        return view;
    }

    private void Initialization(View view) {
        attendeesList = view.findViewById(R.id.attendees_card);
        userProgress = view.findViewById(R.id.progress_card);
        userNotes = view.findViewById(R.id.notes_card);
    }
}