package alex.com.myplaces;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import alex.com.myplaces.domain.model.Location;
import alex.com.myplaces.domain.model.Place;
import alex.com.myplaces.domain.usecases.EnableLocationUpdateUseCase;
import alex.com.myplaces.domain.usecases.GetPlacesUseCase;
import alex.com.myplaces.domain.usecases.UseCase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Process.class, SystemClock.class})
public class MainViewModelTest {

    private MainViewModel mainViewModel;

    @Mock
    private Application application;

    @Mock
    private EnableLocationUpdateUseCase enableLocationUpdateUseCase;

    @Mock
    private GetPlacesUseCase getPlacesUseCase;

    @Captor
    private ArgumentCaptor<UseCase.UseCaseResponseCallback> getPlacesUseCaseResponseCallbackInCaptor;

    @Captor
    private ArgumentCaptor<UseCase.UseCaseResponseCallback> enableLocationUseCaseResponseCallbackInCaptor;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mainViewModel = new MainViewModel(this.application, enableLocationUpdateUseCase, getPlacesUseCase);
    }

    @After
    public void tearDown() {
        this.mainViewModel = null;
    }

    @Test
    public void getPlacesTest() {

        final double lat = 12;
        final double lng = 12;
        final List<Place> mockPlaces = new ArrayList<>();
        final Place mockPlace = new Place();
        mockPlace.setTitle("title_1");
        final List<Double> mockPosition = new ArrayList<>(2);
        mockPosition.add(13D);
        mockPosition.add(13D);
        mockPlace.setPosition(mockPosition);
        mockPlaces.add(mockPlace);
        GetPlacesUseCase.ResponseValues mockResponseValues = new GetPlacesUseCase.ResponseValues(mockPlaces);

        this.mainViewModel.loadPlaces(lat, lng);
        Assert.assertTrue("Test loader showing.", this.mainViewModel.showLoaderPlaces.get());

        verify(getPlacesUseCase).execute(any(GetPlacesUseCase.RequestValues.class), getPlacesUseCaseResponseCallbackInCaptor.capture());
        getPlacesUseCaseResponseCallbackInCaptor.getValue().response(mockResponseValues);

        Assert.assertFalse("Test loader hidden.", this.mainViewModel.showLoaderPlaces.get());
        Assert.assertEquals("Test load places size.", 1, this.mainViewModel.places.size());

        Place testPlace = this.mainViewModel.places.get(0);
        Assert.assertNotNull("Test place not null.", testPlace);
        Assert.assertEquals("Test place title.", mockPlace.getTitle(), testPlace.getTitle());
        Assert.assertEquals("Test place lat.", mockPlace.getLat(), testPlace.getLat(), 0);
        Assert.assertEquals("Test place lng.", mockPlace.getLng(), testPlace.getLng(), 0);
    }

    @Test
    public void getPlacesFailedTest() {

        final double lat = 12;
        final double lng = 12;

        this.mainViewModel.loadPlaces(lat, lng);
        Assert.assertTrue("Test loader showing.", this.mainViewModel.showLoaderPlaces.get());

        verify(getPlacesUseCase).execute(any(GetPlacesUseCase.RequestValues.class), getPlacesUseCaseResponseCallbackInCaptor.capture());
        getPlacesUseCaseResponseCallbackInCaptor.getValue().responseError();

        Assert.assertFalse("Test loader hidden.", this.mainViewModel.showLoaderPlaces.get());
        Assert.assertNull("Test load places failed.", this.mainViewModel.getPlacesMutableLiveData().getValue());
    }

    @Test
    public void getGeolocationTest() {

        final double lat = 12;
        final double lng = 12;
        final Location mockLocation = new Location(lat, lng);
        final EnableLocationUpdateUseCase.ResponseValues mockResponseValues = new EnableLocationUpdateUseCase.ResponseValues(mockLocation);

        this.mainViewModel.enableGeoLocation();
        Assert.assertTrue("Test loader showing.", this.mainViewModel.showLoader.get());

        verify(enableLocationUpdateUseCase).execute(Mockito.<UseCase.RequestValues>any(), enableLocationUseCaseResponseCallbackInCaptor.capture());
        enableLocationUseCaseResponseCallbackInCaptor.getValue().response(mockResponseValues);

        Assert.assertFalse("Test loader hidden.", this.mainViewModel.showLoader.get());

        Location testLocation = this.mainViewModel.getLocationMutableLiveData().getValue();
        Assert.assertNotNull("Test location not null.", testLocation);
        Assert.assertEquals("Test location lat.", mockLocation.getLat(), testLocation.getLat(), 0);
        Assert.assertEquals("Test location lng.", mockLocation.getLng(), testLocation.getLng(), 0);
    }

    @Test
    public void getGeolocationFailedTest() {

        this.mainViewModel.enableGeoLocation();
        Assert.assertTrue("Test loader showing.", this.mainViewModel.showLoader.get());

        verify(enableLocationUpdateUseCase).execute(Mockito.<UseCase.RequestValues>any(), enableLocationUseCaseResponseCallbackInCaptor.capture());
        enableLocationUseCaseResponseCallbackInCaptor.getValue().responseError();

        Assert.assertFalse("Test loader hidden.", this.mainViewModel.showLoaderPlaces.get());
        Assert.assertNull("Test get location failed.", this.mainViewModel.getLocationMutableLiveData().getValue());
    }

}
