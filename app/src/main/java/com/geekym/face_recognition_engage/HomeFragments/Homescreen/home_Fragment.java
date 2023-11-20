package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.Attendance.Attendance_Scanner_Activity;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.adapter.PromptAdapter;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.qr_screens.QRGeneratorActivity;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.qr_screens.QRScannerActivity;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.utils.JavaUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class home_Fragment extends Fragment implements PromptAdapter.PromptClickListener {

    private DatabaseReference reference;
    private String userID;
    ImageView clockInOut;
    TextView DateDis, PresentMark_Time;
    Calendar calendar;
    String isAdmin;
    LinearLayout attendanceBox, timeDate;

    PromptAdapter promptAdapter;

    RecyclerView promptRecyclerView;

    FloatingActionButton goToTeachersPromptScreen;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        SharedPreferences userData = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE); //Creating SharedPreference

        Initialization(view);   //Function to initialize the variables

        //Fetching userData from SharedPreference
        String SPname = userData.getString("name", "0");
        String SPemail = userData.getString("email", "0");
        String SPcollegeID = userData.getString("collegeID", "0");
        String SPcollegeName = userData.getString("collegeName", "0");
        String SPAdmin = userData.getString("admin", "0");

        String userIDSP = userData.getString("userID", "0");
        isAdmin = userData.getString("admin", "false");
        Log.d("", "UID : " + userIDSP);
        if (isAdmin.equals("true")) {
            attendanceBox.setVisibility(View.GONE);
            clockInOut.setVisibility(View.GONE);
            timeDate.setVisibility(View.GONE);
        } else {
            goToTeachersPromptScreen.setVisibility(View.GONE);
        }

        StringBuilder markTime = new StringBuilder(Objects.requireNonNull(userData.getString("markTime", "0"))); //Fetching marked attendance time


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

        try {

            FirebaseRecyclerOptions<ClassPrompt> classPrompt;

            if (isAdmin.equals("true")) {
                classPrompt =
                        new FirebaseRecyclerOptions.Builder<ClassPrompt>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendees").child(SPcollegeName).child(year).child(month).child(date).orderByChild("userID").equalTo(userID), ClassPrompt.class).build();
            } else {
                classPrompt =
                        new FirebaseRecyclerOptions.Builder<ClassPrompt>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendees").child(SPcollegeName).child(year).child(month).child(date), ClassPrompt.class).build();
            }
            promptAdapter = new PromptAdapter(classPrompt, isAdmin.equals("true"), this);

        } catch (Exception e) {
            Log.d("", "Firebase rcv issue: " + e);
        }


        if (promptAdapter.getItemCount() == 0) {
            Log.d("Size is", String.valueOf(promptAdapter.getItemCount()));
            promptRecyclerView.setVisibility(View.GONE);
        } else {
            promptRecyclerView.setAdapter(promptAdapter);
        }

        promptAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            //Handle the condition when there's any change in item count of the adapter
            void checkEmpty() {
                if (promptAdapter.getItemCount() == 0) { //If no item found in adapter
                    promptRecyclerView.setVisibility(View.GONE); //Disable recyclerView

                } else { //If item is found in adapter
                    promptRecyclerView.setAdapter(promptAdapter);
                }
            }
        });


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

        goToTeachersPromptScreen.setOnClickListener(goTo -> {
            if (isAdmin.equals("true")) {
                startActivity(new Intent(requireContext(), TeachersFormScreen.class));
            }
        });

        clockInOut.setOnClickListener(view1 -> {
            if (isConnected()) {    //To check Internet Connectivity

                if (markTime.toString().equals("0")) {
                    startActivity(new Intent(getContext(), Attendance_Scanner_Activity.class)); //To Face Scanning (Marking Attendance) Activity
                } else {
                    //Pop a dialog when the user clicks on Delete Account Button, warn them
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false); //Optional
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

                    Button Proceed = dialog.findViewById(R.id.proceed);
                    Button Cancel = dialog.findViewById(R.id.cancel);
                    TextView title = dialog.findViewById(R.id.dialog_title);
                    TextView description = dialog.findViewById(R.id.dialog_description);

                    Proceed.setText("Yes, retake");
                    Proceed.setBackground(getResources().getDrawable(R.drawable.positive));
                    title.setText("Attendance already marked");
                    description.setText("Do you want to retake your attendance?");

                    Proceed.setOnClickListener(v -> { //On Delete button press -> Call delete function
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), Attendance_Scanner_Activity.class)); //To Face Scanning (Marking Attendance) Activity
                    });

                    Cancel.setOnClickListener(v -> dialog.dismiss()); //On Cancel
                    dialog.show();
                }

            } else {
                DynamicToast.makeError(requireContext(), "Please connect to Internet").show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        promptRecyclerView.setVisibility(View.VISIBLE);
        promptAdapter.startListening();
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
        attendanceBox = view.findViewById(R.id.attendance_box);
        timeDate = view.findViewById(R.id.linearLayout2);
        goToTeachersPromptScreen = view.findViewById(R.id.teachersPromptFormBtn);
        promptRecyclerView = view.findViewById(R.id.prompt_recycler_view);
        promptRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(ClassPrompt model, Integer clickedMode) {
        Log.d("", "Clicked on: " + clickedMode);

        if (clickedMode == JavaUtils.CARD_VIEW_CLICKED.intValue()) {

            if (!isAdmin.equals("true")) { //TODO: REMOVE BEFORE PUSHING
                Intent intentToAttendanceScanner = new Intent(requireContext(), Attendance_Scanner_Activity.class);
                intentToAttendanceScanner.putExtra("classPrompt", model);
                startActivity(intentToAttendanceScanner);
            } else {
                Log.d("", "String value of classPrompt is: " + model.toString());
                Intent intentToStudentList = new Intent(requireContext(), SeeAllStudentsActivity.class);
                intentToStudentList.putExtra("classPrompt", model);
                startActivity(intentToStudentList);
            }
        } else {
            if (!isAdmin.equals("true")) { //TODO: REMOVE BEFORE PUSHING
                Toast.makeText(requireContext(), "Open qr scanner", Toast.LENGTH_SHORT).show();
                Intent intentToQRGenerator = new Intent(requireContext(), QRScannerActivity.class);
                startActivity(intentToQRGenerator);
            } else {
                Toast.makeText(requireContext(), "Open qr generator", Toast.LENGTH_SHORT).show();
                Intent intentToQRGenerator = new Intent(requireContext(), QRGeneratorActivity.class);
                intentToQRGenerator.putExtra("classPrompt", model);
                startActivity(intentToQRGenerator);
            }
        }
    }
}