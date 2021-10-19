package com.androidapps.sharelocation.adapters;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.androidapps.sharelocation.DistanceApi.DistanceApiClient;
import com.androidapps.sharelocation.DistanceApi.DistanceResponse;
import com.androidapps.sharelocation.model.Element;
import com.androidapps.sharelocation.viewmodel.MainViewModel;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.utilities.RestUtil;
import com.androidapps.sharelocation.viewmodel.RiderViewModel;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public  class  BindingAdapter {

    //HomePageViewModel homePageViewModel;
    public final ObservableField<String> firstName = new ObservableField<>();
    static ObservableArrayList<String> livepolyString = new ObservableArrayList<>();
    @androidx.databinding.BindingAdapter("android:notification")
    public static void notificationVisibility(ImageView notification,boolean isUserDriver) {

        if (isUserDriver) {

            notification.setVisibility(View.GONE);
        } else {
            notification.setVisibility(View.VISIBLE);
        }
    }

    @androidx.databinding.BindingAdapter(value = {"android:liveRouteOnVisibility"})
    public static void liveRouteOn(ImageView routeViewOn, StringToJsonSerialization routeDetail) {


      if(routeDetail.isNotificationOn())
          routeViewOn.setVisibility(View.VISIBLE);

      else routeViewOn.setVisibility(View.GONE);
       // homePageViewModel.getLiveRoute();

       // Log.d("liveRouteOn: ", String.valueOf(homePageViewModel.livepolyString.size()));


/*if(livepolyString.size()>0)
    livepolyString.clear();

if(homePageViewModel.getLiveRoute()!=null) {

    livepolyString.addAll(homePageViewModel.getLiveRoute().getValue());
    Log.d("liveRouteOn:", String.valueOf(livepolyString.get(0)));
}*/


      }





   /* List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
                                for (int j = 0; j < subscribedChannels.size(); j++) {

                                    Log.d("subscribedChannel ", String.valueOf(subscribedChannels.get(j)));
                                    String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();

                                    //  String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();
                                    if (currentRoute.equals(subscribedChannels.get(j))) {

                                        Log.d("subscribed", "visible");
                                        routeViewOn.setVisibility(View.VISIBLE);

                                    }
                                    else {
                                        routeViewOn.setVisibility(View.GONE);
                                    }
                                }*/



    @androidx.databinding.BindingAdapter("android:liveRouteOffVisibility")
    public static void liveRouteOff(ImageView routeViewOff,StringToJsonSerialization routeDetail) {
        if(routeDetail.isNotificationOn())
            routeViewOff.setVisibility(View.GONE);

        else routeViewOff.setVisibility(View.VISIBLE);
  /*      List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        for (int j = 0; j < subscribedChannels.size(); j++) {

            Log.d("subscribedChannel ", String.valueOf(subscribedChannels.get(j)));
            String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();

            //  String currentRoute = "BusRoute " + routeDetail.getRouteName() + " " + ParseUser.getCurrentUser().getObjectId();
            if (currentRoute.equals(subscribedChannels.get(j))) {

                Log.d("subscribed", "visible");
                routeViewOff.setVisibility(View.GONE);

            }
            else {
                routeViewOff.setVisibility(View.VISIBLE);
            }
        }*/

    }

    @androidx.databinding.BindingAdapter("android:setNameVisibility")
    public static void setNameVisibility(Group view, String needToEdit) {

        if (needToEdit.equals("Name")) {

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("android:setPasswordVisibility")
    public static void setPasswordVisibility(Group view, String needToEdit) {

        if (needToEdit.equals("Password")) {

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @androidx.databinding.BindingAdapter("android:setLeaveVisibility")
    public static void setLeaveVisibility(Group view, String needToEdit) {

        if (needToEdit.equals("Leave")) {

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("android:setDeletetVisibility")
    public static void setDeletetVisibility(Group view, String needToEdit) {

        if (needToEdit.equals("Delete")) {

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("android:setPhoneVisibility")
    public static void setPhoneVisibility(Group view, String needToEdit) {

        if (needToEdit.equals("Phone")) {

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("android:setFeedbackVisibility")
    public static void setFeedbackVisibility(Group feedbackGroup, String needToEdit) {

        if (needToEdit.equals("SendFeedback")) {

            feedbackGroup.setVisibility(View.VISIBLE);
        } else {
            feedbackGroup.setVisibility(View.GONE);
        }
    }

    @androidx.databinding.BindingAdapter("android:leaveButtonSetClickable")
    public static void leaveButtonSetClickable(Button button, String circleName) {

        if (circleName.equals("You are not in any circle")) {

            button.setClickable(false);
        } else {
            button.setClickable(true);
        }
    }


    @androidx.databinding.BindingAdapter("android:isNull")
    public static void isNullValue(EditText editText, String value) {


        {
            Log.d( "isNullValue:",value);
            if (value.equals(null)) {

                editText.setHint("Enter Phone Number");
            }

        }
    }


    @androidx.databinding.BindingAdapter("android:setImageSrc")
    public static void setSrc(CircleImageView imageView, Bitmap bitmap) {

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @androidx.databinding.BindingAdapter(value = {"android:disConnectVisbility","android:riderViewModel"})
    public static void disconnectUser(Button disconnectButton, String userId, RiderViewModel viewModel) {

        boolean isUserDriver=viewModel.isUserDriver();
     if(userId.equals(ParseUser.getCurrentUser().getObjectId())){

         disconnectButton.setVisibility(View.GONE);

     }

     else if(isUserDriver){
         disconnectButton.setText("Call Rider");
         viewModel.disConnectOrCallRider.setValue("CallRider");
         disconnectButton.setVisibility(View.VISIBLE);
     }
     else {
         viewModel.disConnectOrCallRider.setValue("DisConnect");
         disconnectButton.setVisibility(View.VISIBLE);
     }

    }

    @androidx.databinding.BindingAdapter(value = {"android:userIdForStopSignVisibility","android:riderViewModel"})
    public static void stopSignVisibility(ImageView busStopSign, String userId, RiderViewModel viewModel) {

        boolean isUserDriver=viewModel.isUserDriver();
        if(userId.equals(ParseUser.getCurrentUser().getObjectId())){

            busStopSign.setVisibility(View.GONE);

        }

        else if(isUserDriver){

            busStopSign.setVisibility(View.GONE);
        }
        else {
            busStopSign.setVisibility(View.VISIBLE);

        }

    }

    @androidx.databinding.BindingAdapter(value = {"android:startVisibility","android:riderViewModel"})
    public static void setDistance(Button startButton,String userId,RiderViewModel viewModel) {
        boolean isUserDriver=viewModel.isUserDriver();
        if(userId.equals(ParseUser.getCurrentUser().getObjectId())){

            startButton.setVisibility(View.GONE);
        }

        else if(!isUserDriver){

            startButton.setText("Call Driver");

            viewModel.startOrCallDriver.setValue("CallDriver");
            startButton.setVisibility(View.VISIBLE);

        }
        else if(isUserDriver) {

            startButton.setVisibility(View.VISIBLE);

            viewModel.startOrCallDriver.setValue("StartDriving");

        }



    }


    @androidx.databinding.BindingAdapter(value = {"android:sendMssageVisibility"})
    public static void sendMssageVisibility(ImageView sendMessageView,RiderViewModel riderViewModel) {

        boolean isUserDriver=riderViewModel.isUserDriver();

          if(isUserDriver){


              sendMessageView.setVisibility(View.VISIBLE);
          }


          else if(!isUserDriver){


              sendMessageView.setVisibility(View.GONE);
          }

    }

    @androidx.databinding.BindingAdapter(value = {"android:nearbyTaxiVisibility"})
    public static void nearbyTaxiVisibility(ImageView nearbyTaxiView,RiderViewModel riderViewModel) {

        boolean isUserDriver=riderViewModel.isUserDriver();

        if(isUserDriver){


            nearbyTaxiView.setVisibility(View.GONE);
        }


        else if(!isUserDriver){


            nearbyTaxiView.setVisibility(View.VISIBLE);
        }

    }



       @androidx.databinding.BindingAdapter("android:setVisibilityForFragment")
        public static void setFragmentVisibility(Group group, String selectedFragment) {
            Log.d( "setFragmentVisibility: ",selectedFragment);

      /*  if(group.getId()== R.id.group_create && selectedFragment.equals("Create")){
            group.setVisibility(View.VISIBLE);
        }
        else if(group.getId()==R.id.group_join && selectedFragment.equals("Join")){
            group.setVisibility(View.VISIBLE);

        }

        else if(group.getId()==R.id.group_add_dp && selectedFragment.equals("AddDp")){

            group.setVisibility(View.VISIBLE);
        }

        else if(group.getId()==R.id.group_existing_circle && selectedFragment.equals("Members")){
            group.setVisibility(View.VISIBLE);

        }
        else if(group.getId()==R.id.group_add_person && selectedFragment.equals("AddPerson")){
            group.setVisibility(View.VISIBLE);

        }
        else if(group.getId()==R.id.group_no_network && selectedFragment.equals("NoNetwork")){
            group.setVisibility(View.VISIBLE);

        }
        else  {

            group.setVisibility(View.GONE);

        }*/
    }



    @androidx.databinding.BindingAdapter("android:setTextWatcher")
    public static void getPasswordTextWatcher(EditText editText, MainViewModel viewModel) {


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence enteredText, int i, int i1, int i2) {
                Log.d("onTextChanged: ", enteredText.toString());
                if (editText.getId() == R.id.edit_first_name)
                    viewModel.getFirstNameLiveDataInstance().setValue(enteredText.toString());


                if (editText.getId() == R.id.edit_last_name)
                    viewModel.getLastNameLiveDataInstance().setValue(enteredText.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @androidx.databinding.BindingAdapter("android:distance")
    public static void setDistance(TextView textView, String distance) {

        if(distance.equals("0")  || distance.contains("ft") ){
            textView.setVisibility(View.GONE);
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(distance+ "  away");
        }
    }

    @androidx.databinding.BindingAdapter(value = {"android:userId","android:viewModel","android:originLatitude","android:originLongitude","android:destinationLatitude","android:destinationLongitude"},requireAll=false)
    public static void getDistanceInfo(TextView textview,String userId,RiderViewModel viewModel,double originLatitude,double originLongitude,double destinationLatitude,double destinationLongitude) {
        // http://maps.googleapis.com/maps/api/distancematrix/json?destinations=Atlanta,GA|New+York,NY&origins=Orlando,FL&units=imperial
        if(originLatitude==0 && originLongitude==0 &&  destinationLatitude==0 &&  destinationLongitude==0  )
        {
            textview.setVisibility(View.GONE);
            return;
        }
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", originLatitude+","+originLongitude);
        mapQuery.put("destinations", destinationLatitude+","+destinationLongitude);

        mapQuery.put("key","AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo");
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                Log.d( "onResponse: ","called");


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

                    Log.d( "onResponse:distance",element.getDistance().getText());
                   /* getDistanceLiveInstance().setValue(element.getDistance().getText());
                    getTravelDistance(element.getDistance().getText());*/
if( element.getDistance().getText().contains("ft")) {


    textview.setVisibility(View.GONE);
}/*
                    if( element.getDistance().getText().contains("0.1 mi")) {
                        Log.d("onResponse:","0.1");
viewModel.isHeDriver(userId);

                    }*/


                    else {
                        textview.setVisibility(View.VISIBLE);
                        textview.setText(element.getDistance().getText()+ "  away");
                    }
                }

                else if( response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getStatus().equals("ZERO_RESULTS") ){

                    Log.d( "onResponse:zero","zero");
                    textview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

                Log.d("onFailure:retro",t.getMessage());

            }
        });
        //Log.d("getDistanceInfo: ",distance);
        //return distance;
    }
}


/*    @androidx.databinding.BindingAdapter("android:createNewCircleVisibility")
    public static void createNewCircleVisibility(Group createNewGroup, String circleCode)
    {

       if(circleCode!=null) {
           Log.d( "createNewCircleVisib",circleCode);
          createNewGroup.setVisibility(View.GONE);

       }
       else {
          // Log.d( "createNewCirVisib",circleCode);
           createNewGroup.setVisibility(View.VISIBLE);
       }
    }


    @androidx.databinding.BindingAdapter(value = {"android:viewModel","android:circleCode"})
    public static void joinCircleVisibility(Group joinGroup,MainViewModel viewModel,String circleCode)
    {

        if(circleCode==null) {

           // Log.d( "joinCircleVisitynu: ",circleCode);
            joinGroup.setVisibility(View.GONE);

        }
        else {
            Log.d( "joinCircleVisibility: ",circleCode);
            joinGroup.setVisibility(View.VISIBLE);
            viewModel.getAdminForCircleCode();
        }*/







   /* @androidx.databinding.BindingAdapter(value={"android:onClickItem","android:viewModel","android:model","android:dialog"},requireAll=false)
    public static void setSelectedCircleName(ConstraintLayout  layout,HomePageViewModel viewModel ,UserDetailsPojo pojo, CircleNameDialogRecyclerView dialogFragment){
*//*
android:viewModel="@{viewModel}"
    android:model="@{pojo}"
    android:dialog="@{dialog}"
android:onClickItem="@{()->dialog.dismiss()}"*//*
        Log.d("selectedGroupName: ", pojo.getCircleName());
        viewModel.getSelectedInviteCodeLiveData().setValue(pojo.getInviteCode());
        viewModel.getSelectedGroupNameLiveData().setValue(pojo.getCircleName());


        Log.d("getSelectedGroupName", viewModel.getSelectedGroupNameLiveData().getValue() + "   " + viewModel.getSelectedInviteCodeLiveData().getValue());
       // dialogFragment.dismiss();
    }*/

