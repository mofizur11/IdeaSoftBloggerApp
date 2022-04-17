package com.idea.solutions.ideasoft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    private Context context;
    private ArrayList<ModelComment> commentArrayList;

    //constructor
    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_comment.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new HolderComment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        //Get data
        ModelComment modelComment = commentArrayList.get(position);
        String id = modelComment.getId();
        String name = modelComment.getName();
        String published = modelComment.getPublished();
        String comment = modelComment.getComment();
        String image = modelComment.getProfileImage();

        //format GMT data format to proper format dd/MM/yyyy
        String gmtData = published;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
        String formattedDate = "";
        try {
            Date date = dateFormat.parse(gmtData);
            formattedDate = dateFormat2.format(date);

        } catch (Exception e) {
            formattedDate = published;
            e.printStackTrace();
        }

        //set data
        holder.nameTv.setText(name);
        holder.dateTv.setText(formattedDate);
        holder.commentTv.setText(comment);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_person_outline_24).into(holder.profileIv);
        } catch (Exception e) {
            holder.profileIv.setImageResource(R.drawable.ic_baseline_person_outline_24);
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    //view holder class for row_comment.xml
    class HolderComment extends RecyclerView.ViewHolder {

        //UI View of row_comment.xml
        ImageView profileIv;
        TextView nameTv, dateTv, commentTv;

        public HolderComment(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            commentTv = itemView.findViewById(R.id.commentTv);
        }
    }
}
