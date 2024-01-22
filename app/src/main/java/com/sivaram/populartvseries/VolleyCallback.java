package com.sivaram.populartvseries;

import com.android.volley.VolleyError;

import org.json.JSONException;

public interface VolleyCallback {
    void onSuccess(String response) throws JSONException;
    void onError(VolleyError error) throws JSONException;
}
