package com.idea.solutions.ideasoft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLabel extends RecyclerView.Adapter<AdapterLabel.HolderLabel> {

    private Context context;
    private ArrayList<ModelLabel> labelArrayList;

    //constructor


    public AdapterLabel(Context context, ArrayList<ModelLabel> labelArrayList) {
        this.context = context;
        this.labelArrayList = labelArrayList;
    }

    @NonNull
    @Override
    public HolderLabel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_label.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_label, parent, false);
        return new HolderLabel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLabel holder, int position) {
        //get data
        ModelLabel modelLabel = labelArrayList.get(position);
        String label = modelLabel.getLabel();

        //set data
        holder.labelTv.setText(label);
    }

    @Override
    public int getItemCount() {
        return labelArrayList.size();
    }

    //ViewHolder class that will hold row_label.xml
    class HolderLabel extends RecyclerView.ViewHolder {

        //UI Views of row_label.xml
        private TextView labelTv;

        //constructor
        public HolderLabel(@NonNull View itemView) {
            super(itemView);

            //Init UI Views
            labelTv = itemView.findViewById(R.id.labelTv);

        }
    }
}
