package alex.com.myplaces;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alex.com.myplaces.databinding.ActivityMainBinding;
import alex.com.myplaces.domain.model.Location;
import alex.com.myplaces.domain.model.Place;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private ActivityMainBinding binding;

    private MainViewModel viewModel;

    private MapFragment mapFragment;

    private Map map;

    private Location lastGoodLocation;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.subscribeToNavigationChanges();
        this.setupWidgets();
        this.checkPermissions();
    }

    private void subscribeToNavigationChanges() {
        this.viewModel = obtainViewModel(this);
        this.binding.setViewmodel(viewModel);

        viewModel.getLocationMutableLiveData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (lastGoodLocation == null && location != null && map != null) {
                    Log.d(MainActivity.class.getName(), String.format("Location updated: lat %f - lng %f", location.getLat(), location.getLng()));
                    map.setCenter(new GeoCoordinate(location.getLat(), location.getLng(), 0.0), Map.Animation.NONE);
                    viewModel.loadPlaces(location.getLat(), location.getLng());
                    lastGoodLocation = location;
                }
            }
        });
        viewModel.getPlacesMutableLiveData().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                if (places != null && map != null) {
                    fillMapWithMarkers(places);
                }
            }
        });
    }

    private void setupWidgets() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(this.binding.bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottomsheet_header_height));

        this.binding.rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvPlaces.setHasFixedSize(true);
        PlacesAdapter placesAdapter = new PlacesAdapter();
        this.binding.rvPlaces.setAdapter(placesAdapter);
    }

    private void fillMapWithMarkers(List<Place> places) {
        int pCounter = 0;
        GeoBoundingBox geoBoundingBox = map.getBoundingBox();
        for (Place place : places) {
            if(pCounter < 5) {
                GeoCoordinate geoCoordinate = new GeoCoordinate(place.getLat(), place.getLng());
                MapMarker mapMarker = new MapMarker();
                mapMarker.setCoordinate(geoCoordinate);
                mapMarker.setTitle(place.getTitle());
                mapMarker.setDescription(place.getTitle());
                map.addMapObject(mapMarker);
                geoBoundingBox = geoBoundingBox.merge(new GeoBoundingBox(geoCoordinate, geoCoordinate));
            }
            pCounter++;
        }
        map.zoomTo(geoBoundingBox, Map.Animation.LINEAR, 0);
    }

    @Override
    protected void onStart() {
        this.lastGoodLocation = null;
        super.onStart();
    }

    @BindingAdapter("app:items")
    public static void setItems(RecyclerView recyclerView, List<Place> places) {
        ((PlacesAdapter)recyclerView.getAdapter()).updatePlaces(places);
    }

    private MainViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        factory.bindLocationDataSource(getLifecycle());
        return ViewModelProviders.of(activity, factory).get(MainViewModel.class);
    }

    private void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (missingPermissions.isEmpty()) {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        } else {
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                initMap();
                break;
        }
    }

    private void initMap() {
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.setRetainInstance(false);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    Log.d(MainActivity.class.getName(), "Map fragment initialized.");
                    map = mapFragment.getMap();
                    map.setZoomLevel(map.getMaxZoomLevel() - 1);
                    if (lastGoodLocation != null) {
                        map.setCenter(new GeoCoordinate(lastGoodLocation.getLat(), lastGoodLocation.getLng(), 0.0), Map.Animation.NONE);
                    }
                    viewModel.enableGeoLocation();
                } else {
                    Log.d(MainActivity.class.getName(), "Map fragment not initialized. onEngineInitializationCompleted: error: " + error + ", exiting");;
                    finish();
                }
            }
        });
    }
}
