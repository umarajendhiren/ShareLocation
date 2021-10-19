package com.androidapps.sharelocation.SnapToRoadApiClient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.adapters.RecylerViewAdapter;
import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExerciseFragmet extends BottomSheetDialogFragment  {


    BottomSheetBehavior bottomSheet;
    HomePageViewModel homePageViewModel;
    private RecylerViewAdapter mAdapter;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.botton_sheet_with_view_pager,container,false);

        homePageViewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);

        recyclerView =view.findViewById(R.id.rc_item_shelf_book);
        View rootLayout = view.findViewById(R.id.BookInfoFragment);

//      bottomSheet= BottomSheetBehavior.from(rootLayout);
//
//        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        return view;
    }


    private void ObserveUserDetailLiveData() {

        homePageViewModel.getGroupMemberList().observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
            @Override
            public void onChanged(List<UserDetailsPojo> userDetailsPojoList) {
                Log.d("onChanged:re ", String.valueOf(userDetailsPojoList.size()));
                //  Log.d("onChanged:re 0", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                //Log.d("onChanged:re 1", String.valueOf(userDetailsPojoList.get(0).getUserName()));

                mAdapter = new RecylerViewAdapter(userDetailsPojoList, homePageViewModel);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

}
