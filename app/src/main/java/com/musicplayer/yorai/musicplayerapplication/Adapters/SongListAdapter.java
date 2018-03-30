package com.musicplayer.yorai.musicplayerapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.DataProcessing.Song;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;

public class SongListAdapter extends BaseAdapter {

    private ArrayList<Song> songs;

    private Context context;


    public SongListAdapter(Context c, ArrayList<Song> songs){
        this.songs = songs;
        this.context = c;
    }

    @Override
    public int getCount() {
        return songs.size();
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
        LinearLayout songLay = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        //get album image views
        ImageView albumImageView = (ImageView)songLay.findViewById(R.id.image);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.title);
        TextView artistView = (TextView)songLay.findViewById(R.id.artist);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
//        albumImageView.setImageBitmap(currSong.getAlbumImage());
        //get song's album image
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
