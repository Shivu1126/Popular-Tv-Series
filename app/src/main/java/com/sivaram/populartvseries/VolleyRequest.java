package com.sivaram.populartvseries;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {
    private Context context;
    public VolleyRequest(Context context){
        this.context = context;
    }

    public void getRequest(String urlPath, VolleyCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = this.context.getString(R.string.base_url)+urlPath;
        Log.d("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    callback.onError(error);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.d("CAR", "get Request onError"+error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + context.getString(R.string.access_token));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
