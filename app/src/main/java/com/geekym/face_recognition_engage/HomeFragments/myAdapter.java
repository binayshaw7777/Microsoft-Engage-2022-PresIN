package com.geekym.face_recognition_engage.HomeFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geekym.face_recognition_engage.R;

public class myAdapter extends FirebaseRecyclerAdapter<ModelClass, myAdapter.myViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public myAdapter(@NonNull FirebaseRecyclerOptions<ModelClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModelClass model) {
        holder.Name.setText("Name: "+model.getName());
        holder.ID.setText("ID: "+model.getID());
//        holder.Time.setText("Name: "+model.getTime());
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView Name, ID, Time;
        ImageView Indicator;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameDisplay);
            ID = itemView.findViewById(R.id.collegeIdDisplay);
            Time = itemView.findViewById(R.id.timeDisplay);
            Indicator = itemView.findViewById(R.id.statusIndicator);

        }
    }

}
