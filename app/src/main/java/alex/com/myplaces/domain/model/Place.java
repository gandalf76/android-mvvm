package alex.com.myplaces.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Place {

    private String title;

    private List<Double> position;

    public Place() {
        this.position = new ArrayList<>(2);
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return position.get(0);
    }

    public double getLng() {
        return position.get(1);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (title != null ? !title.equals(place.title) : place.title != null) return false;
        return position != null ? position.equals(place.position) : place.position == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
