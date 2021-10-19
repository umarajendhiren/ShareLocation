package com.androidapps.sharelocation.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    List<Fragment> framentList =new ArrayList<>();
    List<String> fragmentTitleList =new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return framentList.get(position);
    }

    @Override
    public int getItemCount() {
        return framentList.size();
    }

    public void addFragment(Fragment fragment,String title){

        framentList.add(fragment);
        fragmentTitleList.add(title);
    }
}
