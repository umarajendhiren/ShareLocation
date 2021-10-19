package com.androidapps.sharelocation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.androidapps.sharelocation.view.AddPlacesActivity;

import java.util.ArrayList;
import java.util.List;

public class AddPlacesRecylerViewAdapter extends RecyclerView.Adapter<AddPlacesRecylerViewAdapter.GroupNameViewHolder> {
    List<StringToJsonSerialization> placesList = new ArrayList<>();

    public HomePageViewModel homePageViewModel;
    Context context;


    public AddPlacesRecylerViewAdapter(List<StringToJsonSerialization> places, Context context, HomePageViewModel homePageViewModel) {

        placesList = places;
        this.context = context;

        this.homePageViewModel = homePageViewModel;


    }

    @NonNull
    @Override
    public GroupNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.add_places_list, parent, false);
        return new GroupNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupNameViewHolder holder, int position) {

        Log.d("onBindViewHolder: ", String.valueOf(placesList.get(position).getGeoPoint().getLongitude()));
        holder.tvPlaceName.setText(placesList.get(position).getPlaceName());

        String.valueOf(placesList.get(position).getGeoPoint().getLatitude());

        holder.longitude.setText(String.valueOf(placesList.get(position).getGeoPoint().getLongitude()));
        holder.latitude.setText(String.valueOf(placesList.get(position).getGeoPoint().getLatitude()));
        holder.positionToUpdate.setText((String.valueOf(position)));


        if (placesList.get(position).isNotificationOn()) {
            homePageViewModel.AddToGeoFenceList(placesList.get(position).getPlaceName(), placesList.get(position).getGeoPoint().getLatitude(), placesList.get(position).getGeoPoint().getLongitude());

            holder.notificationOn.setVisibility(View.VISIBLE);
            holder.notificationOff.setVisibility(View.GONE);


        } else {
            // homePageViewModel.removeGeoFenceFromList(placesList.get(position).getPlaceName());

            holder.notificationOn.setVisibility(View.GONE);
            holder.notificationOff.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {

        if (placesList != null)

            return placesList.size();

        else return 0;
    }

    public class GroupNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvPlaceName, latitude, longitude, positionToUpdate;
        ImageView close;
        ImageView notificationOn, notificationOff;


        public GroupNameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.place_name);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            positionToUpdate = itemView.findViewById(R.id.position);
            close = itemView.findViewById(R.id.button_close);
            notificationOn = itemView.findViewById(R.id.button_notification_on);
            notificationOff = itemView.findViewById(R.id.button_notification_off);


            tvPlaceName.setOnClickListener(this);
            close.setOnClickListener(this);
            notificationOn.setOnClickListener(this);
            notificationOff.setOnClickListener(this);
        }

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View view) {


            if (view.getId() == R.id.place_name) {


                Intent intent = new Intent(context, AddPlacesActivity.class);
                intent.putExtra("Title", tvPlaceName.getText().toString());
                intent.putExtra("Latitude", latitude.getText().toString());
                intent.putExtra("Longitude", longitude.getText().toString());
                intent.putExtra("Position", positionToUpdate.getText().toString());

                Log.d("onClick:latitude ", latitude.getText().toString());
                Log.d("onClick:longitude ", longitude.getText().toString());
                view.getContext().startActivity(intent);
            }

            if (view.getId() == R.id.button_close) {

                homePageViewModel.removePlaceFromServer(positionToUpdate.getText().toString(), tvPlaceName.getText().toString());

                //  homePageViewModel.unsubscribeForChannal(tvPlaceName.getText().toString());

            }

            if (view.getId() == R.id.button_notification_on) {


                homePageViewModel.setNotification(false, Integer.parseInt(positionToUpdate.getText().toString()));
                notificationOff.setVisibility(View.VISIBLE);
                notificationOn.setVisibility(View.GONE);

                Toast.makeText(context, "Notification turned off for " + placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(), Toast.LENGTH_LONG).show();


            }

            if (view.getId() == R.id.button_notification_off) {


                homePageViewModel.setNotification(true, Integer.parseInt(positionToUpdate.getText().toString()));
                notificationOn.setVisibility(View.VISIBLE);
                notificationOff.setVisibility(View.GONE);

                Toast.makeText(context, "Notification turned on for " + placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(), Toast.LENGTH_LONG).show();

            }
        }
    }


}
