package com.monotas.wearthistoday.autocode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.realm.Realm;

public class ImageTestActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mColorText;
    TextView mTypeText;
    TextView mIdText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
        mImageView = (ImageView)findViewById(R.id.echoImage);
        mColorText = (TextView)findViewById(R.id.echoColor);
        mTypeText = (TextView)findViewById(R.id.echoType);
        mIdText = (TextView)findViewById(R.id.echoId);
        Realm realm;
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        final ClothesData cl = realm.where(ClothesData.class).findFirst();

        try {
            JSONObject obj = new JSONObject();
            obj.put("id",cl.getId());
            obj.put("Color",cl.getColorText());
            obj.put("type",cl.getTypeText());
            String imagebase64string = cl.getImage();
            obj.put("image",imagebase64string);
            JSONObject alldata = new JSONObject();
            SharedPreferences prefs = getSharedPreferences("token",MODE_PRIVATE);
            String dataString = prefs.getString("token","null");
            String url = "http://wearthistoday.monotas.com/api/test/echo";
            alldata.put("token",dataString);
            alldata.put("data",obj);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    alldata,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());
                            try {
                                JSONObject js = response.getJSONObject("data");
                                Bitmap bmp = null;
                                String ims = js.getString("image");
                                byte[] im = Base64.decode(ims,0);
                                Log.d("im", String.valueOf(im));
                                //bmp = BitmapFactory.decodeByteArray(cl.getImageData(),0,cl.getImageData().length);
                                bmp = BitmapFactory.decodeByteArray(im,0,im.length);
                                mImageView.setImageBitmap(bmp);

                                mTypeText.setText(js.getString("type"));
                                mColorText.setText(js.getString("Color"));
                                mIdText.setText(js.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());

                        }
                    }
            );

            RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
