package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Response;


public class FormActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Bundle p = intent.getExtras();
        Bitmap x = (Bitmap) p.get("Image");
        final String json =
                "{\"user\":{" +
                        "\"name\":\"name1\","+
                        "\"password\":\"password\","+
                        "\"password_confirmation\":\"password\""+
                        "}}";
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            String url = "http://wearthistoday.monotas.com/api/echo";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity2",response.toString());
                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("MainActivity3",error.toString());
                        }
                    }

            );

            RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
