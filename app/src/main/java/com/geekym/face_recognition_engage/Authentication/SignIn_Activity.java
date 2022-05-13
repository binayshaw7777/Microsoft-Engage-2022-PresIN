package com.geekym.face_recognition_engage.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geekym.face_recognition_engage.HomeScreen;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn_Activity extends AppCompatActivity {

    EditText Email_editText, Password_editText;
    Button Login_Button;
    TextView SignUp, ForgotPass;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Initialization();

        ForgotPass.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), Forgot_Password_Activity.class)));

        Login_Button.setOnClickListener(view -> Login()); //Login Button CTA

        SignUp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUp_First_Activity.class))); //To SignUp Activity

        // Function to see password and hide password
        Password_editText.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= Password_editText.getRight() - Password_editText.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = Password_editText.getSelectionEnd();
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        Password_editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                        //for hide password
                        Password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        //set drawable image here
                        Password_editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                        //for show password
                        Password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    Password_editText.setLongClickable(false); //Handles Multiple option popups
                    Password_editText.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

    }

    private void Login() {
        String email = Email_editText.getText().toString();
        String pass = Password_editText.getText().toString();

        if (email.isEmpty()) {
            Email_editText.setError("Field can't be empty");
            Email_editText.requestFocus();
            return;
        } else if (pass.isEmpty()) {
            Password_editText.setError("Field can't be empty");
            Password_editText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email_editText.setError("Please enter a valid email address");
            Email_editText.requestFocus();
            return;
        } else if (pass.length() < 6) {
            Password_editText.setError("Password must be at least 6 characters");
            Password_editText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                if (!user.isEmailVerified()) {
                    progressBar.setVisibility(View.GONE);
                    user.sendEmailVerification();
                    Toast.makeText(getApplicationContext(), "Check your email to verify your account and Login again", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent intent2 = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent2);
                    finishAffinity();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failed to Login! Please check your credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Initialization() {
        Email_editText = findViewById(R.id.email_box);
        Password_editText = findViewById(R.id.pass_box);
        SignUp = findViewById(R.id.SignUp_tv);
        mAuth = FirebaseAuth.getInstance();
        Login_Button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar_SignIn);
        ForgotPass = findViewById(R.id.forgotpass_tv);
    }
}