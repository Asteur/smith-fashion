package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    JSONObject rec;
    SharedPreferences prefs;
    ArrayList<JSONObject> ja;
    ArrayDataAdapter ada;
    public ListView mListView;
    String json;
    JSONArray images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mListView = (ListView)findViewById(R.id.resultView);
        ja = new ArrayList<>();
        Intent intent = getIntent();
        json = intent.getExtras().getString("json");
        try {
            rec = new JSONObject(json);
            String url = "http://wearthistoday.monotas.com/api/test/recommand";
            prefs = getSharedPreferences("token",MODE_PRIVATE);
            String data = prefs.getString("token","null");
            rec.put("token",prefs);

            Log.d("rec", String.valueOf(rec));
            Log.d("type",rec.get("images").getClass().toString());
            images = rec.getJSONArray("images");
            // データ送信
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    rec,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        //成功した場合
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());
                            JSONArray jsonArray = new JSONArray();
                            try {
                                jsonArray = (JSONArray) response.get("result");
                                for(int i = 0 ; i < jsonArray.length(); i++){
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    ja.add(jsonObject);

                                }


                                ada = new ArrayDataAdapter(getApplicationContext(),0,ja);

                                mListView.setAdapter(ada);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    //失敗した場合
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());


                        }
                    }
            );
            //ここで送信を行う命令を出す
            RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public class ArrayDataAdapter extends ArrayAdapter<JSONObject> {
        private LayoutInflater layoutInflater;

        public ArrayDataAdapter(Context context, int resource, List<JSONObject> objects) {
            super(context, resource, objects);
            this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.resultlist,parent,false);
            }
            JSONObject clothesData = (JSONObject) getItem(position);


            Bitmap bmp = null;
            String ims;
            try {
                Log.d("index", String.valueOf(clothesData.getInt("index")));
                ims = (String) images.get(clothesData.getInt("index"));

                byte[] im = Base64.decode(ims,0);
                bmp = BitmapFactory.decodeByteArray(im,0,im.length);

                ((ImageView)convertView.findViewById(R.id.resultImageView)).setImageBitmap(bmp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!clothesData.isNull("kawaii")){
                try {
                    ((TextView)convertView.findViewById(R.id.kawaiiresult)).setText(clothesData.getString("kawaii"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!clothesData.isNull("formal")){
                try {
                    ((TextView)convertView.findViewById(R.id.formalresult)).setText(clothesData.getString("formal"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!clothesData.isNull("kakkoii")){
                try {
                    ((TextView)convertView.findViewById(R.id.coolresult)).setText(clothesData.getString("kakkoii"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!clothesData.isNull("casual")){
                try {
                    ((TextView)convertView.findViewById(R.id.casualresult)).setText(clothesData.getString("casual"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return convertView;

        }


    }
}
