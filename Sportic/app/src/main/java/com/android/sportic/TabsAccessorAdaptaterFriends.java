package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdaptaterFriends extends FragmentPagerAdapter {
    public TabsAccessorAdaptaterFriends(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0:
                MyFriends myFriends = new MyFriends();
                return myFriends;
            case 1:
                SearchFriendsFragment searchFriends = new SearchFriendsFragment();
                return searchFriends;
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
                return "My Friends";
            case 1:
                return "Search Friends";
            default:
                return null;
        }
    }
}
