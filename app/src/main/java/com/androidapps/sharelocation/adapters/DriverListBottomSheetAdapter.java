package com.androidapps.sharelocation.adapters;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.androidapps.sharelocation.OnClickCallListener;
import com.androidapps.sharelocation.viewmodel.RiderViewModel;
import com.androidapps.sharelocation.model.UserDetailsPojo;

import java.util.ArrayList;
import java.util.List;


public class DriverListBottomSheetAdapter extends BaseAdapter {

    private final int layoutId;
    private RiderViewModel riderViewModel;
    private List<UserDetailsPojo> driverList = new ArrayList<>();
OnClickCallListener callListener;
//OnPhoneCallClickInterface onPhoneCallClickInterface;
    public DriverListBottomSheetAdapter(int layoutId, RiderViewModel viewModel,OnClickCallListener callListener) {
        this.layoutId = layoutId;
        riderViewModel = viewModel;
      //  this.onPhoneCallClickInterface=onPhoneCallClickInterface;
      this. callListener=callListener;

    }

    @Override
    protected Object getObjForPosition(int position) {
        return driverList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    @Override
    protected ViewModel getViewmodel() {
        return riderViewModel;
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
        return driverList == null ? 0 : driverList.size();
    }

    public void setDriverList(List<UserDetailsPojo> driverList) {
        this.driverList = driverList;
    }


   /* public interface OnPhoneCallClickInterface {

        void onCall(String userId);
    }*/


}