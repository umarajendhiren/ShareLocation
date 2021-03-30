package com.androidapps.sharelocation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentNameBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NameFragment extends Fragment implements View.OnClickListener {


    MainViewModel mainViewModel;
    Button btnContinue;
    EditText firstName, lastName;

    FragmentNameBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = FragmentNameBinding.inflate(inflater, container, false);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);


        firstName = binding.editFirstName;
        lastName = binding.editLastName;
        binding.btnContinue.setOnClickListener(this);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onClick(View view) {
        String enteredFirstName = firstName.getText().toString().trim();
        String enteredlastName = lastName.getText().toString().trim();


        if (view.getId() == R.id.btn_continue) {

            InputMethodManager imm = (InputMethodManager)
                   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (enteredFirstName.equals("") || enteredlastName.equals("")) {
                Toast.makeText(getActivity(), "Please enter first name and last name!", Toast.LENGTH_SHORT).show();
                return;
            }


            ((MainActivity) getActivity()).setFragmentForViewPager(2);
        }
    }
}
