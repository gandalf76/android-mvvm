package alex.com.myplaces;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import java.util.List;

import alex.com.myplaces.domain.model.Location;
import alex.com.myplaces.domain.model.Place;
import alex.com.myplaces.domain.usecases.EnableLocationUpdateUseCase;
import alex.com.myplaces.domain.usecases.GetPlacesUseCase;
import alex.com.myplaces.domain.usecases.UseCase;

public class MainViewModel extends AndroidViewModel {

    public ObservableArrayList<Place> places = new ObservableArrayList<>();

    public ObservableBoolean showLoader = new ObservableBoolean();

    public ObservableBoolean showLoaderPlaces = new ObservableBoolean();

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Place>> placesMutableLiveData = new MutableLiveData<>();

    private EnableLocationUpdateUseCase enableLocationUpdateUseCase;

    private GetPlacesUseCase getPlacesUseCase;

    public MainViewModel(Application application, EnableLocationUpdateUseCase enableLocationUpdateUseCase, GetPlacesUseCase getPlacesUseCase) {
        super(application);
        this.enableLocationUpdateUseCase = enableLocationUpdateUseCase;
        this.getPlacesUseCase = getPlacesUseCase;
    }

    public void enableGeoLocation() {

        this.showLoader.set(true);
        this.enableLocationUpdateUseCase.execute(null, new UseCase.UseCaseResponseCallback<EnableLocationUpdateUseCase.ResponseValues>() {
            @Override
            public void response(EnableLocationUpdateUseCase.ResponseValues responseValues) {
                showLoader.set(false);
                Location location = responseValues.getLocation();
                locationMutableLiveData.setValue(location);
            }

            @Override
            public void responseError() {
                showLoader.set(false);
                locationMutableLiveData.setValue(null);
            }
        });
    }
    public void loadPlaces(double lat, double lng) {
        this.showLoaderPlaces.set(true);
        final GetPlacesUseCase.RequestValues requestValues = new GetPlacesUseCase.RequestValues(lat, lng);
        this.getPlacesUseCase.execute(requestValues, new UseCase.UseCaseResponseCallback<GetPlacesUseCase.ResponseValues>() {
            @Override
            public void response(GetPlacesUseCase.ResponseValues responseValues) {
                showLoaderPlaces.set(false);
                placesMutableLiveData.setValue(responseValues.getPlaces());
                MainViewModel.this.places.addAll(responseValues.getPlaces());
            }

            @Override
            public void responseError() {
                showLoaderPlaces.set(false);
                placesMutableLiveData.setValue(null);
            }
        });
    }

    public MutableLiveData<Location> getLocationMutableLiveData() {
        return this.locationMutableLiveData;
    }

    public MutableLiveData<List<Place>> getPlacesMutableLiveData() {
        return this.placesMutableLiveData;
    }

}
