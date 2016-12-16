package com.monotas.wearthistoday.autocode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class RecommendActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    boolean isPermitted;//位置情報取得許可フラグ, あとで別のActivityに移す
    boolean exist;
    RelativeLayout layout;
    ImageView recommendResult;

    private double longitude;
    private double latitude;
    TextView latitudeText, longitudeText;

    GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi;

    private LocationRequest locationRequest;
    private Location location;
    int casual;
    int formal;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        recommendResult = (ImageView)findViewById(R.id.recommendResult);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        Intent setting = getIntent();
        casual = setting.getIntExtra("casual",0);
        formal = setting.getIntExtra("formal",0);
        layout = (RelativeLayout)findViewById(R.id.activity_recommend);
        isPermitted = false;
        exist = false;
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);
        //sdk>=23の時
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        //それ以前の時
        else {
            isPermitted = true;
        }

        if (isPermitted) {

            Log.d("RecommendActivity", "hoge");
        } else {
            finish();
        }
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(16);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();
        mGoogleApiClient.connect();


    }


    /*位置情報許可の確認*/
    private void checkPermission() {
        //許可済み
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermitted = true;
        } else {
            requestLocationPermission();
        }
    }

    //許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(RecommendActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

        } else {
            Toast.makeText(this, "位置情報の利用を許可してください", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermitted = true;
                return;
            } else {
                Toast.makeText(this, "位置情報の利用を許可しないとアプリは実行できません", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("RecommendActivity", "Connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        Log.d("RecommendActivity","We can get latlang");
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();

        latitudeText.setText(String.valueOf(currentLocation.getLatitude()));
        longitudeText.setText(String.valueOf(currentLocation.getLongitude()));
        //ここから情報を送る。
        SharedPreferences prefs = getSharedPreferences("token",MODE_PRIVATE);
        String dataString = prefs.getString("token","null");
        /*
        try {
            JSONObject obj = new JSONObject();
            String url = "http://wearthistoday.monotas.com/api/test/weather";
            obj.put("token",dataString);
            obj.put("lat",latitude);
            obj.put("lon",longitude);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());

                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());
                            Snackbar.make(layout, "送信できません", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
            );

            RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        try {
            realm = Realm.getDefaultInstance();
            final ClothesData cl = realm.where(ClothesData.class).findFirst();
            JSONObject obj = new JSONObject();
            String url = "http://wearthistoday.monotas.com/api/test/echo";
            obj.put("token",dataString);
            obj.put("casual",casual);
            obj.put("formal",formal);
            String imagebase64string = cl.getImage();
            obj.put("image",imagebase64string);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());
                            try {

                                Bitmap bmp = null;
                                String ims = response.getString("image");
                                byte[] im = Base64.decode(ims,0);
                                Log.d("im", String.valueOf(im));
                                bmp = BitmapFactory.decodeByteArray(im,0,im.length);
                                recommendResult.setImageBitmap(bmp);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());
                            Snackbar.make(layout, "送信できません", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
            );

            RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void like(View v){
        SharedPreferences prefs = getSharedPreferences("token",MODE_PRIVATE);
        String dataString = prefs.getString("token","null");
        try {
            JSONObject obj = new JSONObject();
            String url = "http://wearthistoday.monotas.com/api/test/echo";
            obj.put("token",dataString);

            obj.put("recommend",1);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new com.android.volley.Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response){
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d("FormActivity",response.toString());

                        }
                    },
                    new com.android.volley.Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("FormActivity",error.toString());
                            Snackbar.make(layout, "送信できません", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }
            );

            RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dontlike(View v){
        Intent intent = new Intent(this,ChooseActivity.class);
        startActivity(intent);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
