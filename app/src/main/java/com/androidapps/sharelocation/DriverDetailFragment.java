package com.androidapps.sharelocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentDriverDetailBinding;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverDetailFragment extends Fragment implements View.OnClickListener {

    FragmentDriverDetailBinding binding;
    RiderViewModel riderViewModel;
    private ParseFile imageFile;
    private Bitmap imagebitmap;
    String driverObjectId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        riderViewModel = new ViewModelProvider(requireActivity()).get(RiderViewModel.class);
        binding = FragmentDriverDetailBinding.inflate(inflater, container, false);
       /* binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);*/

        riderViewModel.getDriverCode().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String driverId) {
                Log.d("onChanged:driver", driverId);

                getDriverDetail(driverId);

                driverObjectId = driverId;
            }
        });

        binding.btnJoin.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        View root = binding.getRoot();
        return root;
    }

    private void getDriverDetail(String driverId) {
        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();

        queryUser.getInBackground(driverId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    imageFile = object.getParseFile("profilepicture");


                    if (imageFile != null) {


                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {


                                if (e == null && data != null) {


                                    //this is bitmap of parse file
                                    imagebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    binding.adminName.setText(object.getUsername());
                                    binding.adminDp.setImageBitmap(imagebitmap);

                                }
                            }
                        });
                    }

                    if (imageFile == null) {

                        String driverName = object.getUsername();

                        String firstLetterOfUSer = String.valueOf(driverName.charAt(0));

                        String firstLetterToUpperCase = firstLetterOfUSer.toUpperCase();

                        Bitmap dpWithText = Utilities.getDrawableDpWithText(getActivity(), firstLetterToUpperCase);

                        binding.adminName.setText(driverName);
                        binding.adminDp.setImageBitmap(dpWithText);
                    }


                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_join) {


            // ((RiderActivity) getActivity()).setFragment(2);

            AddDriverInUserDriverList(driverObjectId);

            Intent intent = new Intent(getActivity(), HomaPageActivity.class);
            intent.putExtra("DriverMap", "DriverMap");
            startActivity(intent);


        }

        if (view.getId() == R.id.btn_cancel) {


            getActivity().finish();
        }

    }

    private void AddDriverInUserDriverList(String driverObjectId) {

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                JSONArray driverList = object.getJSONArray("DriverList");
                Log.d("doneDriverList", String.valueOf(driverList));

                if (driverList != null) {

                    for (int i = 0; i < driverList.length(); i++) {

                        try {
                            if (driverList.get(i).equals(driverObjectId)) {
                                Toast.makeText(getActivity(), "Already you have added this Driver !", Toast.LENGTH_LONG).show();
                                return;
                            } else if (driverList.get(i).equals(ParseUser.getCurrentUser().getObjectId())) {

                                Toast.makeText(getActivity(), "Please enter valid driver code!", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }

                    }


                    driverList.put(driverObjectId);
                    object.put("DriverList", driverList);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {


                            if(e==null){
                                //after added driver,subscribe for driver message
                                riderViewModel.subscribeForDriverChannel(driverObjectId);

                            }
                        }
                    });




                    //after added driver in driver list,add rider id in driver object.

                  //  AddRiderInDriverObject(driverObjectId);


                } else if (driverList == null) {
                    Log.d("AddDriverIn", "null");
                    JSONArray driversArray = new JSONArray();
                    driversArray.put(driverObjectId);
                    object.put("DriverList", driversArray);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {


                            if(e==null){
                                //after added driver,subscribe for driver message
                                riderViewModel.subscribeForDriverChannel(driverObjectId);

                            }
                        }
                    });
                }


            }
        });


    }

    private void AddRiderInDriverObject(String driverObjectId) {

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        parseQuery.getInBackground(driverObjectId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    JSONArray riderList = object.getJSONArray("RiderList");
                    Log.d("riderList", String.valueOf(riderList));


                    if (riderList != null) {

                        riderList.put(ParseUser.getCurrentUser().getObjectId());
                        object.put("RiderList", riderList);
                        object.saveInBackground();

                    } else if (riderList == null) {

                        Log.d("AddRider", "null");
                        JSONArray riderArray = new JSONArray();
                        riderArray.put(ParseUser.getCurrentUser().getObjectId());
                        object.put("RiderList", riderArray);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){

                                    Log.d( "rider","Added");
                                }

                                else {

                                    Log.d("done:e",e.getMessage());
                                }
                            }
                        });
                    }
                }


            }

        });
    }
}
