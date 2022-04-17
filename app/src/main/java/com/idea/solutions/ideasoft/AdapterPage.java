package com.idea.solutions.ideasoft;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPage extends RecyclerView.Adapter<AdapterPage.HolderPage> {


    private Context context;
    private ArrayList<ModelPage> pageArrayList;

    //constructor
    public AdapterPage(Context context, ArrayList<ModelPage> pageArrayList) {
        this.context = context;
        this.pageArrayList = pageArrayList;
    }

    @NonNull
    @Override
    public HolderPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_page.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_page, parent, false);
        return new HolderPage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPage holder, int position) {
        //Get data
        ModelPage model = pageArrayList.get(position);
        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        //description/content is in html/web form, format it
        Document document = Jsoup.parse(content);
        try {
            //get thumbnail form page
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            //set the image, if there is any
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_image_24).into(holder.imageIv);

        } catch (Exception e) {
            holder.imageIv.setImageResource(R.drawable.ic_baseline_image_24);
        }

        //format data
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
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(document.text());
        holder.publishInfoTv.setText("By " + authorName + " " + formattedDate);

        //handle page click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start page details activity, pass id of the page, whose details wanna show
                Intent intent = new Intent(context, PageDetailsActivity.class);
                intent.putExtra("pageId", id);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return pageArrayList.size();
    }

    //View holder class for row_page.xml
    class HolderPage extends RecyclerView.ViewHolder {

        //UI Views of row_page.xml
        private TextView titleTv, publishInfoTv, descriptionTv;
        private ImageView imageIv;

        public HolderPage(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
