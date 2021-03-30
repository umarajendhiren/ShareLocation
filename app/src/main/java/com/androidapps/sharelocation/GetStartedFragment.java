package com.androidapps.sharelocation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentGetStartedBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GetStartedFragment extends Fragment implements View.OnClickListener {

    MainViewModel mainViewModel;

    FragmentGetStartedBinding viewBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //   View root = inflater.inflate(R.layout.fragment_get_started, container, false);
        viewBinding = FragmentGetStartedBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        viewBinding.buttonGetStarted.setOnClickListener(this);
        viewBinding.buttonSignIn.setOnClickListener(this);
        viewBinding.signin.setOnClickListener(this);
        viewBinding.close.setOnClickListener(this);


        View view = viewBinding.getRoot();
        return view;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_get_started) {

            //it will open Name fragment.(second fragment)
            ((MainActivity) getActivity()).setFragmentForViewPager(1);
        }

        if (view.getId() == R.id.button_sign_in) {

            viewBinding.groupSignIn.setVisibility(View.VISIBLE);
            viewBinding.groupSignUp.setVisibility(View.GONE);
        }

        if (view.getId() == R.id.close) {

            viewBinding.groupSignIn.setVisibility(View.GONE);
            viewBinding.groupSignUp.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.signin) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (viewBinding.firstName.getText().toString().trim() == null || viewBinding.password.getText().toString().trim() == null || viewBinding.lastName.getText().toString().trim() == null) {
                Toast.makeText(getActivity(), "Please enter all the fields!", Toast.LENGTH_LONG).show();

                return;
            }
            else {
                String first = viewBinding.firstName.getText().toString();
                String last = viewBinding.lastName.getText().toString();
                String pass = viewBinding.password.getText().toString().trim();

                String fullName = first + "  " + last;


                ParseUser.logInInBackground(fullName, pass, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.


                            //when new user logged in ,we need to set any one already existing  circle of user as a selected circle
                            mainViewModel.getAllCircleName();


                            mainViewModel.liveCircleName().observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
                                @Override
                                public void onChanged(List<UserDetailsPojo> userDetailsPojos) {

                                    if (userDetailsPojos.size() > 0) {


                                        mainViewModel.setSelectedCircleName(userDetailsPojos.get(0).getCircleName(), userDetailsPojos.get(0).getInviteCode());

                                        //after set selected circle name we need to show home page for user with currently selected circle name

                                        Intent intent = new Intent(getActivity(), HomaPageActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });


                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            Log.e("error: ", e.getMessage());

                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

        }
    }


}
