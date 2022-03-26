package com.android.sportic;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0:
                MyEventFragment myEventFragment = new MyEventFragment();
                return myEventFragment;
            case 1:
                EventFragment eventFragment = new EventFragment();
                return eventFragment;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case  0:
                return "My Event";
            case 1:
                return "Search Event";
            default:
                return null;
        }
    }
}
