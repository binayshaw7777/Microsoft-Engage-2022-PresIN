package com.geekym.face_recognition_engage.HomeFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.Attendance.Attendance_Scanner_Activity;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class home_Fragment extends Fragment {

    private DatabaseReference reference;
    private String userID;
    ImageView clockInOut;
    TextView DateDis, PresentMark_Time;
    TextClock clock;
    Calendar calendar;
    SimpleDateFormat dateFormat1;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
    Date today = new Date();
    String date1 = dateFormat.format(today);

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        Initialization(view);

        calendar = Calendar.getInstance();
        dateFormat1 = new SimpleDateFormat("EEEE, MMM d");
        String date = dateFormat1.format(calendar.getTime());
        DateDis.setText(date);

        reference.child("Attendance").child(date1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userID))
                    PresentMark_Time.setText(snapshot.child(userID).child("Time").getValue().toString());
                else
                    view.findViewById(R.id.attendance_box).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        clockInOut.setOnClickListener(view1 -> reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() { //To display user's data in card view
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);
                if (userprofile != null) {
                    String Embeddings = userprofile.embeddings;
                    Intent intent = new Intent(getContext(), Attendance_Scanner_Activity.class);
                    intent.putExtra("Embeddings", Embeddings);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    private void Initialization(View view) {
        PresentMark_Time = view.findViewById(R.id.in_count);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
        DateDis = view.findViewById(R.id.text_view_date);
        clock = view.findViewById(R.id.textClock);
        clockInOut = view.findViewById(R.id.clock_inout);
    }
}