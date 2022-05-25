package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Homescreen.Attendance.Attendance_Scanner_Activity;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class home_Fragment extends Fragment {

    private DatabaseReference reference;
    private String userID;
    ImageView clockInOut;
    TextView DateDis, PresentMark_Time;
    Calendar calendar;

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        SharedPreferences userData = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE); //Creating SharedPreference

        Initialization(view);   //Function to initialize the variables

        StringBuilder markTime = new StringBuilder(userData.getString("markTime", "0")); //Fetching marked attendance time
        String userIDSP = userData.getString("userID", "0");

        if (userIDSP.equals("0")) {
            SharedPreferences.Editor editor = userData.edit();
            editor.putString("userID", userID);
            editor.apply();
        }

        if (!markTime.toString().equals("0")) { //if the user has marked attendance
            PresentMark_Time.setText(markTime); //Setting the text view in Home screen
        }


        calendar = Calendar.getInstance();      //Creating a calendar instance
        //Storing formats of date, year and month name in Strings
        String date1 = new SimpleDateFormat("EEEE, MMM d").format(calendar.getTime());
        DateDis.setText(date1);
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());
        String month = new SimpleDateFormat("MMM").format(calendar.getTime());
        String date = new SimpleDateFormat("dd").format(calendar.getTime());

        if (isConnected()) {    //To check Internet Connectivity
            reference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users userprofile = snapshot.getValue(Users.class); //Get User Object from the Firebase User Node
                    if (userprofile != null) {
                        String Embeddings = userprofile.embeddings; //Retrieving the Embeddings Json String from User Object
                        //Checking if the user is opening the app for the first time
                        if (Embeddings.contains("added")) { //If the embedding's key is has not been replaced yet with UserID (Important for Face Recognition)
                            String Replaced = Embeddings.replace("added", userID); //Replacing custom key "added" with userID
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Embeddings", Replaced);
                            reference.child("Users").child(userID).child("embeddings").setValue(Replaced); //replacing the key of embeddings in user node
                        }

                        //Fetching userData from SharedPreference
                        String SPname = userData.getString("name", "0");
                        String SPemail = userData.getString("email", "0");
                        String SPcollegeID = userData.getString("collegeID", "0");
                        String SPcollegeName = userData.getString("collegeName", "0");
                        String SPcollegeYear = userData.getString("year", "0");
                        String SPPhone = userData.getString("phone", "0");
                        String SPAdmin = userData.getString("admin", "0");

                        //If the user data is not added in SharedPreference
                        if (SPemail.equals("0") && SPcollegeID.equals("0") && SPname.equals("0") && SPcollegeName.equals("0") && SPAdmin.equals("0")) {
                            String Name = userprofile.name;
                            String CollegeID = userprofile.id;
                            String CollegeName = userprofile.college;
                            String Email = userprofile.email;
                            String Phone = userprofile.phone;
                            String Year = userprofile.year;
                            String Admin = userprofile.admin;

                            //Then Update userDate SharedPreference with Firebase Database
                            SharedPreferences.Editor editor = userData.edit();
                            editor.putString("name", Name);
                            editor.putString("collegeID", CollegeID);
                            editor.putString("collegeName", CollegeName);
                            editor.putString("email", Email);
                            editor.putString("userID", userID);
                            editor.putString("year", Year);
                            editor.putString("phone", Phone);
                            editor.putString("admin", Admin);
                            editor.apply();
                        }


                        String CollegeName = userData.getString("collegeName", "0");

                        //Check if the User is present 'today'
                        reference.child("Attendees").child(CollegeName).child(year).child(month).child(date).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userID)) {  //User's Attendance is entered in the list for the day
                                    String attendanceTime = Objects.requireNonNull(snapshot.child(userID).child("time").getValue()).toString();
                                    if (markTime.toString().equals("0")) {      //Attendance time has not been added in SharedPreference
                                        PresentMark_Time.setText(attendanceTime);   //If the user is present on current day
                                        SharedPreferences.Editor editor = userData.edit(); //updating sharedpreference
                                        editor.putString("markTime", attendanceTime);
                                        editor.apply();
                                    }
                                } else {
                                    //No such entry found of User's Attendance in the list
                                    PresentMark_Time.setText("--/--");   //If not present or absent, set the text to empty time or "--/--"
                                    SharedPreferences.Editor editor = userData.edit(); //Updating sharedPreference with default value "0"
                                    editor.putString("markTime", "0");
                                    editor.apply();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        reference.child("Users").child(userID).child("StudyData").child(year).child(month).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.hasChild(date)) {
                                    HashMap<String, String> studyData = new HashMap<>();
                                    studyData.put("MaxStudyTime", "00:00:00");
                                    reference.child("Users").child(userID).child("StudyData").child(year).child(month).child(date).setValue(studyData);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        clockInOut.setOnClickListener(view1 -> {
            if (isConnected()) {    //To check Internet Connectivity
                startActivity(new Intent(getContext(), Attendance_Scanner_Activity.class)); //To Face Scanning (Marking Attendance) Activity
            }
        });

        return view;
    }

    //To check Internet Connectivity
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;

        DynamicToast.makeError(requireContext(), "You're not connected to Internet!").show();
        return false;
    }

    //Function to initialize the variables
    private void Initialization(View view) {
        PresentMark_Time = view.findViewById(R.id.in_count);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
        DateDis = view.findViewById(R.id.text_view_date);
        clockInOut = view.findViewById(R.id.clock_inout);
    }
}