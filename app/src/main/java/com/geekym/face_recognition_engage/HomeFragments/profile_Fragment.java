package com.geekym.face_recognition_engage.HomeFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
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


public class profile_Fragment extends Fragment {

    ImageButton Logout;
    ImageView EditProfile;
    private DatabaseReference reference;
    private String userID;
    private FirebaseAuth mAuth;
    TextView Name, Email, CollegeID, CollegeName;
    Button Delete;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables", "ObsoleteSdkInt"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        Initialization(view); //Function to initialize the variables

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

        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() { //To display user's data in card view
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class); //Creating user object from the firebase user node
                if (userprofile != null) {

                    //User object -> Strings
                    String name = userprofile.name;
                    String email = userprofile.email;
                    String uid = userprofile.id;
                    String org = userprofile.college;

                    //Setting up textViews
                    Name.setText("Name: " + name);
                    Email.setText("Email: " + email);
                    CollegeID.setText("College ID: " + uid);
                    CollegeName.setText("College Name: " + org);

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

                                //Changing the name of user in Attendance Node of the Firebase
                                reference.child("Attendees").child(year).child(month).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.hasChild(userID)) {
                                            reference.child("Attendees").child(year).child(month).child(date).child(userID).child("name").setValue(inName);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else DynamicToast.makeError(requireContext(), "Please enter something").show(); //If the user's input is empty

                            dialog.dismiss();
                        });

                        Cancel.setOnClickListener(v -> dialog.dismiss());
                        dialog.show();

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);
        CollegeID = view.findViewById(R.id.uid);
        mAuth = FirebaseAuth.getInstance();
        CollegeName = view.findViewById(R.id.college_name);
        EditProfile = view.findViewById(R.id.edit);
        Delete = view.findViewById(R.id.delete);
    }

}