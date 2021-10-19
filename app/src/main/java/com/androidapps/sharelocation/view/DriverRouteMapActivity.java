package com.androidapps.sharelocation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.adapters.AddBusStopRecylerViewAdapter;
import com.androidapps.sharelocation.DirectionsJSONParser;
import com.androidapps.sharelocation.DistanceApi.DistanceApiClient;
import com.androidapps.sharelocation.DistanceApi.DistanceResponse;
import com.androidapps.sharelocation.model.Element;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.utilities.RestUtil;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.androidapps.sharelocation.utilities.Utilities;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DriverRouteMapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnFocusChangeListener, GoogleMap.OnMarkerClickListener, View.OnClickListener, TextWatcher {

    private static final int AUTOCOMPLETE_REQUEST_CODE_origin = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE_destination = 2;
    public static LatLng originLatLng;
    public static LatLng destinationLatLng;
    static boolean isUpdateAddress = false;
    List<String> PolyStringList=new ArrayList<>();
    private GoogleMap mMap;
    static LatLngBounds.Builder builder;
    List<LatLng> wayPointList = new ArrayList<>();
    static LatLngBounds bounds;
    List<String> distanceAndDurationList;
    static List<LatLng> markerPoints = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    HashMap<String, LatLng> waypoints = new HashMap<>();
    EditText origin, destination, fake, routeName;
    PolylineOptions lineOptions = null;
    HashMap<String, LatLng> wayPoints;
    Button btnSaveRoute;
    boolean isNotificationOn;
    private int width, height;
    private int padding;
    TextView addStopPoint;
    List<LatLng> points = null;
    Marker marker;
    CameraUpdate cu;
    private HomePageViewModel viewModel;
    Calendar cl;
    Date currentTime;
    static List<StringToJsonSerialization> busStopListChanged;
    private String address;
    WebView webView;
    RecyclerView busStopRecyclerView;
    private AddBusStopRecylerViewAdapter adapter;
    String originPlaceName,destinationPlacename;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_route_map);

        viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);


        if (!Places.isInitialized()) {
            Places.initialize(this.getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }
        origin = findViewById(R.id.editText_origin);
        destination = findViewById(R.id.editText_destination);
        routeName = findViewById(R.id.edt_route_name);

        origin.addTextChangedListener(this);
        destination.addTextChangedListener(this);
        routeName.addTextChangedListener(this);


        fake = findViewById(R.id.edt_fake);

        addStopPoint = findViewById(R.id.textView_add_stop);
        addStopPoint.setOnClickListener(this);
        btnSaveRoute = findViewById(R.id.save_route);
        btnSaveRoute.setOnClickListener(this);


        origin.setOnFocusChangeListener(this);
        destination.setOnFocusChangeListener(this);
        fake.setOnFocusChangeListener(this);

        busStopRecyclerView = findViewById(R.id.recycle_bus_stop);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);








        busStopListChanged = new ArrayList<>();

        viewModel.setBusStopLive(busStopListChanged);

        viewModel.getBusStopLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

                if(stringToJsonSerializations!=null && stringToJsonSerializations.size()>0)
                Log.d("onChanged:bus", String.valueOf(stringToJsonSerializations.size()));
                if(busStopListChanged.size()>0)
                busStopListChanged.clear();


                if (stringToJsonSerializations != null  ) {

                    busStopListChanged.addAll(stringToJsonSerializations);
                    Log.d("onChanged:busstop", String.valueOf(busStopListChanged.size()));
                    //bus stop origin and destination
                    viewModel.setBusStopList(busStopListChanged);
                  //  viewModel.setExistingBusStopList(busStopListChanged);


                }

                StringToJsonSerialization origin = new StringToJsonSerialization();
                origin.setPlaceName("Origin");
                origin.setGeoPoint(0, 0);

                busStopListChanged.add(0, origin);


                // int lastIndex=busStopList.size()-1;
                StringToJsonSerialization destination = new StringToJsonSerialization();
                destination.setPlaceName("Destination");
                destination.setGeoPoint(0, 0);

                busStopListChanged.add(destination);

                Log.d("onChangedaft:", String.valueOf(busStopListChanged.size()));


                adapter = new AddBusStopRecylerViewAdapter(busStopListChanged, DriverRouteMapActivity.this, viewModel);
                busStopRecyclerView.setAdapter(adapter);




                if (markerPoints.size() > 1) {

                    getPolygone();


                }


               /* else if(stringToJsonSerializations==null || stringToJsonSerializations.size()==0){
                    Log.d("onChangednull:", "called");

                    StringToJsonSerialization origin = new StringToJsonSerialization();
                    origin.setPlaceName("Origin");
                    origin.setGeoPoint(0, 0);

                    busStopList.add(0, origin);


                    // int lastIndex=busStopList.size()-1;
                    StringToJsonSerialization destination = new StringToJsonSerialization();
                    destination.setPlaceName("Destination");
                    destination.setGeoPoint(0, 0);

                    busStopList.add(destination);

                    adapter = new AddBusStopRecylerViewAdapter(busStopList, DriverRouteMapActivity.this, viewModel);
                    busStopRecyclerView.setAdapter(adapter);
                    if (markerPoints.size() > 1) {

                        getPolygone();
                    }

                }*/
            }
        });







        viewModel.getRoutePolyLine().observe(this, new Observer<List<List<HashMap<String, String>>>>() {
            @Override
            public void onChanged(List<List<HashMap<String, String>>> polyLists) {
              if(polyLists!=null && polyLists.size()>0){

                Log.d("onPostExecute:", String.valueOf(polyLists.size()));

                for (int i = 0; i < polyLists.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = polyLists.get(i);
                    Log.d("doInBackground:", "called1");

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                        Log.d("poly:", String.valueOf(position));

                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                    Log.d("doInBackground:", "called3");


                }

// Drawing polyline in the Google Map for the i-th route
                Log.d("doInBackground:", "called4");
                if (points.size() != 0) {
                    mMap.addPolyline(lineOptions);

                    // getSnappedRout(0);
                }

            }}
        });







        currentTime = Calendar.getInstance().getTime();


        Log.d("Date", String.valueOf(currentTime));



        // Date d1 = new Date();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        Location location = new Location("sharedpreference");

        SharedPreferences sharedPrefLocation = this.getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);

        String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

        Log.d("onChangedlatitude: ", changedLatitude);


        location.setLatitude(Double.parseDouble(changedLatitude));


        String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


        Log.d("onChangedlongitude: ", changedLongitude);
        location.setLongitude(Double.parseDouble(changedLongitude));

        /*webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);


        webView.loadUrl("file:///assets/MoveMarker.js");
*/


        viewModel.getUpdateRouteDetail().observe(this, new Observer<StringToJsonSerialization>() {
            @Override
            public void onChanged(StringToJsonSerialization updateRouteDetails) {

                if (updateRouteDetails.getRouteName() != null) {
                    List<StringToJsonSerialization> busStopList = new ArrayList<>();
                    String savedOrigin = null;
                    String savedSestination = null;

                    if (updateRouteDetails != null) {
                        routeName.setText(updateRouteDetails.getRouteName());

                        savedOrigin = Utilities.getAddressFromLocation(DriverRouteMapActivity.this, updateRouteDetails.getOrigin().latitude, updateRouteDetails.getOrigin().longitude);
                        savedSestination = Utilities.getAddressFromLocation(DriverRouteMapActivity.this, updateRouteDetails.getDestination().latitude, updateRouteDetails.getDestination().longitude);

                        origin.getText().clear();
                        origin.setText(String.valueOf(savedOrigin));

                        destination.setText(String.valueOf(savedSestination));

                        originPlaceName=updateRouteDetails.getOriginPlaceName();
                        destinationPlacename=updateRouteDetails.getDestinationPlaceName();

                        isNotificationOn=updateRouteDetails.isNotificationOn();

                     /*   if(busStopListChanged !=null && busStopListChanged.size()>=2){

                            Log.d("getBusStopList", String.valueOf(viewModel.getBusStopList().size()));
                            busStopList.clear();
                            busStopList.addAll(viewModel.getBusStopList());
                        }

                        else {*/
                        wayPoints = updateRouteDetails.getWayPoints();

                        if (wayPoints != null) {
                            for (String key : wayPoints.keySet()) {

                                StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setPlaceName(key);
                                serialization.setGeoPoint(wayPoints.get(key).latitude, wayPoints.get(key).longitude);

                                busStopList.add(serialization);
                                Log.d( "onChanged:poin",key);

                             /*   new java.util.Timer().schedule(
                                        new java.util.TimerTask() {
                                            @Override
                                            public void run() {
                                                // your code here

                                                viewModel.addBusStop(key,wayPoints.get(key).latitude,wayPoints.get(key).longitude);
                                            }
                                        },
                                        2000
                                );*/



                            }
                        }

                    /* if(viewModel.getBusStopLive().getValue().size()>2) {
                           Log.d( "bus: ", String.valueOf(viewModel.getBusStopLive().getValue().size()));
                        }

                       else{*/
                           viewModel.setExistingBusStopList(busStopList);





                        // viewModel.getAllSavedBusStops();
/*
                    if (markerPoints.get(0) != null) {
                        markerPoints.remove(0);
                    }
                    markerPoints.add(0, updateRouteDetails.getOrigin());


                    Log.d("markerSixw:", String.valueOf(markerPoints.size()));
                    if (markerPoints.size()>1) {
                        Log.d("onActivityResult:","remove");
                        // markerPoints.remove(1);
                        markerPoints.set(1,updateRouteDetails.getDestination());
                    }
                    else {
                        markerPoints.add(1, destinationLatLng);
                    }*/

                        if (markerPoints.size() > 0) {

                            markerPoints.clear();
                       /* markerPoints.add(0, updateRouteDetails.getOrigin());
                        markerPoints.add(1, updateRouteDetails.getDestination());;
                        Log.d("onChanged:ori", String.valueOf(updateRouteDetails.getOrigin()));*/
                        }

                        markerPoints.add(0, updateRouteDetails.getOrigin());
                        markerPoints.add(1, updateRouteDetails.getDestination());

                        Log.d("onChanged:ori", String.valueOf(updateRouteDetails.getOrigin()));
                        Log.d("onChanged:ori", String.valueOf(updateRouteDetails.getDestination()));


                        originLatLng = updateRouteDetails.getOrigin();
                        destinationLatLng = updateRouteDetails.getDestination();


                        isUpdateAddress = true;
                        Log.d("onChanged:way", String.valueOf(busStopList.size()));

                        viewModel.setBusStopLive(busStopList);
                      //  viewModel.setExistingBusStopList(busStopList);

                        // getPolygone();

                    }
                }
            }
        });

        Log.d("isUpdateAddres: ", String.valueOf(isUpdateAddress));


        if (!isUpdateAddress)
            UpdateMap(location);
     /*   // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
/*

        LatLng wilmington = new LatLng(39.8209, -75.5493);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wilmington, 16));*/

      /*  mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.d("onMapClick:lat", String.valueOf(latLng));

                if (markerPoints.size() > 1) {
                    Log.d("onMapClick: ", "clear");
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {

                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);
                    Log.d("onMapClick:ori", String.valueOf(origin));
                    Log.d("onMapClick:des", String.valueOf(dest));

                    // Getting URL to the Google Directions API
                    //chase
                    double topLat = 39.8236;
                    double topLng = -75.5468;
                    LatLng topLatLng = new LatLng(topLat, topLng);
                    waypoints.add(topLatLng);
                    String url = getDirectionsUrl(origin, dest, waypoints);

                    Log.d("onMapClick:", url);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });*/

    }


    public void UpdateMap(Location location) {

        Log.i("UpdateMap: ", "Called");

        LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 15));
        address = Utilities.getAddressFromLocation(this, location.getLatitude(), location.getLongitude());

        origin.setText(address);
        originLatLng = new LatLng(userLatlng.latitude, userLatlng.longitude);
       /* originLatLng = new LatLng(userLatlng.latitude, userLatlng.longitude);
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(userLatlng);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);*/

        markerPoints.add(0, userLatlng);

        Log.d("UpdateMap: ", String.valueOf(markerPoints.size()));
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.hasFocus()) {
            hideKeyboard(view);
            Log.d("onFocusChange:", "called");
            if (view.getId() == R.id.editText_origin) {

                if (!origin.getText().toString().isEmpty()) {
                    Log.d("onFocusChange:", "clearori");
                    origin.getText().clear();
                }
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this.getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_origin);
            } else if (view.getId() == R.id.editText_destination) {

                if (!destination.getText().toString().isEmpty()) {
                    Log.d("onFocusChange:", "cleardes");
                    destination.getText().clear();
                }

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this.getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_destination);
            }
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("onBackPressed:", "clesr");
        fake.requestFocus();
        isUpdateAddress=false;
        viewModel.addBusStop(null,0.0,0.0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (busStopListChanged != null && busStopListChanged.size() > 0) {
           // isUpdateAddress = false;
          //  busStopListChanged.clear();
            Log.d("onStop:", "clesr");
         //   viewModel.setBusStopLive(busStopListChanged);

            StringToJsonSerialization stringToJsonSerialization = new StringToJsonSerialization();
            viewModel.setUpdateRouteDetail(stringToJsonSerialization);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResultcode:", String.valueOf(resultCode));
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_origin) {

            //origin.requestFocus(View.FOCUS_LEFT);
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("onActivityResult", "ori");

                Log.i("Place: ", place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());

                originPlaceName=place.getName();

                originLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                String selectedPlace = String.valueOf(place.getAddress());

                origin.setText(selectedPlace);

                if (markerPoints.size() > 0 && markerPoints.get(0) != null) {
                    markerPoints.remove(0);
                    // markerPoints.set(0, originLatLng);
                }
                markerPoints.add(0, originLatLng);
                origin.clearFocus();
                fake.requestFocus();


                // Creating MarkerOptions
              /*  MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(originLatLng);

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                mMap.clear();
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);*/


                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                // do query with address

                Log.d("onActivityResult", String.valueOf(markerPoints.size()));
                if (markerPoints.size() > 1) {

                    getPolygone();
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d("can:c", "called");
                origin.clearFocus();
                fake.requestFocus();
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_destination) {

            // destination.requestFocus(View.FOCUS_LEFT);
            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", "des");
                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.i("Place: ", place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
                destinationPlacename=place.getName();

                destinationLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(place.getLatLng(), 15));
                String selectedPlace = String.valueOf(place.getAddress());

                destination.setText(selectedPlace);


                // Creating MarkerOptions
              /*  MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(destinationLatLng);


                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);*/


                Log.d("markerSixw:", String.valueOf(markerPoints.size()));
                if (markerPoints.size() > 1) {
                    Log.d("onActivityResult:", "remove");
                    markerPoints.remove(1);
                    // markerPoints.set(1, destinationLatLng);
                }
                markerPoints.add(1, destinationLatLng);

                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                // do query with address
                destination.clearFocus();
                fake.requestFocus();


                //chase
              /*  double topLat = 39.8236;
                double topLng = -75.5468;
                LatLng topLatLng = new LatLng(topLat, topLng);
                waypoints.add(topLatLng);*/

                if (markerPoints.size() > 1) {
                    getPolygone();
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                destination.clearFocus();
                fake.requestFocus();
            }
        }


    }


    public void getPolygone() {

        if (origin.getText().toString().trim().isEmpty() || destination.getText().toString().trim().isEmpty()) {

            Log.d("markerPoints ", String.valueOf(markerPoints.size()));
            if (mMap != null && markerList.size() > 0)
                mMap.clear();
            markerPoints.clear();

            fake.requestFocus();
            return;
        }
        for (int i = 0; i < markerPoints.size(); i++) {

            Log.d("i", String.valueOf(markerPoints.get(i)));
        }
        if (waypoints.size() > 0) {
            waypoints.clear();
            wayPointList.clear();
        }

        if (mMap != null && markerList.size() > 0) {

            mMap.clear();
            markerList.clear();
        }

        MarkerOptions optionsOrigin = new MarkerOptions();

        // Setting the position of the marker
        optionsOrigin.position(originLatLng);

        optionsOrigin.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        marker = mMap.addMarker(optionsOrigin);

        Log.d("getPolygone:ori", String.valueOf(originLatLng));
        marker.setTag("Origin");
        markerList.add(marker);


        MarkerOptions optionsDestination = new MarkerOptions();

        // Setting the position of the marker

        optionsDestination.position(destinationLatLng);
        optionsDestination.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


        // Add new marker to the Google Map Android API V2
        //   mMap.addMarker(optionsDestination);


        marker = mMap.addMarker(optionsDestination);


        marker.setTag("Destination");
        markerList.add(marker);

        // Log.d("busStopLatLng", String.valueOf(busStopList.size()));

        if (busStopListChanged != null && busStopListChanged.size() > 0) {
            for (int i = 0; i < busStopListChanged.size(); i++) {

                LatLng busStopLatLng = new LatLng(busStopListChanged.get(i).getGeoPoint().getLatitude(), busStopListChanged.get(i).getGeoPoint().getLongitude());
                String busStopName = busStopListChanged.get(i).getPlaceName();
                Log.d("busStopLatLng:", String.valueOf(busStopLatLng.latitude));
                Log.d("busStopLatLng:", String.valueOf(busStopLatLng.longitude));
                Log.d("originLatLng:", String.valueOf(originLatLng.latitude));
                Log.d("originLatLng:", String.valueOf(originLatLng.longitude));
                Log.d("destinationLatLng:", String.valueOf(destinationLatLng.latitude));
                Log.d("destinationLatLng:", String.valueOf(destinationLatLng.longitude));
                if (busStopLatLng.latitude == originLatLng.latitude && busStopLatLng.longitude == originLatLng.longitude) {

                    Log.d("sameWithOri", busStopLatLng.latitude + " " + originLatLng.latitude + " " + busStopLatLng.longitude + " " + originLatLng.longitude);
                } else if (busStopLatLng.latitude == destinationLatLng.latitude && busStopLatLng.longitude == destinationLatLng.longitude) {
                    Log.d("sameWithdes", busStopLatLng.latitude + " " + destinationLatLng.latitude + " " + busStopLatLng.longitude + " " + destinationLatLng.longitude);


                } else if (busStopLatLng.latitude != 0 && busStopLatLng.longitude != 0) {

                    waypoints.put(busStopName, busStopLatLng);

                    Log.d("waypoint", String.valueOf(busStopLatLng));

                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position of the marker
                    markerOptions.position(busStopLatLng);

                 //   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                   // Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.ic__bus_stop);

                    Drawable busStopDrawable = getResources().getDrawable(R.drawable.ic_stopsign);

                    BitmapDescriptor icon = getMarkerIconFromDrawable(busStopDrawable);


                    markerOptions.icon(icon);


                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(markerOptions);
                }


            }

        }
        builder = new LatLngBounds.Builder();

        LatLng orgin = new LatLng(originLatLng.latitude, originLatLng.longitude);
        LatLng destination = new LatLng(destinationLatLng.latitude, destinationLatLng.longitude);
        Log.d("onActivityResult", orgin + " " + destination);

      /*  String snappedUrl = getSnappedLatLng(orgin, destination);
        Log.d("snappedUrl:", snappedUrl);*/


        builder.include(orgin);
        builder.include(destination);
        if (waypoints != null && waypoints.size() > 0) {
            //for (int i = 0; i < waypoints.size(); i++) {

            Set<String> key = waypoints.keySet();
            for (String k : key) {
                Log.d("key", k);
                builder.include(new LatLng(waypoints.get(k).latitude, waypoints.get(k).longitude));
                wayPointList.add(new LatLng(waypoints.get(k).latitude, waypoints.get(k).longitude));
                // }

            }
        }
        bounds = builder.build();

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        padding = (int) (width * 0.20);

        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


        mMap.animateCamera(cu);


      /*  LatLng snappedOrigin = new LatLng(39.794534, -75.488243);
        LatLng snappeddes = new LatLng(39.797576, -75.486151);
        String snappedUrl = getSnappedLatLng(snappedOrigin, snappeddes);
        Log.d("snappedUrl:", snappedUrl);


        Log.d("onActivityResult", snappedOrigin + " " + snappeddes);
        builder.include(snappedOrigin);
        builder.include(snappeddes);

        bounds = builder.build();

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        padding = (int) (width * 0.20);

        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


        mMap.animateCamera(cu);*/




        String url = getDirectionsUrl(orgin, destination, wayPointList);


       // DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
       // downloadTask.execute(url);



/*

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        //post again

                                        animateMarker(position, markerList.get(0),false);

                                        handler.postDelayed(this, 3000);

                                    }
                                }
                    , 3000);
*/


    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("onMarkerClick:", String.valueOf(marker.getPosition()));

        String markerAddress = Utilities.getAddressFromLocation(this, marker.getPosition().latitude, marker.getPosition().longitude);


        getDistanceInfo(marker, markerAddress, originLatLng.latitude, originLatLng.longitude, marker.getPosition().latitude, marker.getPosition().longitude);

        return true;
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.textView_add_stop) {
            Log.d("onClick:stop", "called");
            Intent startAddPlacesActivity = new Intent(this, AddPlacesActivity.class);

            startAddPlacesActivity.putExtra("Title", "Add Bus Stop");

            startActivity(startAddPlacesActivity);


        }
        if (view.getId() == R.id.save_route) {

            StringToJsonSerialization routeDetail = new StringToJsonSerialization();

            Log.d("onClick:", "called");
            if (routeName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter route name!", Toast.LENGTH_LONG).show();

            } else if (origin.getText().toString().trim().isEmpty() || originLatLng == null) {
                Toast.makeText(this, "Please enter starting address!", Toast.LENGTH_LONG).show();

            } else if (destination.getText().toString().trim().isEmpty() || destinationLatLng == null) {
                Toast.makeText(this, "Please enter destination!", Toast.LENGTH_LONG).show();

            }


            //else if (polyPointsString != null && polyPointsString.size() > 0) {
            else  {
                PolyStringList=viewModel.getPolyStringList();
                if (PolyStringList != null && PolyStringList.size() > 0)

                Log.d( "onClick:route",routeName.getText().toString());
                routeDetail.setRouteName(routeName.getText().toString());
                routeDetail.setOriginPlaceName(originPlaceName);
                routeDetail.setDestinationPlaceName(destinationPlacename);
                routeDetail.setOrigin(originLatLng);
                routeDetail.setDestination(destinationLatLng);
                routeDetail.setPolyPoints(PolyStringList);
                routeDetail.setNotificationOn(isNotificationOn);
                routeDetail.setObjectId(ParseUser.getCurrentUser().getObjectId());



                if (waypoints.size() > 0) {
                    Log.d("onClick:way ", String.valueOf(waypoints.size()));
                    routeDetail.setWayPoints(waypoints);
                } else routeDetail.setWayPoints(null);


                if (!isUpdateAddress) {

                    viewModel.saveRoute(routeDetail);

                    finish();
                    // viewModel.shareRouteMap(polyPointsString);
                } else if (isUpdateAddress) {


                    viewModel.updateRouteDetailInServer(routeDetail);
                    isUpdateAddress=false;
                    finish();
                }
            }


        }


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    /*    Log.d( "onTextChanged:", String.valueOf(charSequence));
        if(markerPoints.size()>1)
        getPolygone();
        */

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            Log.d("Background Task", "called1");

            parserTask.execute(result);

        }


    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {


                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);

               /* SnappedPointParser pointParser = new SnappedPointParser();
                routes = pointParser.parseSnappedPoints(jObject);*/

                Log.d("doInBackground:", "called");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("routSize", String.valueOf(routes));
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            MarkerOptions markerOptions = new MarkerOptions();

            Log.d("onPostExecute:", String.valueOf(result.size()));

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);
                Log.d("doInBackground:", "called1");

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    Log.d("poly:", String.valueOf(position));

                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
                Log.d("doInBackground:", "called3");


            }

