package com.androidapps.sharelocation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.ActivityChangeAccountDetailsBinding;
import com.androidapps.sharelocation.viewmodel.AccountDetailViewModel;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangeAccountDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    AccountDetailViewModel viewModel;

    public static MutableLiveData<String> currentEditText = new MutableLiveData<>();

    ActivityChangeAccountDetailsBinding databinding;

    boolean isValidPhoneNumber;

  static   boolean isNetworkConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_change_account_details);
        databinding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(AccountDetailViewModel.class);
        databinding.setViewModel(viewModel);


        Toolbar toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);


        databinding.buttonLeave.setOnClickListener(this);
        databinding.buttonReset.setOnClickListener(this);
        databinding.buttonDelete.setOnClickListener(this);
        databinding.buttonSave.setOnClickListener(this);
        databinding.buttonSavephone.setOnClickListener(this);
        databinding.sendFeedback.setOnClickListener(this);


        viewModel.getAccountDetail(this);


        if (getIntent().getExtras().get("NeedToEdit") != null) {

            String needToEdit = getIntent().getExtras().get("NeedToEdit").toString();
            Log.d( "needToEdit",needToEdit);
            if (needToEdit.equals("Name")) {


                actionBar.setTitle("Edit Name");
                viewModel.needToEdit.setValue("Name");

                currentEditText.setValue("Name");


            } else if (needToEdit.equals("Leave")) {


                actionBar.setTitle("Leave Circle");

                viewModel.needToEdit.setValue("Leave");


                currentEditText.setValue("Leave");

            } else if (needToEdit.equals("Phone")) {


                actionBar.setTitle("Edit Phone Number");

                viewModel.needToEdit.setValue("Phone");


                currentEditText.setValue("Phone");
            } else if (needToEdit.equals("Password")) {


                actionBar.setTitle("Edit Password");


                viewModel.needToEdit.setValue("Password");


                currentEditText.setValue("Password");

            }

            else if (needToEdit.equals("DeleteAccount")) {

                actionBar.setTitle("Delete Account");


                viewModel.needToEdit.setValue("Delete");


                currentEditText.setValue("Delete");

            }

            else if (needToEdit.equals("SendFeedback")) {

                actionBar.setTitle("Contact us");


                viewModel.needToEdit.setValue("SendFeedback");


                currentEditText.setValue("SendFeedback");

            }

        }



        viewModel.isNetworkConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isNetworkAvailable) {

                Log.d( "onChanged:net", String.valueOf(isNetworkAvailable));
                 isNetworkConnected=isNetworkAvailable;
            }
        });

    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_save && currentEditText.getValue().equals("Name")) {


            Log.d( "onChanged:net", String.valueOf(isNetworkConnected));


            if (databinding.firstName.getText().toString().trim().equals(null) || databinding.lastName.getText().toString().trim() == null) {
                Toast.makeText(this, "Please Enter Both Name!", Toast.LENGTH_SHORT).show();
                return;
            } else {


                if(isNetworkConnected){
                viewModel.saveName(databinding.firstName.getText().toString().trim(), databinding.lastName.getText().toString().trim());

                finish();}

                else if(!isNetworkConnected){
                    Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }


        }

        if (view.getId() == R.id.button_savephone && currentEditText.getValue().equals("Phone")) {

            if (databinding.phone.getText().toString().trim().equals(null)) {
                Toast.makeText(this, "Please Enter Valid Mobile Number!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                isValidMobile(databinding.phone.getText().toString(),databinding.countryCode.getText().toString());

                if(isValidPhoneNumber) {
                    Log.d("onClick:phone","valid");
                    if(isNetworkConnected){

                    viewModel.savePhoneNumber(databinding.countryCode.getText().toString()+ " "+databinding.phone.getText().toString());

                    finish();}

                    else if(!isNetworkConnected){
                        Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
        if (view.getId() == R.id.button_reset && currentEditText.getValue().equals("Password")) {


            if (databinding.resetMail.getText().toString().trim().equals(null) || databinding.resetMail.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter valid E-mail Id!", Toast.LENGTH_LONG).show();
                return;
            }

            else {
                if(isNetworkConnected){
                viewModel.resetPassword(databinding.resetMail.getText().toString());

                    Log.d("onClick:reter ",databinding.resetMail.getText().toString());

                finish();}

                 else if(!isNetworkConnected){
                    Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }


        }


        if (view.getId() == R.id.button_leave && currentEditText.getValue().equals("Leave")) {
            if(isNetworkConnected){

            viewModel.leaveCircle(databinding.circleName.getText().toString());

                Toast.makeText(this, "Left from circle!", Toast.LENGTH_SHORT).show();
            finish();}


             else if(!isNetworkConnected){
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }


        if (view.getId() == R.id.button_delete && currentEditText.getValue().equals("Delete")) {
            if(isNetworkConnected){
            viewModel.deleteUser();

            finish();}


             else if(!isNetworkConnected){
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }


        if (view.getId() == R.id.send_feedback && currentEditText.getValue().equals("SendFeedback")) {

            if (databinding.message.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please write your feedback!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if(isNetworkConnected){
                viewModel.sendFeedback(databinding.message.getText().toString());

                finish();}

                 else if(!isNetworkConnected){
                    Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }




    private boolean isValidMobile(String phone, String countryCode) {

//       return android.util.Patterns.PHONE.matcher(phone).matches();
        isValidPhoneNumber = false;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();


        try {
            String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, isoCode);
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            PhoneNumberUtil.PhoneNumberType isMobile = phoneNumberUtil.getNumberType(phoneNumber);

            Log.i("isValid: ", String.valueOf(isValid));


            if (isValid && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile || PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
                Log.i("isValidMobile: ", "validMobileNumber");
                {

                    isValidPhoneNumber = true;
                }
            } else {

                Toast.makeText(this, "Provide valid phone number", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (e != null) {
                Log.e("isValidMobile: ", e.toString());
                Log.e("isValidMobile: ", e.getMessage());
                Toast.makeText(this, "Provide valid phone number", Toast.LENGTH_SHORT).show();

            }
        }


        return isValidPhoneNumber;

    }



}
/*1 toast
1234567891 toast
92307133041---11 digit toast
9207136018    correct
9203456456  correct
9237136018 toast
*/





