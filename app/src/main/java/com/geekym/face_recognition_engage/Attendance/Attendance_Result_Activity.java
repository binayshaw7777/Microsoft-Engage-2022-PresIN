package com.geekym.face_recognition_engage.Attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.HomeScreen;
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
import java.util.HashMap;

public class Attendance_Result_Activity extends AppCompatActivity {

    Button Home;
    TextView Name;
    private DatabaseReference reference;
    private String userID;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        Initialize();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        Date today = new Date();
        final String Time = timeFormat.format(today);

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());

        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() { //To display user's data in card view
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users userprofile = snapshot.getValue(Users.class);

                assert userprofile != null;
                String name = userprofile.name;
                String id = userprofile.id;
                Name.setText(name);

                HashMap<String, String> map = new HashMap<>();
                map.put("time", Time);
                map.put("status", "Present");
                reference.child("Users").child(userID).child("Attendance").child(year).child(month).child(date).setValue(map);
                map.put("name", name);
                map.put("id", id);
                reference.child("Attendees").child(year).child(month).child(date).child(userID).setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

        Home.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeScreen.class)));

    }

    private void Initialize() {
        Home = findViewById(R.id.back_home);
        Name = findViewById(R.id.name_display);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
    }
}