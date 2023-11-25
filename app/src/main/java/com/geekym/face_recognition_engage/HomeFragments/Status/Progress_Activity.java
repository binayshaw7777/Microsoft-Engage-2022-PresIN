package com.geekym.face_recognition_engage.HomeFragments.Status;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Objects;

public class Progress_Activity extends AppCompatActivity {

    TextView totalDays, presentDays, absentDays, requireDays;
    private DatabaseReference reference;
    long c = 0; //count number of children (days present) in the user's Attendance node
    PercentageChartView mChart;
    ValueLineChart mCubicValueLineChart;
    ValueLineSeries series = new ValueLineSeries();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Initialization(); //Function to initialize the variables

        SharedPreferences userDataSP = getApplicationContext().getSharedPreferences("userData", 0);
        String userID = userDataSP.getString("userID", "0");

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
                float remainingDaysLeft = daysInMonth - todayDate;
                float percent = (count * 100) / daysInMonth;
                float expected = ((c + remainingDaysLeft) * 100) / daysInMonth;
                mChart.setProgress(percent, true);

                //Set calculated data in TextView
                totalDays.setText("Total days in this month: "+daysInMonth+" days");
                presentDays.setText("Present: "+(int)count+" days");
                absentDays.setText("Absent: "+(int)absent+" days");
                requireDays.setText("Attendance if daily attended: "+String.format("%.2f", expected)+"%");

                //Set color of the Progress Bar and Graph According to the Attendance Percentage %
                if (percent >= 0 && percent < 30) {
                    setThis(getResources().getColor(R.color.red_desat), getResources().getColor(R.color.shallow_red));
                    series.setColor(0xFFFF3F3F);

                } else if (percent >= 30 && percent < 50) {
                    setThis(getResources().getColor(R.color.orange), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFF5100);

                } else if (percent >= 50 && percent < 65) {
                    setThis(getResources().getColor(R.color.orange_yellow), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFF9900);

                } else if (percent >= 65 && percent < 75) {
                    setThis(getResources().getColor(R.color.yellow), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFFDD00);

                } else {
                    setThis(getResources().getColor(R.color.green_desat), getResources().getColor(R.color.shallow_green));
                    series.setColor(0xFF4CA456);
                }

                float[] graphArray = new float[daysInMonth]; //Store the days present in a new array of no. of days in that month. Eg: 31 for January

                for (DataSnapshot ds : snapshot.getChildren()) {
                    int i = Integer.parseInt(Objects.requireNonNull(ds.getKey()));
                    graphArray[i] = 1.4f; //Storing the value 1 on the index (date) the user was present
                }

                for (int i=0; i<graphArray.length; i++) {
                    series.addPoint(new ValueLinePoint(String.valueOf(i), graphArray[i])); //adding the point in the graph on that index
                }



//                mCubicValueLineChart.setShowDecimal(true);
                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    //Function to set colors for the Progress Bar
    private void setThis(int first, int second) {
        mChart.setProgressColor(first);
        mChart.setBackgroundBarColor(second);
        mChart.setTextColor(first);
    }

    //Function to initialize the variables
    private void Initialization() {
        reference = FirebaseDatabase.getInstance().getReference();
        mChart = findViewById(R.id.ring_progress);
        totalDays = findViewById(R.id.totalDays);
        presentDays = findViewById(R.id.presentDays);
        absentDays = findViewById(R.id.absentDays);
        requireDays = findViewById(R.id.requireDays);
        mCubicValueLineChart = findViewById(R.id.lineChart);
    }
}