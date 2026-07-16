package com.example.campusfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private ArrayList<CampusLocation> locationList;
    private OnItemClickListener mListener;

    // Interface menerima 'CampusLocation'
    public interface OnItemClickListener {
        void onItemClick(CampusLocation item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public LocationAdapter(ArrayList<CampusLocation> list) {
        this.locationList = list;
    }

    // ViewHolder
    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgPhoto;
        public TextView tvName;
        public TextView tvDesc;

        public LocationViewHolder(View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_item_photo_horizontal);
            tvName = itemView.findViewById(R.id.tv_item_name_horizontal);
            tvDesc = itemView.findViewById(R.id.tv_item_desc_horizontal);
        }
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_horizontal, parent, false);
        return new LocationViewHolder(v);
    }

    // Logika klik
    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        // Ambil data yang tampil (terfilter saat search)
        CampusLocation currentItem = locationList.get(position);

        holder.tvName.setText(currentItem.getName());
        holder.tvDesc.setText(currentItem.getDescription());
        holder.imgPhoto.setImageResource(currentItem.getImageResourceId());

        // Listener klik untuk mengirim 'currentItem'
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void setFilteredList(ArrayList<CampusLocation> filteredList) {
        this.locationList = filteredList;
        notifyDataSetChanged();
    }
}