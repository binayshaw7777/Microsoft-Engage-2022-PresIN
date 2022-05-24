package com.geekym.face_recognition_engage.HomeFragments.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.geekym.face_recognition_engage.R;

public class Profile extends AppCompatActivity {

    TextView Name, Email, CollegeID, CollegeName, CollegeYear, Phone;
    Button ProfileToEditProfile;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Initialization();     //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = Profile.this.getSharedPreferences("userData", 0);
        String SPname = userDataSP.getString("name", "0");
        String SPemail = userDataSP.getString("email", "0");
        String SPcollegeID = userDataSP.getString("collegeID", "0");
        String SPcollegeName = userDataSP.getString("collegeName", "0");
        String SPcollegeYear = userDataSP.getString("year", "0");
        String SPPhone = userDataSP.getString("phone", "0");
        String userID = userDataSP.getString("userID", "0");

        //If the user data was saved in SharedPreference -> which is always true, still checking to make sure it does not creates issue
        if (!SPemail.equals("0") && !SPcollegeID.equals("0") && !SPname.equals("0") && !SPcollegeName.equals("0")) {
            Name.setText("Name: " + SPname);
            Email.setText("Email: " + SPemail);
            CollegeID.setText("College ID: " + SPcollegeID);
            CollegeName.setText("College Name: " + SPcollegeName);
        }

        //Check for Phone/Contact Number
        if (SPPhone.equals("0"))
            Phone.setText("Phone: Not Added");
        else
            Phone.setText("Phone: " + SPPhone);

        //Check for Study Year
        if (SPcollegeYear.equals("0"))
            CollegeYear.setText("Study Year: Not Added");
        else
            CollegeYear.setText("Year: " + SPcollegeYear);


        ProfileToEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountSettings.class);
            startActivity(intent);
            finish();
        });

    }

    //Function to initialize the variables
    private void Initialization() {
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        CollegeID = findViewById(R.id.college_id);
        Phone = findViewById(R.id.phone);
        CollegeYear = findViewById(R.id.college_year);
        CollegeName = findViewById(R.id.college_name);
        ProfileToEditProfile = findViewById(R.id.ProfileToEdit);
    }
}