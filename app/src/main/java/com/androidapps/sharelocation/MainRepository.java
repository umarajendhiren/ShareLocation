package com.androidapps.sharelocation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.SubscriptionHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

//getRiderLocationLive().setValue(riderLocation);
public class MainRepository {
    public   static StringToJsonSerialization  roteDetail;

    public StringToJsonSerialization  getRoteDetail(){
        if(roteDetail==null)
            return roteDetail=new StringToJsonSerialization();
        else return roteDetail;
    }

    public   static List<StringToJsonSerialization>  stopList;

    public List<StringToJsonSerialization> getStopList(){
        if(stopList==null)
            return stopList=new ArrayList<>();
        else return stopList;
    }
    public static MutableLiveData<Boolean> isValidInviteCode;
    public static MutableLiveData<ParseGeoPoint> lastKnownLocation;
    public static MutableLiveData<Boolean> isDriverAvailable;
    public static boolean isDriverMarkerAvailable = false;
    int count = 0;
    public static MutableLiveData<String> distanceLive;
    public static String distance;
    public static MutableLiveData<Boolean> isNetworkCOnnected;
    private List<LatLng> points = new ArrayList<>();
    private List<LatLng> reFormedPoints = new ArrayList<>();
    HashSet<LatLng> pointsSet = new HashSet<>();

    HashMap<String, String> point;

    final Handler handlerSetPosition = new Handler();


    public MutableLiveData<Boolean> isNetworkCOnnectedInstance() {

        if (isNetworkCOnnected == null) {

            isNetworkCOnnected = new MutableLiveData<>();

            SharedPreferences sharedPrefConnectivity = mContext.getSharedPreferences("Network", Context.MODE_PRIVATE);
            String isConnected = sharedPrefConnectivity.getString("com.androidapps.sharelocation.network", "null");

            Log.d("isConnected: ", isConnected);
            if (isConnected.equals("connected")) {

                isNetworkCOnnected.setValue(true);
            } else if (isConnected.equals("notconnected")) {
                isNetworkCOnnected.setValue(false);
            }

            return isNetworkCOnnected;
        } else return isNetworkCOnnected;
    }

    public MutableLiveData<String> getDistanceLiveInstance() {

        if (distanceLive == null) {

            distanceLive = new MutableLiveData<>();

            return distanceLive;
        } else return distanceLive;
    }

    public static int objectPosition;
    public static MutableLiveData<StringToJsonSerialization> updateRoute;

    public MutableLiveData<StringToJsonSerialization> getUpdateRoute() {


        if (updateRoute == null) {

            updateRoute = new MutableLiveData<>();

            return updateRoute;
        } else

            return updateRoute;
    }

    public MutableLiveData<Boolean> getIsDriverAvailable() {

        if (isDriverAvailable == null) {

            isDriverAvailable = new MutableLiveData<>();

            return isDriverAvailable;
        } else return isDriverAvailable;
    }


    List<String> objectsToget = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
    public static MutableLiveData<String> driverCode;
    public static boolean isItriderMapFragment;
    public static boolean isItDriverMapFragment;
    public static boolean isItRouteMap;

    public static MutableLiveData<ParseGeoPoint> riderLocationLive;
    boolean isMarkerAvailable = false;
    boolean IsUpdatedDriverMarkerAvailable = false;


    public MutableLiveData<ParseGeoPoint> getRiderLocationLive() {

        if (riderLocationLive == null) {

            riderLocationLive = new MutableLiveData<>();
            return riderLocationLive;
        } else return riderLocationLive;
    }


    public MutableLiveData<String> getDriverCodeInstance() {

        if (driverCode == null) {

            driverCode = new MutableLiveData<>();
            return driverCode;
        } else return driverCode;
    }

    MutableLiveData<String> removedGeoFenceId;
    private static GoogleMap mRiderMap;
    private static GoogleMap mDriverMap;

    public MutableLiveData<String> getRemovedGeoFenceInstance() {

        if (removedGeoFenceId == null) {
            removedGeoFenceId = new MutableLiveData<>();
            return removedGeoFenceId;
        } else return removedGeoFenceId;
    }

    ParseGeoPoint riderLocation;
    ParseGeoPoint driverLocation;


    MutableLiveData<List<StringToJsonSerialization>> geoFenceListLive;

    public MutableLiveData<List<StringToJsonSerialization>> getGeoFenceList() {

        if (geoFenceListLive == null) {
            geoFenceListLive = new MutableLiveData<>();
            return geoFenceListLive;
        } else return geoFenceListLive;
    }

    public MutableLiveData<ParseGeoPoint> getLastKnownLocation() {

        if (lastKnownLocation == null) {
            lastKnownLocation = new MutableLiveData<>();
            return lastKnownLocation;
        } else return lastKnownLocation;
    }

    public MutableLiveData<Boolean> getIsAlreadyInCircleInstance() {

        if (isAlreadyInCircle == null) {
            isAlreadyInCircle = new MutableLiveData<>();

            return isAlreadyInCircle;
        } else return isAlreadyInCircle;
    }


    public MutableLiveData<Boolean> getIsValidInviteCodeInstance() {

        if (isValidInviteCode == null) {
            isValidInviteCode = new MutableLiveData<>();
            // isValidInviteCode.setValue(false);
            return isValidInviteCode;
        } else return isValidInviteCode;
    }

    public static MutableLiveData<String> currentUser = new MutableLiveData<>();

    List<StringToJsonSerialization> placeNameAndGeoPointList = new ArrayList<>();

    public static MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();

    MutableLiveData<String> updateUserId = new MutableLiveData<>();

    public static MutableLiveData<String> currentUserNameLive = new MutableLiveData<>();
    public static MutableLiveData<String> userMailLive = new MutableLiveData<>();
    public static MutableLiveData<String> userPhoneLive = new MutableLiveData<>();
    public static MutableLiveData<String> countryCodeLive = new MutableLiveData<>();
    Canvas canvas;
    Bitmap resizedBitmap;

    View customMarkerLayoutWithImage, markerLayoutWithoutImage;
    CircleImageView imageViewDp;
    ImageView imageViewDpBackground;
    public static ArrayList<LatLng> markersLatLong = new ArrayList<>();
    public static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    public ArrayList<Bitmap> riderMarkerList = new ArrayList<>();
    public MutableLiveData<List<UserDetailsPojo>> userDetailsListLiveData;
    public static MutableLiveData<List<UserDetailsPojo>> DriverDetailsListLiveData;


    public MutableLiveData<List<UserDetailsPojo>> getUserDetailsListLiveData() {

        if (userDetailsListLiveData == null) {
            userDetailsListLiveData = new MutableLiveData<>();

            return userDetailsListLiveData;
        }
        return userDetailsListLiveData;

    }


    public static MutableLiveData<List<UserDetailsPojo>> getDriverDetailsListLiveData() {

        if (DriverDetailsListLiveData == null) {
            DriverDetailsListLiveData = new MutableLiveData<>();

            return DriverDetailsListLiveData;
        }
        return DriverDetailsListLiveData;

    }


    static ArrayList<Marker> markers;
    static ArrayList<Marker> riderMarkers;
    static MutableLiveData<ArrayList<Marker>> ridermarkersLive;
    ArrayList<String> updatedRiderinDriverObjectId = new ArrayList<>();
    ArrayList<String> riderList = new ArrayList<>();

    static MutableLiveData<String> selectedDriverId;

    public MutableLiveData<String> getSelectedDriverId() {

        if (selectedDriverId == null) {
            selectedDriverId = new MutableLiveData<>();

            return selectedDriverId;
        }
        return selectedDriverId;

    }


    public MutableLiveData<ArrayList<Marker>> getRiderMarkerInstance() {

        if (ridermarkersLive == null) {
            ridermarkersLive = new MutableLiveData<>();

            return ridermarkersLive;
        }
        return ridermarkersLive;

    }

    static MutableLiveData<ArrayList<Marker>> markersLive;

    public MutableLiveData<ArrayList<Marker>> getMarkersLiveInstance() {

        if (markersLive == null) {
            markersLive = new MutableLiveData<>();

            return markersLive;
        }
        return markersLive;

    }

    public static ArrayList<ParseFile> parseFilesImage = new ArrayList<>();
    public static ArrayList<String> imageUri = new ArrayList<>();
    TextView tvFirstLetter;

    ParseFile imageFile, imageFileUpdated;
    Bitmap imagebitmap, imagebitmapUpdated;
    public static MutableLiveData<Bitmap> userDpBitmapLive = new MutableLiveData<>();
    Bitmap bitmap, updatedbitmap, updatedBitmapWithText;
    String userName, userObjectId;
    String firstLetterToUpperCase;
    LatLng locationLatlong;
    String updatesLocationUserName;
    public static MutableLiveData<List<UserDetailsPojo>> existingMemberDetailLive = new MutableLiveData<>();
    public List<UserDetailsPojo> existingMemberDetailList = new ArrayList<>();
    public static MutableLiveData<List<StringToJsonSerialization>> placeNameAndGeoPointLive;

    public MutableLiveData<List<StringToJsonSerialization>> getplaceNameAndGeoPointLive() {

        if (placeNameAndGeoPointLive == null) {
            placeNameAndGeoPointLive = new MutableLiveData<>();

            return placeNameAndGeoPointLive;
        }
        return placeNameAndGeoPointLive;
    }


    public static MutableLiveData<List<StringToJsonSerialization>> BusStopLive;

    public  MutableLiveData<List<StringToJsonSerialization>> getBusStopLive() {

        if (BusStopLive == null) {
            BusStopLive = new MutableLiveData<>();

            return BusStopLive;
        }
        return BusStopLive;
    }

    public static MutableLiveData<List<StringToJsonSerialization>> BusRouteLive;

    public MutableLiveData<List<StringToJsonSerialization>> getBusRouteLive() {

        if (BusRouteLive == null) {
            BusRouteLive = new MutableLiveData<>();

            return BusRouteLive;
        }
        return BusRouteLive;
    }

    static List<StringToJsonSerialization> busRouteList;
    public static MutableLiveData<Boolean> isHomeAvailable = new MutableLiveData<>();
    public static MutableLiveData<Boolean> isSchoolAvailable = new MutableLiveData<>();
    public static MutableLiveData<Boolean> isGroceryAvailable = new MutableLiveData<>();
    public static MutableLiveData<Boolean> isWorkAvailable = new MutableLiveData<>();

    List<StringToJsonSerialization> geofenceList;
    static List<StringToJsonSerialization> busStopList = new ArrayList<>();
    public static List<StringToJsonSerialization> existingBusStopList = new ArrayList<>();


    List<StringToJsonSerialization> busStopgeofenceList;
    List<Geofence> busStopgeofenceListBuilder;
    static GoogleMap map;
    static Marker markerNew;
    public static List<String> driverList = new ArrayList<>();

    WindowManager windowManager;


    static LatLngBounds.Builder builder;
    static LatLng routeOrigin, routeDestination;
    static LatLngBounds bounds;
    int resizedMarkerwidth;
    int height;
    int width;
    int padding;

    int resizedMarkerHeight;
    // offset from edges of the map in pixels
    static CameraUpdate cu;
    Context mContext;
    LatLng updatedPosition;
    MarkerOptions markerOption;
    static ArrayList<Marker> driverMarkerRoute;


    static LatLng driverOrigin;
    static LatLng driverDesstination;
    String userAddress;
    String currentLocationAddress;
    static List<UserDetailsPojo> userDetailsPojoList = new ArrayList<>();
    static List<UserDetailsPojo> driverDetailsPojoList = new ArrayList<>();
    String adminId;

    public static MutableLiveData<UserDetailsPojo> userDetailsPojoForAdmin = new MutableLiveData<>();
    Bitmap dpWithTextDpForRecyclerView;
    static LatLng mRiderLocation;
    static LatLng mDriverLocation;

    static String locationPermission;
    //  static MutableLiveData<GoogleMap> mRiderMap;

    public static MutableLiveData<Boolean> isAlreadyInCircle = new MutableLiveData<>();
    public static MutableLiveData<String> selectedGroupNameLiveData;
    public static MutableLiveData<String> selectedInviteCodeLiveData;
    public static MutableLiveData<String> circleNameLive = new MutableLiveData<>();


    public static MutableLiveData<String> inviteCodeLive = new MutableLiveData<>();


    public MutableLiveData<String> getCircleNameLive() {

        if (circleNameLive == null) {
            circleNameLive = new MutableLiveData<>();

            return circleNameLive;
        }
        return circleNameLive;
    }


    public static MutableLiveData<List<UserDetailsPojo>> circleNameAndDpLive = new MutableLiveData<List<UserDetailsPojo>>();


    public static MutableLiveData<Boolean> isNotificationOn;

    public MutableLiveData<Boolean> getIsNotificationOnInstance() {

        if (isNotificationOn == null) {
            isNotificationOn = new MutableLiveData<>();
            isNotificationOn.setValue(false);
            return isNotificationOn;
        }
        return isNotificationOn;
    }


    public static MutableLiveData<Boolean> subscribeForCircleNameLiveData;

    public MutableLiveData<Boolean> getSubscribeForCircleNameLiveData() {

        if (subscribeForCircleNameLiveData == null) {
            subscribeForCircleNameLiveData = new MutableLiveData<>();
            subscribeForCircleNameLiveData.setValue(false);
            return subscribeForCircleNameLiveData;
        }
        return subscribeForCircleNameLiveData;
    }


    public static MutableLiveData<Boolean> subscribeForUserLiveData;

    public MutableLiveData<Boolean> getSubscribeForUserLiveData() {

        if (subscribeForUserLiveData == null) {
            subscribeForUserLiveData = new MutableLiveData<>();
            subscribeForUserLiveData.setValue(false);
            return subscribeForUserLiveData;
        }
        return subscribeForUserLiveData;
    }

    static List<UserDetailsPojo> circleNameDpList;
    public static MutableLiveData<Boolean> notification = new MutableLiveData<Boolean>();

   /* public MutableLiveData<GoogleMap> getLiveMap() {

        if (mRiderMap == null) {
            mRiderMap = new MutableLiveData<>();
            return mRiderMap;
        }
        return mRiderMap;
    }
*/

    public MutableLiveData<String> getSelectedInviteCodeLiveData() {


        if (selectedInviteCodeLiveData == null) {
            return selectedInviteCodeLiveData = new MutableLiveData<>();
        } else return selectedInviteCodeLiveData;
    }

    public MutableLiveData<String> getSelectedGroupNameLiveData() {


        if (selectedGroupNameLiveData == null) {
            return selectedGroupNameLiveData = new MutableLiveData<>();


        } else return selectedGroupNameLiveData;
    }

    ParseQuery<ParseObject> queryCircleName;
    ParseQuery<ParseUser> queryParseUser;

    List<String> polyList;
    static MutableLiveData<StringToJsonSerialization> polyStringLive;


    public MutableLiveData<StringToJsonSerialization> getPolyStringLive() {


        if (polyStringLive == null) {
            return polyStringLive = new MutableLiveData<>();


        } else return polyStringLive;
    }

    static GoogleMap routeMap;
    static String driverIdForRouteMap;
    static int pointId = 0;

    ParseLiveQueryClient parseLiveQueryClient;
    List<String> subscribedChannals;
    GeofencingClient geofencingClient;
    static boolean isItValid;

    static List<String> driverListArray = new ArrayList<>();

    public static Location currentUserLocation;
    Runnable runnable;

    @Inject
    public MainRepository(@ApplicationContext Context context,

                          ParseQuery<ParseObject> queryCircleName,
                          ParseQuery<ParseUser> queryParseUser,
                          ParseLiveQueryClient parseLiveQueryClient,
                          @Nullable List channelsList

    ) {
        Log.d("MainRepository: ", "called");
        Log.d("livequery ", String.valueOf(parseLiveQueryClient));
        mContext = context;
        this.queryCircleName = queryCircleName;
        this.queryParseUser = queryParseUser;
        this.parseLiveQueryClient = parseLiveQueryClient;
        // List channelsList
        subscribedChannals = channelsList;
        /*getSubscribeForUserLiveData().setValue(false);
        getSubscribeForCircleNameLiveData().setValue(false);*/
       /* subscribeForUserLiveQuery();
        subscribeForCircleNameLiveQuery();*/


        markers = new ArrayList<>();


        riderMarkers = new ArrayList<>();

        // sendContanctUsMail();
    }


