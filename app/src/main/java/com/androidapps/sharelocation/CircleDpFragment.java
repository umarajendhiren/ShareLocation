package com.androidapps.sharelocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentCircleDpBinding;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

import static android.app.Activity.RESULT_OK;

@AndroidEntryPoint
public class CircleDpFragment extends Fragment implements View.OnClickListener {


    CreateJoinViewModel viewModel;
    int photoPickRequestCode = 456;
    int readExternalstoragePermissionReCode = 123;

    FragmentCircleDpBinding viewBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(requireActivity()).get(CreateJoinViewModel.class);

        viewBinding = FragmentCircleDpBinding.inflate(inflater, container, false);
        View rootView = viewBinding.getRoot();

        viewBinding.btnContinue.setOnClickListener(this);
        viewBinding.btnAddPhoto.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.btn_add_photo) {

            /*we need to ask access permission explicitly after marshmallow*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

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


        }

        if (view.getId() == R.id.btn_continue) {

            startActivity(new Intent(getActivity(), HomaPageActivity.class));
        }
    }


    public void pickPhoto() {


        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, photoPickRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == photoPickRequestCode && resultCode == RESULT_OK && data != null) {


            Uri selectedImageUri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();

                viewBinding.imageDp.setImageBitmap(bitmap);

                // file should match jpg,png,txt
                final ParseFile parseFile = new ParseFile("Image.jpg", bytes);


                viewModel.storeParseFileInServer(parseFile, getActivity());


                viewBinding.btnAddPhoto.setText("Change Photo");


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == readExternalstoragePermissionReCode && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {


            pickPhoto();
        }
    }
}
