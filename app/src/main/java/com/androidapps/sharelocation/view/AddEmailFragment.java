package com.androidapps.sharelocation.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentEmailBinding;
import com.androidapps.sharelocation.viewmodel.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddEmailFragment extends Fragment implements View.OnClickListener {

    FragmentEmailBinding viewBinding;

    MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        viewBinding = FragmentEmailBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewBinding.btnContinue.setOnClickListener(this);
        viewBinding.noEmail.setOnClickListener(this);


        View view = viewBinding.getRoot();
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String enteredMail = viewBinding.editEmail.getText().toString().trim();

            if (enteredMail.equals("")) {
                Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            mainViewModel.getEmailLiveDataInstance().setValue(enteredMail);
            ((MainActivity) getActivity()).setFragmentForViewPager(3);
/*
            Navigation.findNavController(view)
                    .navigate(R.id.action_EmailFragment_to_PasswordFragment);*/

        }

        if(view.getId()==R.id.no_email){

            mainViewModel.getEmailLiveDataInstance().setValue("SignUpWithoutEmail");
            ((MainActivity) getActivity()).setFragmentForViewPager(3);
           /* Navigation.findNavController(view)
                    .navigate(R.id.action_EmailFragment_to_PasswordFragment);*/

        }

    }


}
