package com.monotas.wearthistoday.autocode;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by daiki on 2016/12/05.
 */

public class RequestSingleton {
    private static RequestSingleton ourInstance;
    private RequestQueue mRequestQue;
    private static Context mCtx;

    public static synchronized RequestSingleton getInstance(Context context){
        if(ourInstance == null){
            ourInstance = new RequestSingleton(context);
        }
        return ourInstance;
    }

    private RequestSingleton(Context context){
        mCtx = context;
        mRequestQue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQue == null){
            mRequestQue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQue;
    }

    public <T> void addToReqeustQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
