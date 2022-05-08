package com.geekym.face_recognition_engage.Authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp_First_Activity extends AppCompatActivity {

    EditText Name, Email, OrgName, OrgID, Password;
    Button ScanFace, Signup;
    TextView LoginPage;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_first);

        Initialization();

        LoginPage.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SignIn_Activity.class))); //Already have an account

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sEmail = Email.getText().toString();
                String sPass = Password.getText().toString();
                String sID = OrgID.getText().toString();
                String sOrg = OrgName.getText().toString();
                String sName = Name.getText().toString();

                validate(sName, sEmail, sPass, sID, sOrg);

                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(sEmail, sPass)
                        .addOnCompleteListener(SignUp_First_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Users users = new Users(sName, sEmail, sID, sOrg);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                startActivity(new Intent(getApplicationContext(), SignIn_Activity.class));
                                                user.sendEmailVerification();
                                                // updateUI(user);
                                                Toast.makeText(SignUp_First_Activity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(SignUp_First_Activity.this, "Verification Mail Sent", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(getApplicationContext(), "User data failed.", Toast.LENGTH_SHORT).show();
                                                //            progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed" + task.getException(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    //  updateUI(null);
                                }
                            }
                        });
            }
        });

        // Function to see password and hide password
        Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= Password.getRight() - Password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = Password.getSelectionEnd();
                        if (passwordVisible) {
                            //set drawable image here
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                            //for hide password
                            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                            Password.setLongClickable(false); //Handles Multiple option popups
                        } else {
                            //set drawable image here
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            //for show password
                            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                            Password.setLongClickable(false); //Handles Multiple option popups
                        }
                        Password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void validate(String sName, String sEmail, String sPass, String sID, String sOrg) {

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
        } else if (sOrg.isEmpty()) {
            OrgName.setError("Field can't be empty");
            OrgName.requestFocus();
            return;
        }
    }


    private void Initialization() {
        Name = findViewById(R.id.name_box);
        Email = findViewById(R.id.email_box);
        OrgName = findViewById(R.id.orgname_box);
        OrgID = findViewById(R.id.id_box);
        Password = findViewById(R.id.pass_box);
        ScanFace = findViewById(R.id.add_face);
        Signup = findViewById(R.id.signup_button);
        LoginPage = findViewById(R.id.SignIn_tv);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar_SignUp1);
    }
}