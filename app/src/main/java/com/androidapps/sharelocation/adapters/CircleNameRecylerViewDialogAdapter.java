package com.androidapps.sharelocation.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.databinding.DialogCircleListBinding;

import java.util.ArrayList;
import java.util.List;

public class CircleNameRecylerViewDialogAdapter extends RecyclerView.Adapter<CircleNameRecylerViewDialogAdapter.GroupNameViewHolder> {
    List<UserDetailsPojo> circleNameList = new ArrayList<>();
    CircleNameDialogRecyclerView dialogFragment;
    DialogCircleListBinding databinding;
    public HomePageViewModel homePageViewModel;

    public CircleNameRecylerViewDialogAdapter(List<UserDetailsPojo> groupName, HomePageViewModel viewModel, CircleNameDialogRecyclerView fragment) {
        homePageViewModel = viewModel;
        circleNameList = groupName;
        dialogFragment = fragment;
    }

    @NonNull
    @Override
    public GroupNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        DialogCircleListBinding itemBinding = DialogCircleListBinding.inflate(inflater, parent, false);
        return new GroupNameViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupNameViewHolder holder, int position) {

        Log.d("onBindViewHolder: ", circleNameList.get(position).getCircleName());
        Log.d("onBindViewHolder: ", circleNameList.get(position).getInviteCode());
        UserDetailsPojo pojo = circleNameList.get(position);
        holder.Bind(pojo);


    }

    @Override
    public int getItemCount() {
        return circleNameList.size();
    }

    public class GroupNameViewHolder extends RecyclerView.ViewHolder {
        public GroupNameViewHolder(@NonNull DialogCircleListBinding itemBinding) {
            super(itemBinding.getRoot());
            databinding = itemBinding;

        }


        public void Bind(UserDetailsPojo pojo) {
            databinding.setPojo(pojo);
            databinding.setViewModel(homePageViewModel);
            databinding.setDialog(dialogFragment);
            databinding.executePendingBindings();

        }
    }


}
