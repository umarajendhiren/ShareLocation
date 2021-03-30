package com.androidapps.sharelocation;
//we will get exception,if we try to launch fragment using FrgmentScnario.because FrgmentScnario launch fragment in empty activity .
// we can not launch fragment in empty activity when we use Hit.
//because if we use Hilt, Host Activity of Fragment Must have @AndroidEntryPoint annotation.in FragmentScenario,we can not annotate host activity with  @AndroidEntryPoint.
// So we need to create Host Activity for Fragment by ourselves for testing.

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HiltTestActivity  extends AppCompatActivity {
}
