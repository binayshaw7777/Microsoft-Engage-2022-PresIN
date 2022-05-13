package com.geekym.face_recognition_engage.Introduction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.HomeScreen;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)

            SendUserToLoginActivity(); //If the user has not logged in, send them to On-Boarding Activity

        else {

            //If user was logged in last time
            new Handler().postDelayed(() -> {

                Intent loginIntent;

                if (currentUser.isEmailVerified())
                    loginIntent = new Intent(Splash_Screen_Activity.this, HomeScreen.class); //If the user email is verified
                else
                    loginIntent = new Intent(Splash_Screen_Activity.this, SignIn_Activity.class); //If the user email is not verified

                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }, 2900);
        }

    }

    //If the user has not logged in, send them to On-Boarding Activity
    private void SendUserToLoginActivity() {
        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(Splash_Screen_Activity.this, onboarding.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }, 2900);
    }
}