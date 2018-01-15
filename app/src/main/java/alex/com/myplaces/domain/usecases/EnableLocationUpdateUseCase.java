package alex.com.myplaces.domain.usecases;

import alex.com.myplaces.data.LocationDataRepository;
import alex.com.myplaces.data.LocationDataSource;
import alex.com.myplaces.domain.model.Location;

public class EnableLocationUpdateUseCase extends UseCase<UseCase.RequestValues, EnableLocationUpdateUseCase.ResponseValues> {

    private final LocationDataRepository locationDataRepository;

    public EnableLocationUpdateUseCase(LocationDataRepository locationDataRepository) {
        this.locationDataRepository = locationDataRepository;
    }

    @Override
    public void execute(RequestValues requestValues, final UseCaseResponseCallback<ResponseValues> responseCallback) {

        this.locationDataRepository.enableLocationUpdate(new LocationDataSource.GetLocationCallback() {

            @Override
            public void onLocationUpdated(double lat, double lng) {
                Location location = new Location(lat, lng);
                ResponseValues responseValues = new ResponseValues(location);
                responseCallback.response(responseValues);
            }
        });
    }

    public static final class ResponseValues implements UseCase.ResponseValues {

        private Location location;

        public ResponseValues(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return  this.location;
        }
    }
}
