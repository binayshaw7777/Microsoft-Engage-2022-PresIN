package com.geekym.face_recognition_engage.Attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

        Initialize(); //Function to initialize the variables

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); //Eg: 12:33 AM & 12:33 PM
        final String Time = timeFormat.format(new Date());      //Storing current time in string of the above mentioned format

        SharedPreferences userDataSP = Attendance_Result_Activity.this.getSharedPreferences("userData", 0);
        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
        editor.putString("markTime", Time);      //Replacing the name value with updated name
        editor.apply();

        String name = userDataSP.getString("name", "0");    //Name from SharedPreference
        String CollegeID = userDataSP.getString("collegeID", "0");     //CollegeID from SharedPreference
        String CollegeName = userDataSP.getString("collegeName", "0");  //CollegeName from SharedPreference

        Calendar cal = Calendar.getInstance(); //Creating a calendar instance
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());   //Storing year as string
        String month = new SimpleDateFormat("MMM").format(cal.getTime());   //Storing month name as string
        String date = new SimpleDateFormat("dd").format(cal.getTime());     //Storing date as string

        Name.setText(name);     //Setting the textView text -> User's Name

        HashMap<String, String> map = new HashMap<>(); //Creating a Hashmap to store the attendance status
        map.put("time", Time);
        map.put("status", "Present");

        //Pushing the data in User's node
        reference.child("Users").child(userID).child("Attendance").child(year).child(month).child(date).setValue(map);
        map.put("name", name);
        map.put("id", CollegeID);

        //Pushing the data in Global Attendance Node with extra data like name and College ID
        reference.child("Attendees").child(CollegeName).child(year).child(month).child(date).child(userID).setValue(map);

        //Takes the user to Home Fragment after successfully taking the Attendance
        Home.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeScreen.class)));

    }

    //Function to initialize the variables
    private void Initialize() {
        Home = findViewById(R.id.back_home);
        Name = findViewById(R.id.name_display);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
    }
}