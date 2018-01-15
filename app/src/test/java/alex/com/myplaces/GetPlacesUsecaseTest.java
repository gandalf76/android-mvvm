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

import java.util.ArrayList;
import java.util.List;

import alex.com.myplaces.data.PlacesDataRepository;
import alex.com.myplaces.domain.model.Place;
import alex.com.myplaces.domain.usecases.GetPlacesUseCase;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Process.class, SystemClock.class})
public class GetPlacesUsecaseTest {

    private GetPlacesUseCase getPlacesUseCase;

    @Mock
    private PlacesDataRepository placesDataRepository;

    @Mock
    private GetPlacesUseCase.UseCaseResponseCallback getPlacesUseCaseCallback;

    @Captor
    private ArgumentCaptor<PlacesDataRepository.GetPlacesCallback> getPlacesCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<GetPlacesUseCase.ResponseValues> placesArgumentCaptor;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.getPlacesUseCase = new GetPlacesUseCase(this.placesDataRepository);
    }

    @After
    public void tearDown() {
        this.getPlacesUseCase = null;
    }

    @Test
    public void getPlacesTest() {
        final double lat = 12;
        final double lng = 12;
        final GetPlacesUseCase.RequestValues requestValues = new GetPlacesUseCase.RequestValues(lat, lng);
        final List<Place> mockPlaces = new ArrayList<>();
        final Place mockPlace = new Place();
        mockPlace.setTitle("title_1");
        final List<Double> mockPosition = new ArrayList<>(2);
        mockPosition.add(13D);
        mockPosition.add(13D);
        mockPlace.setPosition(mockPosition);
        mockPlaces.add(mockPlace);

        this.getPlacesUseCase.execute(requestValues, getPlacesUseCaseCallback);
        verify(placesDataRepository).getPlaces(anyDouble(), anyDouble(), getPlacesCallbackArgumentCaptor.capture());
        getPlacesCallbackArgumentCaptor.getValue().onPlacesLoaded(mockPlaces);
        verify(getPlacesUseCaseCallback).response(placesArgumentCaptor.capture());
        List<Place> returnPlaces = placesArgumentCaptor.getValue().getPlaces();

        Assert.assertEquals("Test load places size.", 1, returnPlaces.size());

        Place testPlace = returnPlaces.get(0);
        Assert.assertNotNull("Test place not null.", testPlace);
        Assert.assertEquals("Test place title.", mockPlace.getTitle(), testPlace.getTitle());
        Assert.assertEquals("Test place lat.", mockPlace.getLat(), testPlace.getLat(), 0);
        Assert.assertEquals("Test place lng.", mockPlace.getLng(), testPlace.getLng(), 0);
    }

    @Test
    public void getPlacesFailedTest() {

        final double lat = 12;
        final double lng = 12;
        final GetPlacesUseCase.RequestValues requestValues = new GetPlacesUseCase.RequestValues(lat, lng);

        this.getPlacesUseCase.execute(requestValues, getPlacesUseCaseCallback);
        verify(placesDataRepository).getPlaces(anyDouble(), anyDouble(), getPlacesCallbackArgumentCaptor.capture());
        getPlacesCallbackArgumentCaptor.getValue().onPlacesLoadFailed();
        verify(getPlacesUseCaseCallback).responseError();
    }

}
