package com.geekym.face_recognition_engage.Introduction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class onboarding extends AppCompatActivity {
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // when this activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            Intent login = new Intent(getApplicationContext(), SignIn_Activity.class);
            startActivity(login);
            finish();
        }
        setContentView(R.layout.activity_onboarding);

        Initialization();

        // fill list screen
        final List<com.geekym.face_recognition_engage.Introduction.ScreenItem> mList = new ArrayList<>();
        mList.add(new com.geekym.face_recognition_engage.Introduction.ScreenItem("Let's Get Started", "Minimal and Clean UI", R.drawable.s1));
        mList.add(new com.geekym.face_recognition_engage.Introduction.ScreenItem("Face Recognition", "Scan you face to mark your attendance", R.drawable.s2));
        mList.add(new com.geekym.face_recognition_engage.Introduction.ScreenItem("Attendance Tracker", "Check and plan your attendance", R.drawable.s3));

        // setup viewpager
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tab layout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listener
        btnNext.setOnClickListener(v -> {

            position = screenPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size() - 1) { // when we reach to the last screen
                // TODO : show the GET STARTED Button and hide the indicator and the next button
                loadLastScreen();
            }
        });

        // tab layout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Get Started button click listener
        btnGetStarted.setOnClickListener(v -> {

            //open main activity
            Intent login = new Intent(getApplicationContext(), SignIn_Activity.class);
            startActivity(login);
            // also we need to save a boolean value to storage so next time when the user run the app
            // we could know that he is already checked the intro screen activity
            // i'm going to use shared preferences to that process
            savePrefsData();
            finish();
        });

        // skip button click listener
        tvSkip.setOnClickListener(v -> screenPager.setCurrentItem(mList.size()));
    }

    private void Initialization() {
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        screenPager = findViewById(R.id.screen_viewpager);
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpened", false);
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.apply();
    }

    // show the GET STARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the get started button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
    }
}