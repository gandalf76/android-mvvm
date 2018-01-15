package alex.com.myplaces.data.network.volley.requests;

import android.support.annotation.NonNull;

import com.android.volley.Response;

import java.lang.reflect.Type;

import alex.com.myplaces.BuildConfig;
import alex.com.myplaces.data.network.utils.UriBuilder;
import alex.com.myplaces.data.network.volley.ApiErrorListener;

public class GetPlacesRequest extends AbstractRequest<ResultRequestObject> {

    private static final String TAG = "get_places_request";

    private static final String PLACES_PATH = "places/v1/discover/around";

    private static final String AT_QUERY_PARAMETER = "at";

    private static final String APP_ID_QUERY_PARAMETER = "app_id";

    private static final String APP_CODE_QUERY_PARAMETER = "app_code";

    public GetPlacesRequest(double lat, double lng, @NonNull Response.Listener<ResultRequestObject> resultListener,
                              @NonNull ApiErrorListener errorListener) {
        super(Method.GET, composeUri(lat, lng), resultListener, errorListener);
        setTag(TAG);
    }

    @NonNull
    private static String composeUri(double lat, double lng) {
        UriBuilder uriBuilder = new UriBuilder();
        uriBuilder = uriBuilder.parse(BuildConfig.hereBaseUrl);
        uriBuilder.appendPath(PLACES_PATH);
        StringBuilder sb = new StringBuilder(String.valueOf(lat))
                .append(",")
                .append(String.valueOf(lng));
        uriBuilder.appendQueryParameter(AT_QUERY_PARAMETER, sb.toString());
        uriBuilder.appendQueryParameter(APP_ID_QUERY_PARAMETER, BuildConfig.hereAppId);
        uriBuilder.appendQueryParameter(APP_CODE_QUERY_PARAMETER, BuildConfig.hereAppCode);
        return uriBuilder.build();
    }


    @Override
    protected Type getResponseType() {
        return ResultRequestObject.class;
    }
}
