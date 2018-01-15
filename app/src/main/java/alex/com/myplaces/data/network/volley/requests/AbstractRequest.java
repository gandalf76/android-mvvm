package alex.com.myplaces.data.network.volley.requests;


import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;

import alex.com.myplaces.data.network.volley.ApiError;
import alex.com.myplaces.data.network.volley.ApiErrorListener;

public abstract class AbstractRequest <ResponseType> extends Request<ResponseType> {

    private static final String TAG = AbstractRequest.class.getName();

    private final Response.Listener<ResponseType> resultListener;

    public AbstractRequest(int method, @NonNull String uri, @NonNull Response.Listener<ResponseType> resultListener, @NonNull final ApiErrorListener errorListener) {
        this(method, Collections.<String, String>emptyMap(), uri, resultListener, errorListener);
    }

    public AbstractRequest(int method, Map<String, String> customHeaders, @NonNull String uri, @NonNull Response.Listener<ResponseType> resultListener, @NonNull final ApiErrorListener errorListener) {
        super(method, uri, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ApiError error = parseApiError(volleyError);
                Log.d(TAG,"Request failed error:" + error);
            }
        });
        this.resultListener = resultListener;
    }

    private static ApiError parseApiError(VolleyError volleyError) {
        Log.d(TAG,"Request failed error:" + volleyError);

        ApiError apiErr = new ApiError();
        apiErr.setDescription(volleyError.getMessage());
        if(volleyError.networkResponse!=null) {
            apiErr.setCode(volleyError.networkResponse.statusCode);
        }
        return apiErr;
    }

    @Override
    protected Response<ResponseType> parseNetworkResponse(NetworkResponse networkResponse) {
        Gson gson = new Gson();
        try {
            ResponseType response = null;
            if (networkResponse.data != null && networkResponse.data.length > 0) {
                InputStream inputStream = new ByteArrayInputStream(networkResponse.data);
                Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                response =  gson.fromJson(inputStreamReader, getResponseType());
            }
            Log.d(TAG, "Service response status: " + networkResponse.statusCode);
            switch (networkResponse.statusCode) {
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_ACCEPTED:
                case HttpURLConnection.HTTP_NO_CONTENT:
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(networkResponse));
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_NOT_MODIFIED:
                    return Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
                default:
                    throw new IllegalArgumentException("No response received.");
            }

        } catch (UnsupportedEncodingException | JsonParseException | ClassCastException | IllegalArgumentException e) {
            Log.e(TAG, "Malformed response received. " + e);
            NetworkError error=new NetworkError(networkResponse);
            Response<ResponseType> resp = Response.error(error);
            return resp;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected void deliverResponse(ResponseType response) {
        Log.d(TAG, "Delivery API response " + response);
        this.resultListener.onResponse(response);
    }

    protected abstract Type getResponseType();

}
