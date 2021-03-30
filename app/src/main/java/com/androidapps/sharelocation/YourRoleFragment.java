package com.androidapps.sharelocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class YourRoleFragment extends Fragment implements View.OnClickListener {
    MainViewModel mainViewModel;
    Button btnMom, btnDad, btnSon, btnSpouse, btnGrandparent, btnFriend, btnOther;

    @Override
    public void onResume() {
        super.onResume();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_your_role, container, false);
        btnMom = root.findViewById(R.id.btn_mom);
        btnDad = root.findViewById(R.id.btn_dad);
        btnSon = root.findViewById(R.id.btn_son);
        btnSpouse = root.findViewById(R.id.btn_spouse);
        btnGrandparent = root.findViewById(R.id.btn_grandparent);
        btnFriend = root.findViewById(R.id.btn_friend);
        btnOther = root.findViewById(R.id.btn_other);

        btnMom.setOnClickListener(this);
        btnDad.setOnClickListener(this);
        btnSon.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnSpouse.setOnClickListener(this);
        btnGrandparent.setOnClickListener(this);
        btnOther.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_mom:
                mainViewModel.getYourRoleLiveDataInstance().setValue("Mom");
                break;
            case R.id.btn_dad:
                mainViewModel.getYourRoleLiveDataInstance().setValue("Dad");
                break;
            case R.id.btn_son:
                mainViewModel.getYourRoleLiveDataInstance().setValue("son/Daughter");
                break;
            case R.id.btn_spouse:
                mainViewModel.getYourRoleLiveDataInstance().setValue("Spouse/Partner");
                break;
            case R.id.btn_grandparent:
                mainViewModel.getYourRoleLiveDataInstance().setValue("Grandparent");
                break;
            case R.id.btn_friend:
                mainViewModel.getYourRoleLiveDataInstance().setValue("friend");
                break;
            case R.id.btn_other:
                mainViewModel.getYourRoleLiveDataInstance().setValue("other");
                break;

        }


        mainViewModel.getYourRoleLiveData();
        ((MainActivity) getActivity()).setFragmentForViewPager(9);

    }
}
