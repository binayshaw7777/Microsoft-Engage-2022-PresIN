package com.geekym.face_recognition_engage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.geekym.face_recognition_engage.HomeFragments.home_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.profile_Fragment;
import com.geekym.face_recognition_engage.HomeFragments.status_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeScreen extends AppCompatActivity {

    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Initialization();

        getSupportFragmentManager().beginTransaction().replace(R.id.main, new home_Fragment()).commit();

        bottomBar.setSelectedItemId(R.id.home);
        bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
                return true;
            }
        });

    }

    private void Initialization() {
        bottomBar = findViewById(R.id.bottomBar);
    }
}