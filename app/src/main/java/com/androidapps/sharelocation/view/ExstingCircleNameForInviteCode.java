package com.androidapps.sharelocation.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.adapters.ExistingMemberRecyclerViewAdapter;
import com.androidapps.sharelocation.databinding.ExistingCircleForInviteCodeBinding;
import com.androidapps.sharelocation.viewmodel.CreateJoinViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExstingCircleNameForInviteCode extends Fragment implements View.OnClickListener {


    TextView circleNameOfExistingCircle;

    CreateJoinViewModel createJoinViewModel;
    ExistingMemberRecyclerViewAdapter recylerViewAdapter;
    RecyclerView recyclerView;

    Button btnJoin, btnCancel;
    ExistingCircleForInviteCodeBinding databinding;

    List<UserDetailsPojo> membersList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        createJoinViewModel = new ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);
        databinding = ExistingCircleForInviteCodeBinding.inflate(inflater, container, false);

        databinding.setViewModel(createJoinViewModel);
        View view = databinding.getRoot();

        circleNameOfExistingCircle = databinding.textViewCircleName;


        recyclerView = databinding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnJoin = databinding.btnJoin;
        btnCancel = databinding.btnCancel;

        btnJoin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        membersList.clear();
        recylerViewAdapter = new ExistingMemberRecyclerViewAdapter(membersList);
        recyclerView.setAdapter(recylerViewAdapter);

        createJoinViewModel.getExistingMemberDetail(getActivity()).observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
            @Override
            public void onChanged(List<UserDetailsPojo> userDetailsPojoList) {
                if(userDetailsPojoList.size()>0) {
                    circleNameOfExistingCircle.setText("Great!You are about to join " + userDetailsPojoList.get(0).getCircleName() + " Circle");
                    recylerViewAdapter = new ExistingMemberRecyclerViewAdapter(userDetailsPojoList);
                    recyclerView.setAdapter(recylerViewAdapter);
                }
               /* else if(userDetailsPojoList.size()==0){
                    getActivity().finish();
                }*/
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_join) {

            Log.d("onClick: ","join caleed");
            createJoinViewModel.join();


            Intent intent = new Intent(getActivity(), HomaPageActivity.class);
            startActivity(intent);
           // createJoinViewModel.getSelectedFragmentInstance().setValue("Join");

        }
        if (view.getId() == R.id.btn_cancel) {

            Intent intent = new Intent(getActivity(), HomaPageActivity.class);
            startActivity(intent);

            //createJoinViewModel.getSelectedFragmentInstance().setValue("Join");

        }
    }
}
