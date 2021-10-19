package com.androidapps.sharelocation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.viewmodel.RiderViewModel;
import com.androidapps.sharelocation.databinding.FragmentDriverMessageBinding;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverMessageFragment extends Fragment implements View.OnClickListener {


    RiderViewModel mainViewModel;
    Button send;
    EditText message;

    FragmentDriverMessageBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(RiderViewModel.class);
        binding = FragmentDriverMessageBinding.inflate(inflater, container, false);

        binding.setLifecycleOwner(this);


        message=binding.message;

        binding.sendMessage.setOnClickListener(this);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onClick(View view) {



        if (view.getId() == R.id.send_message) {

            final HashMap<String, String> params = new HashMap<>();


            Log.d("diverName", ParseUser.getCurrentUser().getUsername());
            Log.d("driverMessage", message.getText().toString().trim());
            params.put("driverMessage", message.getText().toString().trim());
            params.put("driverName", ParseUser.getCurrentUser().getUsername());
            params.put("channel", ParseUser.getCurrentUser().getObjectId());
          //  params.put("deviceToken", ParseUser.getCurrentUser().getObjectId());


            ParseCloud.callFunctionInBackground("sendDriverMessage", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object response, ParseException exc) {
                    if (exc == null) {
                        Log.d("done: ", "Push message sent!!!");
                    } else {
                        // Something went wrong
                        Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error: ", String.valueOf(exc));

                    }
                }
            });
        }
    }
}
