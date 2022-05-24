package com.geekym.face_recognition_engage.Authentication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Objects;

public class SignUp_Second_Activity extends AppCompatActivity {

    EditText Name, Email, OrgID, Password;
    TextView LoginPage;
    private FirebaseAuth mAuth;
    boolean passwordVisible;
    View CreateAccount_Button;
    ProgressBar buttonProgress;
    TextView CreateAccount_Text;
    Spinner spinner;
    DatabaseReference databaseRef;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    StringBuilder selectedItem = new StringBuilder();
    ImageView registerCollege;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_second);

        Initialization();     //Function to initialize the variables

        list.add("College Name");
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem.setLength(0);
                selectedItem.append(parent.getItemAtPosition(position).toString());
            }
            // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        registerCollege.setOnClickListener(v -> insertCollegeNames());

        fetchCollegeNames();

        CreateAccount_Text.setText("Create Account");
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        cl.setBackground(getResources().getDrawable(R.drawable.positive)); //Change the button drawable to green

        LoginPage.setOnClickListener(view -> intentNow()); //Already have an account

        CreateAccount_Button.setOnClickListener(view -> {

            if (isConnected()) { //Check internet connection

                String Embeddings = getIntent().getStringExtra("Face_Embeddings"); //Get embeddings of the user's face

                //Get all the input from the user
                String sEmail = Email.getText().toString().trim();
                String sPass = Password.getText().toString().trim();
                String sID = OrgID.getText().toString().trim();
                String sName = Name.getText().toString().trim();

                //Check if the details entered are valid or not
                if (sName.isEmpty()) {
                    Name.setError("Field can't be empty");
                    Name.requestFocus();
                    return;
                }
                if (sEmail.isEmpty()) {
                    Email.setError("Field can't be empty");
                    Email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    Email.setError("Please enter a valid email address");
                    Email.requestFocus();
                    return;
                } else if (sPass.isEmpty()) {
                    Password.setError("Field can't be empty");
                    Password.requestFocus();
                    return;
                } else if (sPass.length() < 6) {
                    Password.setError("Password must be at least 6 characters");
                    Password.requestFocus();
                    return;
                } else if (sID.isEmpty()) {
                    OrgID.setError("Field can't be empty");
                    OrgID.requestFocus();
                    return;
                } else if(selectedItem.toString().equals("College Name")) {
                    DynamicToast.makeError(SignUp_Second_Activity.this, "Select College Name").show();
                    return;
                }

                //Creating a new Account
                buttonProgress.setVisibility(View.VISIBLE);
                CreateAccount_Text.setVisibility(View.GONE);
                mAuth.createUserWithEmailAndPassword(sEmail, sPass)
                        .addOnCompleteListener(SignUp_Second_Activity.this, task -> {
                            if (task.isSuccessful()) {
                                //Successfully Created a new account

                                Users users = new Users(sName, sEmail, sID, selectedItem.toString(), "0", "0", Embeddings); //Creating a User Object with the inputs by user

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(users).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                DynamicToast.makeSuccess(this, "Registered Successfully").show();
                                                DynamicToast.makeSuccess(this, "Verification Mail Sent").show(); //Send a verification link
                                                intentNow();
                                                assert user != null;
                                                user.sendEmailVerification();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                                DynamicToast.makeError(this, "Failed").show();
                                            }
                                            buttonProgress.setVisibility(View.GONE);
                                            CreateAccount_Text.setVisibility(View.VISIBLE);
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                DynamicToast.makeError(this, "Authentication Failed").show();
                                buttonProgress.setVisibility(View.GONE);
                                CreateAccount_Text.setVisibility(View.VISIBLE);
                            }
                        });
            }

        });


        // Function to see password and hide password
        Password.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= Password.getRight() - Password.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = Password.getSelectionEnd();
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                        //for hide password
                        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        //set drawable image here
                        Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                        //for show password
                        Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    Password.setLongClickable(false); //Handles Multiple option popups
                    Password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

    }

    private void fetchCollegeNames() {
        listener = databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren())
                    list.add(Objects.requireNonNull(snap.getValue()).toString());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //To check Internet Connectivity
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;

        DynamicToast.makeError(getApplicationContext(), "You're not connected to Internet!").show();
        return false;
    }

    private void intentNow() {
        startActivity(new Intent(getApplicationContext(), SignIn_Activity.class));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    public void insertCollegeNames() {

        Dialog dialog = new Dialog(SignUp_Second_Activity.this);
        dialog.setContentView(R.layout.edittext_dialog);
        dialog.getWindow().setBackgroundDrawable(SignUp_Second_Activity.this.getDrawable(R.drawable.custom_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        EditText editText = dialog.findViewById(R.id.edittext_box);
        TextView title = dialog.findViewById(R.id.dialog_title);

        Proceed.setText("Add College");
        editText.setHint("Enter College Name here");
        title.setText("Register your College");

        Proceed.setOnClickListener(v -> {

            String inputCollege = editText.getText().toString().trim();

            if (!inputCollege.isEmpty()) { //If the user's input is not Empty

                databaseRef.push().setValue(inputCollege)
                        .addOnCompleteListener(task -> {
                            list.clear();
                            fetchCollegeNames();
                            adapter.notifyDataSetChanged();
                            DynamicToast.makeSuccess(SignUp_Second_Activity.this, "College Registered!").show();
                        });

            } else DynamicToast.makeError(getApplicationContext(), "Please enter something").show(); //If the user's input is empty

            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //Function to initialize the variables
    private void Initialization() {
        Name = findViewById(R.id.name_box);
        Email = findViewById(R.id.email_box);
        OrgID = findViewById(R.id.id_box);
        Password = findViewById(R.id.pass_box);
        LoginPage = findViewById(R.id.SignIn_tv);
        mAuth = FirebaseAuth.getInstance();
        CreateAccount_Button = findViewById(R.id.login_button);
        buttonProgress = findViewById(R.id.buttonProgress);
        CreateAccount_Text = findViewById(R.id.buttonText);
        spinner = (Spinner) findViewById(R.id.college_names_spinner);
        databaseRef = FirebaseDatabase.getInstance().getReference("College_Names");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        registerCollege = findViewById(R.id.add_college);
    }
}