    public void subscribeForUserLiveQuery() {

        Log.d("subscribeForUser: ", String.valueOf(getSubscribeForUserLiveData().getValue()));
       /* if (getSubscribeForUserLiveData().getValue()) {

            Log.d("subscribe", "subsribed");
        } else {*/
        // getSubscribeForUserLiveData().setValue(true);
        // ParseLiveQueryClient parseLiveQueryClient = null;
        // try {

        //   parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/"));

        //  ParseQuery<ParseUser> queryCircleCode = ParseUser.getQuery();
        getSubscribeForUserLiveData().setValue(true);
        SubscriptionHandling<ParseUser> subscriptionHandling = parseLiveQueryClient.subscribe(queryParseUser);


        markersLatLong.clear();
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseUser>() {
            @Override
            public void onEvent(ParseQuery<ParseUser> queryCircleMembers, final ParseUser object) {
                Log.d("onEvent: ", "Update");

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {


                    public void run() {

                        Log.d("isItRoute", String.valueOf(isItRouteMap));
                        if (isItRouteMap && object.getObjectId().equals(driverIdForRouteMap) && routeMap != null && driverMarkerRoute.get(0) != null) {
                            Log.d("run:updatesDriver", object.getObjectId());
                            LatLng driverLocation = getLocationLatLong(object, "userCurrentLocation");


                            userAddress = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);


                            driverDesstination = new LatLng(driverLocation.latitude, driverLocation.longitude);
                            //   driverOrigin=new LatLng(driverMarkerRoute.get(0).getPosition().latitude,driverMarkerRoute.get(0).getPosition().longitude);

                            if (driverOrigin.latitude == driverDesstination.latitude && driverOrigin.longitude == driverDesstination.longitude) {
                                Log.d("done:same", "driverOrigin");

                            } else {


                                Log.d("done:origin", String.valueOf(driverOrigin));
                                Log.d("done:des", String.valueOf(driverDesstination));
                                Log.d("done:marker", String.valueOf(driverMarkerRoute.get(0).getPosition()));


                                String snappedUrl = getSnappedLatLng(driverOrigin, driverDesstination);


                                Log.d("snappedUrl:", snappedUrl);

                                handlerSetPosition.removeCallbacks(runnable);

                                DownloadTask downloadTask = new DownloadTask();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(snappedUrl);

                                builder = new LatLngBounds.Builder();


                                Log.d("onActivityResult", driverOrigin + " " + driverDesstination);

                                builder.include(routeOrigin);
                                builder.include(routeDestination);
                                builder.include(driverOrigin);
                                builder.include(driverDesstination);

                                bounds = builder.build();

                                width = mContext.getResources().getDisplayMetrics().widthPixels;
                                height = mContext.getResources().getDisplayMetrics().heightPixels;
                                padding = (int) (width * 0.10);

                                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                routeMap.animateCamera(cu);


                                driverOrigin = driverDesstination;


                            }
                            return;

                        }


                        Log.d("riderInstance", getRiderMarkerInstance().toString());
                        Log.d("userInDriverMap", String.valueOf(isItDriverMapFragment));
                        Log.d("userInRiderMap", String.valueOf(isItriderMapFragment));

                        SharedPreferences isInBackground = mContext.getSharedPreferences("isInBackground", MODE_PRIVATE);

                        String InBackground = isInBackground.getString("Background", "null");
                        Log.d("InBackground", InBackground);

                        /*if (InBackground.equals("true")) {

                            boolean isCurrentUserDriver = isUserDriver();
                            if (isCurrentUserDriver) {

                                boolean isDriver = object.getBoolean("IsDriver");

                                if (isDriver) {
                                    Log.d("updatesUser", "driver");

                                    updateDriverMarker();

                                } else if (!isDriver) {

                                    Log.d("updatesUser", "rider");


                                    iScurrentUserDriver(object.getObjectId());

                                }
                            } else if (getRiderMarkerInstance().getValue() != null && !isCurrentUserDriver && getRiderMarkerInstance().getValue().size() > 0) {

                                Log.d("riderMarker", String.valueOf(getRiderMarkerInstance().getValue().size()));
                                updateUserId.setValue(object.getObjectId());
                                Log.d("isItriderMapFragment", isItriderMapFragment + " " + getRiderMarkerInstance().getValue().size());


                                boolean isDriver = object.getBoolean("IsDriver");


                                if (isDriver) {
                                    Log.d("updatesUser", "driver");
                                    isHeInDriverList(object);
                                    //  mDriverMap.clear();
                                    //  AddDriverMarker(mDriverMap);
                                    // updateDriverMarker();

                                } *//*else if (!isDriver) {

                                Log.d("updatesUser", "rider");

                                //  isHeInRiderList(object);

                                iScurrentUserDriver(object.getObjectId());

                            }*//* else if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

                                    if (mRiderMap != null) {
                                        Log.d("updatesUser", "current");
                                        Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.size()));

                                     *//*   riderMarkers.clear();
                                        driverDetailsPojoList.clear();
                                        mRiderMap.clear();*//*
                                        mRiderMap.clear();
                                        new java.util.Timer().schedule(
                                                new java.util.TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        // your code here
                                                        Log.d("run: ", "waiting for server, notify update");

                                                        getRiderMarker(mRiderMap);
                                                    }
                                                },
                                                2000
                                        );


                                    }

                                }

                            }


                        }*/
                        if (InBackground.equals("false")) {
                            if (isItDriverMapFragment) {


                                boolean isDriver = object.getBoolean("IsDriver");

                                if (isDriver) {
                                    Log.d("updatesUser", "driver");

                                    updateDriverMarker();

                                } else if (!isDriver) {

                                    Log.d("updatesUser", "rider");


                                    iScurrentUserDriver(object.getObjectId());

                                }

                            } else if (getRiderMarkerInstance().getValue() != null && isItriderMapFragment && getRiderMarkerInstance().getValue().size() > 0) {

                                Log.d("riderMarker", String.valueOf(getRiderMarkerInstance().getValue().size()));
                                updateUserId.setValue(object.getObjectId());
                                Log.d("isItriderMapFragment", isItriderMapFragment + " " + getRiderMarkerInstance().getValue().size());


                                boolean isDriver = object.getBoolean("IsDriver");


                                if (isDriver) {
                                    Log.d("updatesUser", "driver");
                                    isHeInDriverList(object);
                                    //  mDriverMap.clear();
                                    //  AddDriverMarker(mDriverMap);
                                    // updateDriverMarker();

                                } /*else if (!isDriver) {

                                Log.d("updatesUser", "rider");

                                //  isHeInRiderList(object);

                                iScurrentUserDriver(object.getObjectId());

                            }*/ else if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

                                    if (mRiderMap != null) {
                                        Log.d("updatesUser", "current");
                                        Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.size()));

                                     /*   riderMarkers.clear();
                                        driverDetailsPojoList.clear();
                                        mRiderMap.clear();*/
                                        mRiderMap.clear();
                                        new java.util.Timer().schedule(
                                                new java.util.TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        // your code here
                                                        Log.d("run: ", "waiting for server, notify update");

                                                        getRiderMarker(mRiderMap);
                                                    }
                                                },
                                                2000
                                        );


                                    }

                                }

                            } else if (!isItriderMapFragment && !isItDriverMapFragment && mContext != null && getMarkersLiveInstance().getValue() != null && bitmapArrayList.size() > 0) {


                                //getting updated image file
                                //if user previously did not have image,just now added his dp,then this image file replaces already existing letter marker
                                imageFileUpdated = object.getParseFile("profilepicture");

                                if (imageFileUpdated != null) {


                                    imageFileUpdated.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null && data != null) {
                                                updateUserId.setValue(object.getObjectId());

                                                updatedPosition = getLocationLatLong(object, "userCurrentLocation");
                                                Log.d("updatedPosition", String.valueOf(updatedPosition));
                                                if (updatedPosition != null) {
                                                    imagebitmapUpdated = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                    //get marker using tag  and set new position or new dp .marker tag is object id.
                                                    for (int i = 0; i < markers.size(); i++) {
                                                        if (markers.get(i).getTag().equals(object.getObjectId())) {

                                                            //get custom marker using parse file and marker layout
                                                            getCustomMarker(R.layout.custom_marker, imagebitmapUpdated);


                                                            //get resized marker after update dp
                                                            resizeMarker(updatedbitmap, 230, 230);


                                                            String updatedAddress = Utilities.getAddressFromLocation(mContext, updatedPosition.latitude, updatedPosition.longitude);
                                                            markers.get(i).setSnippet(updatedAddress);

                                                            //if position changed,set new position
                                                            markers.get(i).setPosition(updatedPosition);

                                                            //if dp changed set new dp in custom marker from bitmap
                                                            markers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                                            //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                                            bitmapArrayList.set(i, resizedBitmap);


                                                            //change camera position after update location of maker
                                                            ChangeCamaraPosition(mContext, markers);


                                                        }
                                                    }

                                                }


                                            }

                                            //need to update bottom sheet when data updated
                                            //get custom bitmap image view from layout
                                            if (userDetailsPojoList.size() > 0) {
                                                for (int k = 0; k < userDetailsPojoList.size(); k++) {

                                                    if (userDetailsPojoList.get(k).getUserObjectId().equals(object.getObjectId())) {

                                                        //get updated user name  and location and dp.
                                                        String updatedName = object.getUsername();
                                                        String updatesCurrentLocation = Utilities.getAddressFromLocation(mContext, updatedPosition.latitude, updatedPosition.longitude);

                                                        //set update value in list
                                                        userDetailsPojoList.get(k).setUserName(updatedName);

                                                      /*  if (updatesCurrentLocation.isEmpty()) {
                                                            userDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                        } else {
                                                            userDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocation);
                                                        }*/
                                                        userDetailsPojoList.get(k).setUserDp(imagebitmapUpdated);

                                                        userDetailsPojoList.get(k).setLatitude(updatedPosition.latitude);
                                                        userDetailsPojoList.get(k).setLongitude(updatedPosition.longitude);
                                                        userDetailsPojoList.get(k).setDestinationLatitude(currentUserLocation.getLatitude());
                                                        userDetailsPojoList.get(k).setDestinationLongitude(currentUserLocation.getLongitude());

                                                        //set new set of value to the live data.so observer observe this data change and will update recycler view in bottom sheet.
                                                        //  userDetailsListLiveData.setValue(userDetailsPojoList);
                                                        //  getUserDetailsListLiveData().setValue(userDetailsPojoList);


                                                        //
                                                      /*  userDetailsPojo.setLatitude(locationLatlong.latitude);
                                                        userDetailsPojo.setLongitude(locationLatlong.longitude);

                                                        userDetailsPojo.setDestinationLatitude(currentUserLocation.getLatitude());
                                                        userDetailsPojo.setDestinationLongitude(currentUserLocation.getLongitude());
*/
                                                        locationPermission = object.getString("LocationPermission");

                                                        Log.d("locationPermission", locationPermission);

                                                        if (locationPermission.equals("ON")) {
                                                            if (updatesCurrentLocation.isEmpty()) {
                                                               /* userDetailsPojoList.get(k).setLatitude(0);
                                                                userDetailsPojoList.get(k).setLongitude(0);
                                                                userDetailsPojoList.get(k).setDestinationLatitude(0);
                                                                userDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                                userDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                            } else {
                                                                userDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocation);
                                                            }
                                                        } else if (locationPermission.equals("OFF")) {
                                                            userDetailsPojoList.get(k).setLatitude(0);
                                                            userDetailsPojoList.get(k).setLongitude(0);
                                                            userDetailsPojoList.get(k).setDestinationLatitude(0);
                                                            userDetailsPojoList.get(k).setDestinationLongitude(0);
                                                            userDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");
                                                        }


                                                        if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {


                                                            currentUserLocation.setLatitude(updatedPosition.latitude);
                                                            currentUserLocation.setLongitude(updatedPosition.longitude);

                                                            Log.d("currentUserLocation", String.valueOf(currentUserLocation));

                                                            for (int j = 0; j < userDetailsPojoList.size(); j++) {

                                                                userDetailsPojoList.get(j).setDestinationLatitude(currentUserLocation.getLatitude());
                                                                userDetailsPojoList.get(j).setDestinationLongitude(currentUserLocation.getLongitude());
                                                            }

                                                        }
                                                        getUserDetailsListLiveData().setValue(userDetailsPojoList);

                                                    }
                                                }
                                            }
                                        }

                                    });
                                } else {
                                    updateUserId.setValue(object.getObjectId());
                                    Log.d("updatedId ", object.getObjectId());
                                    //if parse image file is null it will set first letter of user as dp in marker.
                                    updatedPosition = getLocationLatLong(object, "userCurrentLocation");
                                    Log.d("updatedPosition", String.valueOf(updatedPosition));
                                    if (updatedPosition != null) {

                                        String userName = object.getString("username");
                                        Log.d("username", userName);
                                        if (markers.size() > 0) {
                                            for (int i = 0; i < markers.size(); i++) {
                                                Log.d("run:marker", String.valueOf(markers.size()));

                                                if (markers.get(i).getTag().equals(object.getObjectId())) {
                                                    Log.d("run:markerTag", String.valueOf(markers.get(i).getTag()));
                                                    getCustomMarkerWithoutDp(userName);

                                                    String updatedAddress = Utilities.getAddressFromLocation(mContext, updatedPosition.latitude, updatedPosition.longitude);
                                                    markers.get(i).setSnippet(updatedAddress);
                                                    resizeMarker(updatedBitmapWithText, 230, 230);

                                                    markers.get(i).setPosition(updatedPosition);
//6

                                                    markers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                                    bitmapArrayList.set(i, resizedBitmap);

                                                    ChangeCamaraPosition(mContext, markers);

                                                }
                                            }
                                        }
                                    }


                                    //update bottom sheet recycler view.
                                    dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                                    // if location changed ,it will change address text view  in user list recycler view
                                    if (userDetailsPojoList.size() > 0) {
                                        for (int k = 0; k < userDetailsPojoList.size(); k++) {

                                            if (userDetailsPojoList.get(k).getUserObjectId().equals(object.getObjectId())) {

                                                String updatedName = object.getUsername();

                                                //get readable address from current Location latitude and longitude.
                                                String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, updatedPosition.latitude, updatedPosition.longitude);

                                                userDetailsPojoList.get(k).setUserName(updatedName);

                                             /*   if (updatesCurrentLocationAddress.isEmpty()) {
                                                    userDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                } else {
                                                    userDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                }*/
                                                userDetailsPojoList.get(k).setUserDp(dpWithTextDpForRecyclerView);

                                                userDetailsPojoList.get(k).setLatitude(updatedPosition.latitude);
                                                userDetailsPojoList.get(k).setLongitude(updatedPosition.longitude);
                                                userDetailsPojoList.get(k).setDestinationLatitude(currentUserLocation.getLatitude());
                                                userDetailsPojoList.get(k).setDestinationLongitude(currentUserLocation.getLongitude());
                                                //set new set of value
                                                // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                //  getUserDetailsListLiveData().setValue(userDetailsPojoList);


                                                locationPermission = object.getString("LocationPermission");

                                                Log.d("locationPermission", locationPermission);

                                                if (locationPermission.equals("ON")) {
                                                    if (updatesCurrentLocationAddress.isEmpty()) {
                                                       /* userDetailsPojoList.get(k).setLatitude(0);
                                                        userDetailsPojoList.get(k).setLongitude(0);
                                                        userDetailsPojoList.get(k).setDestinationLatitude(0);
                                                        userDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                        userDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                    } else {
                                                        userDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                    }
                                                } else if (locationPermission.equals("OFF")) {
                                                    userDetailsPojoList.get(k).setLatitude(0);
                                                    userDetailsPojoList.get(k).setLongitude(0);
                                                    userDetailsPojoList.get(k).setDestinationLatitude(0);
                                                    userDetailsPojoList.get(k).setDestinationLongitude(0);
                                                    userDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");
                                                }


                                                if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {


                                                    currentUserLocation.setLatitude(updatedPosition.latitude);
                                                    currentUserLocation.setLongitude(updatedPosition.longitude);

                                                    Log.d("currentUserLocation", String.valueOf(currentUserLocation));

                                                    for (int j = 0; j < userDetailsPojoList.size(); j++) {

                                                        userDetailsPojoList.get(j).setDestinationLatitude(currentUserLocation.getLatitude());
                                                        userDetailsPojoList.get(j).setDestinationLongitude(currentUserLocation.getLongitude());
                                                    }

                                                }
                                                getUserDetailsListLiveData().setValue(userDetailsPojoList);
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }
                });
            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ParseUser>() {
            @Override
            public void onEvent(ParseQuery<ParseUser> queryCircleMembers, ParseUser object) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        Log.d("onEvent: ", "Created");

                    }
                });

            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, new SubscriptionHandling.HandleEventCallback<ParseUser>() {
            @Override
            public void onEvent(ParseQuery<ParseUser> queryCircleMembers, final ParseUser object) {
                Log.d("onEvent: ", "deleted");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {


                        //if the user delete his account from Parse server ,we need to delete that user from all circle in CircleName Object.
                        ParseQuery<ParseObject> deleteUserFromAllCircle = new ParseQuery<ParseObject>("CircleName");
                        List<String> deletedUserId = new ArrayList<>();
                        deletedUserId.add(object.getObjectId());
                        deleteUserFromAllCircle.whereContainedIn("circleMember", deletedUserId);
                        deleteUserFromAllCircle.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> circleNameObjects, ParseException e) {
                                if (e == null && circleNameObjects.size() > 0) {

                                    Log.d("HeIsInCircle ", String.valueOf(circleNameObjects.size()));

                                    for (int i = 0; i < circleNameObjects.size(); i++) {

                                        JSONArray circleMemberArray = circleNameObjects.get(i).getJSONArray("circleMember");

                                        for (int j = 0; j < circleMemberArray.length(); j++) {

                                            try {
                                                if (circleMemberArray.get(j).equals(object.getObjectId())) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                        circleMemberArray.remove(j);
                                                        circleNameObjects.get(i).put("circleMember", circleMemberArray);
                                                        circleNameObjects.get(i).saveInBackground();
                                                        Log.d("userdeletedfromcircle", circleNameObjects.get(i).getObjectId());
                                                    }

                                                }
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                        }

                                    }


                                }
                            }
                        });
                    }
                });
            }
        });

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.LEAVE, new SubscriptionHandling.HandleEventCallback<ParseUser>() {
            @Override
            public void onEvent(ParseQuery<ParseUser> query, ParseUser object) {
                Log.d("onEvent: ", "leaving");
            }
        });


        final HashMap<String, String> params = new HashMap<>();
        params.put("channel", circleNameLive.getValue() + " " + inviteCodeLive.getValue());

        params.put("circleName", circleNameLive.getValue());



     /*   ParseCloud.callFunctionInBackground("onLiveQueryEvent", params, new FunctionCallback<Object>() {


            @Override
            public void done(Object object, ParseException e) {
                if(e==null){


                    Log.d( "onLiveQueryEvent","called");


                }

                else if(e!=null){


                }
            }
        });*/


    }


    private void iScurrentUserDriver(String updatedRider) {

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    boolean isCurrentUserDriver = object.getBoolean("IsDriver");
                    //updated user is rider,if current user driver ,
                    //check updatec  rider has current user as Driver.

                    if (isCurrentUserDriver) {


                        queryParseUser.getInBackground(updatedRider, new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser updatedRider, ParseException e) {

                                JSONArray driverListArray = updatedRider.getJSONArray("DriverList");


                                driverList.clear();
//driverList.addAll((Collection<? extends String>) driverListArray);

                                for (int i = 0; i < driverListArray.length(); i++) {
                                    try {
                                        driverList.add(String.valueOf(driverListArray.get(i)));
                                        Log.d("done:driver", String.valueOf(driverListArray.get(i)));
                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();
                                    }
                                }

                                Log.d("driverList", String.valueOf(driverList.size()));

                               /* if (driverListArray != null && driverListArray.length() > 0) {


                                    for (int i = 0; i < driverListArray.length(); i++) {

                                        try {

                                            if (driverListArray.get(i).equals(ParseUser.getCurrentUser().getObjectId())) {
                                                Log.d("done:driverId", String.valueOf(driverListArray.get(i)));
                                                checkRiderMarkerAvailable(updatedRider);

                                            }


                                        } catch (JSONException jsonException) {
                                            jsonException.printStackTrace();
                                        }
                                    }
                                }*/


//current user driver available in updated rider.so need to check marker available for rider.
                                //if available need to update location else add new marker for rider.
                                if (driverList.contains(ParseUser.getCurrentUser().getObjectId())) {
                                    Log.d("done:marker", "aval");
                                    checkRiderMarkerAvailable(updatedRider);
                                } else {
                                    Log.d("done:marker", " not aval");
                                    for (int k = 0; k < riderMarkers.size(); k++) {
                                        if (updatedRider.getObjectId().equals(riderMarkers.get(k).getTag())) {
                                            Log.d("driverDisconnected", String.valueOf(riderMarkers.get(k).getTag()));
                                            //remove marker
                                            riderMarkers.get(k).remove();
                                            riderMarkers.remove(k);
                                            getRiderMarkerInstance().setValue(riderMarkers);

                                            Log.d("getRiderMarkerInstance", String.valueOf(getRiderMarkerInstance().getValue().size()));
                                            //change camera position after update location of maker
                                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                        }
                                    }

                                    //update bottom sheet
                                    if (driverDetailsPojoList.size() > 0) {
                                        for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                            if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                driverDetailsPojoList.remove(k);
                                                getDriverDetailsListLiveData().setValue(driverDetailsPojoList);

                                            }
                                        }
                                    }
                                }


                                //if markerList contain updated driver id but driver list does not  contains current user driver means rider disconnected driver need to remove rider marker


                            }
                        });
                    }
                }
            }
        });
    }

    private void checkRiderMarkerAvailable(ParseUser updatedRider) {

        isMarkerAvailable = false;
        Log.d("isMarkerAvaliable", String.valueOf(isMarkerAvailable));

        for (int i = 0; i < riderMarkers.size(); i++) {

            Log.d("checkRider", String.valueOf(riderMarkers.get(i).getTag()));
            // if(riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())){
            if (updatedRider.getObjectId().equals(riderMarkers.get(i).getTag())) {
                Log.d("checkRiderMarker", "marker availabele update marker and position");

                isMarkerAvailable = true;
                queryParseUser.getInBackground(updatedRider.getObjectId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser updatedRider, ParseException e) {

                        if (e == null) {


                            // JSONArray driverList=updatedRider.getJSONArray("DriverList");

                            // for(int j=0;j<driverList.length();j++){

                            //if driver list contains current user driver need to  update rider location


                            // if(driverList.get(j).equals(ParseUser.getCurrentUser().getObjectId())){


                            imageFile = updatedRider.getParseFile("profilepicture");


                            if (imageFile != null) {


                                imageFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {


                                        if (e == null && data != null) {


                                            //this is bitmap of parse file
                                            imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                            mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                                            riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                            //getRiderLocationLive().setValue(riderLocation);
                                            userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);
                                            Log.d("done:userMarker", String.valueOf(userAddress));
                                            locationPermission = updatedRider.getString("LocationPermission");

                                            Log.d("locationPermission", locationPermission);

                                            for (int i = 0; i < riderMarkers.size(); i++) {
                                                if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {
                                                    Log.d("done:ride", String.valueOf(riderMarkers.get(i).getTag()));

                                                    getCustomMarker(R.layout.custom_marker, imagebitmap);

                                                    //get resized marker after update dp
                                                    resizeMarker(updatedbitmap, 215, 215);

                                                    //if position changed,set new position
                                                    riderMarkers.get(i).setPosition(mRiderLocation);
                                                    riderMarkers.get(i).setSnippet(userAddress);

                                                    //if dp changed set new dp in custom marker from bitmap
                                                    riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                                    //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                                    //bitmapArrayList.set(i, resizedBitmap);

                                                    getRiderMarkerInstance().setValue(riderMarkers);
                                                    //change camera position after update location of maker
                                                    ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                                    if (driverDetailsPojoList.size() > 0) {
                                                        for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                                            if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                                String updatedName = updatedRider.getUsername();

                                                                //get readable address from current Location latitude and longitude.
                                                                String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                                                driverDetailsPojoList.get(k).setUserName(updatedName);


                                                                driverDetailsPojoList.get(k).setUserDp(imagebitmap);

                                                                driverDetailsPojoList.get(k).setLatitude(riderLocation.getLatitude());
                                                                driverDetailsPojoList.get(k).setLongitude(riderLocation.getLongitude());

                                                                driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                                                driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                                                             /*   double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);

                                                                Log.d("done:distancer", String.valueOf(distance));
                                                                Log.d("done:driver", String.valueOf(getRiderLocationLive().getValue()));
                                                                Log.d("done:rider", String.valueOf(riderLocation));*/

                                                                // getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());

                                                                // driverDetailsPojoList.get(k).setDistanceInMiles(distance);

                                                                if (locationPermission.equals("ON")) {
                                                                    if (updatesCurrentLocationAddress.isEmpty()) {
                                                                     /*   driverDetailsPojoList.get(k).setLatitude(0);
                                                                        driverDetailsPojoList.get(k).setLongitude(0);

                                                                        driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                                        driverDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                                        driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                                    } else {
                                                                        driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                                    }
                                                                } else if (locationPermission.equals("OFF")) {
                                                                    driverDetailsPojoList.get(k).setLatitude(0);
                                                                    driverDetailsPojoList.get(k).setLongitude(0);

                                                                    driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                                    driverDetailsPojoList.get(k).setDestinationLongitude(0);

                                                                    driverDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");

                                                                }


                                                                //set new set of value
                                                                // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                                getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                                            }
                                                        }


                                                    }

                                                }
                                            }
                                        }
                                    }
                                });
                            }

                            if (imageFile == null) {

                                String riderName = updatedRider.getUsername();

                                Log.d("riderName", riderName);

                                String firstLetterOfUSer = String.valueOf(riderName.charAt(0));

                                String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                                mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                                riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                // getRiderLocationLive().setValue(riderLocation);
                                locationPermission = updatedRider.getString("LocationPermission");

                                Log.d("locationPermission", locationPermission);

                                // resizeMarker(updatedBitmapWithText, 215, 215);
                                userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);


                                for (int i = 0; i < riderMarkers.size(); i++) {
                                    if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {


                                        getCustomMarkerWithoutDp(firstLetterToUpperCase);

                                        //get resized marker after update dp
                                        resizeMarker(updatedBitmapWithText, 215, 215);

                                        //if position changed,set new position
                                        riderMarkers.get(i).setPosition(mRiderLocation);
                                        riderMarkers.get(i).setSnippet(userAddress);

                                        //if dp changed set new dp in custom marker from bitmap
                                        riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                        //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                        //bitmapArrayList.set(i, resizedBitmap);

                                        getRiderMarkerInstance().setValue(riderMarkers);


                                        //change camera position after update location of maker
                                        ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                                        // if location changed ,it will change address text view  in user list recycler view
                                        if (driverDetailsPojoList.size() > 0) {
                                            for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                                if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                    String updatedName = updatedRider.getUsername();

                                                    //get readable address from current Location latitude and longitude.
                                                    String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                                    driverDetailsPojoList.get(k).setUserName(updatedName);


                                                    driverDetailsPojoList.get(k).setUserDp(dpWithTextDpForRecyclerView);

                                                    driverDetailsPojoList.get(k).setLatitude(riderLocation.getLatitude());
                                                    driverDetailsPojoList.get(k).setLongitude(riderLocation.getLongitude());

                                                    driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                                    driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                                                    /*double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                                                    Log.d("done:distancerr", String.valueOf(distance));
                                                    Log.d("done:driver", String.valueOf(getRiderLocationLive().getValue()));
                                                    Log.d("done:rider", String.valueOf(riderLocation));*/
                                                   /* getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());

                                                    driverDetailsPojoList.get(k).setDistanceInMiles( distance);*/


                                                    if (locationPermission.equals("ON")) {
                                                        if (updatesCurrentLocationAddress.isEmpty()) {
                                                           /* driverDetailsPojoList.get(k).setLatitude(0);
                                                            driverDetailsPojoList.get(k).setLongitude(0);

                                                            driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                            driverDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                            driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                        } else {
                                                            driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                        }
                                                    } else if (locationPermission.equals("OFF")) {
                                                        driverDetailsPojoList.get(k).setLatitude(0);
                                                        driverDetailsPojoList.get(k).setLongitude(0);

                                                        driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                        driverDetailsPojoList.get(k).setDestinationLongitude(0);

                                                        driverDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");

                                                    }


                                                    //set new set of value
                                                    // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                                }
                                            }


                                        }
                                    }
                                }
                            }





                                       /* else{
                                            map.clear();
                                            AddDriverMarker(map);
                                        }*/

                        }


                    }


                });
            }
        }


        if (!isMarkerAvailable) {


            Log.d("checkRiderMarker", "Add new marker");
            getRiderDetails(updatedRider.getObjectId());
        }

       /* if (getRiderMarkerInstance().getValue() != null && isItriderMapFragment && getRiderMarkerInstance().getValue().size() > 0) {

            for (int i = 0; i < getRiderMarkerInstance().getValue().size(); i++) {

                Log.d("checkRiderMarker", String.valueOf(getRiderMarkerInstance().getValue().get(i).getTag()));

                if (updatedRider.getObjectId().equals(getRiderMarkerInstance().getValue().get(i).getTag())) {
                    int positionToUpdate = i;
                    Log.d("checkRiderMarker", "marker available, update position");

                    queryParseUser.getInBackground(updatedRider.getObjectId(), new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser updatedRider, ParseException e) {

                            if (e == null) {

                                imageFile = updatedRider.getParseFile("profilepicture");


                                if (imageFile != null) {


                                    imageFile.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {


                                            if (e == null && data != null) {


                                                //this is bitmap of parse file
                                                imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                              *//*  getCustomMarker(R.layout.custom_marker, imagebitmap);
                                                resizeMarker(updatedbitmap, 215, 215);*//*
                                                mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                                                riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                                getRiderLocationLive().setValue(riderLocation);
                                                userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);
                                                Log.d("done:userMarker", String.valueOf(userAddress));


                                                for (int i = 0; i < riderMarkers.size(); i++) {
                                                    if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {


                                                        getCustomMarker(R.layout.custom_marker,imagebitmap);

                                                        //get resized marker after update dp
                                                        resizeMarker(updatedbitmap, 215, 215);

                                                        //if position changed,set new position
                                                        riderMarkers.get(i).setPosition(mRiderLocation);

                                                        //if dp changed set new dp in custom marker from bitmap
                                                        riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                                        //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                                        //bitmapArrayList.set(i, resizedBitmap);

                                                        getRiderMarkerInstance().setValue(riderMarkers);
                                                        //change camera position after update location of maker
                                                        ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                                        if (driverDetailsPojoList.size() > 0) {
                                                            for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                                                if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                                    String updatedName = updatedRider.getUsername();

                                                                    //get readable address from current Location latitude and longitude.
                                                                    String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                                                    driverDetailsPojoList.get(k).setUserName(updatedName);

                                                                    if (updatesCurrentLocationAddress.isEmpty()) {
                                                                        driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                                    } else {
                                                                        driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                                    }
                                                                    driverDetailsPojoList.get(k).setUserDp(imagebitmap);

                                                                    driverDetailsPojoList.get(k).setLatitude(mRiderLocation.latitude);
                                                                    driverDetailsPojoList.get(k).setLongitude(mRiderLocation.longitude);
                                                                    //set new set of value
                                                                    // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                                                }
                                                            }


                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    });
                                }

                                if (imageFile == null) {

                                    String riderName = updatedRider.getUsername();

                                    Log.d("riderName", riderName);

                                    String firstLetterOfUSer = String.valueOf(riderName.charAt(0));

                                    String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                                    mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                                    riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                    getRiderLocationLive().setValue(riderLocation);


                                   // resizeMarker(updatedBitmapWithText, 215, 215);
                                    userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);


                                    for (int i = 0; i < riderMarkers.size(); i++) {
                                        if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {


                                            getCustomMarkerWithoutDp(firstLetterToUpperCase);

                                            //get resized marker after update dp
                                            resizeMarker(updatedBitmapWithText, 215, 215);

                                            //if position changed,set new position
                                            riderMarkers.get(i).setPosition(mRiderLocation);

                                            //if dp changed set new dp in custom marker from bitmap
                                            riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                            //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                            //bitmapArrayList.set(i, resizedBitmap);

                                            getRiderMarkerInstance().setValue(riderMarkers);


                                            //change camera position after update location of maker
                                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());



                                            dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                                            // if location changed ,it will change address text view  in user list recycler view
                                            if (driverDetailsPojoList.size() > 0) {
                                                for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                                    if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                        String updatedName = updatedRider.getUsername();

                                                        //get readable address from current Location latitude and longitude.
                                                        String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                                        driverDetailsPojoList.get(k).setUserName(updatedName);

                                                        if (updatesCurrentLocationAddress.isEmpty()) {
                                                            driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                        } else {
                                                            driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                        }
                                                        driverDetailsPojoList.get(k).setUserDp(dpWithTextDpForRecyclerView);

                                                        driverDetailsPojoList.get(k).setLatitude(mRiderLocation.latitude);
                                                        driverDetailsPojoList.get(k).setLongitude(mRiderLocation.longitude);
                                                        //set new set of value
                                                        // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                        getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                                    }
                                                }


                                            }
                                        }
                                    }
                                }





                            }

                        }
                    });
                } else if (ParseUser.getCurrentUser().getObjectId().equals(getRiderMarkerInstance().getValue().get(i).getTag())) {
                    Log.d("checkRiderMarker", "driver  available");
                } else {
                    Log.d("checkRiderMarker", "marker not  available ,add new marker");


                    //add new marker for updated object

                    getRiderDetails(updatedRider.getObjectId());

                }

            }
        }*/
    }

    public void updateDriverMarker() {

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser updatedRider, ParseException e) {

                if (e == null) {

                    imageFile = updatedRider.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                    mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                                    riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                    getRiderLocationLive().setValue(riderLocation);
                                    userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);
                                    Log.d("done:userMarker", String.valueOf(userAddress));


                                    locationPermission = updatedRider.getString("LocationPermission");

                                    Log.d("locationPermission", locationPermission);

                                    for (int i = 0; i < riderMarkers.size(); i++) {
                                        if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {

                                            // IsUpdatedDriverMarkerAvailable=true;
                                            getCustomMarker(R.layout.custom_marker, imagebitmap);

                                            //get resized marker after update dp
                                            resizeMarker(updatedbitmap, 215, 215);

                                            //if position changed,set new position
                                            riderMarkers.get(i).setPosition(mRiderLocation);

                                            riderMarkers.get(i).setSnippet(userAddress);

                                            //if dp changed set new dp in custom marker from bitmap
                                            riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                            //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                            //bitmapArrayList.set(i, resizedBitmap);

                                            getRiderMarkerInstance().setValue(riderMarkers);
                                            //change camera position after update location of maker
                                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                            if (driverDetailsPojoList.size() > 0) {
                                                for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                                    if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                                        String updatedName = updatedRider.getUsername();

                                                        //get readable address from current Location latitude and longitude.
                                                        String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                                        driverDetailsPojoList.get(k).setUserName(updatedName);


                                                        driverDetailsPojoList.get(k).setUserDp(imagebitmap);

                                                        driverDetailsPojoList.get(k).setLatitude(mRiderLocation.latitude);
                                                        driverDetailsPojoList.get(k).setLongitude(mRiderLocation.longitude);
                                                        driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                                        driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                                                       /* double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                                                        Log.d("done:distance", String.valueOf(distance));*/
                                                        // getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());

//set distance with rider
                                                        // driverDetailsPojoList.get(k).setDistanceInMiles( distance);


                                                        if (locationPermission.equals("ON")) {
                                                            if (updatesCurrentLocationAddress.isEmpty()) {
                                                             /*   driverDetailsPojoList.get(k).setLatitude(0);
                                                                driverDetailsPojoList.get(k).setLongitude(0);

                                                                driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                                driverDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                                driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");


                                                            } else {
                                                                driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                            }
                                                        } else if (locationPermission.equals("OFF")) {
                                                            driverDetailsPojoList.get(k).setLatitude(0);
                                                            driverDetailsPojoList.get(k).setLongitude(0);
                                                            driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                            driverDetailsPojoList.get(k).setDestinationLongitude(0);
                                                            //driverDetailsPojoList.get(k).setDistanceInMiles("0");
                                                            driverDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");
                                                        }

                                                        //set new set of value
                                                        // userDetailsListLiveData.setValue(userDetailsPojoList);
                                                        getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                                    }
                                                }

                                                for (int j = 0; j < driverDetailsPojoList.size(); j++) {

                                                    double riderLatitude = driverDetailsPojoList.get(j).getLatitude();
                                                    double riderLongitude = driverDetailsPojoList.get(j).getLongitude();
                                                    double driverLatitude = driverDetailsPojoList.get(j).getDestinationLatitude();
                                                    double driverLongitude = driverDetailsPojoList.get(j).getDestinationLongitude();


                                                    driverDetailsPojoList.get(j).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                                    driverDetailsPojoList.get(j).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                                                    ParseGeoPoint riderLocation = new ParseGeoPoint(riderLatitude, riderLongitude);
                                                    Log.d("riderLocation", String.valueOf(riderLocation));

                                                    //  double updatedDriverDistance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                                                    //  getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLatitude,riderLongitude);


                                                    // Log.d("updatedDriverDistance", String.valueOf(updatedDriverDistance));
                                                    // driverDetailsPojoList.get(j).setDistanceInMiles( distance);

                                                }
                                                getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                            }

                                        }
                                    }

                                   /* if(!IsUpdatedDriverMarkerAvailable){

                                        //add new marker for current user driver
                                        Log.d("done","marker not availa");

                                        AddDriverMarker(mRiderMap);
                                    }*/
                                }
                            }
                        });
                    }

                    if (imageFile == null) {

                        String riderName = updatedRider.getUsername();

                        Log.d("riderName", riderName);

                        String firstLetterOfUSer = String.valueOf(riderName.charAt(0));

                        String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                        mRiderLocation = getLocationLatLong(updatedRider, "userCurrentLocation");
                        riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                        getRiderLocationLive().setValue(riderLocation);
                        locationPermission = updatedRider.getString("LocationPermission");

                        Log.d("locationPermission", locationPermission);

                        // resizeMarker(updatedBitmapWithText, 215, 215);
                        userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);


                        for (int i = 0; i < riderMarkers.size(); i++) {
                            if (riderMarkers.get(i).getTag().equals(updatedRider.getObjectId())) {

                                //IsUpdatedDriverMarkerAvailable=true;
                                getCustomMarkerWithoutDp(firstLetterToUpperCase);

                                //get resized marker after update dp
                                resizeMarker(updatedBitmapWithText, 215, 215);

                                //if position changed,set new position
                                riderMarkers.get(i).setPosition(mRiderLocation);

                                riderMarkers.get(i).setSnippet(userAddress);

                                //if dp changed set new dp in custom marker from bitmap
                                riderMarkers.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                                //to get currect size marker when zoom in and out,we need to add updated resized bitmap to array list
                                //bitmapArrayList.set(i, resizedBitmap);

                                getRiderMarkerInstance().setValue(riderMarkers);


                                //change camera position after update location of maker
                                ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                                dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                                // if location changed ,it will change address text view  in user list recycler view
                                if (driverDetailsPojoList.size() > 0) {
                                    for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                                        if (driverDetailsPojoList.get(k).getUserObjectId().equals(updatedRider.getObjectId())) {

                                            Log.d("driverDetailsPoj", driverDetailsPojoList.get(k).getUserName());

                                            String updatedName = updatedRider.getUsername();

                                            //get readable address from current Location latitude and longitude.
                                            String updatesCurrentLocationAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);

                                            driverDetailsPojoList.get(k).setUserName(updatedName);

                                            driverDetailsPojoList.get(k).setUserDp(dpWithTextDpForRecyclerView);

                                            /*driverDetailsPojoList.get(k).setLatitude(mRiderLocation.latitude);
                                            driverDetailsPojoList.get(k).setLongitude(mRiderLocation.longitude);*/

                                            driverDetailsPojoList.get(k).setLatitude(mRiderLocation.latitude);
                                            driverDetailsPojoList.get(k).setLongitude(mRiderLocation.longitude);
                                            driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                            driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                                            Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.get(k).getLatitude()));
                                            Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.get(k).getLongitude()));
                                            Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.get(k).getDestinationLatitude()));
                                            Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.get(k).getDestinationLongitude()));



                                         /*   double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                                            Log.d("done:distance", String.valueOf(distance));*/
                                           /* getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());


                                            driverDetailsPojoList.get(k).setDistanceInMiles( distance);*/

                                            if (locationPermission.equals("ON")) {
                                                if (updatesCurrentLocationAddress.isEmpty()) {
                                                  /*  driverDetailsPojoList.get(k).setLatitude(0);
                                                    driverDetailsPojoList.get(k).setLongitude(0);

                                                    driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                    driverDetailsPojoList.get(k).setDestinationLongitude(0);*/
                                                    driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                                } else {
                                                    driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocationAddress);
                                                }
                                            } else if (locationPermission.equals("OFF")) {
                                                // driverDetailsPojoList.get(k).setDistanceInMiles("0");
                                                driverDetailsPojoList.get(k).setLatitude(0);
                                                driverDetailsPojoList.get(k).setLongitude(0);
                                                driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                                driverDetailsPojoList.get(k).setDestinationLongitude(0);
                                                driverDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");
                                            }

                                            //set new set of value
                                            // userDetailsListLiveData.setValue(userDetailsPojoList);
                                            // getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                        } else {


                                            driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                            driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                                            Log.d("done:updatedis", String.valueOf(driverDetailsPojoList.get(k).getDestinationLatitude()));
                                            Log.d("done:updatedis", String.valueOf(driverDetailsPojoList.get(k).getDestinationLongitude()));
                                        }
                                    }
                                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);


                                    /*for (int j = 0; j < driverDetailsPojoList.size(); j++) {

                                        double riderLatitude = driverDetailsPojoList.get(j).getLatitude();
                                        double riderLongitude = driverDetailsPojoList.get(j).getLongitude();

                                        driverDetailsPojoList.get(j).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                        driverDetailsPojoList.get(j).setDestinationLatitude(getRiderLocationLive().getValue().getLongitude());

                                        ParseGeoPoint riderLocation = new ParseGeoPoint(riderLatitude, riderLongitude);
                                        Log.d("riderLocation", String.valueOf(riderLocation));

                                       *//* double updatedDriverDistance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);

                                        Log.d("updatedDriverDistance", String.valueOf(updatedDriverDistance));*//*
                                       // getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLatitude,riderLongitude);

                                      //  driverDetailsPojoList.get(j).setDistanceInMiles(distance);



                                    }*/


                                }

                            }
                        }

                     /*   if(!IsUpdatedDriverMarkerAvailable){

                            //add new marker for current user driver
                            Log.d("done","marker not availa");

                            AddDriverMarker(mRiderMap);
                        }*/
                    }


                }

            }
        });
    }

    private void isHeInRiderList(ParseUser updatedDriverId) {
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                JSONArray riderList = object.getJSONArray("RiderList");


                if (riderList == null || riderList.length() == 0) {

                    Log.d("done:updatedRider", "NotInList");
                }


                for (int i = 0; i < riderList.length(); i++) {

                    Log.d("isHeInRiderList: ", String.valueOf(riderList.length()));

                    try {
                        if (riderList.get(i).equals(updatedDriverId)) {

                            Log.d("driverHasRider", String.valueOf(riderList.get(i)));

                            //after check isHeInRiderList need to update rider location in map.


                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }
        });
    }

    private void isHeInDriverList(ParseObject driverId) {

//getAllSavedBusStops(String.valueOf(driverId));
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                JSONArray driverList = object.getJSONArray("DriverList");


                Log.d("isHeInDriverList: ", String.valueOf(driverList.length()));
                for (int i = 0; i < driverList.length(); i++) {

                    try {
                        driverList.get(i).equals(driverId.getObjectId());

                        Log.d("driverId ", String.valueOf(driverId.getObjectId()));
                        Log.d("driverlist ", String.valueOf(driverList.get(i)));
                        updateDriverLicationInMap(driverId);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateDriverLicationInMap(ParseObject driverId) {

        Log.d("updateDriver", String.valueOf(driverId));
        queryParseUser.getInBackground(driverId.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                boolean isDriverAvailable = object.getBoolean("isDriverAvailable");
                if (isDriverAvailable) {


                    Log.d("done:ob", object.getObjectId());
                    LatLng driverLocation = getLocationLatLong(object, "userCurrentLocation");

              /*  Log.d("ridermarkers", String.valueOf(ridermarkers.size()));
                Log.d("locatiom", String.valueOf(driverLocation));
                Log.d("ridermarkers00be", String.valueOf(ridermarkers.get(0).getPosition()));
                Log.d("ridermarkers01be", String.valueOf(ridermarkers.get(1).getPosition()));*/

                    for (int k = 0; k < getRiderMarkerInstance().getValue().size(); k++) {
                        String markerTag = String.valueOf(getRiderMarkerInstance().getValue().get(k).getTag());


                        if (markerTag.equals(updateUserId.getValue())) {
                            Log.d("markerTag", markerTag);
                            Log.d("updateUserId", updateUserId.getValue());
                            isDriverMarkerAvailable = true;

                            Log.d("beposition", String.valueOf(getRiderMarkerInstance().getValue().get(k).getPosition()));


                            getRiderMarkerInstance().getValue().get(k).setPosition(driverLocation);

                            String updatedAddress = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);
                            getRiderMarkerInstance().getValue().get(k).setSnippet(updatedAddress);


                            Log.d("afposition", String.valueOf(getRiderMarkerInstance().getValue().get(k).getPosition()));
                              /*  String driverAddress = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);


                                getCustomMarkerWithBus();

                                resizeMarker(updatedbitmap, 215, 215);
                                markerOption = new MarkerOptions().position(driverLocation).title(object.getUsername()).snippet(driverAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                                mRiderMap.addMarker(markerOption);


                                markerNew = mRiderMap.addMarker(markerOption);
                                markerNew.setTag(object.getObjectId());

                                riderMarkerList.add(resizedBitmap);


                                ridermarkers.add(markerNew);

*/
                        }


                    }

                    if (!isDriverMarkerAvailable) {

                        Log.d("isDriverMarkerAva", "not avai");


                        String driverName = object.getUsername();
                        LatLng driveLocation = getLocationLatLong(object, "userCurrentLocation");

                        String driverAddress = Utilities.getAddressFromLocation(mContext, driveLocation.latitude, driveLocation.longitude);

                        getCustomMarkerWithBus();

                        resizeMarker(updatedbitmap, 215, 215);
                        markerOption = new MarkerOptions().position(driveLocation).title(object.getUsername()).snippet(driverAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                        // mRiderMap.addMarker(markerOption).setTag(object.getObjectId());
                        Log.d("done:add", "called");
                        Marker marker = mRiderMap.addMarker(markerOption);
                        marker.setTag(object.getObjectId());

                        riderMarkers.add(marker);

                        Log.d("riderMarkersSize", String.valueOf(riderMarkers.size()));
                        getRiderMarkerInstance().setValue(riderMarkers);

                        // getDriverDetails(object.getObjectId());
                    }

                    Log.d("driverDetailsPojoList", String.valueOf(driverDetailsPojoList.size()));

                    for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                        if (driverDetailsPojoList.get(k).getUserObjectId().equals(object.getObjectId())) {

                            //get updated user name  and location and dp.
                            String updatedName = object.getUsername();
                            String updatesCurrentLocation = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);

                            //set update value in list
                            driverDetailsPojoList.get(k).setUserName(updatedName);

                        /*    if (updatesCurrentLocation.isEmpty()) {
                                driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                            } else {
                                driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocation);
                            }*/
                            //driverDetailsPojoList.get(k).setUserDp(imagebitmapUpdated);


                            driverDetailsPojoList.get(k).setLatitude(driverLocation.latitude);
                            driverDetailsPojoList.get(k).setLongitude(driverLocation.longitude);
                            driverDetailsPojoList.get(k).setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                            driverDetailsPojoList.get(k).setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());
                            //  driverDetailsPojoList.get(k).setDestinationLatitude(driverLocation.longitude);


                            ParseGeoPoint driverLocationGeo = new ParseGeoPoint(driverLocation.latitude, mRiderLocation.longitude);

                            Log.d("address", updatesCurrentLocation);
                            Log.d("driverLocationlive", String.valueOf(driverLocationGeo));
                            Log.d("riderLocationlive", String.valueOf(getRiderLocationLive().getValue()));

                           /* double distance = getRiderLocationLive().getValue().distanceInMilesTo(driverLocationGeo);
                            Log.d("done:distance", String.valueOf(distance));*/
                          /*  getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());


                            driverDetailsPojoList.get(k).setDistanceInMiles( distance);
*/


                            locationPermission = object.getString("LocationPermission");

                            Log.d("locationPermission", locationPermission);

                            if (locationPermission.equals("ON")) {
                                if (updatesCurrentLocation.isEmpty()) {
                                  /*  driverDetailsPojoList.get(k).setLatitude(0);
                                    driverDetailsPojoList.get(k).setLongitude(0);
                                    driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                    driverDetailsPojoList.get(k).setDestinationLongitude(0);
*/
                                    driverDetailsPojoList.get(k).setUserCurrentLocation("Unknown Address");

                                } else {
                                    driverDetailsPojoList.get(k).setUserCurrentLocation(updatesCurrentLocation);
                                }
                            } else if (locationPermission.equals("OFF")) {
                                driverDetailsPojoList.get(k).setUserCurrentLocation("Location permissions off");
                                driverDetailsPojoList.get(k).setLatitude(0);
                                driverDetailsPojoList.get(k).setLongitude(0);
                                driverDetailsPojoList.get(k).setDestinationLatitude(0);
                                driverDetailsPojoList.get(k).setDestinationLongitude(0);
                            }


                            //set new set of value to the live data.so observer observe this data change and will update recycler view in bottom sheet.
                            //  userDetailsListLiveData.setValue(userDetailsPojoList);
                            getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());
                        }
                    }

                } else if (!isDriverAvailable) {

                    for (int k = 0; k < driverDetailsPojoList.size(); k++) {

                        if (driverDetailsPojoList.get(k).getUserObjectId().equals(object.getObjectId())) {

                            //get updated user name  and location and dp.
                            String updatedName = object.getUsername();
                            // String updatesCurrentLocation = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);

                            //set update value in list
                            driverDetailsPojoList.get(k).setUserName(updatedName);
                            driverDetailsPojoList.get(k).setLatitude(0);
                            driverDetailsPojoList.get(k).setLongitude(0);
                            driverDetailsPojoList.get(k).setDestinationLatitude(0);
                            driverDetailsPojoList.get(k).setDestinationLongitude(0);


                            driverDetailsPojoList.get(k).setUserCurrentLocation("Not available");

                            //set new set of value to the live data.so observer observe this data change and will update recycler view in bottom sheet.
                            //  userDetailsListLiveData.setValue(userDetailsPojoList);
                            getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());
                        }
                    }
                }
            }
        });
    }

       /* catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }*/


    public void subscribeForCircleNameLiveQuery() {
        Log.d("subscribeForCircle: ", String.valueOf(getSubscribeForCircleNameLiveData().getValue()));
           /* if (getSubscribeForCircleNameLiveData().getValue()) {

                Log.d("subscribe", "subsribedCir");
            } else {*/
        //  getSubscribeForCircleNameLiveData().setValue(true);
        //ParseLiveQueryClient parseLiveQueryClient = null;


     /*   try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/"));


            ParseQuery<ParseObject> queryCircleCode = new ParseQuery<ParseObject>("CircleName");*/
        getSubscribeForCircleNameLiveData().setValue(true);
        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(queryCircleName);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {


                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {


                        Log.d("updatedObject", object.getObjectId());


                        ParseQuery<ParseObject> objectParseQuery = new ParseQuery<ParseObject>("CircleName");
                        objectParseQuery.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject updatedObject, ParseException e) {

                                String updatedCircleName = String.valueOf(object.get("circleName"));
                                String updatedInviteCode = String.valueOf(object.get("password"));

                                Log.d("updatedCircleName", updatedCircleName + "  " + updatedInviteCode);
                                Log.d("circleNameLive", circleNameLive.getValue() + "  " + inviteCodeLive.getValue());
                                Log.d("circleNameLive", getSelectedGroupNameLiveData().getValue() + "  " + getSelectedInviteCodeLiveData().getValue());


                                if (updatedCircleName.equals(getSelectedGroupNameLiveData().getValue()) && updatedInviteCode.equals(getSelectedInviteCodeLiveData().getValue())) {
                                    // Log.d("markersize", String.valueOf(getMarkersLiveInstance().getValue().size()));
                                    // if (getMarkersLiveInstance().getValue().size()>0) {
                                    // ifAddedNewMember(updatedObject);

                                    objectsToget.clear();


                                    Log.d("done:updateCustmmm", String.valueOf(markers.size()));
                                    //  getAllCircleMember(objectsToget);
                                    markers.clear();
                                    map.clear();
                                    // UpdatewithCustomMarker(mContext, map, objectsToget);
                                    new java.util.Timer().schedule(
                                            new java.util.TimerTask() {
                                                @Override
                                                public void run() {


                                                    objectsToget.add(getSelectedGroupNameLiveData().getValue());
                                                    objectsToget.add(getSelectedInviteCodeLiveData().getValue());
                                                    // your code here
                                                    Log.d("run: ", "waiting for server, notify update");
//map.clear();
                                                    UpdatewithCustomMarker(mContext, map, objectsToget);
                                                }
                                            },
                                            5000
                                    );
                                    // getAllPlaceGeoPoints();

                                }
                            }
                        });


                    }
                });
            }

        });

    }

    /*catch (URISyntaxException e) {
            e.printStackTrace();


        }

    }
*/

    private void ifAddedNewMember(ParseObject object) {

        //object is updated circleName single object

        //when new member added to circle,we need to notify other circle member.
        // to do that we need to get object id of  newly added circle member then need to add new marker for him.

        // ParseQuery<ParseObject> parseObjectParseQuery = new ParseQuery<ParseObject>("CircleName");

        queryCircleName.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    List<String> circleMemberList = new ArrayList<>();
                    List<String> markerList = new ArrayList<>();
                    JSONArray circleMemberArray = object.getJSONArray("circleMember");


                    for (int j = 0; j < markers.size(); j++) {

                        markerList.add(String.valueOf(markers.get(j).getTag()));
                    }

                    //if the current marker size less than circleMember length means new member added to the Circle
                    if (markers.size() < circleMemberArray.length()) {
                        if (circleMemberArray.length() > 0) {


                            for (int i = 0; i < circleMemberArray.length(); i++) {
                                try {
                                    String circleMemberId = String.valueOf(circleMemberArray.get(i));
                                    circleMemberList.add(circleMemberId);

                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }

                            //if we remove markerList from circleMemberList list we will get newly added user objectId.
                            //after getting userId we need to Add new marker for newly added user .

                            circleMemberList.removeAll(markerList);

                            for (int k = 0; k < circleMemberList.size(); k++) {

                                //we need updated object id to find circle name to send notification
                                // AddNewMarkerForNewMember(circleMemberList.get(k), object);

                                objectsToget.clear();
                                objectsToget.add(circleNameLive.getValue());
                                objectsToget.add(inviteCodeLive.getValue());


                                //  getAllCircleMember(objectsToget);
                                UpdatewithCustomMarker(mContext, map, objectsToget);


                            }

                            //  sendNewMemberAddedNotification(ParseUser.getCurrentUser().getUsername(), object);

                        }
                    }


                    //if marker size is greater than circleMember means member left from that group,so we need to delete the marker for that user and
                    // also need to send notification  to current channel(circle name is a channel)
                    else if (circleMemberArray.length() > 0 && markers.size() > circleMemberArray.length()) {

                        for (int i = 0; i < circleMemberArray.length(); i++) {
                            try {
                                String circleMemberId = String.valueOf(circleMemberArray.get(i));
                                circleMemberList.add(circleMemberId);

                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }

                        //if we remove circlemember list from marker list we will get exited member object id.
                        markerList.removeAll(circleMemberList);

                        for (int k = 0; k < markerList.size(); k++) {
                            for (int h = 0; h < markers.size(); h++) {
                                if (markers.get(h).getTag().equals(markerList.get(k))) {

                                    Log.d("markerToRemove ", markerList.get(k));

                                    //we need to delete marker for exited user and also remove DP of exited user from bitmap array list and need to send notification for the current Channel.
                                    markers.remove(h);
                                    bitmapArrayList.remove(h);


                                    //get left member name to send notification
                                    queryParseUser.getInBackground(markerList.get(k), new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {

                                            if (e == null) {
                                                objectsToget.clear();
                                                objectsToget.add(circleNameLive.getValue());
                                                objectsToget.add(inviteCodeLive.getValue());
                                                String leftMemberName = parseUser.getUsername();

                                                //  getAllCircleMember(objectsToget);
                                                UpdatewithCustomMarker(mContext, map, objectsToget);

                                                //  ChangeCamaraPosition(mContext);

                                                // MemberLeftFromCircleNotification(leftMemberName, object);

                                            }
                                        }
                                    });


                                    //we need to update bottom sheet as well.
                                    updateBottmSheetForUser(markerList.get(k));
                                }
                            }


                        }











                      /*  if(circleMemberArray.length()==0){
                            Log.d("delete All Marker","no marker");
                            markers.clear();
                            bitmapArrayList.clear();
                           // ChangeCamaraPosition();

                        }*/
                        /*if (circleMemberArray.length() > 0) {

                            for (int i = 0; i < circleMemberArray.length(); i++) {
                                Log.d("circleMemberArrayTotal ", String.valueOf(circleMemberArray.length()));


                                try {
                                    String circleMemberId = String.valueOf(circleMemberArray.get(i));

                                    Log.d("circleMemberId ", circleMemberId);


                                    Log.d("markerSize", String.valueOf(markers.size()));
                                    for (int j = 0; j < markers.size(); j++) {

                                        if (markers.get(j).getTag().equals(circleMemberId)) {

                                            Log.d("thismarkerStillInGroup", String.valueOf(markers.get(j).getTag()));
                                        } else {
                                            Log.d("needToDeleteMarkerFor", String.valueOf(markers.get(j).getTag()));

                                            // AddNewMarkerForNewMember(circleMemberId);

                                           *//* markerOption = new MarkerOptions().position(locationLatlong).icon(BitmapDescriptorFactory.fromBitmap(updatedbitmap));
                                            markerNew = map.addMarker(markerOption);
                                            markerNew.setTag(groupMemeberObject.getObjectId());
                                            markers.add(markerNew);
*//*

                                            markers.remove(j);
                                            bitmapArrayList.remove(j);
                                            Log.d("AfterDeleteMarker ", String.valueOf(markers.size()));
                                            Log.d("AfterDeleteBitmap ", String.valueOf(bitmapArrayList.size()));

                                            ChangeCamaraPosition(mContext);


                                        }


                                    }


                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }
                        }*/
                    }


                }
            }


        });

    }


    private void updateBottmSheetForUser(String userId) {


        if (userDetailsPojoList.size() > 0) {
            for (int k = 0; k < userDetailsPojoList.size(); k++) {

                if (userDetailsPojoList.get(k).getUserObjectId().equals(userId)) {

                    Log.d("userDetailsObjectId", String.valueOf(userDetailsPojoList.get(k).getUserObjectId()));

                    //need to remove exited user detail from list then set new value for user details to update bottom sheet.

                    userDetailsPojoList.remove(k);

                    //set new set of value to the live data.so observer observe this data change and will update recycler view in bottom sheet.
                    // userDetailsListLiveData.setValue(userDetailsPojoList);
                    getUserDetailsListLiveData().setValue(userDetailsPojoList);


                }
            }
        }
    }

    private void MemberLeftFromCircleNotification(String leftMemberName) {


        List<String> subscribedChannals = ParseInstallation.getCurrentInstallation().getList("channels");

        //if subscribed channel contains placeName(place name is Channel name) in installation object means notification is ON for that channel.so we can send notification

        String channelName = null;


        for (int i = 0; i < subscribedChannals.size(); i++) {
            if (subscribedChannals.get(i).contains(circleNameLive.getValue() + " " + inviteCodeLive.getValue())) {
                channelName = subscribedChannals.get(i);
                Log.d("channelName", channelName);
                unsubscribeForChannal(channelName);

            }
        }

        // unsubscribeForChannal();


        final HashMap<String, String> params = new HashMap<>();
        Log.d("leftMemberName ", leftMemberName);
        params.put("leftMemberName", leftMemberName);


        // String circleName = String.valueOf(object.get("circleName"));
        Log.d("updatedCircleName", circleNameLive.getValue());
        params.put("channel", circleNameLive.getValue() + " " + inviteCodeLive.getValue());

        params.put("circleName", circleNameLive.getValue());

        Log.d("deviceToken: ", ParseInstallation.getCurrentInstallation().getDeviceToken());
        params.put("deviceToken", ParseInstallation.getCurrentInstallation().getDeviceToken());

        ParseCloud.callFunctionInBackground("memberLeftPush", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null) {
                    Log.d("done: ", "Push message sent!!!");

                  /*  ParsePush.unsubscribeInBackground(circleNameLive.getValue() + inviteCodeLive.getValue(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d("channel ", "unsubscribed to  " + circleNameLive.getValue() + inviteCodeLive.getValue());
                        }
                    });*/
                } else {
                    // Something went wrong
                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error: ", String.valueOf(exc));

                }

            }
        });

