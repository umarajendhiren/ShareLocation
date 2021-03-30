package com.androidapps.sharelocation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.FragmentPasswordBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import dagger.hilt.android.AndroidEntryPoint;

import static com.parse.Parse.getApplicationContext;

@AndroidEntryPoint
public class PasswordFragment extends Fragment implements View.OnClickListener {
    @Nullable
    private IdlingResourceForTest mIdlingResource;
    Button btnContinue;
    EditText editPassword;
    MainViewModel viewModel;
    FragmentPasswordBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        binding = FragmentPasswordBinding.inflate(inflater, container, false);


        editPassword = binding.editPassword;
        binding.btnContinue.setOnClickListener(this);


        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String password = editPassword.getText().toString().trim();

            viewModel.getPasswordLiveDataInstance().setValue(password);


            if (password.equals("")) {
                Toast.makeText(getActivity(), "Please enter your valid password!", Toast.LENGTH_SHORT).show();
                return;
            }

            //before sign up we need to set idle resources to false . so the espresso stop the test wait for the async task done.

            if (viewModel.getIdlingResourceInstance() != null) {
                mIdlingResource = viewModel.getIdlingResourceInstance();

            }

            // The IdlingResource is null in production.
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }


            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

            if (!connected) {

                Log.d("error", "No network!");

                ((MainActivity) getActivity()).setFragmentForViewPager(12);
                return;
            } else {

                if (viewModel.getEmailLiveData().getValue().equals("SignUpWithoutEmail")) {

                    Log.d( "onClick: ","signup without email");
                    ParseUser user = new ParseUser();
                    user.setUsername(viewModel.getFirstNameLiveData().getValue() + "  " + viewModel.getLastNameLiveData().getValue());
                    user.setPassword(viewModel.getPasswordLiveData().getValue());



                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Log.i("done: ", "signUpSuccess");


                                //move to next fragment
                                ((MainActivity) getActivity()).setFragmentForViewPager(4);

                                //once this task completed we need set idling resorces to true. so the espresso re continue the test.

                                if (mIdlingResource != null) {
                                    mIdlingResource.setIdleState(true);
                                }
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong


                                Log.e("errorCode ", String.valueOf(e.getMessage()));


                                //if error code is 100,connection failed to parse server(no network connection)


                                if (e.getCode() == 202) {
                                    Toast.makeText(getActivity(), "Username already in use.Enter a different name or sign in with your username", Toast.LENGTH_LONG).show();
                                    ((MainActivity) getActivity()).setFragmentForViewPager(1);
                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }

                                if (e.getCode() == 203) {
                                    Toast.makeText(getActivity(), "Email already in use.Enter a different email address or sign in with your email", Toast.LENGTH_LONG).show();

                                    ((MainActivity) getActivity()).setFragmentForViewPager(2);

                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }
                                if (e.getCode() == 125) {
                                    Toast.makeText(getActivity(), "Invalid email address.Please enter valid email address.", Toast.LENGTH_LONG).show();

                                    ((MainActivity) getActivity()).setFragmentForViewPager(2);

                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }


                                if (e.getCode() == 100) {


                                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                                    boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                                    if (!connected) {

                                        Log.d("error", "No network!");

                                        ((MainActivity) getActivity()).setFragmentForViewPager(11);
                                    }

                                }

                            }
                        }
                    });


                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(viewModel.getFirstNameLiveData().getValue() + "  " + viewModel.getLastNameLiveData().getValue());
                    user.setPassword(viewModel.getPasswordLiveData().getValue());
                    user.setEmail(viewModel.getEmailLiveData().getValue());


                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Log.i("done: ", "signUpSuccess");


                                //move to next fragment
                                ((MainActivity) getActivity()).setFragmentForViewPager(4);

                                //once this task completed we need set idling resorces to true. so the espresso re continue the test.

                                if (mIdlingResource != null) {
                                    mIdlingResource.setIdleState(true);
                                }
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong


                                Log.e("errorCode ", String.valueOf(e.getCode()));


                                //if error code is 100,connection failed to parse server(no network connection)


                                if (e.getCode() == 202) {
                                    Toast.makeText(getActivity(), "Username already in use.Enter a different name or sign in with your username", Toast.LENGTH_LONG).show();
                                    ((MainActivity) getActivity()).setFragmentForViewPager(1);
                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }

                                if (e.getCode() == 203) {
                                    Toast.makeText(getActivity(), "Email already in use.Enter a different email address or sign in with your email", Toast.LENGTH_LONG).show();

                                    ((MainActivity) getActivity()).setFragmentForViewPager(2);

                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }
                                if (e.getCode() == 125) {
                                    Toast.makeText(getActivity(), "Invalid email address.Please enter valid email address.", Toast.LENGTH_LONG).show();

                                    ((MainActivity) getActivity()).setFragmentForViewPager(2);

                                    //once this task completed we need set idling resorces to true. so the espresso re continue the test.
                                    if (mIdlingResource != null) {
                                        mIdlingResource.setIdleState(true);
                                    }

                                }


                                if (e.getCode() == 100) {


                                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                                    boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

                                    if (!connected) {

                                        Log.d("error", "No network!");

                                        ((MainActivity) getActivity()).setFragmentForViewPager(11);
                                    }

                                }

                            }
                        }
                    });
                }

            }
        }
    }


    /**
     * Only called from test, creates and returns a new  IdlingResourceForTest.
     */
  /*  @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new IdlingResourceForTest();
        }
        return mIdlingResource;
    }*/
}
