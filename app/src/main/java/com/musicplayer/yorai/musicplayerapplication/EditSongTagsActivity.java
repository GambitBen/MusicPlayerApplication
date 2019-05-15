package com.musicplayer.yorai.musicplayerapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.Logic.Tools;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

import java.io.File;

public class EditSongTagsActivity extends AppCompatActivity {

    public View parent_view;

    private EditText songTitle, songArtist, songYear, songGenre;
    private TextView saveButton, cancelButton;

    private Song mSong;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song_tags);
        parent_view = findViewById(R.id.parent_view);
        Tools.systemBarLollipopDark(this);
        mPath = getIntent().getStringExtra("Song");
        mSong = new Song(mPath);
        initComponent();
        actionHandle();
    }

    private void initComponent() {
        songTitle = (EditText) findViewById(R.id.edit_song_title);
        songArtist = (EditText) findViewById(R.id.edit_song_artist);
        songYear = (EditText) findViewById(R.id.edit_song_year);
        songGenre = (EditText) findViewById(R.id.edit_song_genre);

        saveButton = (TextView) findViewById(R.id.save_action);
        cancelButton = (TextView) findViewById(R.id.cancel_action);

        // set values of the EditText s
        songTitle.setText(mSong.getTitle(), TextView.BufferType.EDITABLE);
        songArtist.setText(mSong.getArtist(), TextView.BufferType.EDITABLE);
        songYear.setText(mSong.getYear(), TextView.BufferType.EDITABLE);
        songGenre.setText(""+mSong.getGenre(), TextView.BufferType.EDITABLE);
    }

    private void actionHandle() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int currentPosition = MainActivity.musicSrv.getCurrentPosition();
                MainActivity.musicSrv.resetPlayer();

                mSong.setYear(songYear.getText().toString());

                mSong.save(mPath.replace(".mp3","-temp.mp3"));
                File newFile = new File(mPath);
                newFile.delete();
                newFile = new File(mPath.replace(".mp3","-temp.mp3"));
                newFile.renameTo(new File(mPath));

                MainActivity.musicSrv.playSong();
                MainActivity.musicSrv.seekTo(currentPosition);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onDestroy();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

}
