package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.HomeFragments.Homescreen.adapter.StudentPresentListAdapter;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.model.PresentStudents;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SeeAllStudentsActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    StudentPresentListAdapter presentStudentListAdapter;
    Calendar calendar;
    ClassPrompt classPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_students);

        initialization(); //Function to initialize the variables

        SharedPreferences userData = getSharedPreferences("userData", Context.MODE_PRIVATE); //Creating SharedPreference
        calendar = Calendar.getInstance();      //Creating a calendar instance
        //Storing formats of date, year and month name in Strings
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());
        String month = new SimpleDateFormat("MMM").format(calendar.getTime());
        String date = new SimpleDateFormat("dd").format(calendar.getTime());
        String SPcollegeName = userData.getString("collegeName", "0");
        String classID = classPrompt.getClassID();
        Log.d("Class ID:", classID);



        //Firebase data -> RecyclerView
        FirebaseRecyclerOptions<PresentStudents> presentStudentList =
                new FirebaseRecyclerOptions.Builder<PresentStudents>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendees").child(SPcollegeName).child(year).child(month).child(date).child(classID).child("presentStudents"), PresentStudents.class).build();

        presentStudentListAdapter = new StudentPresentListAdapter(presentStudentList);

        if (presentStudentListAdapter.getItemCount() == 0) {
            Log.d("Size is", String.valueOf(presentStudentListAdapter.getItemCount()));
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.d("","Student perc:" + ((presentStudentListAdapter.getItemCount() / classPrompt.getExpectedStudents())*100));

            recyclerView.setAdapter(presentStudentListAdapter);
        }

        presentStudentListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            //Handle the condition when there's any change in item count of the adapter
            void checkEmpty() {
                if (presentStudentListAdapter.getItemCount() == 0) { //If no item found in adapter
                    recyclerView.setVisibility(View.GONE); //Disable recyclerView

                } else { //If item is found in adapter
                    Log.d("","Student perc:" + ((presentStudentListAdapter.getItemCount() * 100) / classPrompt.getExpectedStudents()));
                    Log.d("","Student list size:" + presentStudentListAdapter.getItemCount());
                    Log.d("","expected stud:" + classPrompt.getExpectedStudents());
                    recyclerView.setAdapter(presentStudentListAdapter);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setVisibility(View.VISIBLE);
        presentStudentListAdapter.startListening();
    }

    private void initialization() {
       recyclerView = findViewById(R.id.student_present_list);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        classPrompt = (ClassPrompt) intent.getSerializableExtra("classPrompt");
    }
}