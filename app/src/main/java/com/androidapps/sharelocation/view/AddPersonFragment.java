package com.androidapps.sharelocation.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentAddPersonBinding;
import com.androidapps.sharelocation.viewmodel.CreateJoinViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddPersonFragment extends Fragment implements OnClickListener {


    FragmentAddPersonBinding dataBinding;
    CreateJoinViewModel createJoinViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        createJoinViewModel = new  ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_person, container, false);
        @ColorInt int color = Color.parseColor("#ffd9ff04");
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        dataBinding.setViewModel(createJoinViewModel);

        createJoinViewModel.getLiveInviteCode().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {


                if(s!=null){
                    Log.d("onChanged:inviteCodeee",s);
                dataBinding.tvCode.setText(s);}
            }
        });

        dataBinding.btnShare.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_share) {
           String code = dataBinding.tvCode.getText().toString().trim();

            if (code.equals(null)) {
                return;
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, code);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Invite Code");
                startActivity(shareIntent);
            }
        }
    }
}
