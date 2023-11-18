package com.example.chatalk.Utills;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageGenerator {
    Context context;
    String url = "https://stablediffusionapi.com/api/v3/img2img";

    ImageGenerator(Context context){
        this.context = context;
    }

    public void generate(String prompt,int width,int height, int count, OnLoaded onLoaded){
        ArrayList<String> arrayList = new ArrayList<>();
        JSONObject js = new JSONObject();
        try{
            String key= "eIHJgcMRKmEYTGDolopnoyoCvIh97prNnAap1DNcnF6BwSelGi5D6dXqm66x";
            js.put("key",key);
            js.put("prompt",prompt);
            js.put("samples",count);
            js.put("width",width);
            js.put("height",height);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response!=null){
                    JSONArray data;
                    try{
                        data= response.getJSONArray("output");
                        for(int i = 0;i<count;i++){
                            arrayList.add(data.getString(i));
                        }
                        onLoaded.loaded(arrayList);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/json");

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20*100,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
