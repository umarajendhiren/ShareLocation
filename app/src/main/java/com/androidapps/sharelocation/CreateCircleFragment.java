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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.parse.ParseUser;

import dagger.hilt.android.AndroidEntryPoint;

import static com.parse.Parse.getApplicationContext;
import static java.lang.Character.toUpperCase;

@AndroidEntryPoint
public class CreateCircleFragment extends Fragment implements View.OnClickListener {

    Button btnContinue;
    TextView tvTitle;
    IdlingResourceForTest mIdlingResource;

    MainViewModel viewModel;
    private boolean isEnabled;

   /* public void onResume() {`
        super.onResume();
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        getUserNameFromCloud();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_circle, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

      /*  FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            Log.d( "backstack:", String.valueOf(i));
            fm.popBackStack();
        }*/



        btnContinue = root.findViewById(R.id.btn_continue);
        tvTitle = root.findViewById(R.id.tv_title);
        btnContinue.setOnClickListener(this);


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
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment",4);

                editor.apply();



                Toast.makeText( getActivity(),"Press again to exit!",Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);



/*
requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
    @Override
    public void handleOnBackPressed() {

        isEnabled=true;
        Log.d( "handleOnBackPressed: ","called");



        callback.setEnabled(false);
       */
/* FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack("PasswordFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);*//*

   FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            Log.d( "backstack:", String.valueOf(i));
            fm.popBackStack();
           requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback() {
               @Override
               public void handleOnBackPressed() {

               }
           });
        }

        // if you want onBackPressed() to be called as normal afterwards
      */
/*  if (isEnabled) {
            isEnabled = false;
            requireActivity().onBackPressed();
        }*//*


    }
});
*/



        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

        if(!connected){

            Log.d( "error","No network!");

            ((MainActivity) getActivity()).setFragmentForViewPager(12);
        }

        else {

            getUserNameFromCloud();
        }
        return root;
    }

    private void getUserNameFromCloud() {

       /* if (viewModel.getIdlingResourceInstance() != null) {
            mIdlingResource = viewModel.getIdlingResourceInstance();

        }*/

        // The IdlingResource is null in production.
      /*  if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }*/
        String userName = ParseUser.getCurrentUser().getUsername();

        String[] username = userName.split(" ", 2);

        String firstName = username[0];


        Character firstLetter = firstName.charAt(0);


        String formattedName = toUpperCase(firstLetter) + firstName.substring(1);


        tvTitle.setText("Hi " + formattedName + "! Now you can join or create your Circle");
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {
            ((MainActivity) getActivity()).setFragmentForViewPager(5);
        }
    }


}
