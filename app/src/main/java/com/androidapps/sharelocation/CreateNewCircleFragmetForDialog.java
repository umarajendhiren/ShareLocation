package com.androidapps.sharelocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateNewCircleFragmetForDialog extends Fragment implements View.OnClickListener {
    ListView listViewSuggesion;
    Button btnSubmit;
    CreateJoinViewModel viewModel;
    EditText circleNameEditText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_create_new_circle_fragmet_for_dialog, container, false);


        viewModel = new ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);
        listViewSuggesion = view.findViewById(R.id.suggesion_list);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        circleNameEditText = view.findViewById(R.id.circle_name);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.circle_name_suggetion, android.R.layout.simple_list_item_1);

        listViewSuggesion.setAdapter(adapter);


        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            if (circleNameEditText.getText().toString() == null) {

                return;
            } else if (circleNameEditText.getText().toString() != null) {


                viewModel.createNewGroup(circleNameEditText.getText().toString(), getContext());

                Intent intent = new Intent(getActivity(), CreateAndJoinCircleActivity.class);
                intent.putExtra("AddDp", "DP");
                startActivity(intent);

            }
        }
    }
}
