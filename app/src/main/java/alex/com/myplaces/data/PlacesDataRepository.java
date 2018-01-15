package alex.com.myplaces.data;

import android.support.annotation.VisibleForTesting;

import java.util.List;

import alex.com.myplaces.domain.model.Place;

public class PlacesDataRepository implements PlacesDataSource{

    private static  PlacesDataRepository instance;

    private PlacesDataSource placesDataSource;

    private PlacesDataRepository(PlacesDataSource placesDataSource) {
        this.placesDataSource = placesDataSource;
    }

    public static PlacesDataRepository getInstance(PlacesDataSource placesDataSource) {

        if (instance == null) {
            synchronized (PlacesDataRepository.class) {
                if (instance == null) {
                    instance = new PlacesDataRepository(placesDataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public void getPlaces(double lat, double lng, final GetPlacesCallback getPlacesCallback) {
        this.placesDataSource.getPlaces(lat, lng, new GetPlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                getPlacesCallback.onPlacesLoaded(places);
            }

            @Override
            public void onPlacesLoadFailed() {
                getPlacesCallback.onPlacesLoadFailed();
            }
        });
    }

    @VisibleForTesting
    public static void destroyInstance() {
        instance = null;
    }
}
