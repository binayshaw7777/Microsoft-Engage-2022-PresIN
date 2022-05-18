package com.geekym.face_recognition_engage.Authentication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Objects;

public class SignUp_Second_Activity extends AppCompatActivity {

    EditText Name, Email, CollegeName, OrgID, Password;
    TextView LoginPage;
    private FirebaseAuth mAuth;
    boolean passwordVisible;
    View buttonView;
    ProgressBar buttonProgress;
    TextView buttonText;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_second);

        Initialization();

        buttonText.setText("Create Account");
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        cl.setBackground(getResources().getDrawable(R.drawable.positive));

        LoginPage.setOnClickListener(view -> intentNow()); //Already have an account

        buttonView.setOnClickListener(view -> {

            if (isConnected()) {

                String Embeddings = getIntent().getStringExtra("Face_Embeddings");

                String sEmail = Email.getText().toString().trim();
                String sPass = Password.getText().toString().trim();
                String sID = OrgID.getText().toString().trim();
                String sCollege = CollegeName.getText().toString().trim();
                String sName = Name.getText().toString().trim();

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

                buttonProgress.setVisibility(View.VISIBLE);
                buttonText.setVisibility(View.GONE);
                mAuth.createUserWithEmailAndPassword(sEmail, sPass)
                        .addOnCompleteListener(SignUp_Second_Activity.this, task -> {
                            if (task.isSuccessful()) {

                                Users users = new Users(sName, sEmail, sID, sCollege, Embeddings);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(users).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                DynamicToast.makeSuccess(this, "Registered Successfully").show();
                                                DynamicToast.makeSuccess(this, "Verification Mail Sent").show();
                                                intentNow();
                                                assert user != null;
                                                user.sendEmailVerification();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                                DynamicToast.makeError(this, "Failed").show();
                                            }
                                            buttonProgress.setVisibility(View.GONE);
                                            buttonText.setVisibility(View.VISIBLE);
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                DynamicToast.makeError(this, "Authentication Failed").show();
                                buttonProgress.setVisibility(View.GONE);
                                buttonText.setVisibility(View.VISIBLE);
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

    private void Initialization() {
        Name = findViewById(R.id.name_box);
        Email = findViewById(R.id.email_box);
        CollegeName = findViewById(R.id.college_name_box);
        OrgID = findViewById(R.id.id_box);
        Password = findViewById(R.id.pass_box);
        LoginPage = findViewById(R.id.SignIn_tv);
        mAuth = FirebaseAuth.getInstance();
        buttonView = findViewById(R.id.login_button);
        buttonProgress = findViewById(R.id.buttonProgress);
        buttonText = findViewById(R.id.buttonText);
    }
}