package com.geekym.face_recognition_engage.HomeFragments.Homescreen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.model.ClassPrompt;
import com.geekym.face_recognition_engage.utils.JavaUtils;

public class PromptAdapter extends FirebaseRecyclerAdapter<ClassPrompt, PromptAdapter.myViewHolder> {

    public interface PromptClickListener {
        void onItemClick(ClassPrompt model, Integer clickMode);
    }

    private PromptClickListener promptClickListener;
    private boolean isAdmin = false;


    public PromptAdapter(@NonNull FirebaseRecyclerOptions<ClassPrompt> options, boolean isAdmin, PromptClickListener listener) {
        super(options);
        this.isAdmin = isAdmin;
        this.promptClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ClassPrompt model) {
        holder.className.setText(model.getClassName());
        holder.teachersName.setText(model.getUserName());
        holder.Time.setText(JavaUtils.formatTimestamp(Long.parseLong(model.getTimeStamp())));

        if (isAdmin) {
            try {
                Context context = holder.itemView.getContext();
                holder.icon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_qr_code, null));
            } catch (Exception e) {
                Log.d("", "Can't change color of QR Scanner icon to QR Code");
            }
        }
        holder.icon.setOnClickListener(view -> {
            if (promptClickListener != null) {
                promptClickListener.onItemClick(model, JavaUtils.CARD_ICON_CLICKED);
            }
        });
        holder.itemView.setOnClickListener(view -> {
            if (promptClickListener != null) {
                promptClickListener.onItemClick(model, JavaUtils.CARD_VIEW_CLICKED);
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
        ImageView icon;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.classNameDisplay);
            teachersName = itemView.findViewById(R.id.facultyNameDisplay);
            Time = itemView.findViewById(R.id.promptTimeDisplay);
            icon = itemView.findViewById(R.id.card_icon);
        }
    }

}
