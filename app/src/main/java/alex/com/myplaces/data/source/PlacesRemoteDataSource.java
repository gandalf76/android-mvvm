package alex.com.myplaces.data.source;

import com.android.volley.Response;

import alex.com.myplaces.data.PlacesDataSource;
import alex.com.myplaces.data.network.volley.ApiError;
import alex.com.myplaces.data.network.volley.ApiErrorListener;
import alex.com.myplaces.data.network.volley.VolleyNetworkService;
import alex.com.myplaces.data.network.volley.requests.GetPlacesRequest;
import alex.com.myplaces.data.network.volley.requests.ResultRequestObject;

public class PlacesRemoteDataSource implements PlacesDataSource {

    private VolleyNetworkService volleyNetworkService;

    public PlacesRemoteDataSource(VolleyNetworkService volleyNetworkService) {
        this.volleyNetworkService = volleyNetworkService;
    }

    @Override
    public void getPlaces(double lat, double lng, final GetPlacesCallback getPlacesCallback) {
        GetPlacesRequest request = new GetPlacesRequest(lat, lng, new Response.Listener<ResultRequestObject>() {
            @Override
            public void onResponse(ResultRequestObject resultRequestObject) {
                getPlacesCallback.onPlacesLoaded(resultRequestObject.getResults().getItems());
            }
        }, new ApiErrorListener() {
            @Override
            public void onErrorResponse(ApiError error) {
                getPlacesCallback.onPlacesLoadFailed();
            }
        });
        this.volleyNetworkService.getRequestQueue().add(request);
    }

}
