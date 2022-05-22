package com.geekym.face_recognition_engage.Authentication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class Forgot_Password_Activity extends AppCompatActivity {

    FirebaseAuth Auth2;
    EditText email;
    View buttonView;
    ProgressBar buttonProgress;
    TextView buttonText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Initialization();     //Function to initialize the variables

        buttonText.setText("Send Password Reset Email");

        buttonView.setOnClickListener(view -> {
            if (isConnected()) {ForgotPassword(); }}); //Send password reset link when connected to internet
    }

    //Validating text input by the User
    private void ForgotPassword() {
        String Email = email.getText().toString().trim();
        if (Email.isEmpty()) {
            email.setError("Field can't be empty");
            email.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Please enter a valid Email id");
            email.requestFocus();

        } else { //If there user entered a valid email
            buttonProgress.setVisibility(View.VISIBLE);
            buttonText.setVisibility(View.GONE);
            Auth2.sendPasswordResetEmail(Email).addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) { //Password Reset link is sent to user's Email
                        buttonProgress.setVisibility(View.GONE);
                        buttonText.setVisibility(View.VISIBLE);
                        DynamicToast.make(this, "Password reset email sent!", getResources()
                                .getColor(R.color.white), getResources().getColor(R.color.green_desat)).show();
                        startActivity(new Intent(getApplicationContext(), SignIn_Activity.class));
                    }
                    else {
                        buttonProgress.setVisibility(View.GONE);
                        buttonText.setText(View.VISIBLE);
                        DynamicToast.makeError(this, "Something went wrong").show();
                    }

                } catch (Exception e) {
                    buttonText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    Log.d(TAG, "Email sent");
                }
            });
        }
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

    //Function to initialize the variables
    private void Initialization() {
        Auth2 = FirebaseAuth.getInstance();
        email = findViewById(R.id.reset_email);
        buttonView = findViewById(R.id.login_button);
        buttonProgress = findViewById(R.id.buttonProgress);
        buttonText = findViewById(R.id.buttonText);
    }
}