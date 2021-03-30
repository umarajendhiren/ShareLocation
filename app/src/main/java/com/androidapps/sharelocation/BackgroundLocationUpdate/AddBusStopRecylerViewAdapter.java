package com.androidapps.sharelocation.BackgroundLocationUpdate;

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

import com.androidapps.sharelocation.AddPlacesActivity;
import com.androidapps.sharelocation.HomePageViewModel;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.StringToJsonSerialization;

import java.util.ArrayList;
import java.util.List;

public class AddBusStopRecylerViewAdapter extends RecyclerView.Adapter<AddBusStopRecylerViewAdapter.BusStopViewHolder> {
    List<StringToJsonSerialization> placesList;

    public HomePageViewModel homePageViewModel;
    Context context;


    public AddBusStopRecylerViewAdapter(List<StringToJsonSerialization> places, Context context, HomePageViewModel homePageViewModel) {
        placesList= new ArrayList<>();
        placesList = places;
        this.context = context;

        this.homePageViewModel = homePageViewModel;


    }

    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.add_bus_stop_list, parent, false);
        return new BusStopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {
        Log.d("onBindViewHolder: ", String.valueOf(placesList.get(position).getPlaceName()));
        Log.d("onBindViewHolder: ", String.valueOf(placesList.get(position).getGeoPoint().getLatitude()));
        Log.d("onBindViewHolder: ", String.valueOf(placesList.get(position).getGeoPoint().getLongitude()));
        holder.tvPlaceName.setText(placesList.get(position).getPlaceName());

        String.valueOf(placesList.get(position).getGeoPoint().getLatitude());

        holder.longitude.setText(String.valueOf(placesList.get(position).getGeoPoint().getLongitude()));
        holder.latitude.setText(String.valueOf(placesList.get(position).getGeoPoint().getLatitude()));
        holder.positionToUpdate.setText((String.valueOf(position)));

        if (placesList.get(position).isNotificationOn()) {
           // homePageViewModel.AddToGeoFenceList(placesList.get(position).getPlaceName(), placesList.get(position).getGeoPoint().getLatitude(), placesList.get(position).getGeoPoint().getLongitude());

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

    public class BusStopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvPlaceName, latitude, longitude, positionToUpdate;
        ImageView close;
        ImageView notificationOn, notificationOff;


        public BusStopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.bus_stop_name);
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


            if(homePageViewModel.isUserDriver()){
                notificationOn.setVisibility(View.GONE);
                notificationOff.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                tvPlaceName.setClickable(true);

            }

            else{


              //  notificationOff.setVisibility(View.VISIBLE);
                /*notificationOff.setVisibility(View.GONE);
                notificationOff.setVisibility(View.VISIBLE);*/
                close.setVisibility(View.GONE);
                tvPlaceName.setClickable(true);



            }


        }

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View view) {

            Log.d("onClick:",tvPlaceName.getText().toString());
            if (view.getId() == R.id.bus_stop_name) {


                Intent intent = new Intent(context, AddPlacesActivity.class);
                intent.putExtra("Title", tvPlaceName.getText().toString());
                intent.putExtra("Latitude", latitude.getText().toString());
                intent.putExtra("Longitude", longitude.getText().toString());
                intent.putExtra("PositionToUpdateBustop", positionToUpdate.getText().toString());
                intent.putExtra("IsBusStop", "true");

                Log.d("onClick:latitude ", latitude.getText().toString());
                Log.d("onClick:longitude ", longitude.getText().toString());
                view.getContext().startActivity(intent);
            }

            if (view.getId() == R.id.button_close) {

                homePageViewModel.removeBusStopFromServer(positionToUpdate.getText().toString());

               // homePageViewModel.unsubscribeForChannal(tvPlaceName.getText().toString());

            }
            if (view.getId() == R.id.button_notification_on) {


                homePageViewModel.setNotificationForBusStop(false, Integer.parseInt(positionToUpdate.getText().toString()),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getGeoPoint().getLatitude(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getGeoPoint().getLongitude(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getObjectId());

                notificationOff.setVisibility(View.VISIBLE);
                notificationOn.setVisibility(View.GONE);

                Toast.makeText(context, "Notification turned off for " + placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(), Toast.LENGTH_LONG).show();


            }

            if (view.getId() == R.id.button_notification_off) {



                homePageViewModel.setNotificationForBusStop(true, Integer.parseInt(positionToUpdate.getText().toString()),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getGeoPoint().getLatitude(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getGeoPoint().getLongitude(),placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getObjectId());
                notificationOn.setVisibility(View.VISIBLE);
                notificationOff.setVisibility(View.GONE);

                Toast.makeText(context, "Notification turned on for " + placesList.get(Integer.parseInt(positionToUpdate.getText().toString())).getPlaceName(), Toast.LENGTH_LONG).show();

            }
        }
    }


}
