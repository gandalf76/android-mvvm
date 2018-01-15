package alex.com.myplaces.data.source;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.PositioningManager;

import java.lang.ref.WeakReference;

import alex.com.myplaces.data.LocationDataSource;
import alex.com.myplaces.domain.model.Location;


public class LocationDataSourceManager implements LocationDataSource, LifecycleObserver, PositioningManager.OnPositionChangedListener {

    private static LocationDataSourceManager instance;

    private GetLocationCallback getLocationCallback;

    private PositioningManager positioningManager;

    public static LocationDataSourceManager getInstance() {

        if (instance == null) {
            synchronized (LocationDataSourceManager.class) {
                instance = new LocationDataSourceManager();
            }
        }
        return instance;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        this.startPositioning();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        if (this.positioningManager != null) {
            this.positioningManager.stop();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void destroy() {
        if (this.positioningManager != null) {
            this.positioningManager.removeListener(this);
        }
    }

    @Override
    public void enableLocationUpdate(GetLocationCallback getLocationCallback) {
        this.getLocationCallback = getLocationCallback;
        this.setupComponents();
    }

    private void setupComponents() {
        this.positioningManager = PositioningManager.getInstance();
        this.setupListeners();
        this.startPositioning();
    }

    private void setupListeners() {
        this.positioningManager.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(this));
    }

    private synchronized void startPositioning() {
        if(this.positioningManager != null && !this.positioningManager.isActive()) {
            this.positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }

    @Override
    public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
        Location location;
        GeoPosition lastKnownPosition = this.positioningManager.getLastKnownPosition();
        if (lastKnownPosition == null) {
            location = new Location(geoPosition.getCoordinate().getLatitude(), geoPosition.getCoordinate().getLongitude());
        } else {
            location = new Location(lastKnownPosition.getCoordinate().getLatitude(), lastKnownPosition.getCoordinate().getLongitude());
        }
        if (this.getLocationCallback != null) {
            this.getLocationCallback.onLocationUpdated(location.getLat(), location.getLng());
        }
    }

    @Override
    public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

    }

}
