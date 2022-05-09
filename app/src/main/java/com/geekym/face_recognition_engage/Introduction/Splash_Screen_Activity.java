package com.geekym.face_recognition_engage.Introduction;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.geekym.face_recognition_engage.HomeScreen;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen_Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance ();

        FirebaseUser currentUser = mAuth.getCurrentUser ();

        if (currentUser == null) {

            SendUserToLoginActivity();
        } else {

            currentUserID = mAuth.getCurrentUser ().getUid ();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent (Splash_Screen_Activity.this, HomeScreen.class);
                    loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( loginIntent );
                    finish ();
                }
            }, 2900);
        }

    }
    private void SendUserToLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent (Splash_Screen_Activity.this, onboarding.class  );
                loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( loginIntent );
                finish ();
            }
        }, 2900);
    }
}