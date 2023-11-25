package com.geekym.face_recognition_engage.HomeFragments.Status.Attendees;

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
import com.geekym.face_recognition_engage.model.PresentStudents;

public class myAdapter extends FirebaseRecyclerAdapter<PresentStudents, myAdapter.myViewHolder> {

    public myAdapter(@NonNull FirebaseRecyclerOptions<PresentStudents> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PresentStudents model) {
        holder.Name.setText("Name: " + model.getFacultyName());
        holder.Session.setText("Session: " + model.getClassName());
        holder.Time.setText("Marked at: " + model.getTime());
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Time, Session;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameDisplay);
            Session = itemView.findViewById(R.id.classNameDisplay);
            Time = itemView.findViewById(R.id.timeDisplay);
        }
    }

}
