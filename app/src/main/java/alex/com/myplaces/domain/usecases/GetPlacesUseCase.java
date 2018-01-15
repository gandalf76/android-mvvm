package alex.com.myplaces.domain.usecases;

import android.util.Log;

import java.util.List;

import alex.com.myplaces.data.PlacesDataRepository;
import alex.com.myplaces.data.PlacesDataSource;
import alex.com.myplaces.domain.model.Place;

public class GetPlacesUseCase extends UseCase<GetPlacesUseCase.RequestValues, GetPlacesUseCase.ResponseValues> {

    private final PlacesDataRepository placesDataRepository;

    public GetPlacesUseCase(PlacesDataRepository placesDataRepository) {
        this.placesDataRepository = placesDataRepository;
    }

    @Override
    public void execute(RequestValues requestValues, final UseCaseResponseCallback<ResponseValues> responseCallback) {
        this.placesDataRepository.getPlaces(requestValues.getLat(), requestValues.getLng(), new PlacesDataSource.GetPlacesCallback() {
            @Override
            public void onPlacesLoaded(List<Place> places) {
                Log.d(GetPlacesUseCase.class.getName(), "Places loaded.");
                ResponseValues responseValues = new ResponseValues(places);
                responseCallback.response(responseValues);
            }

            @Override
            public void onPlacesLoadFailed() {
                Log.e(GetPlacesUseCase.class.getName(), "Error loading places.");
                responseCallback.responseError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final double lat;

        private final double lng;

        public RequestValues(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static final class ResponseValues implements UseCase.ResponseValues {

        private List<Place> places;

        public ResponseValues(List<Place> places) {
            this.places = places;
        }

        public List<Place> getPlaces() {
            return  this.places;
        }
    }
}
