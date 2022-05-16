package com.geekym.face_recognition_engage.HomeFragments.Status;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramijemli.percentagechartview.PercentageChartView;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;

public class Progress_Activity extends AppCompatActivity {

    TextView totalDays, presentDays, absentDays, requireDays;
    private DatabaseReference reference;
    private String userID;
    long c = 0;
    PercentageChartView mChart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Initialization();

        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        String month = new SimpleDateFormat("MM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int daysInMonth = yearMonthObject.lengthOfMonth();
        float todayDate = Float.parseFloat(date);


        reference.child("Users").child(userID).child("Attendance").child(year).child(monthName).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                c = snapshot.getChildrenCount();

                float count = c;
                float absent = todayDate - count;
                float percent = (count / todayDate) * 100;
                float expected = ((c + (daysInMonth - todayDate)) / daysInMonth) * 100;
                mChart.setProgress(percent, true);

                totalDays.setText("Total Days in this month: "+daysInMonth+" days");
                presentDays.setText("No. of days you were present: "+(int)count+" days");
                absentDays.setText("No. of days you were absent: "+(int)absent+" days");
                requireDays.setText("Attendance if you attend daily this month: "+String.format("%.2f", expected)+"%");

                if (percent >= 0 && percent < 30) {
                    mChart.setProgressColor(getResources().getColor(R.color.red_desat));
                    mChart.setBackgroundBarColor(getResources().getColor(R.color.shallow_red));
                    mChart.setTextColor(getResources().getColor(R.color.red_desat));
                } else if (percent >= 30 && percent < 50) {
                    mChart.setProgressColor(getResources().getColor(R.color.orange));
                    mChart.setBackgroundBarColor(getResources().getColor(R.color.shallow_orange_yellow));
                    mChart.setTextColor(getResources().getColor(R.color.orange));
                } else if (percent >= 50 && percent < 65) {
                    mChart.setProgressColor(getResources().getColor(R.color.orange_yellow));
                    mChart.setBackgroundBarColor(getResources().getColor(R.color.shallow_orange_yellow));
                    mChart.setTextColor(getResources().getColor(R.color.orange_yellow));
                } else if (percent >= 65 && percent < 75) {
                    mChart.setProgressColor(getResources().getColor(R.color.yellow));
                    mChart.setBackgroundBarColor(getResources().getColor(R.color.shallow_orange_yellow));
                    mChart.setTextColor(getResources().getColor(R.color.yellow));
                } else {
                    mChart.setProgressColor(getResources().getColor(R.color.green_desat));
                    mChart.setBackgroundBarColor(getResources().getColor(R.color.shallow_green));
                    mChart.setTextColor(getResources().getColor(R.color.green_desat));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Initialization() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        assert user != null;
        userID = user.getUid();
        mChart = findViewById(R.id.ring_progress);
        totalDays = findViewById(R.id.totalDays);
        presentDays = findViewById(R.id.presentDays);
        absentDays = findViewById(R.id.absentDays);
        requireDays = findViewById(R.id.requireDays);
    }
}