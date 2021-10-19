package com.androidapps.sharelocation.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.Group;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentCircleNameBinding;
import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.viewmodel.MainViewModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.parse.Parse.getApplicationContext;

@AndroidEntryPoint
public class CircleNameFragment extends Fragment implements View.OnClickListener {
    View root;
    Button btnContinue, joinWithGroup, cancelToJoin;
    EditText editCircleName;
    MainViewModel mainViewModel;
    Group existingCircle, newCircle;
    TextView circleNameForEnteredCode, tvAdminName;

    String circleCodeToJoin;
    CircleImageView adminDpImageView;


    FragmentCircleNameBinding dataBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_circle_name, container, false);
        dataBinding.setViewModel(mainViewModel);

        //if the user entered circle code to join,we can get it from viewmodel.so based on that we can set visibility of layout.


        existingCircle = dataBinding.groupExistingCircle;


        newCircle = dataBinding.groupNewCircle;


        btnContinue = dataBinding.btnContinue;


        editCircleName = dataBinding.editCircleName;

        btnContinue.setOnClickListener(this);


        //view in existing circle name group
        circleNameForEnteredCode = dataBinding.textViewCircleName;
        tvAdminName = dataBinding.adminName;
        adminDpImageView = dataBinding.adminDp;
        joinWithGroup = dataBinding.btnJoin;
        cancelToJoin = dataBinding.btnCancel;

        joinWithGroup.setOnClickListener(this);
        cancelToJoin.setOnClickListener(this);

        root = dataBinding.getRoot();



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
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment",6);

                editor.apply();



                Toast.makeText( getActivity(),"Press again to exit!",Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (mainViewModel.createOrjoin.getValue() != null) {

            if (mainViewModel.createOrjoin.getValue().equals("create")) {

                Log.d("onResumecreate: ", mainViewModel.createOrjoin.getValue());
                existingCircle.setVisibility(View.GONE);
                newCircle.setVisibility(View.VISIBLE);


            } else if (mainViewModel.createOrjoin.getValue().equals("join")) {
                Log.d("onResumejoin: ", mainViewModel.createOrjoin.getValue());









                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if(!connected){

                    Log.d( "error","No network!");

                    ((MainActivity) getActivity()).setFragmentForViewPager(12);
                   /* NavDirections action =CircleNameFragmentDirections.actionCircleNameFragmentToNoNetworkFragment();

                    Navigation.findNavController(root).navigate(action);*/
                }

                else {

                    getAdminForCircleCode();
                }




            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String enteredCircleName = editCircleName.getText().toString();

            if(enteredCircleName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter circle name!", Toast.LENGTH_SHORT).show();
                return;
            }
            mainViewModel.getCircleNameLiveDataInstance().setValue(enteredCircleName);
           // mainViewModel.getCircleNameLiveData();

            ((MainActivity) getActivity()).setFragmentForViewPager(7);

            /*Navigation.findNavController(view)
                    .navigate(R.id.action_circleNameFragment_to_circleCodeFragment);*/
        }

        if (view.getId() == R.id.btn_join) {




            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

            if(!connected){

                Log.d( "error","No network!");

                ((MainActivity) getActivity()).setFragmentForViewPager(12);

                /*Navigation.findNavController(view)
                        .navigate(R.id.action_circleNameFragment_to_NoNetworkFragment);*/
            }

            else {

                mainViewModel.joinWithCircle();
                ((MainActivity) getActivity()).setFragmentForViewPager(8);
               /* Navigation.findNavController(view)
                        .navigate(R.id.action_circleNameFragment_to_profilePhotoFragment2);*/
            }








        }

        if (view.getId() == R.id.btn_cancel) {

            ((MainActivity) getActivity()).setFragmentForViewPager(5);
           /* Navigation.findNavController(view)
                    .navigate(R.id.action_circleNameFragment_to_CreateNewCircle);*/


        }
    }


    private void getAdminForCircleCode() {
        circleCodeToJoin = mainViewModel.getCircleCodeToJoinLiveData().getValue();
        if (circleCodeToJoin.isEmpty())
            return;
        else {

            /*need to query admin detail from circleName class*/


            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("CircleName");
            parseQuery.whereEqualTo("password", circleCodeToJoin);
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (objects.size() == 0) {

                        Toast.makeText(getActivity(), "Please enter valid circle code!", Toast.LENGTH_LONG).show();

                        ((MainActivity) getActivity()).setFragmentForViewPager(5);

                      /*  NavDirections action =CircleNameFragmentDirections.actionCircleNameFragmentToCreateNewCircle();

                        Navigation.findNavController(root).navigate(action);*/
                    } else if (e == null && objects.size() > 0) {

                        newCircle.setVisibility(View.GONE);
                        existingCircle.setVisibility(View.VISIBLE);

                        String adminId = String.valueOf(objects.get(0).get("adminId"));
                        String circleName = String.valueOf(objects.get(0).get("circleName"));


                        mainViewModel.getAdminDetailFromUserObject(adminId, circleName, circleCodeToJoin, getActivity()).observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
                            @Override
                            public void onChanged(List<UserDetailsPojo> userDetailsPojos) {

                                circleNameForEnteredCode.setText("Great!You're about to join the " + circleName + " Circle");
                                tvAdminName.setText(userDetailsPojos.get(0).getUserName());
                                adminDpImageView.setImageBitmap(userDetailsPojos.get(0).getUserDp());

                            }
                        });

                    }
                }
            });


        }
    }
}
