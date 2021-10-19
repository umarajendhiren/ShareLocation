package com.androidapps.sharelocation.view;

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

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.viewmodel.CreateJoinViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateNewCircleFragmetForDialog extends Fragment implements View.OnClickListener {
    ListView listViewSuggesion;
    Button btnContinue;
    CreateJoinViewModel viewModel;
    EditText circleNameEditText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_create_new_circle_fragmet_for_dialog, container, false);


        viewModel = new ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);
        listViewSuggesion = view.findViewById(R.id.suggesion_list);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);

        circleNameEditText = view.findViewById(R.id.circle_name);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.circle_name_suggetion, android.R.layout.simple_list_item_1);

        listViewSuggesion.setAdapter(adapter);


        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {
            if (circleNameEditText.getText().toString() .isEmpty()) {

                return;
            }

            else {
                viewModel.circleNameNew.setValue(circleNameEditText.getText().toString());
                viewModel.getSelectedFragmentInstance().setValue("AddDp");

              /*  Intent intent = new Intent(getActivity(), CreateAndJoinCircleActivity.class);
                intent.putExtra("AddDp", "DP");
                intent.putExtra("circleNameEditText",circleNameEditText.getText().toString());
                startActivity(intent);*/

            }
        }
    }
}
