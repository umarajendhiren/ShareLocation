package com.androidapps.sharelocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentInviteCodeBinding;

import dagger.hilt.android.AndroidEntryPoint;

import static com.parse.Parse.getApplicationContext;

@AndroidEntryPoint
public class CreateNewCircle extends Fragment implements View.OnClickListener {

    MainViewModel mainViewModel;
    FragmentInviteCodeBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite_code, container, false);
        binding.setViewModel(mainViewModel);
        View view = binding.getRoot();



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d( "handleOnBackPressed: ","called");
                // Handle the back button event
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    Log.d("backstack:", String.valueOf(i));
                    fm.popBackStack();


                }

                SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = selectedFragment.edit();
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment",5);

                editor.apply();



                Toast.makeText( getActivity(),"Press again to exit!",Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);






        binding.btnCreateCircle.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_create_circle) {
            mainViewModel.createOrjoin.setValue("create");

            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

            if(!connected){

                Log.d( "error","No network!");

                ((MainActivity) getActivity()).setFragmentForViewPager(12);
            }

            else {

                ((MainActivity) getActivity()).setFragmentForViewPager(6);
            }

        }

        if (view.getId() == R.id.btn_submit) {

            if (binding.editCode.getText().toString().trim().isEmpty()) {

                Toast.makeText(getActivity(), "Please enter invite code!", Toast.LENGTH_LONG).show();
                return;
            } else {
                mainViewModel.createOrjoin.setValue("join");
                mainViewModel.getCircleCodeToJoinLiveDataInstance().setValue(binding.editCode.getText().toString().trim());









                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if(!connected){

                    Log.d( "error","No network!");

                    ((MainActivity) getActivity()).setFragmentForViewPager(12);
                }

                else {

                    ((MainActivity) getActivity()).setFragmentForViewPager(6);
                }
            }
        }


    }
}
