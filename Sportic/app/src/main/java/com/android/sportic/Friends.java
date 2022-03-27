package com.android.sportic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Friends extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewpager;

    TabsAccessorAdaptaterFriends tabsAccessorAdaptaterFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        tabLayout = (TabLayout) findViewById(R.id.FriendsTab);
        viewpager = (ViewPager) findViewById(R.id.FriendsPager);

        tabsAccessorAdaptaterFriends = new TabsAccessorAdaptaterFriends(getSupportFragmentManager());
        viewpager.setAdapter(tabsAccessorAdaptaterFriends);

        tabLayout.setupWithViewPager(viewpager);


    }
}