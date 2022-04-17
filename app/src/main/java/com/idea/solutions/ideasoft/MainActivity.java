package com.idea.solutions.ideasoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView postRV;
    private Button loadMoreBtn;

    private EditText searchEt;
    private ImageButton searchBtn;

    private String url = "";
    private String nextToken = "";
    private boolean isSearch = false;

    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;

    private ProgressDialog progressDialog;

    private static final String TAG = "MAIN_TAG";

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Idea Soft BD");
        actionBar.setSubtitle("Posts");

        //Init Ui View
        postRV = findViewById(R.id.postRV);
        loadMoreBtn = findViewById(R.id.loadMoreBtn);
        searchEt = findViewById(R.id.searchEt);
        searchBtn = findViewById(R.id.searchBtn);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");

        //init & clear list before adding data it
        postArrayList = new ArrayList<>();
        postArrayList.clear();

        loadPost();

        //load more button click
        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text from edit text
                String query = searchEt.getText().toString().trim();
                if (TextUtils.isEmpty(query)) {
                    loadPost();
                } else {
                    searchPosts(query);
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextToken = "";
                url = "";

                postArrayList = new ArrayList<>();
                postArrayList.clear();

                //get text from edit text
                String query = searchEt.getText().toString().trim();
                if (TextUtils.isEmpty(query)) {
                    loadPost();
                } else {
                    searchPosts(query);
                }
            }
        });
    }

    private void searchPosts(String query) {
        isSearch = true;
        Log.d(TAG, "searchPosts: isSearch: " + isSearch);

        progressDialog.show();

        if (nextToken.equals("")) {
            Log.d(TAG, "searchPosts: Next page token is empty, no more post");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    + "/posts/search?q=" + query
                    + "&key=" + Constants.API_KEY;
        } else if (nextToken.equals("end")) {
            Log.d(TAG, "searchPosts: Next Token is empty/end, no more post...");
            Toast.makeText(this, "No more posts...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        } else {
            Log.d(TAG, "searchPosts: next token:" + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    + "/posts/search?q=" + query
                    + "&pageToken=" + nextToken
                    + "&key=" + Constants.API_KEY;
        }
        Log.d(TAG, "searchPosts: URL : " + url);

        //Request data, Method is GET not POST

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // we got response, so dismiss dialog first
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: " + response);
                try {
                    //response is JSON object
                    JSONObject jsonObject = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");

                        Log.d(TAG, "onResponse: nextPageToken " + nextToken);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Reached end of page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Reached end of page..." + e.getMessage());
                        nextToken = "end";
                    }

                    //get  json array from json
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    //continue getting data while its complected
                    for (int i = 0; i <= jsonArray.length(); i++) {
                        try {
                            //get data
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //    String image = jsonObject1.getJSONObject("author").getString("image");

                            //set data
                            ModelPost modelPost = new ModelPost("" + authorName,
                                    "" + content,
                                    "" + id,
                                    "" + published,
                                    "" + selfLink,
                                    "" + title,
                                    "" + updated,
                                    "" + url);

                            //ad data/model to List
                            postArrayList.add(modelPost);
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: 1: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    //setup adapter
                    adapterPost = new AdapterPost(MainActivity.this, postArrayList);
                    //set adapter to recycler view
                    postRV.setAdapter(adapterPost);
                    progressDialog.dismiss();

                } catch (Exception e) {
                    Log.d(TAG, "onResponse: 2: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void loadPost() {
        isSearch = false;
        Log.d(TAG, "loadPost: isSearch: " + isSearch);

        progressDialog.show();

        if (nextToken.equals("")) {
            Log.d(TAG, "loadPosts: Next page token is empty, no more post");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    + "/posts?maxResults=" + Constants.MAX_POST_RESULTS
                    + "&key=" + Constants.API_KEY;
        } else if (nextToken.equals("end")) {
            Log.d(TAG, "loadPosts: Next Token is empty/end, no more post...");
            Toast.makeText(this, "No more posts...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        } else {
            Log.d(TAG, "loadPosts: next token:" + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID
                    + "/posts?maxResults=" + Constants.MAX_POST_RESULTS
                    + "&pageToken=" + nextToken
                    + "&key=" + Constants.API_KEY;
        }
        Log.d(TAG, "loadPosts: URL : " + url);

        //Request data, Method is GET not POST

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // we got response, so dismiss dialog first
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: " + response);
                try {
                    //response is JSON object
                    JSONObject jsonObject = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");

                        Log.d(TAG, "onResponse: nextPageToken " + nextToken);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Reached end of page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Reached end of page..." + e.getMessage());
                        nextToken = "end";
                    }

                    //get  json array from json
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    //continue getting data while its complected
                    for (int i = 0; i <= jsonArray.length(); i++) {
                        try {
                            //get data
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //    String image = jsonObject1.getJSONObject("author").getString("image");

                            //set data
                            ModelPost modelPost = new ModelPost("" + authorName,
                                    "" + content,
                                    "" + id,
                                    "" + published,
                                    "" + selfLink,
                                    "" + title,
                                    "" + updated,
                                    "" + url);

                            //ad data/model to List
                            postArrayList.add(modelPost);
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: 1: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    //setup adapter
                    adapterPost = new AdapterPost(MainActivity.this, postArrayList);
                    //set adapter to recycler view
                    postRV.setAdapter(adapterPost);
                    progressDialog.dismiss();

                } catch (Exception e) {
                    Log.d(TAG, "onResponse: 2: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get id of clicked meu item
        int id = item.getItemId();
        //handle menu item clicks
        if (id == R.id.action_pages) {
            startActivity(new Intent(this, PagesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}