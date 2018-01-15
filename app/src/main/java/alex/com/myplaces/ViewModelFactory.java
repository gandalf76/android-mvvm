package alex.com.myplaces;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import alex.com.myplaces.data.LocationDataRepository;
import alex.com.myplaces.data.PlacesDataRepository;
import alex.com.myplaces.data.network.volley.VolleyNetworkService;
import alex.com.myplaces.data.source.LocationDataSourceManager;
import alex.com.myplaces.data.source.PlacesRemoteDataSource;
import alex.com.myplaces.domain.usecases.EnableLocationUpdateUseCase;
import alex.com.myplaces.domain.usecases.GetPlacesUseCase;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory instance;

    private final Application application;

    private final EnableLocationUpdateUseCase enableLocationUpdateUseCase;

    private final GetPlacesUseCase getPlacesUseCase;

    public static ViewModelFactory getInstance(Application application) {

        if (instance == null) {
            synchronized (ViewModelFactory.class) {
                if (instance == null) {
                    LocationDataSourceManager locationDataSourceManager = LocationDataSourceManager.getInstance();
                    EnableLocationUpdateUseCase enableLocationUpdateUseCase = new EnableLocationUpdateUseCase(LocationDataRepository.getInstance(locationDataSourceManager));
                    GetPlacesUseCase getPlacesUseCase = new GetPlacesUseCase(PlacesDataRepository.getInstance(new PlacesRemoteDataSource(VolleyNetworkService.getInstance(application))));

                    instance = new ViewModelFactory(application,
                            enableLocationUpdateUseCase,
                            getPlacesUseCase);
                }
            }
        }
        return instance;
    }

    public void bindLocationDataSource(Lifecycle lifecycle) {
        if (((LifecycleRegistry) lifecycle).getObserverCount() == 0) {
            lifecycle.addObserver(LocationDataSourceManager.getInstance());
        }
    }

    @VisibleForTesting
    public static void destroyInstance() {
        instance = null;
    }

    private ViewModelFactory(Application application, EnableLocationUpdateUseCase enableLocationUpdateUseCase, GetPlacesUseCase getPlacesUseCase) {
        this.application = application;
        this.enableLocationUpdateUseCase = enableLocationUpdateUseCase;
        this.getPlacesUseCase = getPlacesUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(this.application, this.enableLocationUpdateUseCase, this.getPlacesUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}