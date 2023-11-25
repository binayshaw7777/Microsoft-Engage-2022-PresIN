package com.geekym.face_recognition_engage.HomeFragments.Status;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Status.Attendees.Attendees;
import com.geekym.face_recognition_engage.R;

public class status_Fragment extends Fragment {

    CardView attendeesList, userProgress, attendancePrediction;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_, container, false);

        Initialization(view); //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = requireContext().getSharedPreferences("userData", 0);
        String Admin = userDataSP.getString("admin", "false");

        assert Admin != null;
        if (Admin.equals("true")) {
            attendeesList.setVisibility(View.GONE);
        }

        attendeesList.setOnClickListener(v -> intentNow(Attendees.class));      //Starts the Attendees Activity

        userProgress.setOnClickListener(view1 -> intentNow(Progress_Activity.class));   //Starts the Progress Activity

        attendancePrediction.setOnClickListener(view1 -> intentNow(AttendanceCalculateActivity.class));   //Starts the Progress Activity


        return view;
    }

    //Single Method to handle Intents in this activity
    private void intentNow(Class<?> targetClass) {
        startActivity(new Intent(getContext(), targetClass));
    }

    //Function to initialize the variables
    private void Initialization(View view) {
        attendeesList = view.findViewById(R.id.attendees_card);
        userProgress = view.findViewById(R.id.progress_card);
        attendancePrediction = view.findViewById(R.id.attendace_prediction_card);
    }
}