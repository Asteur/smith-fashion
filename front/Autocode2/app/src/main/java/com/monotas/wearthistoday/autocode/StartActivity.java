package com.monotas.wearthistoday.autocode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class StartActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    SharedPreferences prefs;
    String data;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        layout = (RelativeLayout)findViewById(R.id.activity_start);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        // Tokenを取り出す. (もしないなら"null"が得られる)


        prefs = getSharedPreferences("token",MODE_PRIVATE);
        data = prefs.getString("token","null");
        if(data == "null"){
            Log.d("MainActivity","null");

        }
        else{
            Log.d("MainActivity","not");
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        }
    }

    public void login(View v){
        Log.d("OK","OK");
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
                        Log.d("Success","成功!");
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
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
                        Log.d("onCancel", "Cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("Error", String.valueOf(exception));
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
