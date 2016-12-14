package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
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

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Response;


public class FormActivity extends AppCompatActivity {
    /*ここでView関連のオブジェクトを宣言*/
    Spinner colorSpinner;
    Spinner typeSpinner;
    ImageView imageView;

    /**/

    Bitmap clothesImage;
    Realm realm;

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form);
        colorSpinner = (Spinner)findViewById(R.id.colorSpinner);
        typeSpinner = (Spinner)findViewById(R.id.typeSpinner);
        imageView = (ImageView)findViewById(R.id.imageView);


        Intent intent = getIntent();
        Bundle p = intent.getExtras();
        clothesImage = (Bitmap) p.get("Image");
        imageView.setImageBitmap(clothesImage);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }
    /*登録ボタン*/
    public void register(View v){
        /*ここでフォームから情報を取得*/
        final String typeText = (String)typeSpinner.getSelectedItem();
        final String colorText=(String)colorSpinner.getSelectedItem();

        final String json =
                "{\"user\":{" +
                        "\"name\":\"name1\","+
                        "\"password\":\"password\","+
                        "\"password_confirmation\":\"password\""+
                        "}}";
        JSONObject obj = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        clothesImage.compress(Bitmap.CompressFormat.JPEG,50,baos);
        final byte[] mImageData = baos.toByteArray();
        prefs = getSharedPreferences("size",MODE_PRIVATE);
        int data = prefs.getInt("size",0);
        /*データの登録(ローカル)*/
        realm.beginTransaction();
        int id = data + 1;
        prefs.edit().putInt("size",id).apply();
        ClothesData clothesData = realm.createObject(ClothesData.class);
        clothesData.setColorText(colorText);
        clothesData.setTypeText(typeText);
        clothesData.setImage(Base64.encodeToString(mImageData,Base64.NO_WRAP));
        clothesData.setId(id);
        realm.commitTransaction();
        realm.close();
        //ここで通信を行います。
        try {
            obj = new JSONObject();
            obj.put("id",id);
            obj.put("Color",colorText);
            obj.put("type",typeText);
            obj.put("Image",mImageData);
            JSONObject alldata = new JSONObject();
            prefs = getSharedPreferences("token",MODE_PRIVATE);
            String dataString = prefs.getString("token","null");
            String url = "http://wearthistoday.monotas.com/api/test/echo";
            alldata.put("token",dataString);
            alldata.put("Data",obj);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    alldata,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());
                            finish();
                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());
                            finish();
                        }
                    }
            );

            RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



}
