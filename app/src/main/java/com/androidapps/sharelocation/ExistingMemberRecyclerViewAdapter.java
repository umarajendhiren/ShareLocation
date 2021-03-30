package com.androidapps.sharelocation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.databinding.ExistingMemberListBinding;

import java.util.ArrayList;
import java.util.List;

public class ExistingMemberRecyclerViewAdapter extends RecyclerView.Adapter<ExistingMemberRecyclerViewAdapter.UserViewHolder> {


    ExistingMemberListBinding databinding;

    List<UserDetailsPojo> userDetailsPojos = new ArrayList<>();

    public ExistingMemberRecyclerViewAdapter(List<UserDetailsPojo> userDetailsList) {



        userDetailsPojos = userDetailsList;


    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ExistingMemberListBinding itemBinding = ExistingMemberListBinding.inflate(inflater, parent, false);
        return new UserViewHolder(itemBinding);
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
        public UserViewHolder(@NonNull ExistingMemberListBinding itemBinding) {
            super(itemBinding.getRoot());
            databinding = itemBinding;

        }


        public void Bind(UserDetailsPojo pojo) {
            databinding.setPojo(pojo);
            databinding.executePendingBindings();

        }
    }


}



