package com.idea.solutions.ideasoft;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PagesActivity extends AppCompatActivity {

    private ActionBar actionBar;

    //UI Views
    private RecyclerView pagesRv;

    //arrayList of ModelPage
    private ArrayList<ModelPage> pageArrayList;
    //AdapterPage instance
    private AdapterPage adapterPage;

    //TAG
    private static final String TAG = "PAGES_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Idea Soft BD");
        actionBar.setSubtitle("Pages");
        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init UI Views
        pagesRv = findViewById(R.id.pageRv);

        loadPages();
    }

    private void loadPages() {
        //show progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading pages");
        progressDialog.show();

        //API URL
        String url = "https://www.googleapis.com/blogger/v3/blogs/" + Constants.BLOG_ID
                + "/pages?key=" + Constants.API_KEY;
        Log.d(TAG, "loadPages: URL: " + url);

        //API request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //API response is received successfully
                Log.d(TAG, "onResponse: " + response);
                progressDialog.dismiss();

                //API response is in JSON Object
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //get array of pages from that jsonObject
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    //init and clear list before adding data into it
                    pageArrayList = new ArrayList<>();
                    pageArrayList.clear();

                    //get all pages from array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            //get data, again JSON Object
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            //get data from that jsonObject1
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String displayName = jsonObject1.getJSONObject("author").getString("displayName");
                            String image = jsonObject1.getJSONObject("author").getJSONObject("image").getString("url");

                            //set data to model
                            ModelPage model = new ModelPage(
                                    "" + displayName,
                                    "" + content,
                                    "" + id,
                                    "" + published,
                                    "" + selfLink,
                                    "" + title,
                                    "" + updated,
                                    "" + url);

                            //add model to arrayList of model
                            pageArrayList.add(model);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }

                    //setup adapter
                    adapterPage = new AdapterPage(PagesActivity.this, pageArrayList);
                    //set adapter to recyclerView
                    pagesRv.setAdapter(adapterPage);

                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed to get API response
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(PagesActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}