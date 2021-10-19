package com.androidapps.sharelocation.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.databinding.GroupMemberListBinding;
import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.UserViewHolder> {


    List<UserDetailsPojo> userDetailsPojos = new ArrayList<>();
    GroupMemberListBinding listBinding;
    HomePageViewModel homePageViewModel;

    public RecylerViewAdapter(List<UserDetailsPojo> userDetailsList, HomePageViewModel viewModel) {

        homePageViewModel = viewModel;
        userDetailsPojos = userDetailsList;


    }


    @NonNull
    @Override
    public RecylerViewAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        GroupMemberListBinding itemBinding = GroupMemberListBinding.inflate(inflater, parent, false);
        return new RecylerViewAdapter.UserViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UserDetailsPojo pojo = userDetailsPojos.get(position);
        holder.Bind(pojo);

    }

    @Override
    public int getItemCount() {
        return userDetailsPojos.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public UserViewHolder(@NonNull GroupMemberListBinding itemBinding) {
            super(itemBinding.getRoot());
            listBinding = itemBinding;

        }


        public void Bind(UserDetailsPojo pojo) {
            listBinding.setPojo(pojo);
            listBinding.setViewModel(homePageViewModel);
            listBinding.executePendingBindings();

        }
    }


}
