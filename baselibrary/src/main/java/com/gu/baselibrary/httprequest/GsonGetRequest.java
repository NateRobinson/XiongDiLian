package com.gu.baselibrary.httprequest;


import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Makes a get request and converts the response from JsonElement into a
 * list of objects/object using with Google Gson.
 */
public class GsonGetRequest<T> extends Request<T> {
    private Gson gson;
    private Type type;
    private Response.Listener<T> listener;
    private String cookie = "";

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url           URL of the request to make
     * @param type          is the type of the object to be returned
     * @param listener      is the listener for the right answer
     * @param errorListener is the listener for the wrong answer
     */
    public GsonGetRequest
    (
            @NonNull String url,
            @NonNull Type type,
            @NonNull Response.Listener<T> listener,
            @NonNull Response.ErrorListener errorListener,
            String cookie
    ) {
        super(Method.GET, url, errorListener);
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        this.gson = gb.create();
        this.type = type;
        this.listener = listener;
        this.cookie = cookie;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Cookie", cookie);
        return headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            return (Response<T>) Response.success
                    (
                            gson.fromJson(json, type),
                            HttpHeaderParser.parseCacheHeaders(response)
                    );
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
