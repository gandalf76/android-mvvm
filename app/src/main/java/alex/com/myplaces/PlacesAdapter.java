package alex.com.myplaces;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alex.com.myplaces.databinding.ItemPlaceBinding;
import alex.com.myplaces.domain.model.Place;

public class PlacesAdapter extends RecyclerView.Adapter {

    private List<Place> places;

    public PlacesAdapter() {
        this.places = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPlaceBinding binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Place place = this.places.get(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.setPlace(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void updatePlaces(List<Place> places) {
        this.places.clear();
        this.places.addAll(places);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ItemPlaceBinding binding;

        public ViewHolder(ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setPlace(Place place) {
            this.binding.setPlace(place);
            this.binding.executePendingBindings();
        }
    }
}
