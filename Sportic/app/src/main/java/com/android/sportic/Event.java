package com.android.sportic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class Event extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewpager;

    TabsAccessorAdapter tabsAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        tabLayout = (TabLayout) findViewById(R.id.EventTab);
        viewpager = (ViewPager) findViewById(R.id.EventPager);

        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewpager.setAdapter(tabsAccessorAdapter);

        tabLayout.setupWithViewPager(viewpager);
    }

}
