package com.geekym.face_recognition_engage.HomeFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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


public class profile_Fragment extends Fragment {

    ImageButton Logout;
    ImageView EditProfile;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    TextView Name, Email, CollegeID, CollegeName;
    Button Delete;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables", "ObsoleteSdkInt"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        Initialization(view); //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = requireContext().getSharedPreferences("userData", 0);
        String SPname = userDataSP.getString("name", "0");
        String SPemail = userDataSP.getString("email", "0");
        String SPcollegeID = userDataSP.getString("collegeID", "0");
        String SPcollegeName = userDataSP.getString("collegeName", "0");
        String userID = userDataSP.getString("userID", "0");


        //If the user data was saved in SharedPreference -> which is always true, still checking to make sure it does not creates issue
        if (!SPemail.equals("0") && !SPcollegeID.equals("0") && !SPname.equals("0") && !SPcollegeName.equals("0")) {
            Name.setText("Name: " + SPname);
            Email.setText("Email: " + SPemail);
            CollegeID.setText("College ID: " + SPcollegeID);
            CollegeName.setText("College Name: " + SPcollegeName);
        }

        Calendar cal = Calendar.getInstance(); //Initializing Calendar

        //year, month name, date to Stirngs
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());

        Delete.setOnClickListener(view1 -> {
            //Pop a dialog when the user clicks on Delete Account Button, warn them

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.custom_dialog);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
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

        Logout.setOnClickListener(view1 -> {
            //Pop up a Dialog when the user clicks on logout button, ask them whether they really want to logout or not.

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.custom_dialog);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

            Button Proceed = dialog.findViewById(R.id.proceed);
            Button Cancel = dialog.findViewById(R.id.cancel);
            TextView title = dialog.findViewById(R.id.dialog_title);
            TextView description = dialog.findViewById(R.id.dialog_description);

            Proceed.setText("Logout");
            Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
            title.setText("Confirm Logout");
            description.setText("Do you really want to logout?");

            Proceed.setOnClickListener(v -> { //On logout -> Logout the current logged in user
                dialog.dismiss();
                LogoutFun();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss()); //On Cancel
            dialog.show();
        });

        EditProfile.setOnClickListener(view1 -> {
            //Dialog Popup of EditText

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.edittext_dialog);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

            Button Proceed = dialog.findViewById(R.id.proceed);
            Button Cancel = dialog.findViewById(R.id.cancel);
            EditText editText = dialog.findViewById(R.id.edittext_box);

            Proceed.setOnClickListener(v -> {

                String inName = editText.getText().toString().trim();

                if (!inName.isEmpty()) { //If the user's input is not Empty

                    reference.child("Users").child(userID).child("name").setValue(inName); //Updating the name of the user
                    Name.setText("Name: " + inName); //also updating the name in the TextView of Profile Fragment
                    SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
                    editor.putString("name", inName);      //Replacing the name value with updated name
                    editor.apply();

                    String collegeName = userDataSP.getString("collegeName", ""); //fetching college name from SharedPreference

                    //Changing the name of user in Attendance Node of the Firebase
                    reference.child("Attendees").child(collegeName).child(year).child(month).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.hasChild(userID)) {
                                //Replacing the student name from Attendance Log
                                reference.child("Attendees").child(collegeName).child(year).child(month).child(date).child(userID).child("name").setValue(inName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else
                    DynamicToast.makeError(requireContext(), "Please enter something").show(); //If the user's input is empty

                dialog.dismiss();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss());
            dialog.show();

        });

        return view;
    }

    private void LogoutFun() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SignIn_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    //To delete account of the current user
    private void deleteAccount() {
        SharedPreferences userDataSP = requireContext().getSharedPreferences("userData", 0);
        String userID = userDataSP.getString("userID", "0");
        final FirebaseUser currentUser = mAuth.getCurrentUser(); //get the current user
        assert currentUser != null;
        currentUser.delete().addOnCompleteListener(task -> {
            reference.child("Users").child(userID).setValue(null);
            if (task.isSuccessful()) {
                DynamicToast.makeSuccess(requireContext(), "Account Deleted Successfully!").show();
                LogoutFun();
            } else {
                DynamicToast.makeError(requireContext(), "Something went wrong!").show();
            }
        });
    }

    //Function to initialize the variables
    private void Initialization(View view) {
        Logout = view.findViewById(R.id.logout_button);
        reference = FirebaseDatabase.getInstance().getReference();
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);
        CollegeID = view.findViewById(R.id.uid);
        mAuth = FirebaseAuth.getInstance();
        CollegeName = view.findViewById(R.id.college_name);
        EditProfile = view.findViewById(R.id.edit);
        Delete = view.findViewById(R.id.delete);
    }

}