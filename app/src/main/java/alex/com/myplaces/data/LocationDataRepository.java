package alex.com.myplaces.data;


import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.VisibleForTesting;

import alex.com.myplaces.data.source.LocationDataSourceManager;
import alex.com.myplaces.domain.model.Location;

public class LocationDataRepository implements LocationDataSource {

    private static LocationDataRepository instance;

    private LocationDataSourceManager locationDataSourceManager;

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private LocationDataRepository(LocationDataSourceManager locationDataSourceManager) {
        this.locationDataSourceManager = locationDataSourceManager;
    }

    public static LocationDataRepository getInstance(LocationDataSourceManager locationDataSourceManager) {

        if (instance == null) {
            synchronized (LocationDataRepository.class) {
                if (instance == null) {
                    instance = new LocationDataRepository(locationDataSourceManager);
                }
            }
        }
        return instance;
    }

    @Override
    public void enableLocationUpdate(final GetLocationCallback getLocationCallback) {

        this.locationDataSourceManager.enableLocationUpdate(new GetLocationCallback() {

            @Override
            public void onLocationUpdated(double lat, double lng) {
                getLocationCallback.onLocationUpdated(lat, lng);
            }
        });
    }

    @VisibleForTesting
    public static void destroyInstance() {
        instance = null;
    }

}
