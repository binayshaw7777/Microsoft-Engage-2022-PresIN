package com.geekym.face_recognition_engage.HomeFragments.Homescreen;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.utils.JavaUtils;

public class PromptAdapter extends FirebaseRecyclerAdapter<ClassPrompt, PromptAdapter.myViewHolder> {

    public interface PromptClickListener {
        void onItemClick(ClassPrompt model);
    }

    private PromptClickListener promptClickListener;

    public PromptAdapter(@NonNull FirebaseRecyclerOptions<ClassPrompt> options) {
        super(options);
    }

    public PromptAdapter(@NonNull FirebaseRecyclerOptions<ClassPrompt> options, PromptClickListener listener) {
        super(options);
        this.promptClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ClassPrompt model) {
        holder.className.setText(model.getClassName());
        holder.teachersName.setText(model.getUserName());
        holder.Time.setText(JavaUtils.formatTimestamp(Long.parseLong(model.getTimeStamp())));
        holder.itemView.setOnClickListener(view -> {
            if (promptClickListener != null) {
                promptClickListener.onItemClick(model);
            }
        });
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutpromptcard, parent, false);
        return new PromptAdapter.myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        TextView className, teachersName, Time;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.classNameDisplay);
            teachersName = itemView.findViewById(R.id.facultyNameDisplay);
            Time = itemView.findViewById(R.id.promptTimeDisplay);
        }
    }

}
