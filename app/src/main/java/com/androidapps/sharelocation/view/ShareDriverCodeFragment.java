package com.androidapps.sharelocation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.viewmodel.MainViewModel;
import com.parse.ParseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShareDriverCodeFragment extends Fragment implements View.OnClickListener {


    MainViewModel mainViewModel;

    Button doneSharing,shareCode;
    TextView driverCode;
    String driverId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_code, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        driverCode = view.findViewById(R.id.tv_code);

        doneSharing=view.findViewById(R.id.btn_done);
        shareCode=view.findViewById(R.id.btn_share);

        doneSharing.setOnClickListener(this);
        shareCode.setOnClickListener(this);

         driverId = ParseUser.getCurrentUser().getObjectId();

        driverCode.setText(driverId);



        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_share) {


            if (driverId.equals(null)) {
                return;
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, driverId);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Invite Code");
                startActivity(shareIntent);
            }
        }

        if(view.getId()==R.id.btn_done){

            //show map fragment
           // ((DriverActivity) getActivity()).setFragment(2);
            getActivity().finish();

        }
    }

}



