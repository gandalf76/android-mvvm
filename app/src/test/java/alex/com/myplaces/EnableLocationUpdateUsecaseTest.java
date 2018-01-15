package alex.com.myplaces;

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
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import alex.com.myplaces.data.LocationDataRepository;
import alex.com.myplaces.domain.model.Location;
import alex.com.myplaces.domain.usecases.EnableLocationUpdateUseCase;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Process.class, SystemClock.class})
public class EnableLocationUpdateUsecaseTest {

    private EnableLocationUpdateUseCase enableLocationUpdateUseCase;

    @Mock
    private LocationDataRepository locationDataRepository;

    @Mock
    private EnableLocationUpdateUseCase.UseCaseResponseCallback getEnableLocationUpdateUseCaseCallback;

    @Captor
    private ArgumentCaptor<LocationDataRepository.GetLocationCallback> getLocationCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<EnableLocationUpdateUseCase.ResponseValues> locationArgumentCaptor;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.enableLocationUpdateUseCase = new EnableLocationUpdateUseCase(this.locationDataRepository);
    }

    @After
    public void tearDown() {
        this.enableLocationUpdateUseCase = null;
    }

    @Test
    public void getLocationUpdateTest() {

        final double lat = 12;
        final double lng = 12;

        this.enableLocationUpdateUseCase.execute(null, getEnableLocationUpdateUseCaseCallback);
        verify(locationDataRepository).enableLocationUpdate(getLocationCallbackArgumentCaptor.capture());
        getLocationCallbackArgumentCaptor.getValue().onLocationUpdated(lat, lng);
        verify(getEnableLocationUpdateUseCaseCallback).response(locationArgumentCaptor.capture());

        Location testLocation = locationArgumentCaptor.getValue().getLocation();
        Assert.assertNotNull("Test location not null.", testLocation);
        Assert.assertEquals("Test location lat.", lat, testLocation.getLat(), 0);
        Assert.assertEquals("Test location lng.", lng, testLocation.getLng(), 0);
    }

}
