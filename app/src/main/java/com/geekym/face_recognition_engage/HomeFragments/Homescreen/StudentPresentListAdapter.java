package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.model.ClassPrompt;

public class StudentPresentListAdapter extends FirebaseRecyclerAdapter<ClassPrompt, StudentPresentListAdapter.myViewHolder> {


    public StudentPresentListAdapter(@NonNull FirebaseRecyclerOptions<ClassPrompt> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ClassPrompt model) {
        holder.Name.setText("Name: " +model.getClassName());
        holder.ID.setText("ID: " +model.getUserID());
        holder.Time.setText("Time: " +model.getTimeStamp());
    }

    @NonNull
    @Override
    public StudentPresentListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new StudentPresentListAdapter.myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        TextView Name, ID, Time;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameDisplay);
            ID = itemView.findViewById(R.id.collegeIdDisplay);
            Time = itemView.findViewById(R.id.timeDisplay);
        }
    }

}