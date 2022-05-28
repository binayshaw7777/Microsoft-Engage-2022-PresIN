package com.geekym.face_recognition_engage.HomeFragments.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.geekym.face_recognition_engage.HomeScreen;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AccountSettings extends AppCompatActivity {

    Button Confirm;
    private DatabaseReference reference;
    EditText NameEdit, PhoneEdit, YearEdit;
    boolean dataChanged = false;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Initialization();

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = AccountSettings.this.getSharedPreferences("userData", 0);
        String SPname = userDataSP.getString("name", "0");
        String SPcollegeYear = userDataSP.getString("year", "0");
        String SPPhone = userDataSP.getString("phone", "0");
        String userID = userDataSP.getString("userID", "0");

        if (!SPname.equals("0"))
            NameEdit.setHint(SPname);

        if (!SPcollegeYear.equals("0"))
            YearEdit.setHint(SPcollegeYear);
        else
            YearEdit.setHint("Add Study Year");

        if (!SPPhone.equals("0"))
            PhoneEdit.setHint(SPPhone);
        else
            PhoneEdit.setHint("Add Phone No.");


        Confirm.setOnClickListener(view1 -> {

            Calendar cal = Calendar.getInstance(); //Initializing Calendar

            //year, month name, date to Strings
            String year = new SimpleDateFormat("yyyy").format(cal.getTime());
            String month = new SimpleDateFormat("MMM").format(cal.getTime());
            String date = new SimpleDateFormat("dd").format(cal.getTime());

            String inputName = NameEdit.getText().toString();
            String inputPhone = PhoneEdit.getText().toString();
            String inputYear = YearEdit.getText().toString();

            if (!inputYear.isEmpty() && !inputYear.equals(SPcollegeYear)) { //If the user's input is not Empty
                reference.child("Users").child(userID).child("year").setValue(inputYear); //Updating the study year of the user
                YearEdit.setHint(inputYear); //also updating the study year in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("year", inputYear);      //Replacing the name value with updated study year
                editor.apply();
                dataChanged = true;
            } else if (inputYear.equals(SPcollegeYear)) {
                DynamicToast.makeError(AccountSettings.this, "Same year entered").show();
                return;
            }

            if (inputPhone.length() == 10 && !inputPhone.equals(SPPhone)) { //If the user's input is not Empty
                reference.child("Users").child(userID).child("phone").setValue(inputPhone); //Updating the phone no. of the user
                PhoneEdit.setHint(inputPhone); //also updating the phone no. in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("phone", inputPhone);      //Replacing the name value with updated phone no.
                editor.apply();
                dataChanged = true;
            } else if (inputPhone.equals(SPPhone)) {
                DynamicToast.makeError(AccountSettings.this, "Same phone no. entered").show();
                return;
            } else if (inputPhone.length() != 0) {
                DynamicToast.makeError(AccountSettings.this, "Number should be 10 digits").show();
            }

            if (!inputName.isEmpty() && !inputName.equals(SPname)) { //If the user's input is not Empty

                reference.child("Users").child(userID).child("name").setValue(inputName); //Updating the name of the user
                NameEdit.setHint(inputName); //also updating the name in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("name", inputName);      //Replacing the name value with updated name
                editor.apply();
                dataChanged = true;
                String collegeName = userDataSP.getString("collegeName", ""); //fetching college name from SharedPreference

                //Changing the name of user in Attendance Node of the Firebase
                reference.child("Attendees").child(collegeName).child(year).child(month).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        if (snapshot1.hasChild(userID)) {
                            //Replacing the student name from Attendance Log
                            reference.child("Attendees").child(collegeName).child(year).child(month).child(date).child(userID).child("name").setValue(inputName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (inputName.equals(SPname)) {
                DynamicToast.makeError(AccountSettings.this, "Same name entered").show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            if (dataChanged)
                DynamicToast.makeSuccess(AccountSettings.this, "Profile updated successfully").show();
            else
                DynamicToast.make(AccountSettings.this, "No changes found").show();
            startActivity(intent);
            finish();
        });
    }


    private void Initialization() {
        reference = FirebaseDatabase.getInstance().getReference();
        NameEdit = findViewById(R.id.editName);
        PhoneEdit = findViewById(R.id.editPhone);
        YearEdit = findViewById(R.id.editCollegeYear);
        Confirm = findViewById(R.id.confirm);
    }
}