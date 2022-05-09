package com.geekym.face_recognition_engage.HomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.geekym.face_recognition_engage.Attendance_Result_Activity;
import com.geekym.face_recognition_engage.Attendance_Scanner_Activity;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class home_Fragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    ImageView clockInOut;
    TextView DateDis;
    TextClock clock;
    Calendar calendar;
    SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        Initialization(view);

        clockInOut.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Attendance_Scanner_Activity.class)));

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, MMM d");
        String date = dateFormat.format(calendar.getTime());
        DateDis.setText(date);

        return view;
    }

    private void Initialization(View view) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        DateDis = view.findViewById(R.id.text_view_date);
        clock = view.findViewById(R.id.textClock);
        clockInOut = view.findViewById(R.id.clock_inout);
    }
}