package com.geekym.face_recognition_engage.HomeFragments.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.R;
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

public class AccountSettings extends AppCompatActivity {

    Button Delete, Confirm;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    EditText NameEdit, PhoneEdit, YearEdit;

    @SuppressLint("SetTextI18n")
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

            //year, month name, date to Stirngs
            String year = new SimpleDateFormat("yyyy").format(cal.getTime());
            String month = new SimpleDateFormat("MMM").format(cal.getTime());
            String date = new SimpleDateFormat("dd").format(cal.getTime());

            String inputName = NameEdit.getText().toString();
            String inputPhone = PhoneEdit.getText().toString();
            String inputYear = YearEdit.getText().toString();


            if (!inputYear.isEmpty()) { //If the user's input is not Empty
                reference.child("Users").child(userID).child("year").setValue(inputYear); //Updating the study year of the user
                YearEdit.setHint(inputYear); //also updating the study year in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("year", inputYear);      //Replacing the name value with updated study year
                editor.apply();
            }

            if (!inputPhone.isEmpty()) { //If the user's input is not Empty
                reference.child("Users").child(userID).child("phone").setValue(inputPhone); //Updating the phone no. of the user
                PhoneEdit.setHint(inputPhone); //also updating the phone no. in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("phone", inputPhone);      //Replacing the name value with updated phone no.
                editor.apply();
            }

            if (!inputName.isEmpty()) { //If the user's input is not Empty

                reference.child("Users").child(userID).child("name").setValue(inputName); //Updating the name of the user
                NameEdit.setHint(inputName); //also updating the name in the TextView of Profile Fragment
                SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                editor.putString("name", inputName);      //Replacing the name value with updated name
                editor.apply();

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
            }
        });

        Delete.setOnClickListener(view1 -> {
            //Pop a dialog when the user clicks on Delete Account Button, warn them

            Dialog dialog = new Dialog(AccountSettings.this);
            dialog.setContentView(R.layout.custom_dialog);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(AccountSettings.this.getDrawable(R.drawable.custom_dialog_background));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

            Button Proceed = dialog.findViewById(R.id.proceed);
            Button Cancel = dialog.findViewById(R.id.cancel);
            TextView title = dialog.findViewById(R.id.dialog_title);
            TextView description = dialog.findViewById(R.id.dialog_description);

            Proceed.setText("Delete");
            Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
            title.setText("Confirm Delete");
            description.setText("Do you really want to delete your account?");

            Proceed.setOnClickListener(v -> { //On Delete button press -> Call delete function
                dialog.dismiss();

                deleteAccount();

            });

            Cancel.setOnClickListener(v -> dialog.dismiss()); //On Cancel
            dialog.show();
        });
    }

    //To delete account of the current user
    private void deleteAccount() {
        SharedPreferences userDataSP = AccountSettings.this.getSharedPreferences("userData", 0);
        String userID = userDataSP.getString("userID", "0");
        final FirebaseUser currentUser = mAuth.getCurrentUser(); //get the current user
        assert currentUser != null;
        currentUser.delete().addOnCompleteListener(task -> {
            reference.child("Users").child(userID).setValue(null);
            if (task.isSuccessful()) {
                SharedPreferences.Editor editor = userDataSP.edit();
                editor.clear();
                editor.apply();
                DynamicToast.makeSuccess(AccountSettings.this, "Account Deleted Successfully!").show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                DynamicToast.makeError(AccountSettings.this, "Something went wrong!").show();
            }
        });
    }

    private void Initialization() {
        Delete = findViewById(R.id.delete);
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        NameEdit = findViewById(R.id.editName);
        PhoneEdit = findViewById(R.id.editPhone);
        YearEdit = findViewById(R.id.editCollegeYear);
        Confirm = findViewById(R.id.confirm);
    }
}