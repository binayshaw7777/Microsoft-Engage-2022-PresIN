package com.geekym.face_recognition_engage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Settings.Profile;
import com.geekym.face_recognition_engage.HomeFragments.Status.status_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.home_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.Settings.settings_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.Tools.tools_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreen extends AppCompatActivity {

    BottomNavigationView bottomBar; //Bottom navigation bar
    private DatabaseReference reference;
    Dialog dialog;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Initialization();     //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = HomeScreen.this.getSharedPreferences("userData", 0);
        String SPname = userDataSP.getString("name", "0");
        String SPcollegeID = userDataSP.getString("collegeID", "0");
        String SPcollegeName = userDataSP.getString("collegeName", "0");
        String SPcollegeYear = userDataSP.getString("year", "0");
        String SPPhone = userDataSP.getString("phone", "0");
        String userID = userDataSP.getString("userID", "0");

        //If the data is changed/modified/edited on Firebase RealTime Database
        //We can handle it here. This will edit the shared-preference
        reference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class); //Get User Object from the Firebase User Node
                if (userprofile != null) {
                    String name = userprofile.name;
                    String phone = userprofile.phone;
                    String collegeYear = userprofile.year;
                    String collegeName = userprofile.college;
                    String collegeID = userprofile.id;

                    if (!SPname.equals(name)) {
                        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                        editor.putString("name", name);      //Replacing the name value with updated phone no.
                        editor.apply();
                    }

                    if (!SPPhone.equals(phone)) {
                        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                        editor.putString("phone", phone);      //Replacing the name value with updated phone no.
                        editor.apply();
                    }

                    if (!SPcollegeYear.equals(collegeYear)) {
                        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                        editor.putString("year", collegeYear);      //Replacing the name value with updated phone no.
                        editor.apply();
                    }

                    if (!SPcollegeID.equals(collegeID)) {
                        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                        editor.putString("collegeID", collegeID);      //Replacing the name value with updated phone no.
                        editor.apply();
                    }

                    if (!SPcollegeName.equals(collegeName)) {
                        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                        editor.putString("collegeName", collegeName);      //Replacing the name value with updated phone no.
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.main, new home_Fragment()).commit();

        bottomBar.setSelectedItemId(R.id.home);
        bottomBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {

                case R.id.home:
                    fragment = new home_Fragment();
                    break;

                case R.id.scan:
                    fragment = new status_Fragment();
                    break;

                case R.id.tools:
                    fragment = new tools_fragment();
                    break;

                case R.id.settings:
                    fragment = new settings_Fragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
            return true;
        });

    }

    //Function to initialize the variables
    private void Initialization() {
        bottomBar = findViewById(R.id.bottomBar);
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ObsoleteSdkInt", "SetTextI18n"})
    public void onBackPressed() {

        //Pop-up Dialog box when back pressed -> ask the user if they want to exit or not.

        dialog = new Dialog(HomeScreen.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(HomeScreen.this.getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView description = dialog.findViewById(R.id.dialog_description);

        Proceed.setText("Exit");
        Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
        title.setText("Confirm exit");
        description.setText("Do you really want to exit?");

        Proceed.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}