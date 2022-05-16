package com.geekym.face_recognition_engage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.Status.status_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.home_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.profile_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    BottomNavigationView bottomBar;
    Dialog dialog;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Initialization();

        getSupportFragmentManager().beginTransaction().replace(R.id.main, new home_Fragment()).commit();

        bottomBar.setSelectedItemId(R.id.home);
        bottomBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new home_Fragment();
                    break;
                case R.id.scan:
                    fragment = new status_Fragment();
                    break;
                case R.id.profile:
                    fragment = new profile_Fragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
            return true;
        });

    }

    private void Initialization() {
        bottomBar = findViewById(R.id.bottomBar);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ObsoleteSdkInt", "SetTextI18n"})
    public void onBackPressed() {

        dialog = new Dialog(HomeScreen.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(HomeScreen.this.getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView description = dialog.findViewById(R.id.dialog_description);

        Proceed.setText("Exit");
        Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
        title.setText("Confirm Exit");
        description.setText("Do you really want to exit?");

        Proceed.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}