/*
            Log.d("UpdatedCircleObject: ", object.getObjectId());
            // ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");
            queryCircleName.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        String circleName = String.valueOf(object.get("circleName"));
                        Log.d("updatedCircleName", circleName);
                        params.put("channel", circleName);


                        ParseCloud.callFunctionInBackground("memberLeftPush", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object response, ParseException exc) {
                                if (exc == null) {
                                    Log.d("done: ", "Push message sent!!!");
                                } else {
                                    // Something went wrong
                                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error: ", String.valueOf(exc));

                                }

                            }
                        });

                    }
                }
            });*/
    }


    private void AddNewMarkerForNewMember(String circleMemberId, ParseObject updatedObject) {

        // ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        queryParseUser.getInBackground(circleMemberId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser groupMemeberObject, ParseException e) {
                if (e == null) {

                    Log.d("NewMemberId", groupMemeberObject.getObjectId());


                    imageFile = groupMemeberObject.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    userName = groupMemeberObject.getString("username");
                                    userObjectId = groupMemeberObject.getObjectId();

                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    //get custom marker using parse file
                                    getCustomMarker(R.layout.custom_marker, imagebitmap);


                                    //this custom marker list will be used  when zoom in zoom out
                                    bitmapArrayList.add(updatedbitmap);

                                    //get current location from  json Array
                                    getLocationLatLong(groupMemeberObject, "userCurrentLocation");
                                    if (locationLatlong != null) {
                                        Log.d("userCurrentLocation: ", String.valueOf(locationLatlong));

                                        //get address from current location
                                        currentLocationAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                        UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                        userDetailsPojo.setUserName(userName);
                                        userDetailsPojo.setUserObjectId(userObjectId);


                                        userDetailsPojo.setUserDp(imagebitmap);

                                        userDetailsPojo.setLatitude(locationLatlong.latitude);
                                        userDetailsPojo.setLongitude(locationLatlong.longitude);

                                        locationPermission = groupMemeberObject.getString("LocationPermission");

                                        Log.d("locationPermission", locationPermission);

                                        if (locationPermission.equals("ON")) {
                                            if (currentLocationAddress.isEmpty()) {

                                                userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                            } else {
                                                userDetailsPojo.setUserCurrentLocation(currentLocationAddress);
                                            }
                                        } else if (locationPermission.equals("OFF")) {
                                            userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                            userDetailsPojo.setLatitude(0);
                                            userDetailsPojo.setLongitude(0);
                                            userDetailsPojo.setDestinationLatitude(0);
                                            userDetailsPojo.setDestinationLongitude(0);
                                        }


                                        //set new set of value

//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                                        userDetailsPojoList.add(userDetailsPojo);


                                        // userDetailsListLiveData.setValue(userDetailsPojoList);
                                        getUserDetailsListLiveData().setValue(userDetailsPojoList);


                                        userName = groupMemeberObject.getString("username");

                                        getCustomMarkerWithoutDp(userName);

                                        userAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);


                                        markerOption = new MarkerOptions().position(locationLatlong).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(updatedbitmap));
                                        markerNew = map.addMarker(markerOption);
                                        markerNew.setTag(groupMemeberObject.getObjectId());
                                        // markerNew.showInfoWindow();
                                        markers.add(markerNew);
                                        getMarkersLiveInstance().setValue(markers);

                                        ChangeCamaraPosition(mContext, markers);

                                        //after added to the circle we need to notify other circle member
                                        Log.d("new member", "Added");

                                        // sendNewMemberAddedNotification(userName, updatedObject);

                                    }
                                }

                            }
                        });


                    } else {


                        userName = groupMemeberObject.getString("username");

                        getCustomMarkerWithoutDp(userName);

                        bitmapArrayList.add(updatedBitmapWithText);

                        getLocationLatLong(groupMemeberObject, "userCurrentLocation");
                        if (locationLatlong != null) {
                            userAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                            markerOption = new MarkerOptions().position(locationLatlong).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(updatedBitmapWithText));
                            markerNew = map.addMarker(markerOption);
                            markerNew.setTag(groupMemeberObject.getObjectId());
                            // markerNew.showInfoWindow();
                            markers.add(markerNew);

                            ChangeCamaraPosition(mContext, markers);


                            dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                            userObjectId = groupMemeberObject.getObjectId();

                            getLocationLatLong(groupMemeberObject, "userCurrentLocation");
                            if (locationLatlong != null) {
                                currentLocationAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                userDetailsPojo.setUserName(userName);
                                userDetailsPojo.setUserObjectId(userObjectId);

                                if (currentLocationAddress.isEmpty()) {
                                    userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                } else {
                                    userDetailsPojo.setUserCurrentLocation(currentLocationAddress);
                                }
                                userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);

                                userDetailsPojo.setLatitude(locationLatlong.latitude);
                                userDetailsPojo.setLongitude(locationLatlong.longitude);


                                userDetailsPojoList.add(userDetailsPojo);


                                //userDetailsListLiveData.setValue(userDetailsPojoList);
                                getUserDetailsListLiveData().setValue(userDetailsPojoList);
                                //after added to the circle we need to notify other circle member
                                Log.d("new member", "Added");

                                // sendNewMemberAddedNotification(userName, updatedObject);

                            }

                        }
                    }
                }
            }
        });


    }

    private void sendNewMemberAddedNotification(String newMemberName) {
        // Log.d("UpdatedCircleObject: ", updatedObject.getObjectId());
        final HashMap<String, String> params = new HashMap<>();
        Log.d("newMemberName: ", newMemberName);
        params.put("newMemberName", newMemberName);

        // String circleName = String.valueOf(object.get("circleName"));
        Log.d("newUserAddedInCircle", circleNameLive.getValue());
        params.put("channel", circleNameLive.getValue() + " " + inviteCodeLive.getValue());

        params.put("circleName", circleNameLive.getValue());

        Log.d("deviceToken: ", ParseInstallation.getCurrentInstallation().getDeviceToken());
        params.put("deviceToken", ParseInstallation.getCurrentInstallation().getDeviceToken());

        ParseCloud.callFunctionInBackground("newMemberAddedPush", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null) {
                    Log.d("done: ", "Push message sent!!!");
                } else {
                    // Something went wrong
                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error: ", String.valueOf(exc));

                }
            }
        });
