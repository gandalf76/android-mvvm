package alex.com.myplaces.data;

public interface LocationDataSource {

    interface GetLocationCallback {

        void onLocationUpdated(double lat, double lng);

    }

    void enableLocationUpdate(GetLocationCallback getLocationCallback);
}
