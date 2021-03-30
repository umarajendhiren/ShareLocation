package com.androidapps.sharelocation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.databinding.FragmentBottmNavigationDrawerBinding;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * A simple {@link Fragment} subclass.
 */


@AndroidEntryPoint
public class CircleNameDialogRecyclerView extends DialogFragment implements View.OnClickListener {
    RecyclerView recyclerView;
    CircleNameRecylerViewDialogAdapter adapter;


    HomePageViewModel homePageViewModel;

    FragmentBottmNavigationDrawerBinding dataBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homePageViewModel = new ViewModelProvider(getActivity()).get(HomePageViewModel.class);
        dataBinding = FragmentBottmNavigationDrawerBinding.inflate(inflater, container, false);

        dataBinding.setViewmodel(homePageViewModel);
        dataBinding.setActivity(this);


        this.getDialog().setCanceledOnTouchOutside(true);

        dataBinding.btnCreateCircle.setOnClickListener(this);
        dataBinding.btnJoinCircle.setOnClickListener(this);

        recyclerView = dataBinding.dialogRecycle;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);


        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        View view = dataBinding.getRoot();

        getAllCircleNameListForCurrentUser();


        return view;
    }

    private void getAllCircleNameListForCurrentUser() {


        homePageViewModel.getAllCircleName(getActivity()).observe(getActivity(), new Observer<List<UserDetailsPojo>>() {
            @Override
            public void onChanged(List<UserDetailsPojo> userDetailsPojoList) {

                adapter = new CircleNameRecylerViewDialogAdapter(userDetailsPojoList, homePageViewModel, CircleNameDialogRecyclerView.this);
                recyclerView.setAdapter(adapter);

            }
        });
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {

            case R.id.btn_create_circle:


                Intent intentCreate = new Intent(getContext(), CreateAndJoinCircleActivity.class);


                intentCreate.putExtra("createCircle", "Create");
                startActivity(intentCreate);
                dismiss();


                break;


            case R.id.btn_join_circle:

                Intent intentJoin = new Intent(getContext(), CreateAndJoinCircleActivity.class);
                intentJoin.putExtra("joinCircle", "Join");
                startActivity(intentJoin);
                dismiss();

                break;

        }
    }


}