/*
            //we need to get circle name from updated object to send notification because circle name is channel name.
            // ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");
            queryCircleName.getInBackground(updatedObject.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        String circleName = String.valueOf(object.get("circleName"));
                        Log.d("newUserAddedInCircle", circleName);
                        params.put("channel", circleName);


                        ParseCloud.callFunctionInBackground("newMemberAddedPush", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object response, ParseException exc) {
                                if (exc == null) {
                                    Log.d("done: ", "Push message sent!!!");
                                } else {
                                    // Something went wrong
                                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error: ", String.valueOf(exc));

                                }
                            }
                        });

                    }

                }
            });*/


    }


    public void storeUserCurrentLocationInServer(final Context context,
                                                 final Location location, final GoogleMap mMap) {
        this.mContext = context;
        //  map = mMap;

        Log.d("storeUserCurre ", "called");

        //store this location to let other group member know the location
        JSONArray myGioPointArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();


        if (currentUserLocation != null && location.getLatitude() == currentUserLocation.getLatitude() && location.getLongitude() == currentUserLocation.getLongitude()) {

            Log.d("storeUserCu ", "no need to save");
            return;
        }


        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    JSONObject gioPointsJsonObject = object.getJSONObject("geoPoints");

                    try {
                        JSONArray locationArray = gioPointsJsonObject.getJSONArray("userCurrentLocation");


                        JSONObject locationJsonObject = locationArray.getJSONObject(0);

                        double latitude = (double) locationJsonObject.get("latitude");
                        double longitude = (double) locationJsonObject.get("longitude");

                        if (latitude == location.getLatitude() && longitude == location.getLongitude()) {

                            Log.d("same ", "no need to save");
                            currentUserLocation = location;

                        } else {


                            myGioPointArray.put(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                            jsonObject.put("userCurrentLocation", myGioPointArray);
                            ParseUser.getCurrentUser().put("geoPoints", jsonObject);


                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        Log.d("userCurrentLocation", "saved!!!");
                                        currentUserLocation = location;
                                    } else {

                                        Log.d("error ", e.getMessage());
                                    }
                                }
                            });
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }


                }
            }
        });






       /* ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    JSONObject gioPointsJsonObject = object.getJSONObject("geoPoints");


                    try {
                        //home json array


                        JSONArray currentLocationGeoPointArray = new JSONArray();
                        currentLocationGeoPointArray.put(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                        gioPointsJsonObject.put("userCurrentLocation", currentLocationGeoPointArray);


                        object.put("geoPoints", gioPointsJsonObject);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("userCurrentLocation", ParseUser.getCurrentUser().getUsername() + "Updated!!!");

                                    //after updated ,we need to check is he in any circle,if yes,we need check circle  saved  places are equal to current location.
                                    userCurrentLocationLive.setValue(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                                    // isHeInAnyCircle(ParseUser.getCurrentUser().getObjectId());


                                } else {


                                    Log.d("error ", e.getMessage());
                                }
                            }
                        });

                    } catch (JSONException ex) {

                        ex.printStackTrace();
                        Log.e("done: ", e.getMessage());
                    }
                } else {
                    Log.e("error: ", e.getMessage());
                }

            }
        });*/

    }


    //objectId is user's ObjectId of updated location.
    //latitude,longitude are user currentLocation from Object.
    public void isHeInAnyCircle(String objectId, double latitude, double longitude) {

        //when the user location updated in User Object we need to find him in CircleName object to update his details there.
        //if his location is nearby any saved location in which he is in,we need to send notification to others.

        Log.d("isHeInAnyCircle: ", objectId);
        ParseQuery<ParseObject> circleNameQuery = new ParseQuery<ParseObject>("CircleName");
        List<String> userId = new ArrayList<>();
        userId.add(objectId);


        circleNameQuery.whereContainedIn("circleMember", userId);
        circleNameQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {


                    Log.d("he is in circle ", String.valueOf(objects.size()) + "  " + objectId);

                    for (int i = 0; i < objects.size(); i++) {

                        JSONArray myGioPointArray = objects.get(i).getJSONArray("placeArray");


                        if (myGioPointArray.length() > 0) {
                            for (int j = 0; j < myGioPointArray.length(); j++) {
                                Gson gson = new Gson();

                                String placeGeoJsonArray = null;
                                try {
                                    placeGeoJsonArray = String.valueOf(myGioPointArray.get(j));
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                                StringToJsonSerialization serialization = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);

                                ParseGeoPoint updatedLocation = new ParseGeoPoint(latitude, longitude);
                                Log.d("placeName ", serialization.getPlaceName());
                                Log.d("placeGeo ", String.valueOf(serialization.getGeoPoint()));
                                Log.d("updatedLocation ", String.valueOf(updatedLocation));

                                double distanceWithSavePlaces = updatedLocation.distanceInMilesTo(serialization.getGeoPoint());

                                Log.d("currentDistanceWith  " + serialization.getPlaceName(), String.valueOf(distanceWithSavePlaces));


                                //first we need to find distance between updated user location and saved location in particular group.
                                //if that distance is less,we need to send notification to other circle member except updated user.
                                if (distanceWithSavePlaces < 0.5) {

                                    Log.d("less distance ", "send notification");
                                    //sendArrivedNPushNotification(objectId, serialization);

                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //updatedUserId is the objectId of user in User object
    //serialization object holds placeName and GeoPoint in placName JSON Array from CircleNameObject
    private void sendArrivedNPushNotification(String updatedUserId, StringToJsonSerialization
            serialization) {


        List<String> subscribedChannals = ParseInstallation.getCurrentInstallation().getList("channels");

        //if subscribed channel contains placeName(place name is Channel name) in installation object means notification is ON for that channel.so we can send notification


        if (subscribedChannals.size() > 0 && subscribedChannals.contains(serialization.getPlaceName())) {

            Log.d("notification ", "ON");

            final HashMap<String, String> params = new HashMap<>();


            Log.d("deviceToken: ", ParseInstallation.getCurrentInstallation().getDeviceToken());
            params.put("deviceToken", ParseInstallation.getCurrentInstallation().getDeviceToken());
            /*query.notEqualTo("deviceToken", request.params.deviceToken);*/


            Log.d("usernearbyLocation: ", serialization.getPlaceName());
            params.put("userLocation", serialization.getPlaceName());

            Log.d("channal: ", serialization.getPlaceName());
            params.put("channels", serialization.getPlaceName());

            params.put("adminId", adminId);


            // ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            queryParseUser.getInBackground(updatedUserId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        updatesLocationUserName = object.getUsername();
                        Log.d("userName: ", updatesLocationUserName);
                        params.put("userName", updatesLocationUserName);

                        // Calling the cloud code function
                        ParseCloud.callFunctionInBackground("arrivedPush", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object response, ParseException exc) {
                                if (exc == null) {
                                    Log.d("done: ", "Push message sent!!!");
                                } else {
                                    // Something went wrong
                                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error: ", String.valueOf(exc));

                                }
                            }
                        });
                    }
                }
            });


        } else {

            //if subscribed channel does not have nearby place name means ,notification is off.we can not send notification to them.
            Log.d("notification", "OFF ");
        }
    }


    private String getAdminIdToSendNotification() {

        //  ParseQuery<ParseObject> circleNameQuery = new ParseQuery<ParseObject>("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());

        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    adminId = String.valueOf(objects.get(0).get("adminId"));

                    Log.d("adminId ", adminId);
                }
            }
        });

        return adminId;
    }


    public void UpdatewithCustomMarker(Context context, GoogleMap
            mMap, List<String> selectedGroup) {

        Log.d("UpdatewithCustomMarker", "called");
        map = mMap;

        // mContext = context;
        parseFilesImage.clear();
        imageUri.clear();
        markersLatLong.clear();

        bitmapArrayList.clear();


        if (selectedGroup.get(0) == null || selectedGroup.get(1) == null || selectedGroup.get(0).equals("defaultCircleName") || selectedGroup.get(1).equals("defaultInviteCode")) {
            markers.clear();
            bitmapArrayList.clear();
            circleNameLive.postValue(null);
            inviteCodeLive.postValue(null);

            Log.d("UpdatewithCustomMarker", String.valueOf(markers.size()));


            //if user left from all circle need to set saved places .

            Log.d("done:noCircle ", "called");
            // getAllPlaceGeoPoints();


            return;
        } else {

            Log.d("selectedGroup", selectedGroup.get(0));
            Log.d("inviteCodelive", selectedGroup.get(1));


            circleNameLive.postValue(selectedGroup.get(0));
            inviteCodeLive.postValue(selectedGroup.get(1));

           /* getSelectedGroupNameLiveData().postValue(selectedGroup.get(0));
            getSelectedInviteCodeLiveData().postValue(selectedGroup.get(1));*/


            ParseQuery<ParseObject> queryCircleName = new ParseQuery<ParseObject>("CircleName");
            queryCircleName.whereEqualTo("circleName", selectedGroup.get(0));
            queryCircleName.whereEqualTo("password", selectedGroup.get(1));


            queryCircleName.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {


                    //Log.d("done:objects ", String.valueOf(objects.size()));
                    if (e == null) {
                        //this object list always contains only one object because circlename and invitecode are unique for each object
                        if (objects.size() > 0) {

                            Log.d("done:objects ", "called");
                            userDetailsPojoList.clear();
                            markers.clear();

                            if (map != null) {
                                map.clear();
                                getAllCircleMember(objects);
                                getAllPlaceGeoPoints();
                            }


                            //we need to subscribe for circleName (like place name)channel so that we can get notification if someone joined or left from that circle.
                            //if user leave circle we need to unsubscribe.
                           /*     ParsePush.subscribeInBackground(selectedGroup.get(0), new SaveCallback() {
                                    @Override

                                    public void done(ParseException e) {
                                        Log.d("subscribedToChannel ", selectedGroup.get(0));
                                    }
                                });*/


                        }


                    } else if (e != null) {
                        Log.e("done:updareerror", e.getMessage());
                    }
                }
            });


        }
    }


    public void getAllCircleMember(List<ParseObject> objects) {
        Log.d("getAllCircleMember: ", "called");

        int i;
        JSONArray circleMemberArray = objects.get(0).getJSONArray("circleMember");

        if (circleMemberArray.length() > 0) {

            for (i = 0; i < circleMemberArray.length(); i++) {
                Log.d("circleMemberArrayTotal ", String.valueOf(circleMemberArray.length()));


                try {
                    String circleMemberId = String.valueOf(circleMemberArray.get(i));


                    // ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                    //  int finalI = i;


                    int finalI = i;
                    queryParseUser.getInBackground(circleMemberId, new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser groupMemeberObject, ParseException e) {
                            if (e == null) {

                                //int   j= finalI;
                                imageFile = groupMemeberObject.getParseFile("profilepicture");


                                if (imageFile != null) {

                                    int j = finalI;
                                    imageFile.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {


                                            if (e == null && data != null) {


                                                userName = groupMemeberObject.getString("username");
                                                userObjectId = groupMemeberObject.getObjectId();

                                                //this is bitmap of parse file
                                                imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                //get custom marker using parse file
                                                getCustomMarker(R.layout.custom_marker, imagebitmap);


                                                //this custom marker list will be used  when zoom in zoom out
                                                bitmapArrayList.add(updatedbitmap);


                                                getLocationLatLong(groupMemeberObject, "userCurrentLocation");


                                                if (locationLatlong != null) {


                                                    Log.d("done:Addedmarker", groupMemeberObject.getObjectId());

                                                    //get current location from  json Array


                                                    userAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                                    markerOption = new MarkerOptions().position(locationLatlong).snippet(userAddress).title(userName).icon(BitmapDescriptorFactory.fromBitmap(updatedbitmap));
                                                    markerNew = map.addMarker(markerOption);
                                                    markerNew.setTag(groupMemeberObject.getObjectId());
                                                    // markerNew.showInfoWindow();
                                                    markers.add(markerNew);

                                                    getMarkersLiveInstance().setValue(markers);
                                                    ChangeCamaraPosition(mContext, markers);


                                                    Log.d("userCurrentLocation: ", String.valueOf(locationLatlong));
                                                    //get address from current location
                                                    currentLocationAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                                    UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                                    userDetailsPojo.setUserName(userName);
                                                    userDetailsPojo.setUserObjectId(userObjectId);


                                                    userDetailsPojo.setUserDp(imagebitmap);

                                                    userDetailsPojo.setLatitude(locationLatlong.latitude);
                                                    userDetailsPojo.setLongitude(locationLatlong.longitude);

                                                    userDetailsPojo.setDestinationLatitude(currentUserLocation.getLatitude());
                                                    userDetailsPojo.setDestinationLongitude(currentUserLocation.getLongitude());

                                                    locationPermission = groupMemeberObject.getString("LocationPermission");

                                                    Log.d("locationPermission", locationPermission);

                                                    if (locationPermission.equals("ON")) {
                                                        if (currentLocationAddress.isEmpty()) {
                                                           /* userDetailsPojo.setLatitude(0);
                                                            userDetailsPojo.setLongitude(0);
                                                            userDetailsPojo.setDestinationLatitude(0);
                                                            userDetailsPojo.setDestinationLongitude(0);*/
                                                            userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                                        } else {
                                                            userDetailsPojo.setUserCurrentLocation(currentLocationAddress);
                                                        }
                                                    } else if (locationPermission.equals("OFF")) {
                                                        userDetailsPojo.setLatitude(0);
                                                        userDetailsPojo.setLongitude(0);
                                                        userDetailsPojo.setDestinationLatitude(0);
                                                        userDetailsPojo.setDestinationLongitude(0);
                                                        userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                                    }


                                                    userDetailsPojoList.add(userDetailsPojo);

                                                    Log.d("j", String.valueOf(j));

                                         /*   if(j ==circleMemberArray.length()-1){


                                                Log.d("done:0", String.valueOf(userDetailsPojoList.get(0).getUserName()));*/
                                                    // Log.d("done:1", String.valueOf(userDetailsPojoList.get(1).getUserName()));
                                                    if (userDetailsPojoList.size() == circleMemberArray.length()) {
                                                        Log.d("userDetailsPojoList", String.valueOf(userDetailsPojoList.size()));
                                                        Log.d("circleMemberArray", String.valueOf(circleMemberArray.length()));
                                                        Log.d("done:0", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                                                        for (int k = 0; k < userDetailsPojoList.size(); k++) {

                                                            if (userDetailsPojoList.get(k).getUserObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                                                Log.d("done:obj", userDetailsPojoList.get(k).getUserObjectId());
                                                                userDetailsPojoList.add(0, userDetailsPojoList.get(k));
                                                                userDetailsPojoList.remove(k + 1);
                                                       /* Log.d("done:00", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                                                        Log.d("done:11", String.valueOf(userDetailsPojoList.get(1).getUserName()));*/


                                                                // getUserDetailsListLiveData().setValue(userDetailsPojoList);

                                                            }
                                                        }
                                                        // Log.d( "done.getUser", String.valueOf(getUserDetailsListLiveData().getValue().size()));
                                                        getUserDetailsListLiveData().setValue(userDetailsPojoList);

                                                        // getUserDetailsListLiveData().getValue().clear();

                                                    }

                                                    // userDetailsListLiveData.setValue(userDetailsPojoList);


                                                   /* userAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                                    markerOption = new MarkerOptions().position(locationLatlong).snippet(userAddress).title(userName).icon(BitmapDescriptorFactory.fromBitmap(updatedbitmap));
                                                    markerNew = map.addMarker(markerOption);
                                                    markerNew.setTag(groupMemeberObject.getObjectId());
                                                    // markerNew.showInfoWindow();
                                                    markers.add(markerNew);

                                                    getMarkersLiveInstance().setValue(markers);
                                                    ChangeCamaraPosition(mContext, markers);*/


                                                }
                                            }

                                        }
                                    });


                                } else {
                                    int j = finalI;
                                    userName = groupMemeberObject.getString("username");

                                    getCustomMarkerWithoutDp(userName);

                                    bitmapArrayList.add(updatedBitmapWithText);

                                    getLocationLatLong(groupMemeberObject, "userCurrentLocation");
                                    if (locationLatlong != null) {
                                        userAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                        markerOption = new MarkerOptions().position(locationLatlong).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(updatedBitmapWithText));
                                        markerNew = map.addMarker(markerOption);

                                        markerNew.setTag(groupMemeberObject.getObjectId());
                                        // markerNew.showInfoWindow();
                                        Log.d("donMarker", String.valueOf(markerNew.getTag()));
                                        markers.add(markerNew);

                                        getMarkersLiveInstance().setValue(markers);

                                        ChangeCamaraPosition(mContext, markers);


                                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                                        userObjectId = groupMemeberObject.getObjectId();

                                       /* getLocationLatLong(groupMemeberObject, "userCurrentLocation");
                                        if (locationLatlong != null) {*/
                                        currentLocationAddress = Utilities.getAddressFromLocation(mContext, locationLatlong.latitude, locationLatlong.longitude);

                                        UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                        userDetailsPojo.setUserName(userName);
                                        userDetailsPojo.setUserObjectId(userObjectId);

                                        userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);

                                        userDetailsPojo.setLatitude(locationLatlong.latitude);
                                        userDetailsPojo.setLongitude(locationLatlong.longitude);


                                        userDetailsPojo.setDestinationLatitude(currentUserLocation.getLatitude());
                                        userDetailsPojo.setDestinationLongitude(currentUserLocation.getLongitude());

                                        locationPermission = groupMemeberObject.getString("LocationPermission");

                                        Log.d("locationPermission", locationPermission);

                                        if (locationPermission.equals("ON")) {
                                            if (currentLocationAddress.isEmpty()) {
                                                 /*   userDetailsPojo.setLatitude(0);
                                                    userDetailsPojo.setLongitude(0);
                                                    userDetailsPojo.setDestinationLatitude(0);
                                                    userDetailsPojo.setDestinationLongitude(0);*/
                                                userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                            } else {
                                                userDetailsPojo.setUserCurrentLocation(currentLocationAddress);
                                            }
                                        } else if (locationPermission.equals("OFF")) {
                                            userDetailsPojo.setLatitude(0);
                                            userDetailsPojo.setLongitude(0);
                                            userDetailsPojo.setDestinationLatitude(0);
                                            userDetailsPojo.setDestinationLongitude(0);
                                            userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                        }


                                        userDetailsPojoList.add(userDetailsPojo);
                                        //  userDetailsListLiveData.setValue(userDetailsPojoList);
                                        // getUserDetailsListLiveData().setValue(userDetailsPojoList);

                                        Log.d("j", String.valueOf(j));

                                         /*   if(j ==circleMemberArray.length()-1){


                                                Log.d("done:0", String.valueOf(userDetailsPojoList.get(0).getUserName()));*/
                                        // Log.d("done:1", String.valueOf(userDetailsPojoList.get(1).getUserName()));
                                        if (userDetailsPojoList.size() == circleMemberArray.length()) {
                                            Log.d("userDetailsPojoList", String.valueOf(userDetailsPojoList.size()));
                                            Log.d("circleMemberArray", String.valueOf(circleMemberArray.length()));
                                            Log.d("done:0", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                                            for (int k = 0; k < userDetailsPojoList.size(); k++) {

                                                if (userDetailsPojoList.get(k).getUserObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                                    Log.d("done:obj", userDetailsPojoList.get(k).getUserObjectId());
                                                    userDetailsPojoList.add(0, userDetailsPojoList.get(k));
                                                    userDetailsPojoList.remove(k + 1);
                                                       /* Log.d("done:00", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                                                        Log.d("done:11", String.valueOf(userDetailsPojoList.get(1).getUserName()));*/
                                                    // getUserDetailsListLiveData().setValue(userDetailsPojoList);

                                                }
                                            }
                                            // Log.d( "done.getUserrr", String.valueOf(getUserDetailsListLiveData().getValue().size()));

                                            getUserDetailsListLiveData().setValue(userDetailsPojoList);
                                        }
                                    }
                                }
                            }


                        }

                    });
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }


    }


    public Bitmap getCustomMarker(int LayoutForMarker, Bitmap bitmapImageFromServer) {

//if user have Dp we need to use that DP to create custom  Marker to put it on Map.
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        customMarkerLayoutWithImage = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(LayoutForMarker, null);

        imageViewDp = (CircleImageView) customMarkerLayoutWithImage.findViewById(R.id.user_dp);
        imageViewDpBackground = customMarkerLayoutWithImage.findViewById(R.id.user_dp_background);


        imageViewDp.setImageBitmap(bitmapImageFromServer);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        customMarkerLayoutWithImage.setLayoutParams(new ViewGroup.LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT));
        customMarkerLayoutWithImage.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        customMarkerLayoutWithImage.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        customMarkerLayoutWithImage.buildDrawingCache();
        updatedbitmap = Bitmap.createBitmap(customMarkerLayoutWithImage.getMeasuredWidth(), customMarkerLayoutWithImage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(updatedbitmap);
        customMarkerLayoutWithImage.draw(canvas);


        return updatedbitmap;
    }

    public Bitmap getCustomMarkerWithBus() {
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        customMarkerLayoutWithImage = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_with_bus, null);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        customMarkerLayoutWithImage.setLayoutParams(new ViewGroup.LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT));
        customMarkerLayoutWithImage.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        customMarkerLayoutWithImage.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        customMarkerLayoutWithImage.buildDrawingCache();
        updatedbitmap = Bitmap.createBitmap(customMarkerLayoutWithImage.getMeasuredWidth(), customMarkerLayoutWithImage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(updatedbitmap);
        customMarkerLayoutWithImage.draw(canvas);


        return updatedbitmap;

    }

    public void setZoomLevelForMarker(float zoomLevelForMarker) {
        //when zoom in we need to minimize marker size.when zoom out need to maximize marker size.
        //zoom leve 1 is for continent,so we need to show big marker when zoom leve 1.

        resizedMarkerwidth = 230;
        resizedMarkerHeight = 230;


        switch ((int) zoomLevelForMarker) {
            case 1:
                resizedMarkerwidth = 230;
                resizedMarkerHeight = 230;


                break;

            case 2:


                resizedMarkerwidth = resizedMarkerwidth - 3;
                resizedMarkerHeight = resizedMarkerwidth;
                break;

            case 3:

                resizedMarkerwidth = resizedMarkerwidth - 6;
                resizedMarkerHeight = resizedMarkerwidth;
                break;


            case 4:
                resizedMarkerwidth = resizedMarkerwidth - 9;
                resizedMarkerHeight = resizedMarkerwidth;

                break;

            case 5:
                resizedMarkerwidth = resizedMarkerwidth - 12;
                resizedMarkerHeight = resizedMarkerwidth;

                break;


            case 6:
                resizedMarkerwidth = resizedMarkerwidth - 15;
                resizedMarkerHeight = resizedMarkerwidth;

                break;

            case 7:

                resizedMarkerwidth = resizedMarkerwidth - 18;
                resizedMarkerHeight = resizedMarkerwidth;

                break;

            case 8:
                resizedMarkerwidth = resizedMarkerwidth - 21;
                resizedMarkerHeight = resizedMarkerwidth;
                break;

            case 9:
                resizedMarkerwidth = resizedMarkerwidth - 24;
                resizedMarkerHeight = resizedMarkerwidth;
                break;

            case 10:
                resizedMarkerwidth = resizedMarkerwidth - 27;
                resizedMarkerHeight = resizedMarkerwidth;
                break;

            case 11:
                resizedMarkerwidth = resizedMarkerwidth - 30;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 12:
                resizedMarkerwidth = resizedMarkerwidth - 33;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 13:
                resizedMarkerwidth = resizedMarkerwidth - 36;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 14:
                resizedMarkerwidth = resizedMarkerwidth - 39;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 15:
                resizedMarkerwidth = resizedMarkerwidth - 42;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 16:
                resizedMarkerwidth = resizedMarkerwidth - 45;
                height = resizedMarkerwidth;
                break;
            case 17:
                resizedMarkerwidth = resizedMarkerwidth - 48;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 18:
                resizedMarkerwidth = resizedMarkerwidth - 51;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 19:
                resizedMarkerwidth = resizedMarkerwidth - 54;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            case 20:
                resizedMarkerwidth = resizedMarkerwidth - 57;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
            default:
                resizedMarkerwidth = 200;
                resizedMarkerHeight = resizedMarkerwidth;
                break;
        }


        if (isItriderMapFragment) {


            for (int k = 0; k < getRiderMarkerInstance().getValue().size(); k++) {


                if (riderMarkerList.size() > 0 && getRiderMarkerInstance().getValue().size() > 0) {
                    resizeMarker(riderMarkerList.get(k), resizedMarkerwidth, resizedMarkerHeight);
                    getRiderMarkerInstance().getValue().get(k).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                }

            }

            Log.d("setZoomLevelForMarker: ", String.valueOf(getRiderMarkerInstance().getValue().size()));
            Log.d("ZoomLevelForMarker: ", String.valueOf(riderMarkerList.size()));
        } else if (!isItriderMapFragment) {
            for (int k = 0; k < markers.size(); k++) {


                if (bitmapArrayList.size() > 0 && markers.size() > 0) {
                    resizeMarker(bitmapArrayList.get(k), resizedMarkerwidth, resizedMarkerHeight);
                    markers.get(k).setIcon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                }

            }

        }
    }

    public Bitmap resizeMarker(Bitmap bitmapDp, int width, int hight) {
        //if new user is added ,we need to add that user's marker and need to resize all markers based on zoom level.

        resizedBitmap = Bitmap.createScaledBitmap(bitmapDp, width, hight, false);

        return resizedBitmap;

    }

    public Bitmap getCustomMarkerWithoutDp(String firstLetterOfName) {

        //if user don't have Dp ,we need to use first letter of user's first name to create custom marker.
        Log.i("FirstLetterOfUser ", String.valueOf(firstLetterToUpperCase));
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        markerLayoutWithoutImage = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_with_text, null);

        tvFirstLetter = markerLayoutWithoutImage.findViewById(R.id.first_letter);

        String firstLetterOfUSer = String.valueOf(firstLetterOfName.charAt(0));

        firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();

        tvFirstLetter.setText(firstLetterToUpperCase);


        DisplayMetrics displayMetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        markerLayoutWithoutImage.setLayoutParams(new ViewGroup.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerLayoutWithoutImage.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerLayoutWithoutImage.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerLayoutWithoutImage.buildDrawingCache();
        updatedBitmapWithText = Bitmap.createBitmap(markerLayoutWithoutImage.getMeasuredWidth(), markerLayoutWithoutImage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmapWithText);
        markerLayoutWithoutImage.draw(canvas);
        return updatedBitmapWithText;


    }


    public void ChangeCamaraPosition(Context context, List<Marker> markersList) {

        //we need to zoom in or zoom out camera based upon number of user in current circle.
    /*    if (markers.size() > 0) {

            builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            bounds = builder.build();

            width = context.getResources().getDisplayMetrics().widthPixels;
            height = context.getResources().getDisplayMetrics().heightPixels;
            padding = (int) (width * 0.20);

            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


            map.animateCamera(cu);


        }*/
        if (markersList.size() > 0) {

            Log.d("markersList", String.valueOf(markersList.size()));

            builder = new LatLngBounds.Builder();
            for (Marker marker : markersList) {
                builder.include(marker.getPosition());
                Log.d("markerslocation", String.valueOf(marker.getPosition()));
            }
            bounds = builder.build();

            width = context.getResources().getDisplayMetrics().widthPixels;
            height = context.getResources().getDisplayMetrics().heightPixels;
            padding = (int) (width * 0.20);

            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            if (isItriderMapFragment || isItDriverMapFragment) {
                mRiderMap.animateCamera(cu);

                Log.d("zoom", String.valueOf(mRiderMap.getCameraPosition().zoom));


            } else if (!isItriderMapFragment) {

                Log.d("ChangeCamaraPosition: ", "called");
                map.animateCamera(cu);
            }


        }
    }

    public LatLng getLocationLatLong(ParseObject object, String currentLocation) {


        //using this method we can get user's current location from User Object.


        try {

         /*   if(object.getJSONObject("geoPoints").isNull("currentLocation")) {
                return null ;
            }*/


            Log.d("getLocationLatLong: ", "called");
            try {
                if (object.getJSONObject("geoPoints").length() != 0) {
                    //  if ((JSONArray) object.getJSONObject("geoPoints").get(currentLocation) != null) {
                    JSONArray locationArray = (JSONArray) object.getJSONObject("geoPoints").get(currentLocation);


                    if (locationArray == null) {
                        return null;
                    }
                    JSONObject locationJsonObject = locationArray.getJSONObject(0);

                    double latitude = (double) locationJsonObject.get("latitude");
                    double longitude = (double) locationJsonObject.get("longitude");

                    locationLatlong = new LatLng(latitude, longitude);

                    Log.d("getLocationLatLong: ", String.valueOf(locationLatlong));


                    if (updateUserId.getValue() != null) {
                        Log.d("updateUserId: ", updateUserId.getValue());

                        //   isHeInAnyCircle(updateUserId.getValue(), latitude, longitude);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }


        return locationLatlong;

    }

    public LiveData<List<UserDetailsPojo>> getUserDetailsLiveData() {

        return getUserDetailsListLiveData();


    }

    public void createNewGroup(String circleName, String inviteCode, CreateJoinViewModel createJoinViewModel) {
        Log.d("createNewGroupcalled: ", circleName + " " + inviteCode);


        SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("com.androidapps.sharelocation.defaultCircleName", circleNameLive.getValue());
        edit.putString("com.androidapps.sharelocation.inviteCode", inviteCodeLive.getValue());
        edit.apply();
//using this method user can create new circle and can share their code with others to join them.
        // ParseQuery<ParseUser> queryCircleName = ParseUser.getQuery();

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null && object != null) {


                    try {

                        JSONObject newJsonObject = new JSONObject();
                        JSONArray newJsonArray = new JSONArray();
                        /*String inviteCode = Utilities.getUniqueId();
                        inviteCode = inviteCode.substring(0, 7);*/

                        newJsonObject.put("circleName", circleName);
                        newJsonObject.put("inviteCode", inviteCode);


                        JSONArray circleNameJsonArrayUpdate = object.getJSONArray("circleNameJsonArray");

                        /*if user create circle for for first time,circleNameJsonArrayUpdate value will be null,so we need to create new json object to put them in array*/
                        if (circleNameJsonArrayUpdate == null || circleNameJsonArrayUpdate.length() == 0) {

                            Log.d("circleNameJsonArray", "null");
                            newJsonArray.put(newJsonObject);
                            object.put("circleNameJsonArray", newJsonArray);
                        } else if (circleNameJsonArrayUpdate.length() > 0) {

                            circleNameJsonArrayUpdate.put(newJsonObject);

                            object.put("circleNameJsonArray", circleNameJsonArrayUpdate);
                        }

                        object.saveInBackground();


//after create the circle name,we need to add current user id with circle name to the circleName object
                        AddToCircleNameClass(circleName, inviteCode, ParseUser.getCurrentUser().getObjectId());


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else if (e != null) {
                    Log.d("done:code", String.valueOf(e.getCode()));

                    if (e.getCode() == 100) {

                        //he connection to the Parse servers failed.
                        createJoinViewModel.getSelectedFragmentInstance().setValue("NoNetwork");
                    }
                }
            }
        });


    }

    public void createNewGroup(String circleName, String inviteCode) {
        Log.d("createNewGroup: ", circleNameLive.getValue() + " " + inviteCodeLive.getValue());


        SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("com.androidapps.sharelocation.defaultCircleName", circleNameLive.getValue());
        edit.putString("com.androidapps.sharelocation.inviteCode", inviteCodeLive.getValue());
        edit.apply();
//using this method user can create new circle and can share their code with others to join them.
        // ParseQuery<ParseUser> queryCircleName = ParseUser.getQuery();

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null && object != null) {


                    try {

                        JSONObject newJsonObject = new JSONObject();
                        JSONArray newJsonArray = new JSONArray();
                        /*String inviteCode = Utilities.getUniqueId();
                        inviteCode = inviteCode.substring(0, 7);*/

                        newJsonObject.put("circleName", circleName);
                        newJsonObject.put("inviteCode", inviteCode);


                        JSONArray circleNameJsonArrayUpdate = object.getJSONArray("circleNameJsonArray");

                        /*if user create circle for for first time,circleNameJsonArrayUpdate value will be null,so we need to create new json object to put them in array*/
                        if (circleNameJsonArrayUpdate == null || circleNameJsonArrayUpdate.length() == 0) {

                            Log.d("circleNameJsonArray", "null");
                            newJsonArray.put(newJsonObject);
                            object.put("circleNameJsonArray", newJsonArray);
                        } else if (circleNameJsonArrayUpdate.length() > 0) {

                            circleNameJsonArrayUpdate.put(newJsonObject);

                            object.put("circleNameJsonArray", circleNameJsonArrayUpdate);
                        }

                        object.saveInBackground();


//after create the circle name,we need to add current user id with circle name to the circleName object
                        AddToCircleNameClass(circleName, inviteCode, ParseUser.getCurrentUser().getObjectId());


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else if (e != null) {
                    Log.d("done:code", String.valueOf(e.getCode()));

                  /*  if(e.getCode()==100){

                        //he connection to the Parse servers failed.
                        createJoinViewModel.getSelectedFragmentInstance().setValue("NoNetwork");
                    }*/
                }
            }
        });


    }


    private void AddToCircleNameClass(String circleName, String inviteCode, String objectId) {


        ParseObject circleNameObject = new ParseObject("CircleName");

        circleNameObject.put("circleName", circleName);
        circleNameObject.put("password", inviteCode);


        //to send notification ,we need admin detail.
        circleNameObject.put("adminId", objectId);


        JSONArray jsonArrayMember = new JSONArray();
        jsonArrayMember.put(objectId);

        JSONArray jsonArrayPlaces = new JSONArray();
        JSONArray jsonArrayGeoFence = new JSONArray();

        circleNameObject.put("circleMember", jsonArrayMember);
        circleNameObject.put("placeArray", jsonArrayPlaces);
        circleNameObject.put("GeoFence", jsonArrayGeoFence);


        circleNameObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Log.d("done: ", "new circle create");


                    //after circle crated,we need to show currently created circle in home page.
                    getSelectedInviteCodeLiveData().setValue(inviteCode);
                    getSelectedGroupNameLiveData().setValue(circleName);

                    circleNameLive.setValue(circleName);
                    inviteCodeLive.setValue(inviteCode);

                    ParsePush.subscribeInBackground(circleName + " " + inviteCode, new SaveCallback() {
                        @Override

                        public void done(ParseException e) {
                            Log.d("done:circle", circleNameLive.getValue() + " " + inviteCodeLive.getValue());
                            Log.d("subscribedToChannel ", circleName + " " + inviteCode);
                        }
                    });

                }
            }
        });

    }


    public void joinWithCircle(String inviteCode) {

        //using inviteCode user can join with already existing circle.

        existingMemberDetailList.clear();
        // getIsAlreadyInCircleInstance().setValue(true);
        //   ParseQuery<ParseObject> query = ParseQuery.getQuery("CircleName");

        queryCircleName.whereEqualTo("password", inviteCode);
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    Log.d("circleNameObject", String.valueOf(objects.size()));
                 /*   if (objects.size() == 0) {
                        //if password is wrong we will get 0 result,so need show the toast that entered password is wrong.

                      *//*  Toast.makeText(mContext, "Please provide valid password.", Toast.LENGTH_LONG).show();
                        return;*//*

                      //  isAlreadyInCircle.setValue(true);
                        return;

                    }*/

                    //this list should contain only one object for the given password.

                    String circleNameForGivenCode = String.valueOf(objects.get(0).get("circleName"));


                    JSONArray circleMemberArray = objects.get(0).getJSONArray("circleMember");

                    if (circleMemberArray.length() == 0) {

                        Log.d("circleMember", "No one in that circle");
                        Toast.makeText(mContext, "No one in that circle!", Toast.LENGTH_SHORT).show();

                        //  existingMemberDetailLive.setValue(existingMemberDetailList);
                      /*  getIsValidInviteCodeInstance().setValue(false);
                        getIsAlreadyInCircleInstance().setValue(true);*/

                       /* Intent intent = new Intent(mContext, HomaPageActivity.class);
                        mContext.startActivity(intent);*/
                        getIsAlreadyInCircleInstance().setValue(true);
                        return;


                    }

                    for (int i = 0; i < circleMemberArray.length(); i++) {

                        //need to show all existing member in that circle before join.

                        try {
                            String existingMember = String.valueOf(circleMemberArray.get(i));
                            Log.d("existingMemberId: ", existingMember);


                            if (existingMember.equals(ParseUser.getCurrentUser().getObjectId())) {

                                Toast.makeText(mContext, "You already in that circle!!", Toast.LENGTH_SHORT).show();

                                getIsAlreadyInCircleInstance().setValue(true);

                                return;


                            } else {

                                getExistingMemberDetail(existingMember, circleNameForGivenCode, inviteCode);
                                getIsAlreadyInCircleInstance().setValue(false);
                            }


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    }


                } else if (e != null && e.getCode() == 100) {
                    Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public LiveData<List<UserDetailsPojo>> getExistingMemberDetail(String
                                                                           existingMemberId, String circleName, String inviteCode) {


        circleNameLive.setValue(circleName);
        inviteCodeLive.setValue(inviteCode);
        //ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        queryParseUser.getInBackground(existingMemberId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    circleNameLive.setValue(circleName);
                    inviteCodeLive.setValue(inviteCode);
                    UserDetailsPojo userDetailsPojo = new UserDetailsPojo();

                    userDetailsPojo.setUserName(object.getUsername());
                    userDetailsPojo.setCircleName(circleName);
                    userDetailsPojo.setInviteCode(inviteCode);


                    imageFile = object.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                    userDetailsPojo.setUserDp(imagebitmap);

                                    existingMemberDetailList.add(userDetailsPojo);
                                    existingMemberDetailLive.setValue(existingMemberDetailList);


                                }
                            }
                        });
                    } else {

                        userName = object.getString("username");

                        String firstLetterOfUSer = String.valueOf(userName.charAt(0));

                        firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();

                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                        userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);
                        existingMemberDetailList.add(userDetailsPojo);
                        existingMemberDetailLive.setValue(existingMemberDetailList);


                    }
                }
            }
        });


        return existingMemberDetailLive;

    }


    public LiveData<UserDetailsPojo> getAdminDetailsOfCircle
            (List<String> objectIdAndCircleName) {
        String objectId = objectIdAndCircleName.get(0);
        String circleName = objectIdAndCircleName.get(1);
        String inviteCode = objectIdAndCircleName.get(2);
        //  ParseQuery<ParseUser> query = ParseUser.getQuery();
        queryParseUser.getInBackground(objectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser objectAdmin, ParseException e) {
                if (e == null) {
                    UserDetailsPojo userDetailsPojo = new UserDetailsPojo();

                    userDetailsPojo.setUserName(objectAdmin.getUsername());
                    userDetailsPojo.setCircleName(circleName);
                    userDetailsPojo.setInviteCode(inviteCode);
                    userDetailsPojo.setObjectId(objectId);


                    imageFile = objectAdmin.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    userName = objectAdmin.getString("username");


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                    userDetailsPojo.setUserDp(imagebitmap);

                                    userDetailsPojoForAdmin.setValue(userDetailsPojo);


                                }
                            }
                        });
                    } else {

                        userName = objectAdmin.getString("username");

                        String firstLetterOfUSer = String.valueOf(userName.charAt(0));

                        firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();

                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                        userObjectId = objectAdmin.getObjectId();

                        userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);


                        userDetailsPojoForAdmin.setValue(userDetailsPojo);


                    }
                }
            }
        });

        return userDetailsPojoForAdmin;
    }

    public void join() {


        // ParseQuery<ParseObject> query = ParseQuery.getQuery("CircleName");

        Log.d("join:cir", circleNameLive.getValue() + " " + inviteCodeLive.getValue());
        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());


        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                JSONArray circleMemberArray = objects.get(0).getJSONArray("circleMember");
                circleMemberArray.put(ParseUser.getCurrentUser().getObjectId());

                objects.get(0).put("circleMember", circleMemberArray);
                objects.get(0).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {


                            //need to add current circle to the circle name dialog recyclerview.

                            AddCircleNameToDialog(circleNameLive.getValue(), inviteCodeLive.getValue());
                        }

                       /* else if(e!=null){
                            Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
            }
        });

    }

    private void AddCircleNameToDialog(String circleName, String inviteCode) {

        // ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    try {
                        JSONArray circleNameJsonArray = object.getJSONArray("circleNameJsonArray");

                        if (circleNameJsonArray == null || circleNameJsonArray.length() == 0) {

                            // this block triggered ,when the user  create circle or join with circle at first time .
                            Log.d("circleName ", "newAdded");
                            JSONArray newCircleNameJsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("circleName", circleName);
                            jsonObject.put("inviteCode", inviteCode);

                            newCircleNameJsonArray.put(jsonObject);
                            object.put("circleNameJsonArray", newCircleNameJsonArray);

                        } else if (circleNameJsonArray.length() > 0) {
                            Log.d("circleName ", "existing");
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("circleName", circleName);
                            jsonObject.put("inviteCode", inviteCode);

                            circleNameJsonArray.put(jsonObject);
                            object.put("circleNameJsonArray", circleNameJsonArray);
                        }


                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Log.d("circleName", "Added to dialog" + circleName);
                                    //need to show currently joined group in home page,by changing selected value live data,observer will observe and change the home page with new member.

                                    getSelectedInviteCodeLiveData().setValue(inviteCode);
                                    getSelectedGroupNameLiveData().setValue(circleName);

                                    circleNameLive.setValue(circleName);
                                    inviteCodeLive.setValue(inviteCode);


                                    //after joined in circle need to set existing member to null
                                    existingMemberDetailList.clear();
                                    existingMemberDetailLive.setValue(existingMemberDetailList);
                                    SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("com.androidapps.sharelocation.defaultCircleName", circleName);
                                    editor.putString("com.androidapps.sharelocation.inviteCode", inviteCode);
                                    editor.apply();


                                    ParsePush.subscribeInBackground(circleName + " " + inviteCode, new SaveCallback() {
                                        @Override

                                        public void done(ParseException e) {
                                            Log.d("subscribedToChannel ", circleName + " " + inviteCode);
                                            sendNewMemberAddedNotification(ParseUser.getCurrentUser().getUsername());
                                        }
                                    });


                                }
                            }
                        });

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }


                }
            }
        });

    }

    public void storeParseFile(ParseFile parseFile) {
//store circle's dp in circleName Class
        //  ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CircleName");
        queryCircleName.whereEqualTo("circleName", getSelectedGroupNameLiveData().getValue());
        queryCircleName.whereEqualTo("password", getSelectedInviteCodeLiveData().getValue());


        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {


                    objects.get(0).put("circleDp", parseFile);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Log.d("done: ", "circle DP saved");
                            }
                        }
                    });
                } else {
                    Log.e("e", e.getMessage());
                }
            }
        });

    }

    public MutableLiveData<List<UserDetailsPojo>> getAllCircleName(Context context) {

        Log.d("getAllCircleName: ", "called1");
        //  mContext = context;
        circleNameDpList = new ArrayList<UserDetailsPojo>();
        circleNameDpList.clear();
        //  ParseQuery<ParseUser> queryCircleName = ParseUser.getQuery();
        // circleNameAndDpLive.setValue(null);
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                Log.d("getAllCircleName: ", "called2");

                if (e == null && object != null) {

                    JSONArray circleNameJsonArray = object.getJSONArray("circleNameJsonArray");
                    // Log.i("circleNameJsonArray: ", String.valueOf(circleNameJsonArray.length()));

                    Log.d("getAllCircleName: ", "called3");


                    if (circleNameJsonArray != null && circleNameJsonArray.length() > 0) {

                        for (int i = 0; i < circleNameJsonArray.length(); i++) {

                            try {


                                String circleName = circleNameJsonArray.getJSONObject(i).getString("circleName");
                                String inviteCode = circleNameJsonArray.getJSONObject(i).getString("inviteCode");

                                //   ParseQuery<ParseObject> query = ParseQuery.getQuery("CircleName");
                                queryCircleName.whereEqualTo("circleName", circleName);
                                queryCircleName.whereEqualTo("password", inviteCode);
                                queryCircleName.addAscendingOrder("createdAt");

                                queryCircleName.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        Log.d("getAllCircleName: ", circleName);
                                        if (e == null && objects.size() > 0) {


                                            imageFile = objects.get(0).getParseFile("circleDp");
                                            if (imageFile != null) {


                                                imageFile.getDataInBackground(new GetDataCallback() {
                                                    @Override
                                                    public void done(byte[] data, ParseException e) {


                                                        if (e == null && data != null) {
                                                            UserDetailsPojo userDetailsPojo = new UserDetailsPojo();


                                                            //this is bitmap of parse file
                                                            imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                            userDetailsPojo.setCircleName(circleName);
                                                            userDetailsPojo.setInviteCode(inviteCode);
                                                            userDetailsPojo.setUserDp(imagebitmap);

                                                            circleNameDpList.add(userDetailsPojo);

                                                            Log.d("done:size ", String.valueOf(circleNameDpList.size()));

                                                            circleNameAndDpLive.setValue(circleNameDpList);
                                                        }
                                                    }
                                                });
                                            } else {

                                                String circleName = objects.get(0).getString("circleName");

                                                String firstLetterOfUSer = String.valueOf(circleName.charAt(0));

                                                firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();

                                                dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(context, firstLetterToUpperCase);
                                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();


                                                userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);
                                                userDetailsPojo.setCircleName(circleName);
                                                userDetailsPojo.setInviteCode(inviteCode);

                                                circleNameDpList.add(userDetailsPojo);

                                                Log.d("done:size ", String.valueOf(circleNameDpList.size()));

                                                circleNameAndDpLive.setValue(circleNameDpList);


                                            }
                                            Log.d("circleNameDpList: ", String.valueOf(circleNameDpList.size()));

                                            // circleNameAndDpLive.setValue(circleNameDpList);
                                        }


                                    }


                                });


                            } catch (
                                    JSONException ex) {
                                ex.printStackTrace();
                            }

                        }


                    } else if (circleNameJsonArray != null && circleNameJsonArray.length() == 0) {

                        Log.d("circleNameJsonArray", "empty");
                        circleNameDpList.clear();

                        circleNameAndDpLive.setValue(circleNameDpList);
                    }
                }
            }
        });


//         circleNameAndDpLiveReturn.setValue(circleNameAndDpLive.getValue());

        return circleNameAndDpLive;
    }

    public void savePlaceGeoPointInServer(String addressTitle, double selectedAddressLatitude,
                                          double selectedAddressLongitude) {


        //  ParseQuery<ParseObject> circleNameQuery = ParseQuery.getQuery("CircleName");
        // Log.d("savePlace", circleNameLive.getValue());
        SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);

        String preferenceCircleName = sharedPref.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
        String preferenceInviteCode = sharedPref.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");


        if (preferenceCircleName.equals(null) || preferenceInviteCode.equals(null))
            return;
        queryCircleName.whereEqualTo("circleName", preferenceCircleName);
        queryCircleName.whereEqualTo("password", preferenceInviteCode);
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                //we will get only one object for given circle name and invite code
                if (e == null && objects.size() == 1) {
                    Log.d("savePlace", "got object");
                    ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryObject.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {

                            StringToJsonSerialization serialization = new StringToJsonSerialization();
                            serialization.setPlaceName(addressTitle);
                            serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);

                            //by default notification is OFF when first time added the place

                            Gson gson = new Gson();
                            String json = gson.toJson(serialization);

                            JSONArray myGioPointArray = object.getJSONArray("placeArray");
                            myGioPointArray.put(json);


                            object.put("placeArray", myGioPointArray);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        Log.d("done: ", "place Saved");


                                        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");

/*

                                        for (int j = 0; j < subscribedChannels.size(); j++) {


                                            String currentPlace = addressTitle + " " + circleNameLive.getValue() + inviteCodeLive.getValue();
                                            Log.d( "currentPlace",currentPlace);
                                            Log.d("subscribedChanneeGeo ", String.valueOf(subscribedChannels.get(j)));
                                            if (currentPlace.equals(subscribedChannels.get(j))) {
                                                Log.d( "done: ","same");
                                                AddToGeoFenceList(currentPlace , selectedAddressLatitude, selectedAddressLongitude);

                                            }


                                        }
*/


                                        getAllPlaceGeoPoints();


                                    } else {
                                        Log.d("error: ", e.getMessage());
                                    }
                                }
                            });


                        }
                    });

                } else {

                    //to save places under particular circlenname,we need at least one circle.
                    //if there is no circle for current user,need to show Toast to create new circle.

                    Toast.makeText(mContext, "Create atleast one circle to save places!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // public MutableLiveData<List<StringToJsonSerialization>> getAllPlaceGeoPoints() {
    public void getAllPlaceGeoPoints() {

        Log.d("getAllPlaceGeoPoints", circleNameLive.getValue() + " " + inviteCodeLive.getValue());
        placeNameAndGeoPointList.clear();
        if (circleNameLive.getValue() == null || inviteCodeLive.getValue() == null) {

            Log.d("getAllPlaceGeoPoints", "null");
            getplaceNameAndGeoPointLive().setValue(placeNameAndGeoPointList);
        }

        ParseQuery<ParseObject> queryCircleName = ParseQuery.getQuery("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() == 1) {
                    Log.d("done:", ":call");
                    //ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {


                                int JsonArraySize = object.getJSONArray("placeArray").length();

                                Log.d("donejson", String.valueOf(JsonArraySize));

                                if (JsonArraySize == 0) {

                                    Log.d("done:get", "null");
                                    getplaceNameAndGeoPointLive().setValue(null);

                                } else if (JsonArraySize > 0) {

                                    for (int i = 0; i < JsonArraySize; i++) {

                                        Gson gson = new Gson();

                                        String placeGeoJsonArray = null;

                                        try {


                                            placeGeoJsonArray = String.valueOf(object.getJSONArray("placeArray").get(i));

                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        StringToJsonSerialization json = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);

                                        Log.d("getPlaceName: ", json.getPlaceName());
                                        Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));


                                        StringToJsonSerialization serialization = new StringToJsonSerialization();
                                        serialization.setPlaceName(json.getPlaceName());
                                        serialization.setGeoPoint(json.getGeoPoint().getLatitude(), json.getGeoPoint().getLongitude());
                                        serialization.setObjectId(objects.get(0).getObjectId());


                                        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");


                                        for (int j = 0; j < subscribedChannels.size(); j++) {

                                            Log.d("subscribedChannel ", String.valueOf(subscribedChannels.get(j)));
                                            String currentPlace = json.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue();
                                            if (currentPlace.equals(subscribedChannels.get(j))) {

                                                Log.d("setNotificationOn", json.getPlaceName() + " notification ON");
                                                serialization.setNotificationOn(true);
                                            }


                                        }

                                        placeNameAndGeoPointList.add(serialization);
                                        getplaceNameAndGeoPointLive().setValue(placeNameAndGeoPointList);


                                    }

                                }
                            }
                        }
                    });
                }
            }
        });

        //  return placeNameAndGeoPointLive;
    }

    public MutableLiveData<ListViewAddPlaceVisibilityPojo> checkHomeAvailable
            (List<String> placeNames) {


        ListViewAddPlaceVisibilityPojo visibilityPojo = new ListViewAddPlaceVisibilityPojo();
        MutableLiveData<ListViewAddPlaceVisibilityPojo> listViewAddPlaceVisibilityPojo = new MutableLiveData<>();

        for (int i = 0; i < placeNames.size(); i++) {

            Log.d("checkHomeAvailabe: ", String.valueOf(placeNames.get(i)));


            //if the saved place has home we should not show "Add home" else we need to show that.
            if (placeNames.get(i).toLowerCase().equals("home")) {


                visibilityPojo.setHomeAvailable(true);


            } else if (placeNames.get(i).toLowerCase().equals("work")) {

                visibilityPojo.setWorkAvailable(true);

            } else if (placeNames.get(i).toLowerCase().equals("grocery")) {

                visibilityPojo.setGroceryAvailable(true);

            } else if (placeNames.get(i).toLowerCase().equals("school")) {

                visibilityPojo.setSchoolAvailable(true);

            }


        }

        listViewAddPlaceVisibilityPojo.setValue(visibilityPojo);

        Log.d("groceryvisibility: ", String.valueOf(listViewAddPlaceVisibilityPojo.getValue().isGroceryAvailable));
        Log.d("homevisibility: ", String.valueOf(listViewAddPlaceVisibilityPojo.getValue().isHomeAvailable));
        Log.d("schoolvisibility: ", String.valueOf(listViewAddPlaceVisibilityPojo.getValue().isSchoolAvailable));
        Log.d("workvisibility: ", String.valueOf(listViewAddPlaceVisibilityPojo.getValue().isWorkAvailable));


        return listViewAddPlaceVisibilityPojo;
    }


    public void updateAddress(String addressTitle, double selectedAddressLatitude,
                              double selectedAddressLongitude, String postionToChange) {
        //  ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() == 1) {

                    // ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                Log.d("done: ", "going to get ");


                                Gson gson = new Gson();
                                String placeGeoJsonArray = null;
                                try {

                                    Log.d("positionToChange: ", postionToChange);
                                    placeGeoJsonArray = String.valueOf(object.getJSONArray("placeArray").get(Integer.parseInt(postionToChange)));


                                    StringToJsonSerialization json = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);
                                    String geofenceId = json.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue();
                                    Log.d("done:geofenceId", geofenceId);
                                    Log.d("getPlaceName:toUpdate ", json.getPlaceName());
                                    Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));

                                    //update address

                                    List<String> unRegisterGeofencIdeFromPhone = new ArrayList<>();
                                    unRegisterGeofencIdeFromPhone.add(geofenceId);

                                    unRegisterGeoFences(unRegisterGeofencIdeFromPhone);


                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }


                                StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setPlaceName(addressTitle);
                                serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);


                                String jsonUpdate = gson.toJson(serialization);


                                try {
                                    JSONArray myGioPointArray = object.getJSONArray("placeArray").put(Integer.parseInt(postionToChange), jsonUpdate);
                                    object.put("placeArray", myGioPointArray);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                Log.d("done: ", "place updated" + placeNameAndGeoPointList.size());

                                                List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");

                                                for (int j = 0; j < subscribedChannels.size(); j++) {


                                                    String currentPlace = addressTitle + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue();
                                                    Log.d("currentPlace", currentPlace);
                                                    Log.d("subscribedChanneeGeo ", String.valueOf(subscribedChannels.get(j)));
                                                    if (currentPlace.equals(subscribedChannels.get(j))) {
                                                        Log.d("done: ", "same");
                                                        updateGeoFenceInServer(currentPlace, selectedAddressLatitude, selectedAddressLongitude);

                                                    }


                                                }

                                                getAllPlaceGeoPoints();

                                            } else {
                                                Log.d("done: ", e.getMessage());
                                            }
                                        }
                                    });

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }


                            }
                        }
                    });
                }
            }
        });

    }

    public void removePlaceFromServer(String positionToRemove, String placeName) {

        // ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() == 1) {

                    //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {


                                try {


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                        Log.d("positionToRemove", positionToRemove);

                                        JSONArray jsonArray = object.getJSONArray("placeArray");

                                        jsonArray.remove(Integer.parseInt(positionToRemove));
                                        object.put("placeArray", jsonArray);


                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {

                                                    Log.d("updates ", "Place removed!");

                                                    getRemovedGeoFenceInstance().setValue(placeName + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue());

                                                    getAllPlaceGeoPoints();
                                                }
                                            }
                                        });


                                    }
                                } catch (NumberFormatException ex) {
                                    ex.printStackTrace();
                                }

                            }

                        }
                    });
                }
            }
        });
    }

    public void setNotification(boolean isOn, int position) {

        notification.setValue(isOn);


        Log.d("setNotification: ", String.valueOf(notification.getValue()));
        Log.d("Notification:position", String.valueOf(position));


        // ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("done:noti ", String.valueOf(objects.size()));
                if (e == null && objects.size() == 1) {


                    //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {

                                Gson gson = new Gson();
                                String placeGeoJsonArray = null;
                                try {

                                    Log.d("positionToChange: ", String.valueOf(position));
                                    placeGeoJsonArray = String.valueOf(object.getJSONArray("placeArray").get(position));


                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                StringToJsonSerialization json = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);

                                Log.d("getPlaceName:toUpdate ", json.getPlaceName());
                                Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));


                                StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setPlaceName(json.getPlaceName());
                                serialization.setGeoPoint(json.getGeoPoint().getLatitude(), json.getGeoPoint().getLongitude());


                                if (notification.getValue()) {

                                    //if notification on ,subscribe to channel
                                    ParsePush.subscribeInBackground(serialization.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue(), new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            Log.d("channel ", "subscribed to " + serialization.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue());
                                            // AddToGeoFenceList(serialization.getPlaceName() + " " + circleNameLive.getValue() + inviteCodeLive.getValue(), serialization.geoPoint.getLatitude(), serialization.geoPoint.getLongitude());

                                        }
                                    });
                                } else
                                //if notification off ,unsubscribe to channel

                                {


                                    ParsePush.unsubscribeInBackground(serialization.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue(), new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.d("chananl ", "unsubscribed to  " + serialization.getPlaceName() + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue());

                                            //   removeGeoFenceInServer(serialization.getPlaceName() + " " + circleNameLive.getValue() + inviteCodeLive.getValue());

                                        }
                                    });
                                }

                                getAllPlaceGeoPoints();
                            }
                        }
                    });


                }

            }
        });
    }


    public void removeGeoFenceInServer(String placeName) {
        List<String> geofenceIdToRemoveFromMobile = new ArrayList<>();
        Log.d("removeGeoFence:", placeName);


        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("done:noti ", String.valueOf(objects.size()));
                if (e == null && objects.size() == 1) {


                    //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {


                                for (int i = 0; i < objects.size(); i++) {

                                    JSONArray myGioFenceArray = objects.get(i).getJSONArray("GeoFence");


                                    if (myGioFenceArray.length() > 0) {
                                        for (int j = 0; j < myGioFenceArray.length(); j++) {
                                            Gson gson = new Gson();

                                            String GeoJsonArray = null;
                                            try {
                                                GeoJsonArray = String.valueOf(myGioFenceArray.get(j));
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }

                                            StringToJsonSerialization serialization = gson.fromJson(GeoJsonArray, StringToJsonSerialization.class);

                                            // ParseGeoPoint updatedLocation = new ParseGeoPoint(latitude, longitude);


                                            if (serialization.getPlaceName().equals(placeName)) {
                                                Log.d("placeNameGeo ", serialization.getPlaceName());
                                                Log.d("placeGeoGeo ", String.valueOf(serialization.getGeoPoint()));
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    myGioFenceArray.remove(j);
                                                    Log.d("GeoFenceRemovedAt", String.valueOf(j));


                                                    objects.get(i).put("GeoFence", myGioFenceArray);

                                                    objects.get(i).saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.d("done:object ", "updated");


                                                              /*  geofenceIdToRemoveFromMobile.add(serialization.getPlaceName());
                                                                unRegisterGeoFences(geofenceIdToRemoveFromMobile);*/
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void AddToGeoFenceList(String key, double latitude, double longitude) {

/*
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(key)

                .setCircularRegion(
                        latitude,longitude,100
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        Log.d( "AddToGeoFenceList: ", String.valueOf(geofenceList.size()));*/

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (objects.size() == 1) {

                    StringToJsonSerialization serialization = new StringToJsonSerialization();
                    serialization.setPlaceName(key);
                    serialization.setGeoPoint(latitude, longitude);

                    //by default notification is OFF when first time added the place

                    Gson gson = new Gson();
                    String json = gson.toJson(serialization);

                    JSONArray myGioFenceArray = objects.get(0).getJSONArray("GeoFence");
                    if (myGioFenceArray == null) {

                        objects.get(0).put("GeoFence", myGioFenceArray);
                    } else {
                        myGioFenceArray.put(json);
                    }

                    objects.get(0).put("GeoFence", myGioFenceArray);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Log.d("done: ", "geo fence Saved");
                                getAllGeoFences();


                            } else {
                                Log.d("error: ", e.getMessage());
                            }
                        }
                    });


                }
            }
        });
    }


    public void getAllGeoFences() {
        geofenceList = new ArrayList<>();
        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("done:noti ", String.valueOf(objects.size()));
                if (e == null && objects.size() == 1) {


                    //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {


                                for (int i = 0; i < objects.size(); i++) {

                                    JSONArray myGioFenceArray = objects.get(i).getJSONArray("GeoFence");


                                    if (myGioFenceArray.length() > 0) {
                                        for (int j = 0; j < myGioFenceArray.length(); j++) {
                                            Gson gson = new Gson();

                                            String GeoJsonArray = null;
                                            try {
                                                GeoJsonArray = String.valueOf(myGioFenceArray.get(j));
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }

                                            StringToJsonSerialization serialization = gson.fromJson(GeoJsonArray, StringToJsonSerialization.class);

                                            // ParseGeoPoint updatedLocation = new ParseGeoPoint(latitude, longitude);
                                            Log.d("placeNameGeo ", serialization.getPlaceName());
                                            Log.d("placeGeoGeo ", String.valueOf(serialization.getGeoPoint()));


                                            geofenceList.add(serialization);
                                            getGeoFenceList().setValue(geofenceList);

                                            Log.d("done:geoSixe ", String.valueOf(geofenceList.size()));

                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    public void getCurrentUserDetails() {

        //ParseQuery<ParseUser> getUserQuery = ParseUser.getQuery();
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    currentUserNameLive.setValue(object.getUsername());
                    userMailLive.setValue(object.getEmail());

                    String phoneNumberAndCountryCode = String.valueOf(object.get("phoneNumber"));
                    String[] phoneAndCode = phoneNumberAndCountryCode.split(" ");
                    String phone = phoneAndCode[1];
                    String code = phoneAndCode[0];

                    Log.d("done:code", phone + " " + code);

                    // userPhoneLive.setValue(String.valueOf(object.get("phoneNumber")));
                    userPhoneLive.setValue(phone);
                    countryCodeLive.setValue(code);

                    imageFile = object.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


                                    userDpBitmapLive.setValue(imagebitmap);

                                }
                            }
                        });
                    } else {
                        userName = object.getString("username");

                        getCustomMarkerWithoutDp(userName);
                        userDpBitmapLive.setValue(updatedBitmapWithText);
                    }
                }
            }
        });
    }


    public void leaveCircle(String circleName) {


        Log.d("leaveCircle: ", circleNameLive.getValue() + "  " + inviteCodeLive.getValue());


        // ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");

        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    Log.d("circleObject", objects.get(0).getObjectId());
                    JSONArray circleMemberArray = objects.get(0).getJSONArray("circleMember");
                    for (int i = 0; i < circleMemberArray.length(); i++) {

                        try {
                            String circleMember = String.valueOf(circleMemberArray.get(i));

                            if (circleMember.equals(ParseUser.getCurrentUser().getObjectId())) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    circleMemberArray.remove(i);
                                }

                                objects.get(0).put("circleMember", circleMemberArray);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d("current user ", "removed  from circle");

                                            //after leave the circle unsubscribe for current circleName and remob

                                            MemberLeftFromCircleNotification(ParseUser.getCurrentUser().getUsername());
                                              /*  ParsePush.unsubscribeInBackground(circleNameLive.getValue(), new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        Log.d("channel ", "unsubscribed to  " + circleNameLive.getValue());
                                                    }
                                                });*/
                                            //also need to remove from dialog as well
                                            removeCircleNameInDialog();
                                        }
                                    }
                                });


                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        });

    }

    public void removeCircleNameInDialog() {

        // ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

          /*  ParsePush.unsubscribeInBackground(circleNameLive.getValue()+inviteCodeLive.getValue(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("channel ", "unsubscribed to  " + circleNameLive.getValue()+inviteCodeLive.getValue());
                }
            });*/

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    try {


                        JSONArray circleNameJsonArray = object.getJSONArray("circleNameJsonArray");

                        for (int i = 0; i < circleNameJsonArray.length(); i++) {

                            String circleName = String.valueOf(circleNameJsonArray.getJSONObject(i).get("circleName"));
                            String inviteCode = String.valueOf(circleNameJsonArray.getJSONObject(i).get("inviteCode"));

                            if (circleName.equals(circleNameLive.getValue()) && inviteCode.equals(inviteCodeLive.getValue())) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    circleNameJsonArray.remove(i);

                                    Log.d("circle Name removed at", String.valueOf(i));
                                    object.put("circleNameJsonArray", circleNameJsonArray);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {


                                                if (circleNameJsonArray.length() > 0) {

                                                    Log.d("circleNameJsonArray: ", String.valueOf(circleNameJsonArray.length()));


                                                    //after removed current circle we need to show already existing circle

                                                    try {
                                                        Log.d("updatedCircleName: ", String.valueOf(circleNameJsonArray.getJSONObject(0).get("circleName")));
                                                        getSelectedGroupNameLiveData().setValue(String.valueOf(circleNameJsonArray.getJSONObject(0).get("circleName")));
                                                        circleNameLive.setValue(String.valueOf(circleNameJsonArray.getJSONObject(0).get("circleName")));

                                                        getSelectedInviteCodeLiveData().setValue(String.valueOf(circleNameJsonArray.getJSONObject(0).get("inviteCode")));
                                                        inviteCodeLive.setValue(String.valueOf(circleNameJsonArray.getJSONObject(0).get("inviteCode")));

                                                        SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("com.androidapps.sharelocation.defaultCircleName", getSelectedGroupNameLiveData().getValue());
                                                        editor.putString("com.androidapps.sharelocation.inviteCode", getSelectedInviteCodeLiveData().getValue());
                                                        editor.apply();

                                                        Log.d("done:leave ", circleNameLive.getValue() + " " + inviteCodeLive.getValue());


                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                } else {

                                                    //if  zero circle ,show dialog to create new circle,before that clean up all circle with current user as admin.


                                                    CleanUpCircle(ParseUser.getCurrentUser().getObjectId());

                                                    Log.d("zero circle ", "show dialog to create new circle");


                                                    SharedPreferences sharedPref = mContext.getSharedPreferences("circle", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
                                                    editor.putString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");
                                                    editor.apply();

                                                    getSelectedGroupNameLiveData().setValue(null);
                                                    getSelectedInviteCodeLiveData().setValue(null);
                                                    circleNameLive.setValue(null);
                                                    inviteCodeLive.setValue(null);

                                                    bitmapArrayList.clear();
                                                    markers.clear();


                                                    getAllPlaceGeoPoints();

                                                }


                                            }
                                        }
                                    });

                                }
                            }

                        }


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }


                }
            }
        });

    }

    private void CleanUpCircle(String userObjectId) {
        //  ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");
        queryCircleName.whereEqualTo("adminId", userObjectId);

        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    Log.d("Total circle he is in", String.valueOf(objects.size()));

                    for (int i = 0; i < objects.size(); i++) {


                        JSONArray circleMemberArray = objects.get(i).getJSONArray("circleMember");


                        if (circleMemberArray.length() > 0) {

                            //when admin leaves his circle,if circle member is not empty in that circle ,we should not delete that circle.
                            Log.d("Has member: ", "should not delete" + objects.get(i));
                            Log.d("circleMember", String.valueOf(objects.get(i).get("circleMember")));
                        } else {

                            //if circleMember is empty ,need to delete circle.

                            Log.d("goingToDeleteCircle", String.valueOf(objects.get(i)));
                            //objects.remove(i);
                            objects.get(i).deleteInBackground();

                        }

                    }
                }
            }
        });

    }

    public void deleteUser() {


        ParseUser.getCurrentUser().deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {


                    Log.i("deleteUser", "User Account deleted From Server!");

                    Toast.makeText(mContext, "Account deleted!", Toast.LENGTH_SHORT).show();


                } else {
                    Log.e("Error ", e.getMessage());

                }
            }
        });


    }

    public void leaveFromAllCircle() {

        //when the user delete his account ,need to remove him from all circle before delete his account.
        // ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");

        ArrayList<String> currentUser = new ArrayList<>();
        currentUser.add(ParseUser.getCurrentUser().getObjectId());
        queryCircleName.whereContainedIn("circleMember", currentUser);
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {


                    Log.i("userInCircleTotal", String.valueOf(objects.size()));

                    for (int i = 0; i < objects.size(); i++) {

                        deleteFromObject(objects.get(i).getObjectId());


                    }


                } else {
                    Log.e("Error ", e.getMessage());

                }
            }
        });

    }

    private void deleteFromObject(String objectId) {
        String userId = ParseUser.getCurrentUser().getObjectId();

        //   ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");
        queryCircleName.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {

                    JSONArray circleMemberArray = object.getJSONArray("circleMember");
                    for (int j = 0; j < circleMemberArray.length(); j++) {


                        try {

                            if (String.valueOf(circleMemberArray.get(j)).equals(userId)) {


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    circleMemberArray.remove(j);
                                    object.put("circleMember", circleMemberArray);

                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                Log.d("userRemovedFromCircle ", object.getObjectId());

                                                //after removed,we can delete his account from server
                                                deleteUser();
                                            }
                                        }
                                    });


                                }


                            }

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                    }

                } else {
                    Log.e("Error: ", e.getMessage());


                }


            }
        });


    }

    public void isUserLoggedIn(boolean b) {
        Log.d("isUserLoggedIn: ", String.valueOf(b));
        isUserLoggedIn.setValue(b);


    }


    public void signUpInServer(ParseUser user) {

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.i("done: ", "signUpSuccess");
                }
            }
        });
    }


    public void setSelectedCircleName(String circleName, String inviteCode) {


        getSelectedGroupNameLiveData().setValue(circleName);
        getSelectedInviteCodeLiveData().setValue(inviteCode);


    }


    public void resetPassword(String mailId) {


        queryParseUser.whereEqualTo("email", mailId);
        queryParseUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {

                    ParseUser.requestPasswordResetInBackground(mailId, new RequestPasswordResetCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // An email was successfully sent with reset instructions.
                                Toast.makeText(mContext, "An email was successfully sent with reset instructions!", Toast.LENGTH_LONG).show();


                            } else {
                                // Something went wrong. Look at the ParseException to see what's up.
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {


                    Toast.makeText(mContext, "Provide valid mail id!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public void savePhoneNumber(String phoneNumber) {


        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                object.put("phoneNumber", phoneNumber);

                // This will throw an exception, since the ParseUser is not authenticated
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Phone number updated!", Toast.LENGTH_SHORT).show();
                        } else if (e != null) {
                            Toast.makeText(mContext, "Somthing went wrong!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });
    }

    public void saveName(String fisrtName, String lastName) {


        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                object.setUsername(fisrtName + "  " + lastName);


                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Name updated!", Toast.LENGTH_SHORT).show();
                        } else if (e != null) {
                            Toast.makeText(mContext, "Somthing went wrong!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });
    }


    public boolean isValidInviteCode(String inviteCode) {


        queryCircleName.whereEqualTo("password", inviteCode);
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("done:isValid", "called");
                if (e == null) {

                    if (objects.size() == 0) {
                        Log.d("isValidInviteCode0", String.valueOf(objects.size()));
                        getIsValidInviteCodeInstance().setValue(false);
                        Toast.makeText(mContext, "Please provide valid invite code!.", Toast.LENGTH_LONG).show();
                        isItValid = false;


                    } else {
                        Log.d("isValidInviteCode1", String.valueOf(objects.size()));
                        getIsValidInviteCodeInstance().setValue(true);
                        joinWithCircle(inviteCode);
                        isItValid = true;

                    }

                } else if (e != null) {
                    Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    Log.d("error", e.getMessage());
                }
            }

        });

        return isItValid;

    }

    private void registerGeoFences(List<Geofence> geoFenceList) {

        // mGoogleApiClient == null  || !mGoogleApiClient.isConnected()
        geofencingClient = LocationServices.getGeofencingClient(mContext);
        if (geoFenceList == null || geoFenceList.size() == 0) {
            Log.d("AddGeoFences: ", "return");
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(mContext, "Please Check Your Location Permission And Allow All Time!", Toast.LENGTH_SHORT).show();

            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("onSuccess: ", "geoFencesAdd");
                        //getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("onFailure: ", e.getMessage().toString());

                        if (e.getMessage().equals("13"))
                            Toast.makeText(mContext, "Please Check Your Location Permission And Allow All Time!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unRegisterGeoFences() {

     /*   if(mGoogleApiClient == null  || !mGoogleApiClient.isConnected()  )
        {

            return;
        }*/
        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("onSuccess: ", "unregistered");
            }
        });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(busStopgeofenceListBuilder);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


    private void updateGeoFenceInServer(String placeName, double latitude, double longitude) {

        Log.d("updateGeoFence:", placeName);


        queryCircleName.whereEqualTo("circleName", circleNameLive.getValue());
        queryCircleName.whereEqualTo("password", inviteCodeLive.getValue());
        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("done:noti ", String.valueOf(objects.size()));
                if (e == null && objects.size() == 1) {


                    //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

                    queryCircleName.getInBackground(objects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {


                                for (int i = 0; i < objects.size(); i++) {

                                    JSONArray myGioFenceArray = objects.get(i).getJSONArray("GeoFence");


                                    if (myGioFenceArray.length() > 0) {
                                        for (int j = 0; j < myGioFenceArray.length(); j++) {
                                            Gson gson = new Gson();

                                            String GeoJsonArray = null;
                                            try {
                                                GeoJsonArray = String.valueOf(myGioFenceArray.get(j));


                                                StringToJsonSerialization serialization = gson.fromJson(GeoJsonArray, StringToJsonSerialization.class);


                                                if (serialization.getPlaceName().equals(placeName)) {
                                                    Log.d("placeNameGeoeq ", serialization.getPlaceName());
                                                    Log.d("placeGeoGeo ", String.valueOf(serialization.getGeoPoint()));

                                                    try {


                                                        //update address


                                                        StringToJsonSerialization serializationUpdate = new StringToJsonSerialization();
                                                        serializationUpdate.setPlaceName(placeName);
                                                        serializationUpdate.setGeoPoint(latitude, longitude);


                                                        String updateGeoFencee = gson.toJson(serializationUpdate);
                                                        Log.d("done:updateGeoFencee", updateGeoFencee + " " + j);

                                                        myGioFenceArray.put(j, updateGeoFencee);


                                                        objects.get(i).put("GeoFence", myGioFenceArray);

                                                        objects.get(i).saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    Log.d("done:object ", "updated");

                                                                    //  getRemovedGeoFenceInstance().setValue(serialization.getPlaceName());

                                                                    getAllGeoFences();

                                                                }
                                                            }
                                                        });
                                                    } catch (JSONException jsonException) {
                                                        jsonException.printStackTrace();
                                                    }
                                                }


                                                // ParseGeoPoint updatedLocation = new ParseGeoPoint(latitude, longitude);


                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    });
                }


            }
        });
    }


    public void unsubscribeForChannal(String channalName) {
        ParsePush.unsubscribeInBackground(channalName, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("chananl ", "unsubscribed to  " + channalName);

                removeGeoFenceInServer(channalName);

            }
        });
    }

    public void getRiderMarker(GoogleMap riderMap) {
        Log.d("getRiderMarker:mp", "called");

        mRiderMap = riderMap;

        // mRiderLocation=riderLocation;
        isItriderMapFragment = true;

        riderMarkers.clear();

        driverDetailsPojoList.clear();

        if (getRiderMarkerInstance().getValue() != null) {
            Log.d("AddDriverMarker:1 ", String.valueOf(getRiderMarkerInstance().getValue().size()));
            getRiderMarkerInstance().getValue().clear();
        }
        if (getRiderMarkerInstance().getValue() != null) {
            Log.d("getRiderMarker", String.valueOf(riderMarkers.size()));
            Log.d("getRiderMarker1", String.valueOf(driverDetailsPojoList.size()));
            Log.d("getRiderMarker2", String.valueOf(getRiderMarkerInstance().getValue().size()));
            Log.d("getRiderMarker3", String.valueOf(getDriverDetailsListLiveData().getValue().size()));


            //mRiderMap.clear();
        }
        // getDriverDetailsListLiveData().getValue().clear();
        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();

        queryUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    imageFile = object.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    getCustomMarker(R.layout.custom_marker, imagebitmap);
                                    resizeMarker(updatedbitmap, 215, 215);
                                    mRiderLocation = getLocationLatLong(object, "userCurrentLocation");
                                    riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                    getRiderLocationLive().setValue(riderLocation);

                                    Log.d("done:userMarker", String.valueOf(userAddress));

                                    userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);
                                    markerOption = new MarkerOptions().position(mRiderLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                                    Marker marker = mRiderMap.addMarker(markerOption);
                                    marker.setTag(object.getObjectId());

                                    riderMarkers.add(marker);
                                    getRiderMarkerInstance().setValue(riderMarkers);

                                    Log.d("done:rideerss", userAddress);

                                    riderMarkerList.add(resizedBitmap);


                                    UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                    userDetailsPojo.setUserName(object.getUsername());
                                    userDetailsPojo.setUserObjectId(object.getObjectId());

                                    userDetailsPojo.setUserDp(imagebitmap);


                                    userDetailsPojo.setLatitude(mRiderLocation.latitude);
                                    userDetailsPojo.setLongitude(mRiderLocation.longitude);
                                    userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                    userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                                    locationPermission = object.getString("LocationPermission");

                                    Log.d("locationPermission", locationPermission);

                                    if (locationPermission.equals("ON")) {
                                        if (userAddress.isEmpty()) {
                                       /* userDetailsPojo.setLatitude(0);
                                        userDetailsPojo.setLongitude(0);
                                        userDetailsPojo.setDestinationLatitude(0);
                                        userDetailsPojo.setDestinationLongitude(0);*/
                                            userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                        } else {
                                            userDetailsPojo.setUserCurrentLocation(userAddress);
                                        }
                                    } else if (locationPermission.equals("OFF")) {
                                        userDetailsPojo.setLatitude(0);
                                        userDetailsPojo.setLongitude(0);
                                        userDetailsPojo.setDestinationLatitude(0);
                                        userDetailsPojo.setDestinationLongitude(0);
                                        userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                    }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                                    driverDetailsPojoList.add(userDetailsPojo);
                                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                    Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                                    Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));
                                    getAllDriverFromUser();
                                }
                            }
                        });
                    }

                    if (imageFile == null) {

                        String riderName = object.getUsername();

                        Log.d("riderName", riderName);

                        String firstLetterOfUSer = String.valueOf(riderName.charAt(0));

                        String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                        mRiderLocation = getLocationLatLong(object, "userCurrentLocation");
                        riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                        getRiderLocationLive().setValue(riderLocation);

                        getCustomMarkerWithoutDp(firstLetterToUpperCase);

                        resizeMarker(updatedBitmapWithText, 215, 215);
                        userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);


                        markerOption = new MarkerOptions().position(mRiderLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                        Log.d("done:rideraddress", userAddress);
                        // mRiderMap.addMarker(markerOption).setTag(object.getObjectId());

                       /* markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                        Marker marker = mRiderMap.addMarker(markerOption);
                        marker.setTag(object.getObjectId());

                        riderMarkers.add(marker);

                        getRiderMarkerInstance().setValue(riderMarkers);

                        //  ridermarkers.add(markerNew);
                    /*    markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                        riderMarkerList.add(resizedBitmap);


                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                        UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                        userDetailsPojo.setUserName(riderName);
                        userDetailsPojo.setUserObjectId(object.getObjectId());


                        userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);

                        userDetailsPojo.setLatitude(mRiderLocation.latitude);
                        userDetailsPojo.setLongitude(mRiderLocation.longitude);


                        userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                        userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                        locationPermission = object.getString("LocationPermission");

                        Log.d("locationPermission", locationPermission);

                        if (locationPermission.equals("ON")) {
                            if (userAddress.isEmpty()) {
                               /* userDetailsPojo.setLatitude(0);
                                userDetailsPojo.setLongitude(0);
                                userDetailsPojo.setDestinationLatitude(0);
                                userDetailsPojo.setDestinationLongitude(0);*/
                                userDetailsPojo.setUserCurrentLocation("Unknown Address");

                            } else {
                                userDetailsPojo.setUserCurrentLocation(userAddress);
                            }
                        } else if (locationPermission.equals("OFF")) {
                            userDetailsPojo.setLatitude(0);
                            userDetailsPojo.setLongitude(0);
                            userDetailsPojo.setDestinationLatitude(0);
                            userDetailsPojo.setDestinationLongitude(0);
                            userDetailsPojo.setUserCurrentLocation("Location permissions off");
                        }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                        driverDetailsPojoList.add(userDetailsPojo);


                        getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                        Log.d("address", userAddress);
                        Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                        Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));

                        //   addDriverMarker(driverCode.getValue());
                        getAllDriverFromUser();

                    }


                }
            }
        });
    }


    public void getAllDriverFromUser() {

        driverListArray.clear();
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null) {

                    JSONArray driverList = object.getJSONArray("DriverList");

                    if (driverList != null) {
                        if (driverList.length() > 0) {

                            Log.d("driverListSize", String.valueOf(driverList.length()));
                            for (int i = 0; i < driverList.length(); i++) {

                                try {
                                    String driverId = driverList.get(i).toString();
                                    Log.d("done:driverId", driverId);

                                    driverListArray.add(driverId);
                                    getDriverDetails(driverId);

                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }


                        } else if (driverList.length() == 0) {
                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                            Log.d("done:driverList", "empty");
                        }

                    }
                }
            }
        });
    }

    private void getDriverDetails(String driverId) {

        //  for (int i = 0; i < driverIdList.size(); i++) {
        Log.d("getDriverDetails: ", "called");
        queryParseUser.getInBackground(driverId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                Log.d("driverId", object.getObjectId());
                boolean isHeDriver = object.getBoolean("IsDriver");

                boolean isDriverAvaliable = object.getBoolean("isDriverAvailable");

                if (isHeDriver && isDriverAvaliable) {


                    String driverName = object.getUsername();
                    LatLng driverLocation = getLocationLatLong(object, "userCurrentLocation");

                    String driverAddress = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);

                    getCustomMarkerWithBus();

                    resizeMarker(updatedbitmap, 215, 215);
                    markerOption = new MarkerOptions().position(driverLocation).title(object.getUsername()).snippet(driverAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                    // mRiderMap.addMarker(markerOption).setTag(object.getObjectId());
                    Log.d("done:add", "called");
                    Marker marker = mRiderMap.addMarker(markerOption);
                    marker.setTag(object.getObjectId());

                    riderMarkers.add(marker);

                    Log.d("riderMarkersSize", String.valueOf(riderMarkers.size()));
                    getRiderMarkerInstance().setValue(riderMarkers);


                    riderMarkerList.add(resizedBitmap);


                    UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                    userDetailsPojo.setUserName(driverName);
                    userDetailsPojo.setUserObjectId(object.getObjectId());
/*
                    if (driverAddress.isEmpty()) {
                        userDetailsPojo.setUserCurrentLocation("Unknown Address");

                    } else {
                        userDetailsPojo.setUserCurrentLocation(driverAddress);
                    }*/


                    userDetailsPojo.setUserDp(updatedbitmap);

                    userDetailsPojo.setLatitude(driverLocation.latitude);
                    userDetailsPojo.setLongitude(driverLocation.longitude);
                    userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                    userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                    ParseGeoPoint driverLocationGeo = new ParseGeoPoint(driverLocation.latitude, mRiderLocation.longitude);
                   /* double distance = getRiderLocationLive().getValue().distanceInMilesTo(driverLocationGeo);
                    Log.d("done:distance", String.valueOf(distance));*/

                   /* getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),driverLocation.latitude,driverLocation.longitude);



                    userDetailsPojo.setDistanceInMiles(distance);*/


                    locationPermission = object.getString("LocationPermission");

                    Log.d("locationPermission", locationPermission);

                    if (locationPermission.equals("ON")) {
                        if (driverAddress.isEmpty()) {
                           /* userDetailsPojo.setLatitude(0);
                            userDetailsPojo.setLongitude(0);
                            userDetailsPojo.setDestinationLatitude(0);
                            userDetailsPojo.setDestinationLongitude(0);*/
                            userDetailsPojo.setUserCurrentLocation("Unknown Address");

                        } else {
                            userDetailsPojo.setUserCurrentLocation(driverAddress);
                        }
                    } else if (locationPermission.equals("OFF")) {
                        userDetailsPojo.setLatitude(0);
                        userDetailsPojo.setLongitude(0);
                        userDetailsPojo.setDestinationLatitude(0);
                        userDetailsPojo.setDestinationLongitude(0);
                        userDetailsPojo.setUserCurrentLocation("Location permissions off");
                        userDetailsPojo.setDistanceInMiles("0");
                    }


                    Log.d("driveraddress", driverAddress);
                    Log.d("driverLocationlive", String.valueOf(driverLocationGeo));
                    Log.d("riderLocationlive", String.valueOf(getRiderLocationLive().getValue()));


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                    driverDetailsPojoList.add(userDetailsPojo);


                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                    ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());

                    Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                    Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));

                } else if (!isDriverAvaliable) {

                    Bitmap dpWithBus = Utilities.getDrawableDpWithBus(mContext);
                    UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                    userDetailsPojo.setUserName(object.getUsername());
                    userDetailsPojo.setUserObjectId(object.getObjectId());
                    userDetailsPojo.setUserDp(dpWithBus);
                    userDetailsPojo.setLatitude(0);
                    userDetailsPojo.setLongitude(0);
                    userDetailsPojo.setDestinationLatitude(0);
                    userDetailsPojo.setDestinationLongitude(0);
                    userDetailsPojo.setUserCurrentLocation("Not available");
                    driverDetailsPojoList.add(userDetailsPojo);


                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);

                }
            }
        });
    }

    public void AddDriverMarker(GoogleMap map) {
        Log.d("addDriverMarker", "called1");
        mRiderMap = map;
        riderMarkers.clear();
        isItDriverMapFragment = true;

        driverDetailsPojoList.clear();

       /* if(getRiderMarkerInstance().getValue()!=null){
        Log.d( "AddDriverMarker:1 ", String.valueOf(getRiderMarkerInstance().getValue().size()));}*/
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null) {
                    boolean isDriver = object.getBoolean("IsDriver");
                    if (isDriver) {
                        Log.d("addDriverMarker", "called2");
                        imageFile = object.getParseFile("profilepicture");


                        if (imageFile != null) {


                            imageFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {


                                    if (e == null && data != null) {


                                        //this is bitmap of parse file
                                        imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        getCustomMarker(R.layout.custom_marker, imagebitmap);
                                        resizeMarker(updatedbitmap, 215, 215);
                                        mDriverLocation = getLocationLatLong(object, "userCurrentLocation");
                                        driverLocation = new ParseGeoPoint(mDriverLocation.latitude, mDriverLocation.longitude);
                                        getRiderLocationLive().setValue(driverLocation);
                                        userAddress = Utilities.getAddressFromLocation(mContext, mDriverLocation.latitude, mDriverLocation.longitude);
                                        Log.d("done:userMarker", String.valueOf(userAddress));
                                        userName = object.getUsername();

                                        locationPermission = object.getString("LocationPermission");

                                        Log.d("locationPermission", locationPermission);

                                        markerOption = new MarkerOptions().position(mDriverLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                                        Marker marker = mRiderMap.addMarker(markerOption);
                                        marker.setTag(object.getObjectId());

                                        riderMarkers.add(marker);
                                        getRiderMarkerInstance().setValue(riderMarkers);


                                        riderMarkerList.add(resizedBitmap);


                                        UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                        userDetailsPojo.setUserName(object.getUsername());
                                        userDetailsPojo.setUserObjectId(object.getObjectId());

                                        userDetailsPojo.setUserDp(imagebitmap);




                                        /*double distance = getRiderLocationLive().getValue().distanceInMilesTo(driverLocation);
                                        Log.d("done:distance", String.valueOf(distance));*/

                                        //  getDistanceInfo(getRiderLocationLive().getValue().getLatitude(), getRiderLocationLive().getValue().getLongitude(), mDriverLocation.latitude, mDriverLocation.longitude);


                                        userDetailsPojo.setLatitude(mDriverLocation.latitude);
                                        userDetailsPojo.setLongitude(mDriverLocation.longitude);

                                        userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                        userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                                        // userDetailsPojo.setDistanceInMiles(getDistanceLiveInstance().getValue());
                                        if (locationPermission.equals("ON")) {
                                            if (userAddress.isEmpty()) {
                                              /*  userDetailsPojo.setLatitude(0);
                                                userDetailsPojo.setLongitude(0);

                                                userDetailsPojo.setDestinationLatitude(0);
                                                userDetailsPojo.setDestinationLongitude(0);*/
                                                userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                            } else {
                                                userDetailsPojo.setUserCurrentLocation(userAddress);
                                            }
                                        }

                                        if (locationPermission.equals("OFF")) {
                                            // userDetailsPojo.setDistanceInMiles("0");
                                            userDetailsPojo.setLatitude(0);
                                            userDetailsPojo.setLongitude(0);

                                            userDetailsPojo.setDestinationLatitude(0);
                                            userDetailsPojo.setDestinationLongitude(0);
                                            userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                        }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                                        driverDetailsPojoList.add(userDetailsPojo);
                                        getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                        Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                                        Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));
                                        getAllRiderFromUser();
                                    }


                                }
                            });
                        }


                        if (imageFile == null) {

                            userName = object.getUsername();

                            Log.d("driverName", userName);
                            locationPermission = object.getString("LocationPermission");

                            Log.d("locationPermission", locationPermission);

                            String firstLetterOfUSer = String.valueOf(userName.charAt(0));

                            String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                            mDriverLocation = getLocationLatLong(object, "userCurrentLocation");
                            driverLocation = new ParseGeoPoint(mDriverLocation.latitude, mDriverLocation.longitude);
                            getRiderLocationLive().setValue(driverLocation);

                            getCustomMarkerWithoutDp(firstLetterToUpperCase);

                            resizeMarker(updatedBitmapWithText, 215, 215);
                            userAddress = Utilities.getAddressFromLocation(mContext, mDriverLocation.latitude, mDriverLocation.longitude);


                            markerOption = new MarkerOptions().position(mDriverLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                            // mRiderMap.addMarker(markerOption).setTag(object.getObjectId());

                       /* markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                            Marker marker = mRiderMap.addMarker(markerOption);
                            marker.setTag(object.getObjectId());

                            riderMarkers.add(marker);

                            getRiderMarkerInstance().setValue(riderMarkers);

                            //  ridermarkers.add(markerNew);
                    /*    markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                            riderMarkerList.add(resizedBitmap);


                            dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                            UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                            userDetailsPojo.setUserName(userName);
                            userDetailsPojo.setUserObjectId(object.getObjectId());


                            userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);

                            userDetailsPojo.setLatitude(mDriverLocation.latitude);
                            userDetailsPojo.setLongitude(mDriverLocation.longitude);
                            userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                            userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                          /*  double distance = getRiderLocationLive().getValue().distanceInMilesTo(driverLocation);
                            Log.d("done:distance", String.valueOf(distance));*/

                            // getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),mDriverLocation.latitude,mDriverLocation.longitude);
                            Log.d("done:driverDistance", getRiderLocationLive().getValue().getLatitude() + "," + getRiderLocationLive().getValue().getLongitude() + "," + mDriverLocation.latitude + "," + mDriverLocation.longitude);

                            // Log.d( "driverDistance",getDistanceLiveInstance().getValue());

                            //  userDetailsPojo.setDistanceInMiles( getDistanceLiveInstance().getValue());

                            if (locationPermission.equals("ON")) {
                                if (userAddress.isEmpty()) {
                                   /* userDetailsPojo.setLatitude(0);
                                    userDetailsPojo.setLongitude(0);

                                    userDetailsPojo.setDestinationLatitude(0);
                                    userDetailsPojo.setDestinationLongitude(0);*/
                                    userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                } else {

                                    userDetailsPojo.setUserCurrentLocation(userAddress);
                                }
                            } else if (locationPermission.equals("OFF")) {
                                // userDetailsPojo.setDistanceInMiles("0");
                                userDetailsPojo.setLatitude(0);
                                userDetailsPojo.setLongitude(0);

                                userDetailsPojo.setDestinationLatitude(0);
                                userDetailsPojo.setDestinationLongitude(0);
                                userDetailsPojo.setUserCurrentLocation("Location permissions off");
                            }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                            driverDetailsPojoList.add(userDetailsPojo);


                            getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                            Log.d("address", userAddress);
                            Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                            Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));

                            //   addDriverMarker(driverCode.getValue());
                            getAllRiderFromUser();

                        }


                    }
                }
            }
        });


    }

    private void getAllRiderFromUser() {
        riderList.clear();


        String riderId = ParseUser.getCurrentUser().getObjectId();
        Log.d("getAllRiderFromUser", riderId);
        List<String> driverIdList = new ArrayList<>();
        driverIdList.add(riderId);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereContainedIn("DriverList", driverIdList);
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        Log.d("no one", "is watching");
                        ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());
                    } else if (objects.size() > 0) {


                        for (int i = 0; i < objects.size(); i++) {

                            riderList.add(objects.get(i).getObjectId());
                            Log.d("riderId", objects.get(i).getObjectId());
                            getRiderDetails(objects.get(i).getObjectId());
                        }

                    }
                }

                if (e != null) {
                    Log.d("no one", e.getMessage());

                }
            }
        });
        //this is rider list
      /*  driverListArray.clear();
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null) {

                    JSONArray riderList = object.getJSONArray("RiderList");

                    if (riderList != null) {
                        if (riderList.length() > 0) {

                            Log.d("riderList", String.valueOf(riderList.length()));
                            for (int i = 0; i < riderList.length(); i++) {

                                try {
                                    String riderId = riderList.get(i).toString();
                                    Log.d("done:riderId", riderId);

                                    driverListArray.add(riderId);
                                    getRiderDetails(riderId);

                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }


                        } else if (riderList.length() == 0) {
                            ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());


                            Log.d("done:driverList", "empty");
                        }

                    }
                }
            }
        });*/
    }

    private void getRiderDetails(String riderId) {


        Log.d("getDriverDetails: ", "called");
        queryParseUser.getInBackground(riderId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                boolean isHeDriver = object.getBoolean("IsDriver");


                if (e == null && !isHeDriver) {

                    imageFile = object.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    userName = object.getUsername();
                                    getCustomMarker(R.layout.custom_marker, imagebitmap);
                                    resizeMarker(updatedbitmap, 215, 215);
                                    mRiderLocation = getLocationLatLong(object, "userCurrentLocation");
                                    riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                                    // getRiderLocationLive().setValue(riderLocation);
                                    userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);
                                    Log.d("done:userMarker", String.valueOf(userAddress));

                                    locationPermission = object.getString("LocationPermission");

                                    Log.d("locationPermission", locationPermission);
                                    markerOption = new MarkerOptions().position(mRiderLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                                    Marker marker = mRiderMap.addMarker(markerOption);
                                    marker.setTag(object.getObjectId());

                                    riderMarkers.add(marker);
                                    getRiderMarkerInstance().setValue(riderMarkers);


                                    riderMarkerList.add(resizedBitmap);


                                    UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                                    userDetailsPojo.setUserName(object.getUsername());
                                    userDetailsPojo.setUserObjectId(object.getObjectId());


                                    userDetailsPojo.setUserDp(imagebitmap);


                                    userDetailsPojo.setLatitude(riderLocation.getLatitude());
                                    userDetailsPojo.setLongitude(riderLocation.getLongitude());
                                    userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                                    userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());


                                   /* double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                                    Log.d("done:distance", String.valueOf(distance));*/

                                   /* getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());


                                    Log.d("riderDistance", getDistanceLiveInstance().getValue());
                                    userDetailsPojo.setDistanceInMiles( getDistanceLiveInstance().getValue());*/


                                    if (locationPermission.equals("ON")) {
                                        if (userAddress.isEmpty()) {
                                          /*  userDetailsPojo.setLatitude(0);
                                            userDetailsPojo.setLongitude(0);

                                            userDetailsPojo.setDestinationLatitude(0);
                                            userDetailsPojo.setDestinationLongitude(0);*/
                                            userDetailsPojo.setUserCurrentLocation("Unknown Address");

                                        } else {
                                            userDetailsPojo.setUserCurrentLocation(userAddress);
                                        }
                                    } else if (locationPermission.equals("OFF")) {
                                        // userDetailsPojo.setDistanceInMiles("0");
                                        userDetailsPojo.setLatitude(0);
                                        userDetailsPojo.setLongitude(0);
                                        userDetailsPojo.setDestinationLatitude(0);
                                        userDetailsPojo.setDestinationLongitude(0);

                                        userDetailsPojo.setUserCurrentLocation("Location permissions off");
                                    }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                                    driverDetailsPojoList.add(userDetailsPojo);
                                    getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                                    Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                                    Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));
                                    ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());
                                }
                            }
                        });
                    }

                    if (imageFile == null) {

                        userName = object.getUsername();

                        Log.d("riderName", userName);

                        String firstLetterOfUSer = String.valueOf(userName.charAt(0));

                        String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();
                        mRiderLocation = getLocationLatLong(object, "userCurrentLocation");
                        riderLocation = new ParseGeoPoint(mRiderLocation.latitude, mRiderLocation.longitude);
                        //  getRiderLocationLive().setValue(riderLocation);

                        locationPermission = object.getString("LocationPermission");

                        Log.d("locationPermission", locationPermission);

                        getCustomMarkerWithoutDp(firstLetterToUpperCase);

                        resizeMarker(updatedBitmapWithText, 215, 215);
                        userAddress = Utilities.getAddressFromLocation(mContext, mRiderLocation.latitude, mRiderLocation.longitude);


                        markerOption = new MarkerOptions().position(mRiderLocation).title(userName).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                        // mRiderMap.addMarker(markerOption).setTag(object.getObjectId());

                       /* markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                        Marker marker = mRiderMap.addMarker(markerOption);
                        marker.setTag(object.getObjectId());

                        riderMarkers.add(marker);

                        getRiderMarkerInstance().setValue(riderMarkers);

                        //  ridermarkers.add(markerNew);
                    /*    markerNew = mRiderMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());*/

                        riderMarkerList.add(resizedBitmap);


                        dpWithTextDpForRecyclerView = Utilities.getDrawableDpWithText(mContext, firstLetterToUpperCase);

                        UserDetailsPojo userDetailsPojo = UserDetailsPojo.getUserDetailsPojoInstance();
                        userDetailsPojo.setUserName(userName);
                        userDetailsPojo.setUserObjectId(object.getObjectId());


                        userDetailsPojo.setUserDp(dpWithTextDpForRecyclerView);

                        userDetailsPojo.setLatitude(riderLocation.getLatitude());
                        userDetailsPojo.setLongitude(riderLocation.getLongitude());
                        userDetailsPojo.setDestinationLatitude(getRiderLocationLive().getValue().getLatitude());
                        userDetailsPojo.setDestinationLongitude(getRiderLocationLive().getValue().getLongitude());

                       /* double distance = getRiderLocationLive().getValue().distanceInMilesTo(riderLocation);
                        Log.d("done:distance", String.valueOf(distance));*/

                      /*  getDistanceInfo(getRiderLocationLive().getValue().getLatitude(),getRiderLocationLive().getValue().getLongitude(),riderLocation.getLatitude(),riderLocation.getLongitude());

                        userDetailsPojo.setDistanceInMiles(getDistanceLiveInstance().getValue());
                        Log.d("done:riderDistance",getDistanceLiveInstance().getValue());*/
                        Log.d("done:riderDistance", getRiderLocationLive().getValue().getLatitude() + "," + getRiderLocationLive().getValue().getLongitude() + "," + riderLocation.getLatitude() + "," + riderLocation.getLongitude());

                        if (locationPermission.equals("ON")) {
                            if (userAddress.isEmpty()) {
                              /*  userDetailsPojo.setLatitude(0);
                                userDetailsPojo.setLongitude(0);

                                userDetailsPojo.setDestinationLatitude(0);
                                userDetailsPojo.setDestinationLongitude(0);*/
                                userDetailsPojo.setUserCurrentLocation("Unknown Address");

                            } else {
                                userDetailsPojo.setUserCurrentLocation(userAddress);
                            }
                        } else if (locationPermission.equals("OFF")) {
                            //userDetailsPojo.setDistanceInMiles("0");

                            userDetailsPojo.setLatitude(0);
                            userDetailsPojo.setLongitude(0);
                            userDetailsPojo.setDestinationLatitude(0);
                            userDetailsPojo.setDestinationLongitude(0);
                            userDetailsPojo.setUserCurrentLocation("Location permissions off");
                        }


//add this new memeber to the user list.so that bottom sheet will update view with newly added user.
                        driverDetailsPojoList.add(userDetailsPojo);


                        getDriverDetailsListLiveData().setValue(driverDetailsPojoList);
                        Log.d("address", userAddress);
                        Log.d("done:driverDetail", String.valueOf(driverDetailsPojoList.size()));
                        Log.d("done:riderMarker", String.valueOf(riderMarkers.size()));
                        Log.d("done:driverDetaillive", String.valueOf(getDriverDetailsListLiveData().getValue().size()));

                        ChangeCamaraPosition(mContext, getRiderMarkerInstance().getValue());

                        //   addDriverMarker(driverCode.getValue());
                        // getAllRiderFromUser();

                    }


                }
            }
        });
    }

    public void setCurrentUserAsDriver() {


        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                object.put("IsDriver", true);
                object.saveInBackground();

                // AddDriverMarker(map);
            }
        });

    }

    public MutableLiveData<Boolean> isDriverCheckedIn(String driverId) {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        parseQuery.getInBackground(driverId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {


                    boolean isDriverAvailable = object.getBoolean("isDriverAvailable");
                    boolean isDriver = object.getBoolean("IsDriver");


                    if (isDriver && isDriverAvailable) {

                        Log.d("done:isDs1", String.valueOf(isDriverAvailable));
                        getIsDriverAvailable().setValue(true);

                    } else if (isDriver && !isDriverAvailable) {
                        Log.d("done:isDs2", String.valueOf(isDriverAvailable));
                        getIsDriverAvailable().setValue(false);
                    }
                }
            }
        });
        return getIsDriverAvailable();
    }

    public void disConnectUser(String driverId) {

        if (isItriderMapFragment) {

            Log.d("discconectDriver", String.valueOf(isItriderMapFragment));

            queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {

                        JSONArray driverArray = object.getJSONArray("DriverList");

                        for (int i = 0; i < driverArray.length(); i++) {

                            try {


                                if (driverArray.get(i).equals(driverId)) {
                                    Log.d("driverIdToRemove", String.valueOf(driverArray.get(i)));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        driverArray.remove(i);
                                        object.put("DriverList", driverArray);
                                        object.saveInBackground();
                                        notifyDriverAboutRiderDisConnected(driverId, ParseUser.getCurrentUser().getObjectId());

                                        unSubscribeForDriverChannel(driverId);
                                    }

                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }

                    }
                }
            });

        }

    }

    private void notifyDriverAboutRiderDisConnected(String driverId, String riderId) {

        Log.d("notifyDriverAbou", driverId + " " + riderId);
    }

    public void startDriving(String userId, double latitide, double longitude) {


        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    LatLng userCurrentLocation = getLocationLatLong(object, "userCurrentLocation");

                    if (String.valueOf(userCurrentLocation.latitude).equals(String.valueOf(latitide)) && String.valueOf(userCurrentLocation.longitude).equals(String.valueOf(longitude))) {


                        Toast.makeText(mContext, "You are at same place!", Toast.LENGTH_LONG).show();
                    } else {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + userCurrentLocation.latitude + "," + userCurrentLocation.longitude + "&daddr=" + latitide + "," + longitude));

                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }


                } else {

                    Log.e("done:start", e.getMessage());
                }

            }


        });


    }

    public void getDistanceInfo(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude) {
        // http://maps.googleapis.com/maps/api/distancematrix/json?destinations=Atlanta,GA|New+York,NY&origins=Orlando,FL&units=imperial
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
                Log.d("onResponse: ", "called");
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

                    // Log.d( "onResponse:distance",distance);
                    getDistanceLiveInstance().setValue(element.getDistance().getText());
                    getTravelDistance(element.getDistance().getText());
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

                Log.d("onFailure:retro", t.getMessage());

            }
        });
        //Log.d("getDistanceInfo: ",distance);
        //return distance;
    }

    private String getTravelDistance(String travelInfo) {
        // tvTravelInfo.setText(travelInfo);
        Log.d("showTravelDistance:", travelInfo);
        distance = travelInfo;
        return distance;
    }


    public void sendContanctUsMail() {







           /* Parse.Cloud.httpRequest({
                    method: 'POST',
                    url:'https://mandrillapp.com/api/1.0/users/ping,
            headers: {
                "Content-Type: application/json",
                        "key": "tFD6MM3nFDk57Ncqgco4dA"

            }
         });
*/
        /*  //"key": "example key",
    //"raw_message": "From: sender@example.com\nTo: recipient.email@example.com\nSubject: Some Subject\n\nSome content."

   // curl -sS -X POST "https://mandrillapp.com/api/1.0/users/ping" \
    //  --header 'Content-Type: application/json' \
    //  --data-raw '{ "key": "tFD6MM3nFDk57Ncqgco4dA" }'*/
        Log.d("sendContanctUsMail: ", "called");
        final HashMap<String, String> params = new HashMap<>();
        params.put("channel", circleNameLive.getValue() + " " + inviteCodeLive.getValue());

        params.put("circleName", circleNameLive.getValue());

        ParseCloud.callFunctionInBackground("sendMail", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null) {
                    Log.d("done: ", "Push message sent!!!");

                  /*  ParsePush.unsubscribeInBackground(circleNameLive.getValue() + inviteCodeLive.getValue(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d("channel ", "unsubscribed to  " + circleNameLive.getValue() + inviteCodeLive.getValue());
                        }
                    });*/
                } else {
                    // Something went wrong
                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error: ", String.valueOf(exc));

                }

            }
        });
    }

    public void senDriverNearByNotification() {


    }

    public void isHeDriver(String userId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    boolean isHeDriver = object.getBoolean("isDriver");

                    if (isHeDriver) {
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("driverName", object.getUsername());
                        Log.d("driverName", object.getUsername());
                        params.put("currentUserId", ParseUser.getCurrentUser().getObjectId());
                        Log.d("currentUserId", ParseUser.getCurrentUser().getObjectId());

                        params.put("installation", ParseInstallation.getCurrentInstallation().getInstallationId());
                        Log.d("installation", ParseInstallation.getCurrentInstallation().getInstallationId());
                        ParseCloud.callFunctionInBackground("driverNearBy", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object response, ParseException exc) {
                                if (exc == null) {
                                    Log.d("done: ", "Push message sent!!!");


                                } else {
                                    // Something went wrong
                                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error: ", String.valueOf(exc));

                                }

                            }
                        });

                    } else {
                        Log.d("onResponse:", "not Driver");
                    }

                }
            }
        });
    }


    public boolean isUserDriver() {

        SharedPreferences isDriver = mContext.getSharedPreferences("isDriver", MODE_PRIVATE);
        String isUserDriver = isDriver.getString("isUserDriver", "false");
        Log.d("isUserDriverttt: ", isUserDriver);
        if (isUserDriver.equals("true")) {
            return true;
        } else return false;
    }

    public void sendFeedback(String message) {


        ParseObject userFeedback = new ParseObject("UserFeedback");

        userFeedback.put("UserName", ParseUser.getCurrentUser().getUsername());
        userFeedback.put("UserId", ParseUser.getCurrentUser().getObjectId());

        if (ParseUser.getCurrentUser().getEmail() != null) {
            userFeedback.put("UserEmailId", ParseUser.getCurrentUser().getEmail());
        }
        userFeedback.put("Message", message);
        userFeedback.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(mContext, "Your feedback sent!", Toast.LENGTH_SHORT).show();
                } else if (e != null) {

                    Log.d("done:e", e.getMessage() + " " + e.getCode());

                }
            }
        });


    }

    public void callUser(String userId) {


        ParseQuery<ParseUser> getUserMobileNumber = ParseUser.getQuery();


        getUserMobileNumber.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    Log.d("done:phone", "called");

                    if (String.valueOf(object.getString("phoneNumber")) != null && !String.valueOf(object.getString("phoneNumber")).isEmpty()) {
                        String phoneNumber = String.valueOf(object.getString("phoneNumber"));

                        if (phoneNumber.contains(" ")) {
                            String merge[] = phoneNumber.split(" ");
                            phoneNumber = merge[0] + merge[1];

                        }
                        Log.d("done:phoneNumber", phoneNumber);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        mContext.startActivity(callIntent);

                    } else if (object.get("phoneNumber") == null || String.valueOf(object.getString("phoneNumber")).isEmpty()) {

                        Toast.makeText(mContext, "User does not have valid number!", Toast.LENGTH_SHORT).show();
                    }
                } else if (e != null) {

                    Log.d("done:phone", e.getMessage());
                }
            }
        });

    }

    public void reConnectLiveQuery() {

        //need to connect with same client instance to get live update

        parseLiveQueryClient.reconnect();
    }

    public void subscribeForDriverChannel(String driverObjectId) {


        ParsePush.subscribeInBackground(driverObjectId, new SaveCallback() {
            @Override

            public void done(ParseException e) {
                Log.d("subscribedToChannel ", driverObjectId);
            }
        });
    }

    public void unSubscribeForDriverChannel(String driverObjectId) {


        ParsePush.unsubscribeInBackground(driverObjectId, new SaveCallback() {
            @Override

            public void done(ParseException e) {
                Log.d("unsubscribedToChannel ", driverObjectId);
            }
        });
    }

    public void addExistingBusStops() {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    for (int i = 0; i < existingBusStopList.size(); i++) {

                        Log.d( "done:exis ", String.valueOf(existingBusStopList.size()));
                        if (existingBusStopList.get(i).getPlaceName() != null) {
                           /* for (String key : existingBusStopList.get(i).getWayPoints().keySet()) {

                                StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setPlaceName(key);
                                serialization.setGeoPoint(existingBusStopList.get(i).getWayPoints().get(key).latitude, existingBusStopList.get(i).getWayPoints().get(key).longitude);

                                busStopList.add(serialization);
                                Log.d( "onChanged:poin",key);*/

                            Log.d("addressTitlent", "not null");
                            StringToJsonSerialization serialization = new StringToJsonSerialization();
                            serialization.setPlaceName(existingBusStopList.get(i).getPlaceName());
                            serialization.setGeoPoint(existingBusStopList.get(i).getGeoPoint().getLatitude(), existingBusStopList.get(i).getGeoPoint().getLongitude());

                            // busStopgeofenceList.add(serialization);

                            //by default notification is OFF when first time added the place

                            Gson gson = new Gson();
                            String json = gson.toJson(serialization);


                            JSONArray myBusStopArray = object.getJSONArray("BusStopArray");
                            // myGioPointArray.put(json);


                            JSONObject newJsonObject = new JSONObject();
                            JSONArray newJsonArray = new JSONArray();
                            String inviteCode = Utilities.getUniqueId();
                            inviteCode = inviteCode.substring(0, 7);

                            if (myBusStopArray != null && myBusStopArray.length() > 0) {

                                myBusStopArray.put(json);

                                object.put("BusStopArray", myBusStopArray);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Log.d("Bus stop Saved", "aded");

                                            //getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());


                                        }
                                    }
                                });
                            } else if (myBusStopArray == null || myBusStopArray.length() == 0) {


                                newJsonArray.put(json);
                                object.put("BusStopArray", newJsonArray);

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {


                                            Log.d("done:", "adde");
                                         //   getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());

                                        }
                                    }
                                });
                            }
                        }


                    }
                }
            }
        });
    }

    public void addBusStop(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude) {








        if(addressTitle!=null)
        Log.d( "addBusStop:",addressTitle);
        // busStopList.clear();
      /*  StringToJsonSerialization serialization = new StringToJsonSerialization();
        serialization.setPlaceName(addressTitle);
        serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);







        // buildGeoFence(busStopList);
        busStopList.add(serialization);
        getBusStopLive().setValue(busStopList);
        Log.d("done:busstopsize", String.valueOf(busStopList.size()));

    }*/


        // busStopgeofenceList = new ArrayList<>();
        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                 /*   for(int i=0;i<existingBusStopList.size();i++){


                        if (existingBusStopList.get(i).getPlaceName()!= null) {
                           *//* for (String key : existingBusStopList.get(i).getWayPoints().keySet()) {

                                StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setPlaceName(key);
                                serialization.setGeoPoint(existingBusStopList.get(i).getWayPoints().get(key).latitude, existingBusStopList.get(i).getWayPoints().get(key).longitude);

                                busStopList.add(serialization);
                                Log.d( "onChanged:poin",key);*//*

                            Log.d("addressTitlent", "not null");
                            StringToJsonSerialization serialization = new StringToJsonSerialization();
                            serialization.setPlaceName(existingBusStopList.get(i).getPlaceName());
                            serialization.setGeoPoint(existingBusStopList.get(i).getGeoPoint().getLatitude(), existingBusStopList.get(i).getGeoPoint().getLongitude());

                            // busStopgeofenceList.add(serialization);

                            //by default notification is OFF when first time added the place

                            Gson gson = new Gson();
                            String json = gson.toJson(serialization);


                            JSONArray myBusStopArray = object.getJSONArray("BusStopArray");
                            // myGioPointArray.put(json);


                            JSONObject newJsonObject = new JSONObject();
                            JSONArray newJsonArray = new JSONArray();
                            String inviteCode = Utilities.getUniqueId();
                            inviteCode = inviteCode.substring(0, 7);

                            if (myBusStopArray != null && myBusStopArray.length() > 0) {

                                myBusStopArray.put(json);

                                object.put("BusStopArray", myBusStopArray);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Log.d("Bus stop Saved", "aded");



                                        }
                                    }
                                });
                            }

                            else  if (myBusStopArray == null || myBusStopArray.length() == 0) {


                                newJsonArray.put(json);
                                object.put("BusStopArray", newJsonArray);

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {


                                            Log.d( "done:","adde");


                                        }
                                    }
                                });
                            }
                        }






                    }*/


if(addressTitle !=null) {

    Log.d( "addressTitlent","not null");
    StringToJsonSerialization serialization = new StringToJsonSerialization();
    serialization.setPlaceName(addressTitle);
    serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);

    // busStopgeofenceList.add(serialization);

    //by default notification is OFF when first time added the place

    Gson gson = new Gson();
    String json = gson.toJson(serialization);


    JSONArray myBusStopArray = object.getJSONArray("BusStopArray");
    // myGioPointArray.put(json);


    JSONObject newJsonObject = new JSONObject();
    JSONArray newJsonArray = new JSONArray();
    String inviteCode = Utilities.getUniqueId();
    inviteCode = inviteCode.substring(0, 7);

    if (myBusStopArray != null && myBusStopArray.length() > 0) {

        myBusStopArray.put(json);

        object.put("BusStopArray", myBusStopArray);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Log.d("Bus stop Saved", "sc");

                    //after added bus stop add to geofence
//if we add goefence only when add bus stop,it will removed from mobile if it switch off.so we need to add each time when we getAllBusStops()

                    // buildGeoFence(busStopgeofenceList);

                    getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());


                }
            }
        });
    }

  else  if (myBusStopArray == null || myBusStopArray.length() == 0) {

        Log.d("circleNameJsonArray", "null");
        newJsonArray.put(json);
        object.put("BusStopArray", newJsonArray);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Log.d("Bus stop Saved", "null");

                    //after added bus stop add to geofence
//if we add goefence only when add bus stop,it will removed from mobile if it switch off.so we need to add each time when we getAllBusStops()

                    // buildGeoFence(busStopgeofenceList);

                    getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());


                }
            }
        });
    }



}

