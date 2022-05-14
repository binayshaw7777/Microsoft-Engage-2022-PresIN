package com.geekym.face_recognition_engage.Authentication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Objects;

public class SignUp_Second_Activity extends AppCompatActivity {

    EditText Name, Email, CollegeName, OrgID, Password;
    Button Signup;
    TextView LoginPage;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_second);

        Initialization();

        LoginPage.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SignIn_Activity.class))); //Already have an account

        Signup.setOnClickListener(view -> {

            String Embeddings = getIntent().getStringExtra("Face_Embeddings");

            String sEmail = Email.getText().toString();
            String sPass = Password.getText().toString();
            String sID = OrgID.getText().toString();
            String sCollege = CollegeName.getText().toString();
            String sName = Name.getText().toString();

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
            } else if (sCollege.isEmpty()) {
                CollegeName.setError("Field can't be empty");
                CollegeName.requestFocus();
                return;
            }

            //  validate(sName, sEmail, sPass, sID, sOrg);

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(sEmail, sPass)
                    .addOnCompleteListener(SignUp_Second_Activity.this, task -> {
                        if (task.isSuccessful()) {

                            Users users = new Users(sName, sEmail, sID, sCollege, Embeddings);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(users).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            DynamicToast.makeSuccess(this, "Registered Successfully").show();
                                            DynamicToast.makeSuccess(this, "Verification Mail Sent").show();
                                            startActivity(new Intent(getApplicationContext(), SignIn_Activity.class));
                                            assert user != null;
                                            user.sendEmailVerification();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                            DynamicToast.makeError(this, "Failed").show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            DynamicToast.makeSuccess(this, "Authentication Failed").show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
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

    private void Initialization() {
        Name = findViewById(R.id.name_box);
        Email = findViewById(R.id.email_box);
        CollegeName = findViewById(R.id.college_name_box);
        OrgID = findViewById(R.id.id_box);
        Password = findViewById(R.id.pass_box);
        Signup = findViewById(R.id.signup_button);
        LoginPage = findViewById(R.id.SignIn_tv);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar_SignUp1);
    }
}