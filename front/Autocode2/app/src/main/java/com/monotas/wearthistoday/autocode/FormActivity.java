package com.monotas.wearthistoday.autocode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FormActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent intent = getIntent();
        Bundle p = intent.getExtras();
        Bitmap x = (Bitmap)p.get("Image");

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        /*ここで非同期処理を書く*/
                        String datajson = "{\"user\":{" +
                                "\"name\":\"name1\","+
                                "\"password\":\"password\","+
                                "\"password_confirmation\":\"password\""+
                                "}}";
                        try{
                            /*送信の部分*/
                            String buffer= "";
                            HttpURLConnection huc = null;
                            URL url = new URL("");//ここの部分のURLを書き換えてね!!
                            huc = (HttpURLConnection)url.openConnection();
                            huc.setRequestMethod("POST");
                            huc.setInstanceFollowRedirects(false);
                            huc.setRequestProperty("Accept-Language", "jp");
                            huc.setDoOutput(true);
                            huc.setRequestProperty("Content-Type","application/json; charset=utf-8");
                            OutputStream os = huc.getOutputStream();
                            PrintStream ps = new PrintStream(os);
                            ps.print(datajson);
                            ps.close();

                            /*送信結果を受け取る*/
                            BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream(),"UTF-8"));
                            buffer = reader.readLine();
                            JSONArray jsonarray = new JSONArray(buffer);
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonObject = jsonarray.getJSONObject(i);
                                Log.d("HTTP REQ", jsonObject.getString("name"));
                            }
                            huc.disconnect();


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }
}
