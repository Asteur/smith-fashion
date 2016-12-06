package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
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
import java.io.ByteArrayOutputStream;
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
import java.util.jar.Manifest;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Response;


public class FormActivity extends AppCompatActivity {
    /*ここでView関連のオブジェクトを宣言*/
    boolean isPermitted;//位置情報取得許可フラグ
    LocationManager mLocationManager;//位置情報取得用Object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPermitted = false;
        setContentView(R.layout.activity_form);

        //sdk>=23の時
        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }
        //それ以前の時
        else{
            isPermitted = true;
        }
        if(isPermitted){
            mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        }
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        x.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] mImageData = baos.toByteArray();

        try {
            obj = new JSONObject(json);
            obj.put("Image",mImageData);
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
    /*位置情報許可の確認*/
    private void checkPermission(){
        //許可済み
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            isPermitted = true;
        }
        else{
            requestLocationPermission();
        }
    }

    //許可を求める
    private void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(FormActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }
        else{
            Toast.makeText(this,"位置情報の利用を許可してください",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        if(requestCode == 1000){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermitted = true;
                return;
            }else{
                Toast.makeText(this,"位置情報の利用を許可しないとアプリは実行できません",Toast.LENGTH_SHORT).show();
            }
        }
    }
}