package alex.com.myplaces.data.network.volley.requests;

import java.util.List;

import alex.com.myplaces.domain.model.Place;

public class ResultRequestObject {

    private ItemsRequestObject results;

    public ItemsRequestObject getResults() {
        return results;
    }

    public void setResults(ItemsRequestObject results) {
        this.results = results;
    }

    public class ItemsRequestObject {

        private List<Place> items;

        public List<Place> getItems() {
            return items;
        }

        public void setItems(List<Place> items) {
            this.items = items;
        }

    }
}
