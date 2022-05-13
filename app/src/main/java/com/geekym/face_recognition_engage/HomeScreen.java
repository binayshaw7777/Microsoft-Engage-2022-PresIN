package com.geekym.face_recognition_engage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.HomeFragments.home_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.profile_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.status_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    BottomNavigationView bottomBar;

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

    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Confirm Exit");
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setMessage("Do you really want to exit?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Exit", (dialogInterface, i) -> finishAffinity());
        alertDialogBuilder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}