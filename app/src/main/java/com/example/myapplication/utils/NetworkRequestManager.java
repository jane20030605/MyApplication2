package com.example.myapplication.utils;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;

public class NetworkRequestManager {

    private static NetworkRequestManager instance;
    private RequestQueue requestQueue;
    private Context context;

    private NetworkRequestManager(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkRequestManager(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public interface RequestListener {
        void onSuccess(String response);
        void onError(String error);
    }

    public void makeGetRequest(String url, final RequestListener listener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (listener != null) {
                            listener.onSuccess(response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                    }
                });
        addToRequestQueue(jsonArrayRequest);
    }
}
