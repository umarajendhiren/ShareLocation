package com.androidapps.sharelocation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NoNetworkFragment extends Fragment implements View.OnClickListener {


Button refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_no_network,container,false);
        Log.d( "onCreateView: ","claed");
       // getActivity().recreate();
refresh=root.findViewById(R.id.refresh);
refresh.setOnClickListener(this);
        return root;
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.refresh){

            Log.d("onClick:","refresh");
            getActivity().recreate();
        }
    }
}