// Drawing polyline in the Google Map for the i-th route
            Log.d("doInBackground:", "called4");
            if (points.size() != 0) {
                mMap.addPolyline(lineOptions);

                // getSnappedRout(0);
            }
        }


    }

    public void getSnappedRout(int pointId) {
        Log.d("pointId", String.valueOf(pointId));
        Log.d("pointSize", String.valueOf(points.size()));
        if (pointId < points.size() - 1) {
            markerList.get(0).setPosition(points.get(pointId));
            Log.d("current:", String.valueOf(markerList.get(0).getPosition()));
            LatLng toPosition = new LatLng(points.get(pointId + 1).latitude, points.get(pointId + 1).longitude);
            // LatLng toPosition = new LatLng(points.get(points.size()-1).latitude, points.get(points.size()-1).longitude);
            Log.d("toPosi:", String.valueOf(toPosition));
            animateMarker(pointId, toPosition, markerList.get(0), false);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, List<LatLng> waypointsLatLng) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

      /*  https://maps.googleapis.com/maps/api/directions/json?
        origin=Boston,MA&destination=Concord,MA
                &waypoints=via:Charlestown,MA|via:Lexington,MA &departure_time=now
                &key=YOUR_API_KEY*/

        /*https://maps.googleapis.com/maps/api/directions/json?
origin=sydney,au&destination=perth,au
&waypoints=via:-37.81223%2C144.96254%7Cvia:-34.92788%2C138.60008
&key=YOUR_API_KEY*/
        Log.d("waypointsLatLng", String.valueOf(waypointsLatLng.size()));
       // String waypoints = "waypoints=via:";
       String waypoints ="via:";

        for (int i = 0; i < waypointsLatLng.size(); i++) {

            if (i + 1 == waypointsLatLng.size()) {
                Log.d("getDirec ", "equal");
               // waypoints = waypoints.concat(waypointsLatLng.get(i).latitude + "%2c" + waypointsLatLng.get(i).longitude);
                waypoints = waypoints.concat(waypointsLatLng.get(i).latitude + "," + waypointsLatLng.get(i).longitude);


            } else if (i + 1 < waypointsLatLng.size()) {
                Log.d("getDirec ", " not equal");
               /* waypoints = waypoints.concat(waypointsLatLng.get(i).latitude + "%2c" + waypointsLatLng.get(i).longitude);

                waypoints = waypoints.concat("%7Cvia:");*/

                waypoints = waypoints.concat(waypointsLatLng.get(i).latitude + "," + waypointsLatLng.get(i).longitude);

                waypoints = waypoints.concat("|via:");
            }
            Log.d("waypoint ", waypoints);
        }


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + waypoints + "&key=AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";

        Log.d("getDirectionsUrl:", url);

        String key="AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";



        /*https://maps.googleapis.com/maps/api/directions/json?
origin=sydney,au&destination=perth,au
&waypoints=via:-37.81223%2C144.96254%7Cvia:-34.92788%2C138.60008
&key=YOUR_API_KEY*/

        String endpoint= output + "?" + parameters + "&" + waypoints + "&key=AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";
//To preserve insertion order
        LinkedHashMap<String,String> mapQuery = new LinkedHashMap<>();

        String originString=origin.latitude + "," + origin.longitude;
        String desString=dest.latitude + "," + dest.longitude;

        mapQuery.put("origin",originString);
        mapQuery.put("destination",desString);

        mapQuery.put("sensor","false");
        mapQuery.put("mode","driving");


        mapQuery.put("waypoints",waypoints);
        mapQuery.put("key","AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo");


        Log.d( "mapQuer ", String.valueOf(mapQuery));
     viewModel.getRouteDetailJson(mapQuery);
                return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static void hideKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public List<String> getDistanceInfo(Marker marker, String markerAddress, double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude) {

        distanceAndDurationList = new ArrayList<>();
        // http://maps.googleapis.com/maps/api/distancematrix/json?destinations=Atlanta,GA|New+York,NY&origins=Orlando,FL&units=imperial
        if (originLatitude == 0 && originLongitude == 0 && destinationLatitude == 0 && destinationLongitude == 0) {
            // textview.setVisibility(View.GONE);
            return null;
        }
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", originLatitude + "," + originLongitude);
        mapQuery.put("destinations", destinationLatitude + "," + destinationLongitude);

        mapQuery.put("key", "AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo");
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                Log.d("onResponseeeeeee: ", "called");


                if (response.body() != null &&
                        response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements() != null &&
                        response.body().getRows().get(0).getElements().size() > 0 &&
                        response.body().getRows().get(0).getElements().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getDistance() != null &&
                        response.body().getRows().get(0).getElements().get(0).getDuration() != null) {

                    Element element = response.body().getRows().get(0).getElements().get(0);

                    // showTravelDistance(element.getDistance().getText() + "\n" + element.getDuration().getText());

                    Log.d("onResponse:distance", element.getDistance().getText());
                   /* getDistanceLiveInstance().setValue(element.getDistance().getText());
                    getTravelDistance(element.getDistance().getText());*/
                   /* if( element.getDistance().getText().contains("ft")) {

                        Log.d("onResponse:dis ",element.getDistance().getText());
                        distanceAndDurationList.add(0,element.getDistance().getText());

                       // textview.setVisibility(View.GONE);
                    }
                    */

                    /*
                    if( element.getDistance().getText().contains("0.1 mi")) {
                        Log.d("onResponse:","0.1");
viewModel.isHeDriver(userId);

                    }*/



                      /*  textview.setVisibility(View.VISIBLE);
                        textview.setText(element.getDistance().getText()+ "  away");*/
                    Log.d("onResponse:dis ", element.getDistance().getText());
                    Log.d("onResponse:dura", element.getDuration().getText());
                    Log.d("onResponse:time", element.getDuration().getText());
                    distanceAndDurationList.add(0, element.getDistance().getText());
                    distanceAndDurationList.add(1, element.getDuration().getText());
                    marker.setTitle(markerAddress);

                    if (distanceAndDurationList.get(1).contains("day")) {

                        //call day
                        Log.d("onResponse:day", "called");
                        getDay(marker);
                    } else if (distanceAndDurationList.get(1).contains("hour")) {

                        //call hour
                        Log.d("onResponse:hour", "called");
                        getHour(marker);
                    } else if (distanceAndDurationList.get(1).contains("mins")) {
                        Log.d("onResponse:mint", "called");
                        getMinite(marker);
                    }

                } else if (response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getStatus().equals("ZERO_RESULTS")) {

                    Log.d("onResponse:zero", "zero");
                    // textview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

                Log.d("onFailure:retro", t.getMessage());

            }
        });
        //Log.d("getDistanceInfo: ",distance);
        //return distance;
        return distanceAndDurationList;
    }


    public void getSec(Marker marker) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (distanceAndDurationList.get(1).contains("mins")) {
              /*   String minits=distanceAndDurationList.get(1).replace("mins","").trim();

                       Log.d( "onRes:miniy",minits);*/


            String[] time = distanceAndDurationList.get(1).split(" ");

            String mini = time[0];
            String minis = time[1];
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/


            cl.add(Calendar.MINUTE, Integer.parseInt(mini));

            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }


            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


            marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();
        }

    }

    public void getMinite(Marker marker) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (distanceAndDurationList.get(1).contains("mins")) {
              /*   String minits=distanceAndDurationList.get(1).replace("mins","").trim();

                       Log.d( "onRes:miniy",minits);*/


            String[] time = distanceAndDurationList.get(1).split(" ");

            String mini = time[0];
            String minis = time[1];
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/


            cl.add(Calendar.MINUTE, Integer.parseInt(mini));

            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }


            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


            marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();
        }

    }

    public void getHour(Marker marker) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (distanceAndDurationList.get(1).contains("hour")) {

            String[] time = distanceAndDurationList.get(1).split(" ");
            String hour = time[0];
            String hours = time[1];

            if (distanceAndDurationList.get(1).contains("mins")) {
                String mini = time[2];
                String minis = time[3];
                cl.add(Calendar.MINUTE, Integer.parseInt(mini));
            }
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/
            cl.add(Calendar.HOUR, Integer.parseInt(hour));


            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }

            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


            marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();


        }
    }

    public void getDay(Marker marker) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (distanceAndDurationList.get(1).contains("day")) {
    /*    String day=distanceAndDurationList.get(1).replace("day","").trim();
        Log.d( "onRes:day",day);*/

            String[] time = distanceAndDurationList.get(1).split(" ");
            String day = time[0];
            String days = time[1];


            if (distanceAndDurationList.get(1).contains("hour")) {
                String hour = time[2];
                String hours = time[3];
                cl.add(Calendar.HOUR, Integer.parseInt(hour));
            }

            if (distanceAndDurationList.get(1).contains("mins")) {
                String mini = time[4];
                String minis = time[5];
                cl.add(Calendar.MINUTE, Integer.parseInt(mini));
            }


            cl.add(Calendar.DATE, Integer.parseInt(day));


            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }

            Log.d("onRespafter ", cl.get(Calendar.DATE) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR) + cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);

           /* if(cl.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)){

                //same day
            }*/


            marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.DATE) + "/" + cl.get(Calendar.YEAR) + "  at" + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();

        }
    }


    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    private void animateMarkerNew(final LatLng destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            //  final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());


            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        marker.setPosition(newPosition);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(15.5f)
                                .build()));

                        //  marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                        marker.setRotation(getBearing(startPosition, destination));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }





   /* public void animateMarker(final LatLng toPosition,final boolean hideMarke) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(originLatLng);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                markerList.get(0).setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarke) {
                        m.setVisible(false);
                    } else {
                        m.setVisible(true);
                    }
                }
            }
        });
    }*/


    public String getSnappedLatLng(LatLng markerOrigin, LatLng markerDesti) {

        String snappedUrl = "https://roads.googleapis.com/v1/snapToRoads?path=" + markerOrigin.latitude + "," + markerOrigin.longitude + "|" + markerDesti.latitude + "," + markerDesti.longitude + "&interpolate=true&key=AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";
        return snappedUrl;
        //https://roads.googleapis.com/v1/snapToRoads?path=-35.27801,149.12958|-35.28032,149.12907|-35.28099,149.12929|-35.28144,149.12984|-35.28194,149.13003|-35.28282,149.12956|-35.28302,149.12881|-35.28473,149.12836&interpolate=true&key=YOUR_API_KEY
    }

    public void animateMarker(int startPositionId, final LatLng toPosition, Marker marker, final boolean hideMarke) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;


                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                Log.d("run:t", String.valueOf(t));

                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;


                Log.d("run:lat", String.valueOf(lat));
                Log.d("run:lng", String.valueOf(lng));
                marker.setPosition(new LatLng(lat, lng));


                if (t < 1.0) {
                    // Post again 16ms later.
                    Log.d("run:", "called");
                    handler.postDelayed(this, 16);

                } else {
                    if (hideMarke) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                        getSnappedRout(startPositionId + 1);




                     /*   final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {


                                                    //post again

                                                    getSnappedRout(startPositionId+1);

                                                    handler.postDelayed(this, 5000);

                                                }
                                            }
                                , 5000);


                    }*/
                    }
                }
            }
        });
    }


}
