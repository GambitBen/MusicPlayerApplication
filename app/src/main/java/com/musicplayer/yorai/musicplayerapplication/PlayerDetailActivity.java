package com.musicplayer.yorai.musicplayerapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.musicplayer.yorai.musicplayerapplication.Logic.Tools;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

public class PlayerDetailActivity extends AppCompatActivity {

    public View parent_view;

    private ImageView image_album;
    private ImageView bt_play_pause;
    private AppCompatSeekBar seek_song_progressbar;
    private TextView tv_song_current_duration, tv_song_total_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        parent_view = findViewById(R.id.parent_view);
        Tools.systemBarLollipopDark(this);

        initComponent();
        actionHandle();
    }

    private void initComponent() {
        seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
        tv_song_current_duration = (TextView) findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = (TextView) findViewById(R.id.tv_song_total_duration);
        // set Progress bar values
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(Tools.MAX_PROGRESS);

        image_album = (ImageView) findViewById(R.id.song_album_cover);
        bt_play_pause = (ImageView) findViewById(R.id.bt_play_pause);
        changeMusicInfo(MainActivity.musicSrv.getSong());
        if (MainActivity.isMediaPlayerPaused()) {
            bt_play_pause.setImageResource(R.drawable.ic_play);
        } else {
            bt_play_pause.setImageResource(R.drawable.ic_pause);
        }
        updateTimerAndSeekbar();
    }

    private void actionHandle() {
        // setup music component
        bt_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (MainActivity.isMediaPlayerPaused()) {
                    MainActivity.setMediaPlayerPaused(false);
                    MainActivity.musicSrv.startPlayer();
                    bt_play_pause.setImageResource(R.drawable.ic_pause);
                } else {
                    MainActivity.setMediaPlayerPaused(true);
                    MainActivity.musicSrv.pausePlayer();
                    bt_play_pause.setImageResource(R.drawable.ic_play);
                }
            }
        });

        // Listeners
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int totalDuration = MainActivity.musicSrv.getDuration();
                int currentPosition = Tools.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                MainActivity.musicSrv.seekTo(currentPosition);
            }
        });
    }

    private void changeMusicInfo(Song musicSong) {
        if (musicSong == null) return;
        image_album.setImageBitmap(musicSong.getAlbumImage(this));
        ((TextView) findViewById(R.id.song_title)).setText(musicSong.getTitle());
        ((TextView) findViewById(R.id.song_artist)).setText(musicSong.getArtist());
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_back: {
                Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_next: {
                Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_player_detail, menu);
        Tools.changeMenuIconColor(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if(id == R.id.action_settings){
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateTimerAndSeekbar() {
        long totalDuration = MainActivity.musicSrv.getDuration();
        long currentDuration = MainActivity.musicSrv.getCurrentPosition();

        // Displaying Total Duration time
        tv_song_total_duration.setText(Tools.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(Tools.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (Tools.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (MainActivity.isMediaPlayerPaused()) {
            bt_play_pause.setImageResource(R.drawable.ic_play);
        } else {
            bt_play_pause.setImageResource(R.drawable.ic_pause);
        }
    }
}
