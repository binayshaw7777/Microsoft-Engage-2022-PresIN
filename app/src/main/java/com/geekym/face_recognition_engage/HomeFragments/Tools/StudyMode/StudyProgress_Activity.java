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

        Calendar cal = Calendar.getInstance();  //Creating a calendar instance
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());   //Storing year as string
        String month = new SimpleDateFormat("MMM").format(cal.getTime());   //Storing month name as string
        String date = new SimpleDateFormat("dd").format(cal.getTime());     //Storing date as string
        String monthForNoOfDays = new SimpleDateFormat("MM").format(cal.getTime());     //Storing the month (in number) January -> 1
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(monthForNoOfDays));
        int daysInMonth = yearMonthObject.lengthOfMonth(); //Getting number of days in the current month
        float todayDate = Float.parseFloat(date); //getting today's date in float

        //To start the Study timer activity
        gotoTimer.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), StudyTimer_Activity.class));
            finish();
        });

        //Setting up the graph data -> Graph
        reference.child("Users").child(userID).child("StudyData").child(year).child(month).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                float[] daysTotal = new float[daysInMonth]; //Creating a float array of size -> Number of days in current month
                float[] lastSeven = new float[9];   //Creating a float array of size 7 + 2 -> 1 week + front and back days to cover graph curve (edge)

                for (DataSnapshot ds : snapshot.getChildren()) {        //Getting the nodes of the StudyTimer from Firebase Database
                    int atThatDate = Integer.parseInt(Objects.requireNonNull(ds.getKey()));     //Getting the day (node's key) and storing it in number
                    String time1 = Objects.requireNonNull(ds.child("MaxStudyTime").getValue()).toString();     //Node's Child that stores the max time
                    String[] split = time1.split(":");      //Splitting the 00:00:00 format into array -> [00][00][00];
                    float sec = Integer.parseInt(split[2]);       //Getting 2nd index for Second
                    float min = Integer.parseInt(split[1]);     //Getting 1st index for Minute
                    float hour = Integer.parseInt(split[0]);    //Getting 0th index for Hour
                    float totalMinF = (hour * 60) + min + (sec / 60); //Calculating the total time in minutes
                    daysTotal[atThatDate - 1] = totalMinF;
                }

                int todayDate_Integer = (int) todayDate;
                int difference = Math.abs(todayDate_Integer - 7); //if the date is less than 7, like 4th June
                int temp = 0;

                lastSeven[8] = 0.0f; //Filling edge values with 0.0f
                lastSeven[0] = 0.0f;

                if (difference < 8) {   //Checking if the date is less that 7

                    //Filling from last in the graph array
                    for (int i = 7; i > difference; i--) {
                        lastSeven[i] = daysTotal[todayDate_Integer - 1];
                        todayDate_Integer--;
                    }

                    //Filling the remaining/difference days with 0
                    for (int i = difference; i >= 0; i--) {
                        lastSeven[i] = 0.0f;
                    }

                    float Max = 0.0f; //Calculating the max study time past week

                    //Filling the first (difference) or Today date - 7 days with value '0'
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