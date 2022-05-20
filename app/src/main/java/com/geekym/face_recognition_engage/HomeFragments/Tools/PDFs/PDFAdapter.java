package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import android.content.Intent;
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

public class PDFAdapter extends FirebaseRecyclerAdapter<PDFsModel, PDFAdapter.myviewholder> {

    public PDFAdapter(@NonNull FirebaseRecyclerOptions<PDFsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, int position, @NonNull final PDFsModel model) {

        holder.header.setText(model.getFilename());

        holder.view.setOnClickListener(view -> {
            Intent intent=new Intent(holder.view.getContext(), pdfViewer_Activity.class);
            intent.putExtra("filename",model.getFilename());
            intent.putExtra("fileurl",model.getFileurl());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            holder.view.getContext().startActivity(intent);
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_layout_list,parent,false);
        return new myviewholder(view);
    }

    public static class myviewholder extends RecyclerView.ViewHolder {
        TextView header;
        View view;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            header=itemView.findViewById(R.id.notes_title);
            view = itemView;
        }
    }
}
