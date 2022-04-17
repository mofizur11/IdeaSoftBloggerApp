package com.idea.solutions.ideasoft;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.media.AudioRecord.MetricsConstants.ENCODING;

public class PostDetailsActivity extends AppCompatActivity {

    //UI views
    private TextView titleTv, publishInfoTv;
    private WebView webView;
    private RecyclerView labelsRv, commentsRv;

    private String postId; //will get from intent, was passed in intent from AdapterPost
    private static final String TAG = "POST_DETAILS_TAG";
    private static final String TAG_COMMENTS = "POST_COMMENTS_TAG";

    private ArrayList<ModelLabel> labelArrayList;
    private AdapterLabel adapterLabel;

    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;

    //actionBar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //init actionbar
        actionBar = getSupportActionBar();
        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Post Details");

        //init UI Views
        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);
        labelsRv = findViewById(R.id.labelsRv);
        commentsRv = findViewById(R.id.commentsRv);


        //get post id from intent, was passed in intent from AdapterPost
        postId = getIntent().getStringExtra("postId");
        Log.d(TAG, "onCreate: " + postId);

        //setup webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        loadPostDetails();
    }

    private void loadPostDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/" + Constants.BLOG_ID
                + "/posts/" + postId
                + "?key=" + Constants.API_KEY;
        Log.d(TAG, "loadPostDetails: URL" + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //successfully received response
                Log.d(TAG, "onResponse: " + response);

                //Response is a JSON object
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //---------------get data------------
                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String displayName = jsonObject.getJSONObject("author").getString("displayName");

                    //convert GMT data to proper format
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

                    //----------set data---------
                    actionBar.setSubtitle(title);
                    titleTv.setText(title);
                    publishInfoTv.setText("By " + displayName + " " + formattedDate);
                    //content contains web page like html, so load in webview
                    webView.loadDataWithBaseURL(null, content, "text/html", ENCODING, null);

                    //gt labels of post
                    try {
                        //init and clear list before adding data into it
                        labelArrayList = new ArrayList<>();
                        labelArrayList.clear();

                        //json array of labels
                        JSONArray jsonArray = jsonObject.getJSONArray("labels");
                        for (int i = 1; i < jsonArray.length(); i++) {
                            String label = jsonArray.getString(i);
                            //add label in model
                            ModelLabel modelLabel = new ModelLabel(label);
                            //add model in list
                            labelArrayList.add(modelLabel);
                        }
                        //setup adapter
                        adapterLabel = new AdapterLabel(PostDetailsActivity.this, labelArrayList);
                        //set adapter to recycler view
                        labelsRv.setAdapter(adapterLabel);

                    } catch (Exception e) {
                        Log.d(TAG, "onResponse: " + e.getMessage());
                    }

                    //post details loaded, now load comments
                    loadComments();


                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e.getMessage());
                    Toast.makeText(PostDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed receiving response, show error message
                Toast.makeText(PostDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void loadComments() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/" + Constants.BLOG_ID
                + "/posts/" + postId + "/comments?key=" + Constants.API_KEY;
        Log.d(TAG_COMMENTS, "loadComments: " + url);

        //Volley string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //received response
                Log.d(TAG_COMMENTS, "onResponse: " + response);

                //init and clear array list before adding data
                commentArrayList = new ArrayList<>();
                commentArrayList.clear();

                try {
                    //response is in JSON Object form
                    JSONObject jsonObject = new JSONObject(response);
                    //get array of comments
                    JSONArray jsonArrayItems = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        //get specific comment json object
                        JSONObject jsonObjectComment = jsonArrayItems.getJSONObject(i);
                        //get data from that json object
                        String id = jsonObjectComment.getString("id");
                        String published = jsonObjectComment.getString("published");
                        String content = jsonObjectComment.getString("content");
                        String displayName = jsonObjectComment.getJSONObject("author").getString("displayName");
                        String profileImage = "http:" + jsonObjectComment.getJSONObject("author").getJSONObject("image").getString("url");
                        Log.d("TAG_IMAGE_URL", "onResponse: " + profileImage);

                        //add data to model
                        ModelComment modelComment = new ModelComment(
                                "" + id,
                                "" + displayName,
                                "" + profileImage,
                                "" + published,
                                "" + content
                        );
                        //add model to arrayList
                        commentArrayList.add(modelComment);
                    }
                    //setup adapter
                    adapterComment = new AdapterComment(PostDetailsActivity.this, commentArrayList);
                    //set adapter to recyclerView
                    commentsRv.setAdapter(adapterComment);

                } catch (Exception e) {
                    Log.d(TAG_COMMENTS, "onResponse: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed getting response
                Log.d(TAG_COMMENTS, "onErrorResponse: " + error.getMessage());
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