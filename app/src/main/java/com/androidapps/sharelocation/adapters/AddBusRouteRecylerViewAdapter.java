

package com.androidapps.sharelocation.adapters;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.androidapps.sharelocation.OnClickCallListener;
import com.androidapps.sharelocation.model.StringToJsonSerialization;

import java.util.ArrayList;
import java.util.List;


public class AddBusRouteRecylerViewAdapter extends BaseAdapter {

    private final int layoutId;
    private HomePageViewModel homePageViewMode;
    private List<StringToJsonSerialization> busRouteList = new ArrayList<>();
    OnClickCallListener callListener;
    //OnPhoneCallClickInterface onPhoneCallClickInterface;
    public AddBusRouteRecylerViewAdapter(int layoutId, HomePageViewModel viewModel, OnClickCallListener callListener) {
        this.layoutId = layoutId;
        homePageViewMode = viewModel;
        //  this.onPhoneCallClickInterface=onPhoneCallClickInterface;
        this. callListener=callListener;

    }

    @Override
    protected Object getObjForPosition(int position) {
        return busRouteList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    @Override
    protected ViewModel getViewmodel() {
        return homePageViewMode;
    }

    @Override
    protected OnClickCallListener getOnCallListener() {
        Log.d( "getOnCallListener:","called");
        return callListener;
    }

 /*   @Override
    protected OnPhoneCallClickInterface getOnPhoneCallInterface() {
        return onPhoneCallClickInterface;
    }*/

    @Override
    public int getItemCount() {
        return busRouteList == null ? 0 : busRouteList.size();
    }

    public void setBusRouteList(List<StringToJsonSerialization> busRouteList) {
        this.busRouteList = busRouteList;
    }


   /* public interface OnPhoneCallClickInterface {

        void onCall(String userId);
    }*/


}







/*
package com.androidapps.sharelocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBusRouteRecylerViewAdapter extends RecyclerView.Adapter<AddBusRouteRecylerViewAdapter.BusStopViewHolder> {
    List<StringToJsonSerialization> placesList;

    public HomePageViewModel homePageViewModel;
    Context context;


    public AddBusRouteRecylerViewAdapter(List<StringToJsonSerialization> places, Context context, HomePageViewModel homePageViewModel) {
        placesList= new ArrayList<>();
        placesList = places;
        this.context = context;

        this.homePageViewModel = homePageViewModel;


    }

    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.add_bus_route_list, parent, false);
        return new BusStopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {


        holder.tvRouteName.setText(placesList.get(position).getRouteName());



        holder.origin.setText(String.valueOf(placesList.get(position).getOrigin()));
        holder.destination.setText(String.valueOf(placesList.get(position).getDestination()));
        holder.positionToUpdate.setText((String.valueOf(position)));

        Log.d("onBindViewHolder:", String.valueOf(placesList.get(position).getRouteName()));
        Log.d("onBindViewHolder:", String.valueOf(placesList.get(position).getOrigin()));
        Log.d("onBindViewHolder:", String.valueOf(placesList.get(position).getDestination()));

        if(placesList.get(position).getPolyPoints()!=null) {

            List<String> polyStringList = placesList.get(position).getPolyPoints();

            Log.d("onBindViewHolder:", String.valueOf(polyStringList.size()));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, polyStringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.listViewPolyString.setAdapter(arrayAdapter);

        }

        if(placesList.get(position).getWayPoints()!=null) {

            HashMap<String, LatLng> wayPoints = placesList.get(position).getWayPoints();

            Log.d("onBindViewHolder:", String.valueOf(wayPoints.size()));
            Log.d("onBindViewHolderkey:", String.valueOf(wayPoints.keySet()));

            String[] spinnerArray = new String[wayPoints.size()];
            HashMap<String, LatLng> spinnerMap = new HashMap<String, LatLng>();
            for (int i = 0; i < wayPoints.size(); i++)
            {
                spinnerMap.put(i,Province_ID.get(i));
                spinnerArray[i] = Province_NAME.get(i);
            }

            ArrayAdapter<HashMap<String, LatLng>> arrayAdapter = new ArrayAdapter<HashMap<String, LatLng>>(context, android.R.layout.simple_spinner_item, wayPoints);
            holder.listViewBusStops.setAdapter(arrayAdapter);

        }

    }

    @Override
    public int getItemCount() {

        if (placesList != null)

            return placesList.size();

        else return 0;
    }

    public class BusStopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRouteName, origin, destination, positionToUpdate;
        ImageView close;
        ListView listViewPolyString;
        Spinner listViewBusStops;



        public BusStopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.bus_route_name);
            origin = itemView.findViewById(R.id.tv_origin);
            destination = itemView.findViewById(R.id.tv_destination);
            positionToUpdate = itemView.findViewById(R.id.position);
            close = itemView.findViewById(R.id.button_close);
            listViewPolyString=itemView.findViewById(R.id.polyLine_string);
            listViewBusStops=itemView.findViewById(R.id.bus_stops);



            tvRouteName.setOnClickListener(this);
            close.setOnClickListener(this);






        }

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View view) {

            Log.d("onClick:",tvRouteName.getText().toString());
            if (view.getId() == R.id.bus_stop_name) {


             */
/*   Intent intent = new Intent(context, AddPlacesActivity.class);
                intent.putExtra("Title", tvPlaceName.getText().toString());
                intent.putExtra("Latitude", latitude.getText().toString());
                intent.putExtra("Longitude", longitude.getText().toString());
                intent.putExtra("PositionToUpdateBustop", positionToUpdate.getText().toString());
                intent.putExtra("IsBusStop", "true");

                Log.d("onClick:latitude ", latitude.getText().toString());
                Log.d("onClick:longitude ", longitude.getText().toString());
                view.getContext().startActivity(intent);*//*

            }

            if (view.getId() == R.id.button_close) {

                homePageViewModel.removeBusRouteFromServer(positionToUpdate.getText().toString());

               // homePageViewModel.unsubscribeForChannal(tvPlaceName.getText().toString());

            }
         */
/*   if (view.getId() == R.id.button_notification_on) {


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

            }*//*

        }
    }


}
*/
