package com.akaver.tabbedradio;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by akaver on 05/05/2017.
 */

public class WebApiSingletonServiceHandler {
    private static WebApiSingletonServiceHandler mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private WebApiSingletonServiceHandler(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized WebApiSingletonServiceHandler getInstance(Context context){
        if (mInstance == null){
            mInstance = new WebApiSingletonServiceHandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}

