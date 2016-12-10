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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CAPTURE_IMAGE = 100;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    SharedPreferences prefs;

    Button button;
    String data;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (RelativeLayout)findViewById(R.id.activity_main);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
         // Tokenを取り出す. (もしないなら"null"が得られる)


        prefs = getSharedPreferences("token",MODE_PRIVATE);
        data = prefs.getString("token","null");
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
                String url = "http://wearthistoday.monotas.com/api/test/echo";
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
        prefs.edit().remove("token");
        data = "null";
        //ログアウトの処理
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                //ログアウトが終わったら、ログインフォームへ行く!
                LogInActivity();
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

    }
    public void initial_register(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
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
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
