package com.android.sportic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SportAdapter extends BaseAdapter {

    //Fields
    private Context context;
    private List<Sport> sportList;
    private LayoutInflater inflater;
    private static String selectedSport;

    //Constructor
    public SportAdapter(Context context, List<Sport> sportList) {
        this.context = context;
        this.sportList = sportList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sportList.size();
    }

    @Override
    public Sport getItem(int i) {
        return sportList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static String getSelectedSport() { return selectedSport; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_sport, null);

        Sport currentSport = getItem(i);
        String sportName = currentSport.getName();

        ImageView sportIconView = view.findViewById(R.id.sport_icon);
        int iconId = context.getResources().getIdentifier(getItem(i).getTag(), "drawable", context.getPackageName());
        sportIconView.setImageResource(iconId);

        TextView sportNameView = view.findViewById(R.id.sport_name);
        sportNameView.setText(sportName);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sportEventList = new Intent(context.getApplicationContext(), SportEventList.class);
                selectedSport = sportName;
                context.startActivity(sportEventList);
            }
        });

        return view;
    }
}
