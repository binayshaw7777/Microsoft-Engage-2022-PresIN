package com.geekym.face_recognition_engage.HomeFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
    SimpleDateFormat dateFormat1;

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        Initialization(view);   //Function to initialize the variables

        calendar = Calendar.getInstance();
        dateFormat1 = new SimpleDateFormat("EEEE, MMM d");
        String date1 = dateFormat1.format(calendar.getTime());
        DateDis.setText(date1);

        Calendar cal = Calendar.getInstance();  //Creating a calendar instance
        //Storing formats of date, year and month name in Strings
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());

        if (isConnected()) {    //To check Internet Connectivity
            reference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users userprofile = snapshot.getValue(Users.class); //Get User Object from the Firebase User Node
                    if (userprofile != null) {
                        String Embeddings = userprofile.embeddings; //Retrieving the Embeddings Json String from User Object
                        if (Embeddings.contains("added")) { //If the embeddings's key is has not been replaced yet with UserID (Important for Face Recognition)
                            String Replaced = Embeddings.replace("added", userID); //Replacing custom key "added" with userID
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Embeddings", Replaced);
                            reference.child("Users").child(userID).child("embeddings").setValue(Replaced); //replacing the key of embeddings in user node
                        }

                        String CollegeName = userprofile.college;
                        //Check if the User is present 'today'
                        reference.child("Attendees").child(CollegeName).child(year).child(month).child(date).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userID))
                                    PresentMark_Time.setText(Objects.requireNonNull(snapshot.child(userID).child("time").getValue()).toString());  //If the user is present
                                else {
                                    PresentMark_Time.setText("--/--");   //If not present or absent, set the text to empty time or "--/--"
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
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