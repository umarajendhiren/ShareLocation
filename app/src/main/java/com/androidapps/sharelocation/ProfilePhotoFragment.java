package com.androidapps.sharelocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;

@AndroidEntryPoint
public class ProfilePhotoFragment extends Fragment implements View.OnClickListener {
    Button btnContinue;
    Button btnAddPhoto;
    CircleImageView circleImageViewdp;
    MainViewModel viewModel;
    int photoPickRequestCode = 456;
    int readExternalstoragePermissionReCode = 123;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_add_photo, container, false);
        btnContinue = rootView.findViewById(R.id.btn_continue);
        btnAddPhoto = rootView.findViewById(R.id.btn_add_photo);
        circleImageViewdp = rootView.findViewById(R.id.image_dp);
        btnContinue.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("handleOnBackPressed: ", "called");
                // Handle the back button event
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    Log.d("backstack:", String.valueOf(i));
                    fm.popBackStack();


                }

                SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = selectedFragment.edit();
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 8);

                editor.apply();


                Toast.makeText(getActivity(), "Press again to exit!", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return rootView;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {


            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

            if (!connected) {

                Log.d("error", "No network!");

                ((MainActivity) getActivity()).setFragmentForViewPager(12);
            } else {

                ParseUser.getCurrentUser().saveInBackground();

                ((MainActivity) getActivity()).setFragmentForViewPager(9);

            }


        }

        if (view.getId() == R.id.btn_add_photo) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("requestPermission ", "called");
                    /*request permission*/

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, readExternalstoragePermissionReCode);
                } else {
                    /*permission already granted*/
                    pickPhoto();
                }
            } else {
                /*before marshmallow no need to ask permission*/
                pickPhoto();
            }

            /* *//*we need to ask access permission explicitly after marshmallow*//*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    *//*request permission*//*
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, readExternalstoragePermissionReCode);
                } else {
                    *//*permission already granted*//*
                    pickPhoto();
                }
            } else {
                *//*before marshmallow no need to ask permission*//*
                pickPhoto();
            }*/


        }

    }

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     //   super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == readExternalstoragePermissionReCode && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {

            Log.d( "onRequestPerm","called");
            pickPhoto();
        }
    }

    public void pickPhoto() {
        Log.d("pickPhoto","called");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(pickPhoto, photoPickRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d( "onActivityResult2","called");
        if (requestCode == photoPickRequestCode && resultCode == RESULT_OK && data != null) {


            Uri selectedImageUri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();

                circleImageViewdp.setImageBitmap(bitmap);

                // file should match jpg,png,txt
                final ParseFile parseFile = new ParseFile("Image.jpg", bytes);


                ParseUser.getCurrentUser().put("profilepicture", parseFile);


                btnAddPhoto.setText("Change Photo");





                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if(!connected){

                    Log.d( "error","No network!");

                    ((MainActivity) getActivity()).setFragmentForViewPager(12);
                }

                else {

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("DP", "Saved!");

                            }
                        }
                    });

                }







            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == readExternalstoragePermissionReCode && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {

            Log.i("PermissionsResultRead", "read permission granted");
            pickPhoto();
        }
    }

    public void pickPhoto() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(pickPhoto, photoPickRequestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult2", "called");
        if (requestCode == photoPickRequestCode && resultCode == RESULT_OK && data != null) {


            Uri selectedImageUri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();

                circleImageViewdp.setImageBitmap(bitmap);

                // file should match jpg,png,txt
                final ParseFile parseFile = new ParseFile("Image.jpg", bytes);


                ParseUser.getCurrentUser().put("profilepicture", parseFile);


                btnAddPhoto.setText("Change Photo");


                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                if (!connected) {

                    Log.d("error", "No network!");

                    ((MainActivity) getActivity()).setFragmentForViewPager(12);
                } else {

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("DP", "Saved!");

                            }
                        }
                    });

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

