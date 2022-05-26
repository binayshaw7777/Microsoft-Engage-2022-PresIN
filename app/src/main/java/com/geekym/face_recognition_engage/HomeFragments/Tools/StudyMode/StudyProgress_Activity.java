package com.geekym.face_recognition_engage.HomeFragments.Tools.StudyMode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Objects;

public class StudyProgress_Activity extends AppCompatActivity {

    DatabaseReference reference;
    ValueLineChart mCubicValueLineChart;
    ValueLineSeries series = new ValueLineSeries();
    Button gotoTimer;
    TextView maxPastWeek;

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_progress);

        Initialization();   //Function to initialize the variables

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = StudyProgress_Activity.this.getSharedPreferences("userData", 0);
        String userID = userDataSP.getString("userID", "0");

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        String monthForNoOfDays = new SimpleDateFormat("MM").format(cal.getTime());
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(monthForNoOfDays));
        int daysInMonth = yearMonthObject.lengthOfMonth();
        float todayDate = Float.parseFloat(date);

        gotoTimer.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), StudyTimer_Activity.class));
            finish();
        });

        reference.child("Users").child(userID).child("StudyData").child(year).child(month).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                float[] daysTotal = new float[daysInMonth];
                float[] lastSeven = new float[9];

                for (DataSnapshot ds : snapshot.getChildren()) {
                    int atThatDate = Integer.parseInt(Objects.requireNonNull(ds.getKey()));
                    String time1 = Objects.requireNonNull(ds.child("MaxStudyTime").getValue()).toString();
                    String[] split = time1.split(":");
                    float sec = Integer.parseInt(split[2]);
                    float min = Integer.parseInt(split[1]);
                    float hour = Integer.parseInt(split[0]);
                    float totalMinF = (hour * 60) + min + (sec / 60);
                    daysTotal[atThatDate - 1] = totalMinF;
                }

                int todayDate_Integer = (int) todayDate;
                int difference = Math.abs(todayDate_Integer - 7); //if the date is less than 7
                int temp = 0;

                lastSeven[8] = 0.0f; //Filling edge values with 0.0f
                lastSeven[0] = 0.0f;

                if (difference < 8) {   //Checing if the date is less that 7

                    //Filling from last in the graph array
                    for (int i = 7; i > difference; i--) {
                        lastSeven[i] = daysTotal[todayDate_Integer - 1];
                        todayDate_Integer--;
                    }

                    //Filling the remainging/difference days with 0
                    for (int i = difference; i >= 0; i--) {
                        lastSeven[i] = 0.0f;
                    }

                    float Max = 0.0f; //Calculating the max study time past week

                    //Filling the first (difference) or Todaydate - 7 days with value '0'
                    for (int i = 0; i <= difference; i++) {
                        series.addPoint(new ValueLinePoint("0", 0)); //adding the point in the graph on that index
                    }

                    //Filling the valid dates with the values
                    for (int i = difference; i < 9; i++) {
                        series.addPoint(new ValueLinePoint(String.valueOf(temp), lastSeven[i])); //adding the point in the graph on that index
                        Max = Math.max(Max, lastSeven[i]);
                        temp++;
                    }

                    maxPastWeek.setText("Max in last 7 days " + String.format("%.2f", Max) + " minutes");

                } else {

                    //Filling the graph array with valid values
                    for (int i = 7; i > 0; i--) {
                        lastSeven[i] = daysTotal[todayDate_Integer - 1];
                        todayDate_Integer--;
                    }


                    float Max = 0.0f; //Calculating the max study time past week

                    for (int i = 0; i < 9; i++) {
                        series.addPoint(new ValueLinePoint(String.valueOf(difference), lastSeven[i])); //adding the point in the graph on that index
                        Max = Math.max(Max, lastSeven[i]);
                        difference++;
                    }

                    maxPastWeek.setText("Max in last 7 days " + String.format("%.2f", Max) + " minutes");

                }
                mCubicValueLineChart.setShowDecimal(true);
                series.setColor(0xFF56B7F1);
                mCubicValueLineChart.setVisibility(View.VISIBLE);
                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void Initialization() {
        reference = FirebaseDatabase.getInstance().getReference();
        mCubicValueLineChart = findViewById(R.id.lineChart_study);
        gotoTimer = findViewById(R.id.gotoTimer);
        maxPastWeek = findViewById(R.id.maxPastWeek);
    }
}