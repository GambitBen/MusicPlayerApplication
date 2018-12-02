package com.musicplayer.yorai.musicplayerapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int layout;
    private Context context;


    public SongListAdapter(Context context, int layout, ArrayList<Song> songs){
        this.context = context;
        this.layout = layout;
        this.songs = songs;
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

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        //map to song layout
//        LinearLayout songLay = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
//        //get album image views
//        ImageView albumImageView = (ImageView)songLay.findViewById(R.id.image);
//        //get title and artist views
//        TextView songView = (TextView)songLay.findViewById(R.id.title);
//        TextView artistView = (TextView)songLay.findViewById(R.id.artist);
//        //get song using position
//        Song currSong = songs.get(position);
//        //get title and artist strings
////        albumImageView.setImageBitmap(currSong.getAlbumImage());
//        //get song's album image
//        songView.setText(currSong.getTitle());
//        artistView.setText(currSong.getArtist());
//        //set position as tag
//        songLay.setTag(position);
//        return songLay;
//    }

    private class ViewHolder {
        TextView songView, artistView;
        ImageView albumImageView;
        int songPosition;
        Song song;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final SongItem viewHolder;
        if(convertView == null){
            viewHolder = new SongItem();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(layout, null);
            viewHolder.setSongView((TextView) convertView.findViewById(R.id.title));
            viewHolder.setArtistView((TextView) convertView.findViewById(R.id.artist));
            viewHolder.setAlbumImageView((ImageView) convertView.findViewById(R.id.image));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SongItem) convertView.getTag();
        }

        final Song currSong = songs.get(position);

        viewHolder.getSongView().setText(currSong.getTitle());
        viewHolder.getArtistView().setText(currSong.getArtist());
        viewHolder.setSongPosition(position);
        viewHolder.setSong(currSong);

        byte[] albumImage = currSong.getAlbumImage();
        if (albumImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            viewHolder.getAlbumImageView().setImageBitmap(bitmap);
        }
        else{
            viewHolder.getAlbumImageView().setImageResource(R.drawable.song_cover_null);
        }

        return convertView;
    }

}