else if(addressTitle==null){
    Log.d( "addressTitlent","null");
    JSONArray newJsonArray = new JSONArray();
    object.put("BusStopArray", newJsonArray);
    Log.d("Bus stop Saved", "new");

    object.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null) {

                Log.d("Bus stop Saved", "scr");

                //after added bus stop add to geofence
//if we add goefence only when add bus stop,it will removed from mobile if it switch off.so we need to add each time when we getAllBusStops()

                // buildGeoFence(busStopgeofenceList);

                getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());


            }
        }
    });

}


                }
            }
        });
    }

    private void buildGeoFence(StringToJsonSerialization geoFenceList) {

        busStopgeofenceListBuilder = new ArrayList<>();
        //Log.d("buildGeoFence1", String.valueOf(geoFenceList.size()));
        List<HashMap<String, LatLng>> busStopMap = new ArrayList<>();
        String busStopName;

        List<String> busStopKey = new ArrayList<>();
        Set<String> busStopKeySet;

      /*  if (geoFenceList.size() > 0) {
            for (int i = 0; i < geoFenceList.size(); i++) {


                if (geoFenceList.get(i).getWayPoints() != null) {
                    Log.d("getWayPoints", String.valueOf(geoFenceList.get(i).getWayPoints()));
                    busStopMap.add(geoFenceList.get(i).getWayPoints());
                }

            }*/

        double originLatitude=geoFenceList.getOrigin().latitude;
        double originLongitude=geoFenceList.getOrigin().longitude;
        String originPlaceName=geoFenceList.getOriginPlaceName();


        Log.d("buildGeoFence:ori",originPlaceName);


       busStopgeofenceListBuilder.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("origin "+originPlaceName)

                .setCircularRegion(
                        originLatitude, originLongitude, 100
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        double destinationLatitude=geoFenceList.getDestination().latitude;
        double destinationLongitude=geoFenceList.getDestination().longitude;
        String destinationPlaceName=geoFenceList.getDestinationPlaceName();

        Log.d("buildGeoFence:de",destinationPlaceName);

        busStopgeofenceListBuilder.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("destination "+destinationPlaceName)

                .setCircularRegion(
                        destinationLatitude, destinationLongitude, 100
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        if (geoFenceList.getWayPoints() != null) {
            busStopMap.add(geoFenceList.getWayPoints());

            Log.d("buildGeoFence2", String.valueOf(busStopMap.size()));

            for (int k = 0; k < busStopMap.size(); k++) {


                busStopKeySet = busStopMap.get(k).keySet();

                for (String key : busStopKeySet) {

                    Log.d("busStopKeySet", key);

                    busStopKey.add(key);

                    busStopName = key;


                    double latitude = busStopMap.get(k).get(key).latitude;
                    double longitude = busStopMap.get(k).get(key).longitude;
                   // busStopName = "BusRoute " + routeDetail.getRouteName() + " BusStop " + key + " " + latitude + " " + longitude;

                   // Log.d("busStopName: ", "BusRoute " + geoFenceList.getRouteName() + " BusStop " + busStopName + " " + latitude + " " + longitude);

                    busStopgeofenceListBuilder.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                          //  .setRequestId("BusRoute " + geoFenceList.getRouteName() + " BusStop " + busStopName + " " + latitude + " " + longitude)
                            .setRequestId("BusStop " + busStopName )

                            .setCircularRegion(
                                    latitude, longitude, 100
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());


                }



          /*  for (int j = 0; j < busStopKey.size(); j++) {
                String busStopName = busStopKey.get(j);


                double latitude = busStopMap.get(k).get(busStopKey.get(j)).latitude;
                double longitude = busStopMap.get(k).get(busStopKey.get(j)).longitude;



                Log.d("busStopName: ", busStopName + " " + latitude + " " + longitude);

                busStopgeofenceListBuilder.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("BusStop " + busStopName)

                        .setCircularRegion(
                                latitude, longitude, 100
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());


            }*/

            }
            if (busStopgeofenceListBuilder.size() > 0)
                Log.d("buildGeoFencebuilder:", String.valueOf(busStopgeofenceListBuilder.size()));
            registerGeoFences(busStopgeofenceListBuilder);
        }


    }








       /* busStopgeofenceListBuilder = new ArrayList<>();
        for (int i = 0; i < geoFenceList.size(); i++) {


            String requestKey = null;

            requestKey = geoFenceList.get(i).getPlaceName();


            double latitude = geoFenceList.get(i).getGeoPoint().getLatitude();
            double longitude = geoFenceList.get(i).getGeoPoint().getLongitude();

            Log.d("buildGeoFence: ", requestKey);
            busStopgeofenceListBuilder.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId("BusStop " + requestKey)

                    .setCircularRegion(
                            latitude, longitude, 100
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());


            registerGeoFences();
        }*/


    public void getAllSavedBusRoute(String driverObjectId) {
        busRouteList = new ArrayList<>();


        if (getBusRouteLive().getValue() != null && getBusRouteLive().getValue().size() > 0) {

            Log.d("busrouteLive", String.valueOf(getBusRouteLive().getValue().size()));


            getBusRouteLive().getValue().clear();

        }
        ParseQuery<ParseUser> queryObject = ParseUser.getQuery();

        queryObject.getInBackground(driverObjectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    int JsonArraySize = object.getJSONArray("routeDetails").length();

                    Log.d("doneroute", String.valueOf(JsonArraySize));

                    if (JsonArraySize == 0) {

                        Log.d("done:get", "null");
                        getBusRouteLive().setValue(null);

                    } else if (JsonArraySize > 0) {

                        for (int i = 0; i < JsonArraySize; i++) {

                            Gson gson = new Gson();

                            String busroute = null;

                            try {


                                busroute = String.valueOf(object.getJSONArray("routeDetails").get(i));


                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }


                            StringToJsonSerialization json = gson.fromJson(busroute, StringToJsonSerialization.class);


                            StringToJsonSerialization serialization = new StringToJsonSerialization();
                            serialization.setRouteName(json.getRouteName());
                            serialization.setOrigin(json.getOrigin());
                            serialization.setOriginPlaceName(json.getOriginPlaceName());
                            serialization.setDestination(json.getDestination());
                            serialization.setDestinationPlaceName(json.getDestinationPlaceName());
                            serialization.setWayPoints(json.getWayPoints());
                            serialization.setPolyPoints(json.getPolyPoints());
                            serialization.setObjectId(object.getObjectId());
                            Log.d("getroute: ", json.getRouteName());
                            Log.d("getway: ", String.valueOf(json.getWayPoints()));

                            busRouteList.add(serialization);


                        }


                         getBusRouteLive().setValue(busRouteList);
                        getPolyString();
                    }
                }
            }
        });


    }

    public void getAllSavedBusStops(String objectId) {

        if(busStopList.size()>0)
        busStopList.clear();


       /* if (getBusStopLive().getValue() != null && getBusStopLive().getValue().size() > 0) {

            Log.d("busStopLive", String.valueOf(getBusStopLive().getValue().size()));

            getBusStopLive().getValue().clear();
        }*/

        ParseQuery<ParseUser> queryObject = ParseUser.getQuery();

        queryObject.getInBackground(objectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    int JsonArraySize = object.getJSONArray("BusStopArray").length();

                    Log.d("donebus", String.valueOf(JsonArraySize));

                    if (JsonArraySize == 0) {

                        Log.d("done:get", "null");
                        getBusStopLive().setValue(null);

                    } else if (JsonArraySize > 0) {

                        for (int i = 0; i < JsonArraySize; i++) {

                            Gson gson = new Gson();

                            String busStop = null;

                            try {


                                busStop = String.valueOf(object.getJSONArray("BusStopArray").get(i));

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            StringToJsonSerialization json = gson.fromJson(busStop, StringToJsonSerialization.class);

                            Log.d("getPlaceName: ", json.getPlaceName());
                            Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));


                            StringToJsonSerialization serialization = new StringToJsonSerialization();
                            serialization.setPlaceName(json.getPlaceName());
                            serialization.setGeoPoint(json.getGeoPoint().getLatitude(), json.getGeoPoint().getLongitude());
                            serialization.setObjectId(object.getObjectId());


                         /*   List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");


                            for (int j = 0; j < subscribedChannels.size(); j++) {

                                Log.d("subscribedChannel ", String.valueOf(subscribedChannels.get(j)));
                                String currentPlace = "BusStop " + json.getPlaceName() + " " + getSelectedDriverId().getValue();
                                if (currentPlace.equals(subscribedChannels.get(j))) {

                                    Log.d("setNotificationOn", json.getPlaceName() + " notification ON");
                                    serialization.setNotificationOn(true);
                                }


                            }*/



                            busStopList.add(serialization);

                            // buildGeoFence(busStopList);


                        }
                      //  busStopList.addAll(existingBusStopList);
                        Log.d("allbusstopsize", String.valueOf(busStopList.size()));

                        getBusStopLive().setValue(busStopList);


                    }
                }
            }
        });
    }


    public void updateBusStopAddress(String addressTitle, double selectedAddressLatitude,
                                     double selectedAddressLongitude, int postionToChange) {

      /*  Log.d( "updateBusStopAddress:", String.valueOf(postionToChange));
        StringToJsonSerialization serialization = new StringToJsonSerialization();
        serialization.setPlaceName(addressTitle);
        serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);


        busStopList.set(postionToChange - 1, serialization);
        getBusStopLive().setValue(busStopList);
*/



        List<String> geofeceIdToUnregister = new ArrayList();
        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                Log.d("done: ", "going to get ");


                Gson gson = new Gson();
                String placeGeoJsonArray = null;
                try {

                    Log.d("positionToChange: ", String.valueOf(postionToChange));
                    placeGeoJsonArray = String.valueOf(object.getJSONArray("BusStopArray").get(postionToChange));


                    StringToJsonSerialization json = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);

                    Log.d("getPlaceName:toUpdate ", json.getPlaceName());
                    Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));


                  /*  String geofenceId = "BusStop " + json.getPlaceName();


                    //update address

                    List<String> unRegisterGeofencIdeFromPhone = new ArrayList<>();
                    unRegisterGeofencIdeFromPhone.add(geofenceId);

                    unRegisterGeoFences(unRegisterGeofencIdeFromPhone);*/


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


                //update address


                StringToJsonSerialization serialization = new StringToJsonSerialization();
                serialization.setPlaceName(addressTitle);
                serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);


                String jsonUpdate = gson.toJson(serialization);


                try {
                    JSONArray myBusStopArray = object.getJSONArray("BusStopArray").put(postionToChange, jsonUpdate);
                    object.put("BusStopArray", myBusStopArray);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                Log.d("done: ", "place updated");

                                getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());

                                //after update place need to unregister old geo fence and register in new  geofence


                              /*  for (int i = 0; i < getBusStopLive().getValue().size(); i++) {


                                    String geofeceId = "BusStop " + getBusStopLive().getValue().get(i).getPlaceName();
                                    Log.d("done:geoId", geofeceId);

                                    geofeceIdToUnregister.add(geofeceId);
                                }
                                unRegisterGeoFences(geofeceIdToUnregister);
*/


                                            /*    List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");

                                                for (int j = 0; j < subscribedChannels.size(); j++) {


                                                    String currentPlace = addressTitle + " " + circleNameLive.getValue() + " " + inviteCodeLive.getValue();
                                                    Log.d("currentPlace", currentPlace);
                                                    Log.d("subscribedChanneeGeo ", String.valueOf(subscribedChannels.get(j)));
                                                    if (currentPlace.equals(subscribedChannels.get(j))) {
                                                        Log.d("done: ", "same");
                                                        updateGeoFenceInServer(currentPlace, selectedAddressLatitude, selectedAddressLongitude);

                                                    }


                                                }

                                                getAllPlaceGeoPoints();*/


                            } else {
                                Log.d("done: ", e.getMessage());
                            }
                        }
                    });

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


            }

        });
    }


    public void removeBusStopFromServer(String positionToRemove) {
        Log.d("busStopSize:", String.valueOf(busStopList.size()));

      /*  Log.d("busStopSize:", String.valueOf(getStopList().size()));
        if (getStopList().size() > 0) {
            getStopList().remove(Integer.parseInt(positionToRemove)-1);
            getBusStopLive().setValue(getStopList());

        }


    }

      *//*  if (getBusStopLive().getValue().size() > 0) {

            busStopList.remove(Integer.parseInt(positionToRemove) - 1);

            getBusStopLive().setValue(busStopList);
*//*
         *//*  getStopList().clear();
            getStopList().addAll(busStopList);*//*




         */


        ParseQuery<ParseUser> queryObject = ParseUser.getQuery();

        queryObject.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {


                    try {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                            Log.d("positionToRemove", positionToRemove);


                            JSONArray jsonArray = object.getJSONArray("BusStopArray");


                            Gson gson = new Gson();
                            String placeGeoJsonArray = null;

                            if (jsonArray.length() > 0) {
                                try {

                                    placeGeoJsonArray = String.valueOf(object.getJSONArray("BusStopArray").get(Integer.parseInt(positionToRemove) - 1));
                                    StringToJsonSerialization json = gson.fromJson(placeGeoJsonArray, StringToJsonSerialization.class);

                                    Log.d("getPlaceName:toUpdate ", json.getPlaceName());
                                    Log.d("getGeoPoint: ", String.valueOf(json.getGeoPoint()));


                                 /*   String geofenceId = "BusStop " + json.getPlaceName();


                                    //update address

                                    List<String> unRegisterGeofencIdeFromPhone = new ArrayList<>();
                                    unRegisterGeofencIdeFromPhone.add(geofenceId);

                                    unRegisterGeoFences(unRegisterGeofencIdeFromPhone);*/

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }


                                Log.d("done:jsonBusStop", String.valueOf(jsonArray.length()));
                                jsonArray.remove(Integer.parseInt(positionToRemove) - 1);

                                Log.d("done:jsonBusStop", String.valueOf(jsonArray.length()));
                                object.put("BusStopArray", jsonArray);


                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Log.d("updates ", "Place removed!");
                                            Log.d("updates ", String.valueOf(getBusStopLive().getValue().size()));


                                            getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());


                                        }
                                    }
                                });

                            } else {
                                Log.d("done: em", "called");
                                getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());
                            }
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }

                }

            }
        });

    }

    private void unRegisterGeoFences(List<String> GeoFenceIdToRemove) {
        geofencingClient = LocationServices.getGeofencingClient(mContext);
     /*   if(mGoogleApiClient == null  || !mGoogleApiClient.isConnected()  )
        {

            return;
        }*/
        geofencingClient.removeGeofences(GeoFenceIdToRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("onSuccess: ", "unregistered");

                //  getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());
            }
        });
    }


    public void setNotificationForBusStop(boolean isOn, int position, String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude, String driverId) {

        notification.setValue(isOn);
        getSelectedDriverId().setValue(driverId);


        Log.d("setNotification: ", String.valueOf(notification.getValue()));
        Log.d("Notification:position", String.valueOf(position));
        Log.d("Notification:address", addressTitle);
        Log.d("Notification:latitude", String.valueOf(selectedAddressLatitude));
        Log.d("Notification:longi", String.valueOf(selectedAddressLongitude));
        Log.d("Notification:driverId", String.valueOf(driverId));


        //  ParseQuery<ParseObject> queryObject = ParseQuery.getQuery("CircleName");

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                if (e == null) {


                    StringToJsonSerialization serialization = new StringToJsonSerialization();
                    serialization.setPlaceName(addressTitle);
                    serialization.setGeoPoint(selectedAddressLatitude, selectedAddressLongitude);

                    // busStopgeofenceList.add(serialization);

                    //by default notification is OFF when first time added the place

                    Gson gson = new Gson();
                    String json = gson.toJson(serialization);


                    JSONArray myBusStopArray = object.getJSONArray("BusStopArray");
                    // myGioPointArray.put(json);


                    JSONObject newJsonObject = new JSONObject();
                    JSONArray newJsonArray = new JSONArray();
                        /*String inviteCode = Utilities.getUniqueId();
                        inviteCode = inviteCode.substring(0, 7);*/


                    if (myBusStopArray == null || myBusStopArray.length() == 0) {

                        Log.d("circleNameJsonArray", "null");
                        newJsonArray.put(json);
                        object.put("BusStopArray", newJsonArray);
                    } else if (myBusStopArray.length() > 0) {

                        myBusStopArray.put(json);

                        object.put("BusStopArray", myBusStopArray);
                    }


                    if (notification.getValue()) {

                        //if notification on ,subscribe to channel
                        ParsePush.subscribeInBackground("BusStop " + serialization.getPlaceName() + " " + driverId, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("channel ", "subscribed to" + "BusStop " + serialization.getPlaceName() + " " + driverId);
                                // AddToGeoFenceList(serialization.getPlaceName() + " " + circleNameLive.getValue() + inviteCodeLive.getValue(), serialization.geoPoint.getLatitude(), serialization.geoPoint.getLongitude());

                            }
                        });
                    } else {


                        ParsePush.unsubscribeInBackground("BusStop " + serialization.getPlaceName() + " " + driverId, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("chananl ", "unsubscribed to " + "BusStop " + serialization.getPlaceName() + " " + driverId);

                                //   removeGeoFenceInServer(serialization.getPlaceName() + " " + circleNameLive.getValue() + inviteCodeLive.getValue());

                            }
                        });
                    }

                }
            }
        });
    }


    public void getAllBusStopsOfDriver(String driverId) {


    }

    public void getDriverLiveRoute(GoogleMap mMap, String driverId) {


        queryParseUser.getInBackground(driverId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    final Handler handler = new Handler();
                    routeMap = mMap;
                    isItRouteMap = true;
                    driverIdForRouteMap = driverId;
                    driverMarkerRoute = new ArrayList<>();
                    LatLng driverLocation = getLocationLatLong(object, "userCurrentLocation");


                    userAddress = Utilities.getAddressFromLocation(mContext, driverLocation.latitude, driverLocation.longitude);


                    getCustomMarkerWithBus();

                    resizeMarker(updatedbitmap, 215, 215);

                    markerOption = new MarkerOptions().position(driverLocation).title(object.getUsername()).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                    markerNew = mMap.addMarker(markerOption);
                    markerNew.setTag(object.getObjectId());

                    driverMarkerRoute.add(markerNew);

                    driverOrigin = new LatLng(driverLocation.latitude, driverLocation.longitude);

                    // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverOrigin, 15));


                    JSONArray route = object.getJSONArray("PolyLineString");
                    List<LatLng> list = new ArrayList<>();
                    List<LatLng> points = new ArrayList<>();

                    if (route != null && route.length() > 0) {


                        for (int i = 0; i < route.length(); i++) {
                            try {
                                list = DirectionsJSONParser.decodePoly(route.get(i).toString());

                                points.addAll(list);

                          /* for (int l = 0; l < list.size(); l++) {
                               points.add(list.get(l));
                           }*/
                                Log.d("done:String", route.get(i).toString());
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }
                        Log.d("donltlnge:", String.valueOf(points.size()));


                        builder = new LatLngBounds.Builder();


                        routeOrigin = new LatLng(points.get(0).latitude, points.get(0).longitude);

                        int lastIndex = points.size() - 1;
                        routeDestination = new LatLng(points.get(lastIndex).latitude, points.get(lastIndex).longitude);
                        Log.d("pointFirstAndLast", routeOrigin + " " + routeDestination + " " + lastIndex);

//driver current location
                        builder.include(driverOrigin);

                        //driver route origin
                        builder.include(routeOrigin);

                        //driver route destination
                        builder.include(routeDestination);

                        bounds = builder.build();

                        width = mContext.getResources().getDisplayMetrics().widthPixels;
                        height = mContext.getResources().getDisplayMetrics().heightPixels;
                        padding = (int) (width * 0.10);

                        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cu);


                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(points);
                        polylineOptions.width(12);
                        polylineOptions.color(Color.RED);
                        polylineOptions.geodesic(true);

                        mMap.addPolyline(polylineOptions);

                    }


                /*    if (count == 0) {

                        getCustomMarkerWithBus();

                        resizeMarker(updatedbitmap, 215, 215);

                        markerOption = new MarkerOptions().position(driverLocation).title(object.getUsername()).snippet(userAddress).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));


                        markerNew = mMap.addMarker(markerOption);
                        markerNew.setTag(object.getObjectId());

                        driverMarkerRoute.add(markerNew);

                        driverOrigin = new LatLng(driverLocation.latitude, driverLocation.longitude);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverOrigin, 15));



                        count++;
                        getDriverLiveRoute(mMap, driverId);

                       *//* handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    count++;

                                                    //post again
                                                    Log.d("run:driverrout0", "calle");

                                                    getDriverLiveRoute(mMap, driverId);
                                                    handler.postDelayed(this, 10000);


                                                }
                                            }
                                , 10000);*//*

                    } else if (count > 0) {
                      *//*  driverMarkerRoute.get(0).setSnippet(userAddress);
                        driverMarkerRoute.get(0).setPosition(driverLocation);*//*
                       // driverMarkerRoute
                        driverDesstination = new LatLng(driverLocation.latitude, driverLocation.longitude);

                        if (driverOrigin.latitude == driverDesstination.latitude  && driverOrigin.longitude==driverDesstination.longitude) {
                            Log.d("done:same", "driverOrigin");


                        *//*    handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        count++;

                                                        //post again
                                                        Log.d("run:driverrout1", "calle");
                                                        getDriverLiveRoute(mMap, driverId);

                                                        handler.postDelayed(this, 10000);


                                                    }
                                                }
                                    , 10000);
*//*

                        }
                        else {


                            Log.d("done:origin", String.valueOf(driverOrigin));
                            Log.d("done:des", String.valueOf(driverDesstination));

                            String snappedUrl = getSnappedLatLng(driverOrigin, driverDesstination);



                            Log.d("snappedUrl:", snappedUrl);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(snappedUrl);

                            builder = new LatLngBounds.Builder();


                            Log.d("onActivityResult", driverOrigin + " " + driverDesstination);


                            builder.include(driverOrigin);
                            builder.include(driverDesstination);

                            bounds = builder.build();

                            width = mContext.getResources().getDisplayMetrics().widthPixels;
                            height = mContext.getResources().getDisplayMetrics().heightPixels;
                            padding = (int) (width * 0.20);

                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


                            mMap.animateCamera(cu);

               *//*             handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        count++;
                                                        driverOrigin = driverDesstination;
                                                        //post again
                                                        Log.d("run:driverrout", "calle");
                                                        getDriverLiveRoute(mMap, driverId);

                                                        handler.postDelayed(this, 10000);


                                                    }
                                                }
                                    , 10000);

*//*

                        }*/

                }


            }
        });
    }


    public String getSnappedLatLng(LatLng markerOrigin, LatLng markerDesti) {

        String snappedUrl = "https://roads.googleapis.com/v1/snapToRoads?path=" + markerOrigin.latitude + "," + markerOrigin.longitude + "|" + markerDesti.latitude + "," + markerDesti.longitude + "&interpolate=true&key=AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";
        return snappedUrl;
        //https://roads.googleapis.com/v1/snapToRoads?path=-35.27801,149.12958|-35.28032,149.12907|-35.28099,149.12929|-35.28144,149.12984|-35.28194,149.13003|-35.28282,149.12956|-35.28302,149.12881|-35.28473,149.12836&interpolate=true&key=YOUR_API_KEY
    }

    public void shareRouteMap(StringToJsonSerialization routeDetail) {


       // getPolyString();

        Log.d( "shareRouteMap: ","called");

        StringToJsonSerialization existingLiveRoute = getPolyStringLive().getValue();


                if(existingLiveRoute!=null)
                placeToUnregister(existingLiveRoute);

                //get poly line first.

                List<String> polylineOptions = routeDetail.getPolyPoints();

                Log.d("shareRouteMap:", routeDetail.getRouteName());

                ParseQuery<ParseUser> queryObject = ParseUser.getQuery();
                queryObject.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {

                        if (e == null) {

/*JSONArray polyString=new JSONArray();

polyString.put(polylineOptions);*/


                            object.put("PolyLineString", polylineOptions);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(mContext, "Your Route Shared!", Toast.LENGTH_LONG).show();
/*
                                String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();


                                ParsePush.subscribeInBackground(currentRoute, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d("subscribed", currentRoute);
                                    }
                                });*/


                                        getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());


                                    } else if (e != null) {
                                        Log.d("done:polyerror", e.getMessage());
                                    }
                                }
                            });
                        }

                    }
                });

            }


            public void unShareRoute(StringToJsonSerialization routeDetail){

                Log.d("unShareRoute:","called");

                StringToJsonSerialization existingLiveRoute = getPolyStringLive().getValue();

                if(existingLiveRoute!=null)

                    if(routeDetail.getPolyPoints().size()==existingLiveRoute.getPolyPoints().size()) {

                        for (int i = 0; i < routeDetail.getPolyPoints().size(); i++) {

                            if (routeDetail.getPolyPoints().get(i).equals(existingLiveRoute.getPolyPoints().get(i))) {
                                if (i == routeDetail.getPolyPoints().size() - 1) {
                                    Log.d("shareRouteMap:i", String.valueOf(i));


                                    for (int j = 0; j < busRouteList.size(); j++) {

                                        if (existingLiveRoute.getPolyPoints().size() == busRouteList.get(j).getPolyPoints().size()) {

                                            existingLiveRoute.getPolyPoints().removeAll(busRouteList.get(j).getPolyPoints());

                                            if (existingLiveRoute.getPolyPoints().size() == 0) {
                                                Log.d("shareRouteMap:ii", String.valueOf(j));



                                                if (busRouteList.get(j).isNotificationOn())
                                                    busRouteList.get(j).setNotificationOn(false);

                                                Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                                                getBusRouteLive().setValue(busRouteList);
                                                placeToUnregister(existingLiveRoute);







                                                List<String> emptyStringList=new ArrayList<>();

                                                //List<String> polylineOptions =emptyStringList;

                                                Log.d("shareRouteMap:", routeDetail.getRouteName());

                                                ParseQuery<ParseUser> queryObject = ParseUser.getQuery();
                                                queryObject.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
                                                    @Override
                                                    public void done(ParseUser object, ParseException e) {

                                                        if (e == null) {

/*JSONArray polyString=new JSONArray();

polyString.put(polylineOptions);*/


                                                            object.put("PolyLineString", emptyStringList);
                                                            object.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        Toast.makeText(mContext, "Your Route Deleted!", Toast.LENGTH_LONG).show();
/*
                                String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();


                                ParsePush.subscribeInBackground(currentRoute, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d("subscribed", currentRoute);
                                    }
                                });*/

                                                                        getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());


                                                                    } else if (e != null) {
                                                                        Log.d("done:polyerror", e.getMessage());
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });


                                            }

                                        }
                                    }
                                }


                            }
                        }
                    }
            }

    public void saveRoute(StringToJsonSerialization routeDetail) {

        queryParseUser.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    JSONArray routeArray = object.getJSONArray("routeDetails");


                    //JSONObject jsonObject=new JSONObject();

                    Gson gson = new Gson();
                    String jsonrouteDetail = gson.toJson(routeDetail);


                    //jsonObject.put(routeDetail.routeName,jsonrouteDetail);
                    // jsonObject.put(jsonrouteDetail);

                    if (routeArray == null) {
                        Log.d("done:", "routcalled1");
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonrouteDetail);
                        object.put("routeDetails", jsonArray);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(mContext, "Route saved!", Toast.LENGTH_LONG).show();



//to remove tem bus stops
                                    addBusStop(null,0.0,0.0);
                                    busStopList.clear();
                                    getBusStopLive().setValue(busStopList);
                                    getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());

                                } else if (e != null) {
                                    Log.d("error:", e.getMessage());
                                    busStopList.clear();
                                    getBusStopLive().setValue(busStopList);
                                }
                            }
                        });
                    } else {

                        routeArray.put(jsonrouteDetail);
                        Log.d("done:", "routecalled2");
                        object.put("routeDetails", routeArray);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(mContext, "Route saved!", Toast.LENGTH_LONG).show();
                                    getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());
                                    busStopList.clear();
                                    getBusStopLive().setValue(busStopList);


                                } else if (e != null) {
                                    Log.d("error:", e.getMessage());
                                    getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());
                                    busStopList.clear();
                                    getBusStopLive().setValue(busStopList);

                                }
                            }
                        });
                    }


                }
            }
        });


    }

    public void removeBusRouteFromServer(StringToJsonSerialization roteDetail) {

        String routeName = roteDetail.getRouteName();
        Log.d("removeRoute", routeName);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    int JsonArraySize = object.getJSONArray("routeDetails").length();

                    Log.d("doneroute", String.valueOf(JsonArraySize));

                       /* if (JsonArraySize == 0) {

                            Log.d("done:get", "null");
                            getBusRouteLive().setValue(null);

                        } else*/
                    if (JsonArraySize > 0) {

                        for (int i = 0; i < JsonArraySize; i++) {

                            Gson gson = new Gson();

                            String busroute = null;

                            try {


                                busroute = String.valueOf(object.getJSONArray("routeDetails").get(i));
                                StringToJsonSerialization json = gson.fromJson(busroute, StringToJsonSerialization.class);


                                if (json.getRouteName().equals(routeName)) {

                                    Log.d("done:remobe", routeName);

                                    placeToUnregister(json);
/*
                                List<String> unRegisterGeofencIdeFromPhone = new ArrayList<>();
                                HashMap<String, LatLng> wayPoints = json.getWayPoints();

                                String busStopName = null;

                                for (String key : wayPoints.keySet()) {

                                    Log.d("busStopKey", key);


                                    double latitude = wayPoints.get(key).latitude;
                                    double longitude = wayPoints.get(key).longitude;
                                    busStopName = "BusRoute " + json.getRouteName() + key + " " + latitude + " " + longitude;

                                    unRegisterGeofencIdeFromPhone.add(busStopName);
                                    Log.d("busStopName: ", "BusRoute " + json.getRouteName() + key + " " + latitude + " " + longitude);
                                }

                                unRegisterGeoFences(unRegisterGeofencIdeFromPhone);*/

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                        JSONArray routeArray = object.getJSONArray("routeDetails");

                                        routeArray.remove(i);

                                        object.put("routeDetails", routeArray);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {

                                                    Toast.makeText(mContext, routeName + " Route removed!", Toast.LENGTH_LONG).show();
                                                    getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());
                                                }
                                            }
                                        });
                                    }


                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }





                             /*   StringToJsonSerialization serialization = new StringToJsonSerialization();
                                serialization.setRouteName(json.getRouteName());
                                serialization.setOrigin(json.getOrigin());
                                serialization.setDestination(json.getDestination());
                                serialization.setWayPoints(json.getWayPoints());
                                serialization.setPolyPoints(json.getPolyPoints());
                                serialization.setObjectId(object.getObjectId());
                                Log.d("getroute: ", json.getRouteName());
                                Log.d("getway: ", String.valueOf(json.getWayPoints()));*/


                        }


                    }
                }
            }
        });
    }

    public void getPolyString() {


        polyList = new ArrayList<>();

        ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {

                    JSONArray route = object.getJSONArray("PolyLineString");


                    if (route != null && route.length() > 0) {

                        for (int i = 0; i < route.length(); i++) {

                            try {
                                polyList.add(String.valueOf(route.get(i)));

                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }


                        }
                        Log.d("done:polyList", String.valueOf(polyList.size()));
                        // getPolyStringLive().setValue(polyList);
                        for (int i = 0; i < busRouteList.size(); i++) {

                            if (polyList.size() == busRouteList.get(i).getPolyPoints().size()) {

                                polyList.removeAll(busRouteList.get(i).getPolyPoints());
                                Log.d("done:afre", String.valueOf(polyList.size()));
                                if (polyList.size() == 0) {



                                    busRouteList.get(i).setNotificationOn(true);


                                    buildGeoFence(busRouteList.get(i));
                                    getPolyStringLive().setValue(busRouteList.get(i));

                                } else {
                                    busRouteList.get(i).setNotificationOn(false);
                                }
                            }
                        }

                        getBusRouteLive().setValue(busRouteList);
                    }
                }
            }
        });


    }

    public void updateRouteDetail(StringToJsonSerialization roteDetail) {


    }

    public void updateRouteDetailInServer(StringToJsonSerialization routeDetail) {

//if shared route is updated,need to update polyString .


        String routeName = routeDetail.getRouteName();

        Log.d("updateRoute", routeName+" "+objectPosition);
        //Log.d("updateRoute", String.valueOf(routeDetail.getWayPoints().size()));

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {


                    int JsonArraySize = object.getJSONArray("routeDetails").length();

                    Log.d("doneroute", String.valueOf(JsonArraySize));

                       /* if (JsonArraySize == 0) {

                            Log.d("done:get", "null");
                            getBusRouteLive().setValue(null);

                        } else*/
                    if (JsonArraySize > 0) {

                     //   for (int i = 0; i < JsonArraySize; i++) {

                            Gson gson = new Gson();

                            String busroute = null;

                            try {


                              //  busroute = String.valueOf(object.getJSONArray("routeDetails").get(i));
                                busroute = String.valueOf(object.getJSONArray("routeDetails").get(objectPosition));
                                StringToJsonSerialization json = gson.fromJson(busroute, StringToJsonSerialization.class);
                                placeToUnregister(json);


                             /*   if (json.getRouteName().equals(routeName)) {

                                    Log.d("done:update", routeName);*/






                               /* HashMap<String,LatLng> busStopsInSavedRoute=json.getWayPoints();

                                for(String busStopName:busStopsInSavedRoute.keySet()){

                                    StringToJsonSerialization serialization = new StringToJsonSerialization();
                                    serialization.setPlaceName(busStopName);

                                    serialization.setGeoPoint(busStopsInSavedRoute.get(busStopName).latitude, busStopsInSavedRoute.get(busStopName).longitude);

                                    busStopList.add(serialization);
                                }

                                    getBusStopLive().setValue(busStopList);*/


                                    String jsonrouteDetail = gson.toJson(routeDetail);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                        JSONArray routeArray = object.getJSONArray("routeDetails");

                                        routeArray.put(objectPosition, jsonrouteDetail);

                                        object.put("routeDetails", routeArray);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {

                                                    Toast.makeText(mContext, routeName + " Route Updated!", Toast.LENGTH_LONG).show();




                                                    if(busRouteList.get(objectPosition).isNotificationOn()){

                                                        shareRouteMap(routeDetail);
                                                    }

                                                    addBusStop(null,0.0,0.0);
                                                    busStopList.clear();
                                                    getBusStopLive().setValue(busStopList);
                                                    getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());
                                                }

                                                else if(e!=null){
                                                    Log.e( "done:e",e.getMessage() );

                                                }
                                            }
                                        });
                                    }




                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                    }
                }

            }
        });
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {


                jObject = new JSONObject(jsonData[0]);
                // DirectionsJSONParser parser = new DirectionsJSONParser();

                //routes = parser.parse(jObject);

                SnappedPointParser pointParser = new SnappedPointParser();
                routes = pointParser.parseSnappedPoints(jObject);

                Log.d("doInBackground:", "called");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("routSize", String.valueOf(routes));
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            Log.d("onPostExecute:", String.valueOf(result.size()));

            for (int i = 0; i < result.size(); i++) {

                lineOptions = new PolylineOptions();
                points.clear();

                List<HashMap<String, String>> path = result.get(i);


                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    // pointsSet.add(position);

                    //  reFormedPoints.removeAll(points);


                    Log.d("doInBackground:points", String.valueOf(points.size()));
                    Log.d("doInBackground:pointSet", String.valueOf(reFormedPoints.size()));
                    // Log.d("poly:", String.valueOf(position));

                }
                // pointsSet.addAll(points);

                // reFormedPoints.addAll(pointsSet);


                for (int j = 0; j < points.size(); j++) {

                    Log.d("points at " + j, String.valueOf(points.get(j)));
                    if (reFormedPoints.contains(points.get(j))) {

                        Log.d("contains:", String.valueOf(points.get(j)));
                    } else {
                        Log.d(" not contains:", String.valueOf(points.get(j)));
                        reFormedPoints.add(points.get(j));
                    }
                }

               /* for (LatLng pointsSet : pointsSet) {
                    Log.d( "pointsSet", String.valueOf(pointsSet));
                    reFormedPoints.add(pointsSet);

                }*/


                for (int l = 0; l < reFormedPoints.size(); l++) {

                    Log.d("new points at " + l, String.valueOf(reFormedPoints.get(l)));
                }


                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
                Log.d("doInBackground:", "called3");


            }

