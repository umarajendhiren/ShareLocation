package com.androidapps.sharelocation.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.ParseDeleteInterface;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.BUTTON_POSITIVE;
@AndroidEntryPoint
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private int readExternalstoragePermissionReCode = 123;
    private int photoPickRequestCode = 345;
    CircleImageView userDp;
    ImageView editDp,deleteDp;
    TextView editName, leaveCircle, editPass, deleteAccount, editPhone, logOut,feedback;
    HomePageViewModel viewModel;
    ParseDeleteInterface parseDeleteInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        viewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);


        editName = root.findViewById(R.id.change_name);
        leaveCircle = root.findViewById(R.id.leave_circle);
        editPass = root.findViewById(R.id.change_password);
        editPhone = root.findViewById(R.id.change_number);
        deleteAccount = root.findViewById(R.id.delete_account);
        userDp = root.findViewById(R.id.image_dp);
        editDp = root.findViewById(R.id.edit_dp);
        logOut = root.findViewById(R.id.log_out);
        deleteDp=root.findViewById(R.id.delete_dp);
        feedback=root.findViewById(R.id.tv_feedback);


        editName.setOnClickListener(this);
        leaveCircle.setOnClickListener(this);
        editPass.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        editPhone.setOnClickListener(this);
        editDp.setOnClickListener(this);
        logOut.setOnClickListener(this);
        deleteDp.setOnClickListener(this);
        feedback.setOnClickListener(this);
        getUserDp();


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("https://parsefiles.back4app.com/tvo4tsUdmH3YSI7iUgewB41l7eIDKn3wLTg8oOld/")
                .baseUrl("https://parseapi.back4app.com/parse/files/")
                .client(okHttpClient)
                .build();
         parseDeleteInterface= retrofit.create(ParseDeleteInterface.class);


        return root;
    }

    private void getUserDp() {

        Log.d("getUserDp: ", "called");
        viewModel.getUserDpLive().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                Log.d("onChanged:userDp ", String.valueOf(bitmap));
                userDp.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent changeDetails = new Intent(getActivity(), ChangeAccountDetailsActivity.class);

        int id = view.getId();
        switch (id) {


            case R.id.change_name:

                changeDetails.putExtra("NeedToEdit", "Name");
                startActivity(changeDetails);

                break;

            case R.id.leave_circle:

                changeDetails.putExtra("NeedToEdit", "Leave");
                startActivity(changeDetails);

                break;

            case R.id.change_password:

                changeDetails.putExtra("NeedToEdit", "Password");
                startActivity(changeDetails);

                break;

            case R.id.change_number:

                changeDetails.putExtra("NeedToEdit", "Phone");
                startActivity(changeDetails);

                break;

            case R.id.delete_account:
                changeDetails.putExtra("NeedToEdit", "DeleteAccount");
                startActivity(changeDetails);

                break;
            case R.id.tv_feedback:
                changeDetails.putExtra("NeedToEdit", "SendFeedback");
                startActivity(changeDetails);

                break;

            case R.id.log_out:
               /* changeDetails.putExtra("NeedToEdit", "LogOut");
                startActivity(changeDetails);*/

                Log.d("onClick: ", "need to show mainPage after logout");


                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .create();
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Are you sure?");
                //ad.setMessage(message);

                alertDialog.setButton(BUTTON_POSITIVE, "Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("onClick: ", "log out" + i);
                        dialogInterface.dismiss();
                        ParseUser.logOutInBackground();

                        //need to delete selected circle name sharedpreference value,so that other user log in

                        SharedPreferences sharedPref = getContext().getSharedPreferences("circle", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("com.androidapps.sharelocation.defaultCircleName","defaultCircleName");
                        editor.putString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");
                        editor.apply();

                        viewModel.isUserLoggedIn(false);
/*ParseUser.logOut();
ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null*/

                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Never mind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("onClick: ", "no" + i);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();


                break;


          /*  case R.id.image_dp:
                changeDetails.putExtra("NeedToEdit", "UserDp");

                break;*/


            case R.id.edit_dp:
                Log.i("onClick:AddPhoto ", "called");
                /*we need to ask access permission explicitly after marshmallow*/
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

                break;




            case  R.id.delete_dp:



                //String imageFileName= ParseUser.getCurrentUser().getParseFile("profilepicture").getUrl();
               // Log.d( "onClick:delete ",imageFileName);

               /* if(imageFileName.isEmpty())
                    return;

                byte[] bytes = new byte[0];

                final ParseFile parseFile = new ParseFile("Image.jpg", bytes);



                ParseUser.getCurrentUser().put("profilepicture", parseFile);*/
             /*   byte[] bytes = new byte[0];

                File file=new File("");
                final ParseFile parseFile = new ParseFile(file);


                Log.d("onClick: ","delete");
                try {
                    ParseUser.getCurrentUser().getParseFile("profilepicture").getFile().delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

                ParseUser.getCurrentUser().remove("profilepicture");

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e==null) {
                            Log.d("done: ", "deleted");
                            getUserDp();
                        }
                        else {
                            Log.e("error",e.getMessage() );
                        }
                    }
                });









               /* Call<Void> call = parseDeleteInterface.deleteDp(imageFileName);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("onResponse: ",response.message());
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("error: ",t.getMessage());
                    }
                });*/
               // viewModel.deleteDp(imageFileName);


        }


    }


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
        startActivityForResult(pickPhoto, photoPickRequestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == photoPickRequestCode && resultCode == RESULT_OK && data != null) {

            Log.i("photoPickRequestCode ", String.valueOf(photoPickRequestCode));


            Uri selectedImageUri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();

                userDp.setImageBitmap(bitmap);

                // file should match jpg,png,txt
                final ParseFile parseFile = new ParseFile("Image.jpg", bytes);

              /*  String circleName = viewModel.getCircleNameLiveData().getValue();
                String circleCode = viewModel.getCircleCodeLiveData().getValue();
                String yourRole = viewModel.getYourRoleLiveData().getValue();*/

                ParseUser.getCurrentUser().put("profilepicture", parseFile);

             /*   ParseUser.getCurrentUser().put("circlename", circleName);
                ParseUser.getCurrentUser().put("circlecode", circleCode);
                ParseUser.getCurrentUser().put("role", yourRole);*/

                // btnAddPhoto.setText("Change Photo");

                ParseUser.getCurrentUser().saveInBackground();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
