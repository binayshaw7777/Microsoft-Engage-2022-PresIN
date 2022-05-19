package com.geekym.face_recognition_engage.HomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekym.face_recognition_engage.HomeFragments.Status.Notes_Activity;
import com.geekym.face_recognition_engage.R;

public class tools_fragment extends Fragment {

    CardView userNotes, dictionary, studyTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools_fragment, container, false);

        Initialization(view);

        userNotes.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Notes_Activity.class)));


        return view;
    }

    private void Initialization(View view) {
        userNotes = view.findViewById(R.id.notes_card);
        dictionary = view.findViewById(R.id.dictionary_card);
        studyTimer = view.findViewById(R.id.timer_card);
    }
}