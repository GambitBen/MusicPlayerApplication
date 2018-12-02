package com.musicplayer.yorai.musicplayerapplication.Adapters;

import android.widget.ImageView;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.DataProcessing.Song;

/**
 * Created by Yorai on 02-Dec-18.
 */

public class SongItem {
    private TextView songView;
    private TextView artistView;
    private ImageView albumImageView;
    private int songPosition;
    private Song song;

    protected TextView getSongView() {
        return songView;
    }

    protected void setSongView(TextView songView) {
        this.songView = songView;
    }

    protected TextView getArtistView() {
        return artistView;
    }

    protected void setArtistView(TextView artistView) {
        this.artistView = artistView;
    }

    protected ImageView getAlbumImageView() {
        return albumImageView;
    }

    protected void setAlbumImageView(ImageView albumImageView) {
        this.albumImageView = albumImageView;
    }

    public int getSongPosition() {
        return songPosition;
    }

    protected void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }

    public Song getSong() {
        return song;
    }

    protected void setSong(Song song) {
        this.song = song;
    }
}
