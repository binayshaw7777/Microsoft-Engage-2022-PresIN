package com.geekym.face_recognition_engage.HomeFragments.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.Authentication.SignUp_Second_Activity;
import com.geekym.face_recognition_engage.HomeScreen;
import com.geekym.face_recognition_engage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

    Button Confirm;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    EditText NameEdit, PhoneEdit, YearEdit;
    ImageButton Delete;
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
        String SPAdmin = userDataSP.getString("admin", "0");
        String SPEmail = userDataSP.getString("email", "0");

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

        Delete.setOnClickListener(view1 -> {
            //Pop a dialog when the user clicks on Delete Account Button, warn them

            Dialog dialog = new Dialog(AccountSettings.this);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(AccountSettings.this.getDrawable(R.drawable.custom_dialog_background));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

            Button Proceed = dialog.findViewById(R.id.proceed);
            Button Cancel = dialog.findViewById(R.id.cancel);
            TextView title = dialog.findViewById(R.id.dialog_title);
            TextView description = dialog.findViewById(R.id.dialog_description);

            Proceed.setText("Delete");
            Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
            title.setText("Confirm delete");
            description.setText("Do you really want to delete your account?");

            Proceed.setOnClickListener(v -> { //On Delete button press -> Call delete function
                dialog.dismiss();

                deleteAccount(userID, SPEmail);

            });

            Cancel.setOnClickListener(v -> dialog.dismiss()); //On Cancel
            dialog.show();
        });
    }

    //To delete account of the current user
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void deleteAccount(String userID, String Email) {

        Dialog dialog = new Dialog(AccountSettings.this);
        dialog.setContentView(R.layout.edittext_dialog);
        dialog.getWindow().setBackgroundDrawable(AccountSettings.this.getDrawable(R.drawable.custom_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        EditText editText = dialog.findViewById(R.id.edittext_box);
        TextView title = dialog.findViewById(R.id.dialog_title);

        Proceed.setText("Delete");
        editText.setHint("Enter Password");
        title.setText("Please enter your password");

        Proceed.setOnClickListener(v -> {

            String inputPassword = editText.getText().toString().trim();

            if (!inputPassword.isEmpty()) { //If the user's input is not Empty

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential authCredential = EmailAuthProvider.getCredential(Email, inputPassword);

                assert firebaseUser != null;
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> firebaseUser.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().getRoot().child("Users");
                        dbNode.child(userID).setValue(null);
                        SharedPreferences userDataSP = AccountSettings.this.getSharedPreferences("userData", 0);
                        SharedPreferences.Editor editor = userDataSP.edit();
                        editor.clear();
                        editor.apply();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        DynamicToast.makeSuccess(AccountSettings.this, "Account deleted successfully").show();
                    } else {
                        DynamicToast.makeError(AccountSettings.this, "Something went wrong!").show();
                    }
                }));

            } else
                DynamicToast.makeError(getApplicationContext(), "Please enter something").show(); //If the user's input is empty

            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void Initialization() {
        Delete = findViewById(R.id.delete_button);
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        NameEdit = findViewById(R.id.editName);
        PhoneEdit = findViewById(R.id.editPhone);
        YearEdit = findViewById(R.id.editCollegeYear);
        Confirm = findViewById(R.id.confirm);
    }
}