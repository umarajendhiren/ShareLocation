package com.androidapps.sharelocation.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentCircleCodeBinding;
import com.androidapps.sharelocation.utilities.Utilities;
import com.androidapps.sharelocation.viewmodel.MainViewModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static com.parse.Parse.getApplicationContext;

@AndroidEntryPoint
public class CircleCodeFragment extends Fragment implements View.OnClickListener {
    MainViewModel viewModel;


    FragmentCircleCodeBinding viewBinding;
    String code;
    String inviteCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        viewBinding = FragmentCircleCodeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        View root = viewBinding.getRoot();


        viewBinding.btnShare.setOnClickListener(this);
        viewBinding.btnDone.setOnClickListener(this);




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
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment",7);

                editor.apply();



                Toast.makeText( getActivity(),"Press again to exit!",Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);








        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

        if(!connected){

            Log.d( "error","No network!");

            ((MainActivity) getActivity()).setFragmentForViewPager(12);
           /* NavDirections action =CircleCodeFragmentDirections.actionCircleCodeFragmentToProfilePhotoFragment2();


            Navigation.findNavController(root).navigate(action);*/


        }

        else {

            getUniqueCode();

        }



        return root;
    }

    private void getUniqueCode() {

         inviteCode = Utilities.getUniqueId();
        inviteCode = inviteCode.substring(0, 7);


        ParseQuery<ParseObject> queryCircleName = new ParseQuery<ParseObject>("CircleName");
        queryCircleName.whereEqualTo("password", inviteCode);

        queryCircleName.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                //if it is unique code it should be 0 object retrieved.
                if (e == null && objects.size() == 0) {

                    viewBinding.tvCode.setText(inviteCode);

                } else if (objects.size() > 0) {


                    Log.d("invite code", "already exist!");
                    getUniqueCode();
                }
            }


        });
    }





    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_done) {

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            code = viewBinding.tvCode.getText().toString().trim();
            viewModel.getCircleCodeLiveDataInstance().setValue(code);


           // viewModel.getCircleCodeLiveData();






            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

            if(!connected){

                Log.d( "error","No network!");

                ((MainActivity) getActivity()).setFragmentForViewPager(12);
               /* Navigation.findNavController(view)
                        .navigate(R.id.action_circleCodeFragment_to_NoNetworkFragment);*/
            }

            else {

                viewModel.createNewCircle();
                ((MainActivity) getActivity()).setFragmentForViewPager(8);
               /* Navigation.findNavController(view)
                        .navigate(R.id.action_circleCodeFragment_to_profilePhotoFragment2);*/

            }

        }

        if (view.getId() == R.id.btn_share) {
            code = viewBinding.tvCode.getText().toString().trim();

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
