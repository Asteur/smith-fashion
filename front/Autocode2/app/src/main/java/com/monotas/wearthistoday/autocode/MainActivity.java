package com.monotas.wearthistoday.autocode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CAPTURE_IMAGE = 100;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    SharedPreferences prefs;
    Random ran;

    Button button;
    String data;
    RelativeLayout layout;
    //TextView tokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ran = new Random();
        setContentView(R.layout.activity_main);
        //tokenText = (TextView)findViewById(R.id.tokenText);

        layout = (RelativeLayout)findViewById(R.id.activity_main);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
         // Tokenを取り出す. (もしないなら"null"が得られる)


        prefs = getSharedPreferences("token",MODE_PRIVATE);
        data = prefs.getString("token","null");
        //tokenText.setText(data);
        if(data == "null"){
            Log.d("MainActivity","null");
            // LogInフォームへ
            LogInActivity();
        }
        else{
            Log.d("MainActivity","not");
            try {
                JSONObject obj = new JSONObject();
                // ここでTokenを送信する.
                String url = "http://wearthistoday.monotas.com/api/signin";
                obj.put("token",data);
                // データ送信
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        obj,
                        new com.android.volley.Response.Listener<JSONObject>(){
                            //成功した場合
                            @Override
                            public void onResponse(JSONObject response){
                                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                                Log.d("FormActivity",response.toString());
                            }
                        },
                        //失敗した場合
                        new com.android.volley.Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Log.d("FormActivity",error.toString());
                                Snackbar.make(layout, "送信できません", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                        }
                );
                //ここで送信を行う命令を出す
                RequestSingleton.getInstance(this).addToReqeustQueue(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }


    public void register(View v){
        data = prefs.getString("token","null");
        if(data != "null"){
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        }
    }

    public void closet(View v){
        data = prefs.getString("token","null");
        if(data != "null"){
            Intent intent = new Intent(this,ListActivity.class);
            startActivity(intent);
        }
    }
    /*ログアウトのボタン*/
    public void logOut(View v){
        //SharedPreferenceの削除
        prefs.edit().remove("token").apply();
        data = prefs.getString("token","null");
        Log.d("token",data);
        //ログアウトの処理
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                //ログアウトが終わったら、ログインフォームへ行く!
                finish();

            }
        }).executeAsync();
    }


    /*ログイン用のメソッド*/
    public void LogInActivity(){
        callbackManager = CallbackManager.Factory.create();
        // ここでログイン後の処理を実装.
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    //成功した場合
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        String accessToken= loginResult.getAccessToken().getToken();
                        Log.d("Token",accessToken);
                        //アクセストークンの保存
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token",accessToken);
                        editor.apply();
                        try {
                            JSONObject obj = new JSONObject();

                            String url = "http://wearthistoday.monotas.com/api/test/echo";
                            obj.put("token",accessToken);
                            Log.d("logintoken", String.valueOf(obj));
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

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
    }


    public void test(View v){
        //ここで通信のテストする!!
        try {
            JSONObject obj = new JSONObject();

            String url = "http://wearthistoday.monotas.com/api/test/alldata";

            obj.put("token",data);
            JSONArray array = new JSONArray();


            Log.d("logintoken", String.valueOf(obj));


            /*
            String ja= obj.toString();
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, ja, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // do something
                    Log.d("JSONArray",response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // do something
                    Log.d("JSONArray",error.toString());
                }
            });
            */

            HyperRequest hyperRequest = new HyperRequest(Request.Method.POST,url,obj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("Success",response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error",error.toString());
                        }
                    }
            );
            String newUrl = "http://wearthistoday.monotas.com/api/test/load";
            HyperRequest hyperRequest2 = new HyperRequest(Request.Method.POST,url,obj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("Success",response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error",error.toString());
                        }
                    }
            );
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,
                    newUrl,
                    null,
                    new Response.Listener<JSONArray>(){
                        @Override
                        public void onResponse(JSONArray response) {

                            Log.d("test/load", String.valueOf(response));

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("Error", String.valueOf(error));

                        }
                    }
            );



            RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(hyperRequest);
            RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(req);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this,ImageTestActivity.class);
        startActivity(intent);

    }
    public void initial_register(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void recommend(View v){
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");
            Intent formIntent = new Intent(this,FormActivity.class);
            formIntent.putExtra("Image",capturedImage);
            startActivity(formIntent);
        }

    }
    @Override
    protected void onRestart(){
        super.onRestart();

        int val = ran.nextInt(3);
        Log.d("val", String.valueOf(val));
        if(val == 0){
            Intent intent = new Intent(this,EvalActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("main","OnDestroy");
    }
}
