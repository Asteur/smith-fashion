package com.monotas.wearthistoday.autocode;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RecommendActivity extends AppCompatActivity implements LocationListener{
    boolean isPermitted;//位置情報取得許可フラグ, あとで別のActivityに移す
    LocationManager mLocationManager;//位置情報取得用Object
    boolean locationExists;
    private double longitude;
    private double latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        isPermitted = false;
        locationExists = false;
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
            Criteria criteria = new Criteria();
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
            ActivityCompat.requestPermissions(RecommendActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1000);
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

    @Override
    public void onLocationChanged(Location location) {
        if(!locationExists){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            locationExists = true;
            //ここで通信を行います。
            try {
                JSONObject obj = new JSONObject();
                obj.put("latitude",latitude);
                obj.put("longitude",longitude);
                String url = "http://wearthistoday.monotas.com/api/echo";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        obj,
                        new com.android.volley.Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                                Log.d("RecommendActivity",response.toString());


                            }
                        },
                        new com.android.volley.Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Log.d("RecommendActivity",error.toString());
                            }
                        }
                );

                RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
