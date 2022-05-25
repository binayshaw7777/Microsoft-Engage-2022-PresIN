package com.geekym.face_recognition_engage.HomeFragments.Tools.StudyMode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudyTimer_Activity extends AppCompatActivity {

    TextView textView, maxStudiedTime;
    Button start, pause, reset;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    DatabaseReference reference;
    AudioManager audioManager;
    ImageView audioState;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        Initialization();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //Check DND Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            DynamicToast.make(StudyTimer_Activity.this, "Please grant DND mode access").show();
            startActivity(intent);
        }

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = StudyTimer_Activity.this.getSharedPreferences("userData", 0);
        String userID = userDataSP.getString("userID", "0");

        Calendar calendar = Calendar.getInstance();      //Creating a calendar instance
        //Storing formats of date, year and month name in Strings
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());
        String month = new SimpleDateFormat("MMM").format(calendar.getTime());
        String date = new SimpleDateFormat("dd").format(calendar.getTime());

        reference.child("Users").child(userID).child("StudyData").child(year).child(month).child(date).child("MaxStudyTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String DB_StringTime = Objects.requireNonNull(snapshot.getValue()).toString();
                maxStudiedTime.setText(DB_StringTime); //Settings up textView that shows max time studies for the day
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        start.setOnClickListener(view -> {
            pause.setVisibility(View.VISIBLE);
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            reset.setVisibility(View.INVISIBLE);
            start.setVisibility(View.INVISIBLE);
            audioManager.setRingerMode(1);
            audioState.setBackground(getResources().getDrawable(R.drawable.vibrate));
        });

        pause.setOnClickListener(view -> {
            TimeBuff += MillisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            reset.setVisibility(View.VISIBLE);
            start.setVisibility(View.VISIBLE);
            start.setText("Resume");
            pause.setVisibility(View.INVISIBLE);
            audioManager.setRingerMode(2);
            audioState.setBackground(getResources().getDrawable(R.drawable.ringer));

            String currTime = textView.getText().toString(); //Getting the current time that is shown in the timer TextView
            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss"); //out DateFormat

            reference.child("Users").child(userID).child("StudyData").child(year).child(month).child(date).child("MaxStudyTime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String DB_StringTime = Objects.requireNonNull(snapshot.getValue()).toString(); //getting the time from firebase Database

                    try {
                        Date curr = df.parse(currTime);     //Converting String (current time shown in textView -> Date Object)
                        Date db = df.parse(DB_StringTime);  //Converting String (time found in firebase database -> Date Object)

                        assert db != null;
                        if (db.before(curr)) { //checking if the current time shown in textView is greater/max than the database time
                            //updating the database time with the current, i.e the max time
                            reference.child("Users").child(userID).child("StudyData").child(year).child(month).child(date).child("MaxStudyTime").setValue(currTime);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });

        reset.setOnClickListener(view -> {
            pause.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            start.setText("Start");
            MillisecondTime = 0L;
            StartTime = 0L;
            TimeBuff = 0L;
            UpdateTime = 0L;
            Seconds = 0;
            Minutes = 0;
            MilliSeconds = 0;
            textView.setText("00:00:00");
        });

    }

    private void Initialization() {
        reference = FirebaseDatabase.getInstance().getReference();
        textView = findViewById(R.id.TextView);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        reset = findViewById(R.id.reset);
        maxStudiedTime = findViewById(R.id.maxStudyTime);
        handler = new Handler();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioState = findViewById(R.id.audioState);
    }

    public Runnable runnable = new Runnable() {

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            int Hours = Minutes / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            String time = String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds);
            textView.setText(time);
            handler.postDelayed(this, 0);
        }
    };
}