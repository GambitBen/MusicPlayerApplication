package com.musicplayer.yorai.musicplayerapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;

public class ArtistListAdapter extends BaseAdapter {

    private ArrayList<String> artists;
    private LayoutInflater songInf;

    public ArtistListAdapter(Context c, ArrayList<String> artists){
        this.artists = artists;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.artist_elements, parent, false);
        //get title and artist views
        TextView artistView = (TextView)songLay.findViewById(R.id.artist_name);
        //get artist using position
        String currArtist = artists.get(position);
        //set artist string
        artistView.setText(currArtist);
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
