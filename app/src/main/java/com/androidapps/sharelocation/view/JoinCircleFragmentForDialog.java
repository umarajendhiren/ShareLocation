
package com.androidapps.sharelocation.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentJoinCircleForDialogBinding;
import com.androidapps.sharelocation.viewmodel.CreateJoinViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JoinCircleFragmentForDialog extends Fragment implements View.OnClickListener {

    EditText editTextInviteCode;
    Button btnSubmit;

    CreateJoinViewModel createJoinViewModel;
    FragmentJoinCircleForDialogBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        createJoinViewModel = new ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);


        binding = FragmentJoinCircleForDialogBinding.inflate(inflater, container, false);
        editTextInviteCode = binding.editInviteCode;
        btnSubmit = binding.btnSubmit;

        btnSubmit.setOnClickListener(this);
        View view = binding.getRoot();


        return view;
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.btn_submit) {

            if (editTextInviteCode.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter invite code!", Toast.LENGTH_LONG).show();
                return;
            }

            boolean isValid = createJoinViewModel.isItValidPassword(editTextInviteCode.getText().toString().trim());
            Log.d("onClick:isValid", String.valueOf(isValid));
/*
            if (isValid) {


                createJoinViewModel.isAlreadyInCircle().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isAlreadyInCircle) {

                        Log.d("onChangedvalid1", String.valueOf(isAlreadyInCircle));
                        if (isAlreadyInCircle) {
                            Log.d("isAlreadyInCircle","HomaPageActivity");

                            Intent intent = new Intent(getActivity(), HomaPageActivity.class);
                            startActivity(intent);


                        } else  if (!isAlreadyInCircle) {
                            Log.d("isAlreadyInCircle","CreateAndJoinCircleActivity");

                            createJoinViewModel.getSelectedFragmentInstance().setValue("Members");

                            Intent intentToGetExistingCircle = new Intent(getActivity(), CreateAndJoinCircleActivity.class);

                            startActivity(intentToGetExistingCircle);

                        }
                    }

                });

            } else if (!isValid) {

                createJoinViewModel.getSelectedFragmentInstance().setValue("Join");
                Intent intentToGetExistingCircle = new Intent(getActivity(), CreateAndJoinCircleActivity.class);

                startActivity(intentToGetExistingCircle);

            }*/
            createJoinViewModel.isItValidCode().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isValid) {

                    Log.d("onChangedvalid", String.valueOf(isValid));
                    if (isValid) {


                        createJoinViewModel.isAlreadyInCircle().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean isAlreadyInCircle) {


                                if (isAlreadyInCircle) {
                                    Log.d("onChangedvalid1", String.valueOf(isAlreadyInCircle));

                                    Intent intent = new Intent(getActivity(), HomaPageActivity.class);
                                    startActivity(intent);
                                  //  getActivity().finish();

                                    createJoinViewModel.isItValidCode().setValue(false);
                                    createJoinViewModel.isAlreadyInCircle().setValue(false);

                                   // createJoinViewModel.getSelectedFragmentInstance().setValue("Join");



                                } else {

                                    Log.d("onChangedvalid2", String.valueOf(isAlreadyInCircle));
                                    createJoinViewModel.getSelectedFragmentInstance().setValue("Members");

                                   /* Intent intentToGetExistingCircle = new Intent(getActivity(), CreateAndJoinCircleActivity.class);

                                    startActivity(intentToGetExistingCircle);*/

                                }
                            }

                        });

                    } else if (!isValid) {
                        Log.d("onChangedvalid5", String.valueOf(isValid));

                        createJoinViewModel.getSelectedFragmentInstance().setValue("Join");

                       /* Intent intentToGetExistingCircle = new Intent(getActivity(), CreateAndJoinCircleActivity.class);

                        startActivity(intentToGetExistingCircle);*/

                    }
                }
            })
            ;


        }
    }
}