// Drawing polyline in the Google Map for the i-th route
            Log.d("doInBackground:", "called4");
            if (reFormedPoints.size() != 0 && routeMap != null) {
                routeMap.addPolyline(lineOptions);

                getSnappedRout(pointId);
            }
        }


    }

    public void getSnappedRout(int pointId) {


        Log.d("pointId", String.valueOf(pointId));
        Log.d("pointSize", String.valueOf(reFormedPoints.size()));
        if (pointId < reFormedPoints.size() - 1) {
            //driverMarkerRoute.get(0).setPosition(reFormedPoints.get(pointId));
            Log.d("current:", String.valueOf(driverMarkerRoute.get(0).getPosition()));
            LatLng toPosition = new LatLng(reFormedPoints.get(pointId + 1).latitude, reFormedPoints.get(pointId + 1).longitude);
            // LatLng toPosition = new LatLng(points.get(points.size()-1).latitude, points.get(points.size()-1).longitude);
            Log.d("toPosi:", String.valueOf(toPosition));
            animateMarker(pointId, toPosition, driverMarkerRoute.get(0), false);


        }
    }


    public void animateMarker(int startPositionId, final LatLng toPosition, Marker marker, final boolean hideMarke) {

        final long start = SystemClock.uptimeMillis();
        Projection proj = routeMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();
        runnable = new Runnable() {
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
                //  driverOrigin = new LatLng(lat,lng);

                if (t < 1.0) {
                    // Post again 16ms later.
                    Log.d("run:", "called");

                    handlerSetPosition.postDelayed(this, 8);


                } else {
                    if (hideMarke) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                        pointId = startPositionId + 1;
                        getSnappedRout(pointId);


                    }
                }
            }
        };
        handlerSetPosition.post(runnable);

     /*   handler.post(new Runnable() {
            @Override

        });*/
    }

    public void placeToUnregister(StringToJsonSerialization routeDetail){

        List<String> unRegisterGeofencIdeFromPhone = new ArrayList<>();


        if(routeDetail.getWayPoints()!=null) {
            HashMap<String, LatLng> wayPoints = routeDetail.getWayPoints();

            String busStopName = null;

            if (wayPoints != null && wayPoints.size() > 0) {

                for (String key : wayPoints.keySet()) {

                    Log.d("busStopKey", key);


                    double latitude = wayPoints.get(key).latitude;
                    double longitude = wayPoints.get(key).longitude;
                  //  busStopName = "BusRoute " + routeDetail.getRouteName() + " BusStop " + key + " " + latitude + " " + longitude;
                    busStopName = "BusStop " + key;

                    unRegisterGeofencIdeFromPhone.add(busStopName);
                    Log.d("busStopNameToUnreg: ", "BusRoute " + routeDetail.getRouteName() + " BusStop  " + key + " " + latitude + " " + longitude);
                }
            }
        }

        if(unRegisterGeofencIdeFromPhone.size()>0)
        unRegisterGeoFences(unRegisterGeofencIdeFromPhone);
    }

}





   /* public boolean isDriver(String driverCode) {

        queryParseUser.whereEqualTo("objectId", driverCode);
        queryParseUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null  && objects.size()==1) {

                }

                else if(objects.size()==0){

                }
            }
    }*/

