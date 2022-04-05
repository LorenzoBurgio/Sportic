package com.android.sportic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SportList extends AppCompatActivity {

    public String currentSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_list);

        //List of sports
        List<Sport> sportList = new ArrayList<>();
        String[] sport = getResources().getStringArray(R.array.Sport);
        String[] tag = getResources().getStringArray(R.array.Tag);
        for (int i = 0; i < sport.length; i++) {
            sportList.add(new Sport(sport[i], tag[i]));
        }


        // Get Listview
        ListView sportListView = findViewById(R.id.sport_list_view);
        sportListView.setAdapter(new SportAdapter(this, sportList));


    }
}