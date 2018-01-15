package alex.com.myplaces.data;

import java.util.List;

import alex.com.myplaces.domain.model.Place;

public interface PlacesDataSource {

    interface GetPlacesCallback {

        void onPlacesLoaded(List<Place> places);

        void onPlacesLoadFailed();
    }

    /**
     *
     * Load a list {@link Place } close to the position specified
     *
     * @param lat : latitude in degrees
     * @param lng : longitude in degrees
     * @param getPlacesCallback : result of loading places operation
     */
    void getPlaces(double lat, double lng, GetPlacesCallback getPlacesCallback);
}