/*    public void deleteDp(String imageFileName) {

        final HashMap<String, String> params = new HashMap<>();

        params.put("fileNameToDelete", imageFileName);
        Log.d("deleteDpcloud:", "called");




      */

   /* public void getDistance(ParseGeoPoint origin,ParseGeoPoint destination){
  Retrofit retrofit = new Retrofit.Builder()
          .baseUrl("https://maps.googleapis.com/maps/api/distancematrix/")
                // .baseUrl("https://parsefiles.back4app.com/tvo4tsUdmH3YSI7iUgewB41l7eIDKn3wLTg8oOld/")
               // .baseUrl("https://parseapi.back4app.com/parse/")



                .build();
        ParseDeleteInterface parseDeleteInterface = retrofit.create(ParseDeleteInterface.class);

        //Call<Void> call = parseDeleteInterface.deleteDp(imageFileName);
        Call<Void> call = parseDeleteInterface.getDistance(origin.getLatitude(),origin.getLongitude(),destination.getLatitude(),destination.getLongitude());




        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.d("onResponse: ", String.valueOf(response.code()));
                    return;
                }

                //  Log.d("onResponse:success", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("error: ", t.getMessage());
            }
        });
    }*/

/*
 *//* ParseCloud.callFunctionInBackground("deleteDp", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {

                if (exc == null) {
                    Log.d( "done:dp","deleted");
                }
                else{
                    Log.d( "error:", exc.getMessage());
                }
            }
        });*//*
}*/




/*import java.util.Arrays;
import  java.util.*;
public class Main
{
static int addedValue=0;


   static List<Integer> requiredArray=new ArrayList<>();
   static int[] requiredIntArray=new int[requiredArray.size()];

    public static int[] twoSum(int[] nums, int target) {

        for (int i=0;i<=nums.length-1;i++){

           addedValue=addedValue+nums[i];
               requiredIntArray[i]=nums[i];
             //  requiredArray.add(nums[i]);
             System.out.println(i);
             System.out.println(addedValue);

               if(addedValue==target)




                   break;


        }

        requiredArray.toArray(requiredIntArray);
      //return requiredArray.mapToInt(Integer::intValue).toArray();
        return requiredIntArray;
    }


    public static void main(String[] args) {
// TODO Auto-generated method stub
int[] num = {2,7,1,6,11,15};
        int target=9;
        Main obj=new Main();
      //  obj.twoSum(num,target);
        System.out.println(Arrays.toString(obj.twoSum(num,target)));

    }
}
*/






