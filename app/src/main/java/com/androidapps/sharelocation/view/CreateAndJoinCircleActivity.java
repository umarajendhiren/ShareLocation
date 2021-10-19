package com.androidapps.sharelocation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.ActivityCreateAndJoinCircleBinding;
import com.androidapps.sharelocation.viewmodel.CreateJoinViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateAndJoinCircleActivity extends AppCompatActivity  {

    CreateJoinViewModel createJoinViewModel;

    ActivityCreateAndJoinCircleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createJoinViewModel = new ViewModelProvider(this).get(CreateJoinViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_and_join_circle);
        binding.setViewmodel(createJoinViewModel);


        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);


       /* if (getIntent().getExtras() != null) {


            if (getIntent().getExtras().get("createCircle") != null && getIntent().getExtras().get("createCircle").equals("Create")) {
                Log.d("getExtra: ", getIntent().getExtras().get("createCircle").toString());

                ab.setTitle("Create new Circle");

                createJoinViewModel.getSelectedFragmentInstance().setValue("Create");


            } else if (getIntent().getExtras().get("joinCircle") != null && getIntent().getExtras().get("joinCircle").equals("Join")) {
                Log.d("getExtra: ", getIntent().getExtras().get("joinCircle").toString());
                ab.setTitle("Join a Circle");

                createJoinViewModel.getSelectedFragmentInstance().setValue("Join");

            } else if (getIntent().getExtras().get("addPerson") != null && getIntent().getExtras().get("addPerson").equals("Add")) {
                Log.d("getExtra: ", getIntent().getExtras().get("addPerson").toString());
                ab.setTitle("Invite Code");
                createJoinViewModel.getSelectedFragmentInstance().setValue("AddPerson");

            } else if (getIntent().getExtras().get("objectId") != null) {
                Log.d("getExtra: ", getIntent().getExtras().get("objectId").toString());
                ab.setTitle("Group Members");
                createJoinViewModel.getSelectedFragmentInstance().setValue("Members");


            } else if (getIntent().getExtras().get("AddDp") != null) {
                Log.d("getExtra: ", getIntent().getExtras().get("AddDp").toString());
                ab.setTitle("Add Profile Picture for Circle");
                createJoinViewModel.getSelectedFragmentInstance().setValue("AddDp");


            }

            else if (getIntent().getExtras().get("NoNetwork") != null) {
                Log.d("getExtra: ", getIntent().getExtras().get("NoNetwork").toString());
                ab.setTitle("Unable to connect");
                createJoinViewModel.getSelectedFragmentInstance().setValue("NoNetwork");


            }
        }*/



        createJoinViewModel.isConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                Log.d( "onChanged:connecte", String.valueOf(connected));
                if(!connected){

                    ab.setTitle("Unable to connect");
                    createJoinViewModel.getSelectedFragmentInstance().setValue("NoNetwork");
                   // recreate();
                }

                else if (getIntent().getExtras() != null && connected) {


                    if (getIntent().getExtras().get("createCircle") != null && getIntent().getExtras().get("createCircle").equals("Create")) {
                        Log.d("getExtra: ", getIntent().getExtras().get("createCircle").toString());

                        ab.setTitle("Create new Circle");

                        createJoinViewModel.getSelectedFragmentInstance().setValue("Create");


                    } else if (getIntent().getExtras().get("joinCircle") != null && getIntent().getExtras().get("joinCircle").equals("Join")) {
                        Log.d("getExtra: ", getIntent().getExtras().get("joinCircle").toString());
                        ab.setTitle("Join a Circle");

                        createJoinViewModel.getSelectedFragmentInstance().setValue("Join");

                    } else if (getIntent().getExtras().get("addPerson") != null && getIntent().getExtras().get("addPerson").equals("Add")) {
                        Log.d("getExtra: ", getIntent().getExtras().get("addPerson").toString());
                        ab.setTitle("Invite Code");
                        createJoinViewModel.getSelectedFragmentInstance().setValue("AddPerson");

                    } else if (getIntent().getExtras().get("objectId") != null) {
                        Log.d("getExtra: ", getIntent().getExtras().get("objectId").toString());
                        ab.setTitle("Group Members");
                        createJoinViewModel.getSelectedFragmentInstance().setValue("Members");


                    } else if (getIntent().getExtras().get("AddDp") != null) {
                        Log.d("getExtra: ", getIntent().getExtras().get("AddDp").toString());
                        ab.setTitle("Add Profile Picture for Circle");
                        createJoinViewModel.getSelectedFragmentInstance().setValue("AddDp");


                    }

                   /* else if (getIntent().getExtras().get("NoNetwork") != null) {
                        Log.d("getExtra: ", getIntent().getExtras().get("NoNetwork").toString());
                        ab.setTitle("Unable to connect");
                        createJoinViewModel.getSelectedFragmentInstance().setValue("NoNetwork");


                    }*/
                }
            }
        });




        createJoinViewModel.getSelectedFragmentInstance().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d("onChangedmmmm",s);
                if(s.equals("Members")){

                    binding.groupExistingCircle.setVisibility(View.VISIBLE);

                    binding.groupCreate.setVisibility(View.GONE);
                    binding.groupJoin.setVisibility(View.GONE);
                    binding.groupAddPerson.setVisibility(View.GONE);
                    binding.groupAddDp.setVisibility(View.GONE);
                    binding.groupNoNetwork.setVisibility(View.GONE);
                }

                else if(s.equals("Create")){

                    binding.groupCreate.setVisibility(View.VISIBLE);

                    binding.groupExistingCircle.setVisibility(View.GONE);
                    binding.groupJoin.setVisibility(View.GONE);
                    binding.groupAddPerson.setVisibility(View.GONE);
                    binding.groupAddDp.setVisibility(View.GONE);
                    binding.groupNoNetwork.setVisibility(View.GONE);

                }

                else if(s.equals("Join")){
                    binding.groupJoin.setVisibility(View.VISIBLE);

                    binding.groupCreate.setVisibility(View.GONE);
                    binding.groupAddPerson.setVisibility(View.GONE);
                    binding.groupAddDp.setVisibility(View.GONE);
                    binding.groupExistingCircle.setVisibility(View.GONE);
                    binding.groupNoNetwork.setVisibility(View.GONE);

                }

                else if(s.equals("AddPerson")){
                    binding.groupAddPerson.setVisibility(View.VISIBLE);

                    binding.groupCreate.setVisibility(View.GONE);
                    binding.groupJoin.setVisibility(View.GONE);
                    binding.groupAddDp.setVisibility(View.GONE);
                    binding.groupExistingCircle.setVisibility(View.GONE);
                    binding.groupNoNetwork.setVisibility(View.GONE);


                }

                else if(s.equals("AddDp")){
                    binding.groupAddDp.setVisibility(View.VISIBLE);

                    binding.groupCreate.setVisibility(View.GONE);
                    binding.groupJoin.setVisibility(View.GONE);
                    binding.groupAddPerson.setVisibility(View.GONE);
                    binding.groupExistingCircle.setVisibility(View.GONE);
                    binding.groupNoNetwork.setVisibility(View.GONE);


                }

                else if(s.equals("NoNetwork")){


                    binding.groupNoNetwork.setVisibility(View.VISIBLE);

                    binding.groupAddDp.setVisibility(View.GONE);
                    binding.groupCreate.setVisibility(View.GONE);
                    binding.groupJoin.setVisibility(View.GONE);
                    binding.groupAddPerson.setVisibility(View.GONE);
                    binding.groupExistingCircle.setVisibility(View.GONE);

                }


            }
        });


    }


}
