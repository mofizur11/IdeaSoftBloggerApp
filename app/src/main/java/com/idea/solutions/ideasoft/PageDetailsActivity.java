package com.idea.solutions.ideasoft;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.xml.transform.OutputKeys.ENCODING;

public class PageDetailsActivity extends AppCompatActivity {

    //UI Views
    private TextView titleTv, publishInfoTv;
    private WebView webView;

    //that we passed in intent from adapter page
    private String pageId;

    //actionbar
    private ActionBar actionBar;

    private static final String TAG = "PageDetails_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_details);

        //init actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle("Android Tutorials");
        actionBar.setSubtitle("Page Details");
        //back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init UI Views
        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);

        pageId = getIntent().getStringExtra("pageId");
        Log.d(TAG, "onCreate: pageId: "+pageId);

        loadPageDetails();
    }

    private void loadPageDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+"/pages/"+Constants.PAGE_ID+"?key="+Constants.API_KEY;
        Log.d(TAG, "loadPageDetails: URL: "+url);

        //api request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //got api response
                Log.d(TAG, "onResponse: "+response);

                //response is in JSON Object
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    //get data
                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String id = jsonObject.getString("id");
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

                    //set data
                    titleTv.setText(title);
                    publishInfoTv.setText(published);
                    //load content is webView as it is in Web/html form
                    webView.loadDataWithBaseURL(null, content, "text/html", ENCODING, null);


                }catch (Exception e){
                    Log.d(TAG, "onResponse: "+e.getMessage());
                    Toast.makeText(PageDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //failed getting api response
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
                Toast.makeText(PageDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //ad request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //goto previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp();
    }
}