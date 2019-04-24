package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.musicplayer.yorai.musicplayerapplication.Model.Song;
import com.musicplayer.yorai.musicplayerapplication.MainActivity;

import java.io.IOException;
import java.util.ArrayList;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final IBinder musicBind = new MusicBinder();
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> currentPlaylist;
    //current position
    private int songPosn;

    //for notification
    private String songTitle="";
    private static final int NOTIFY_ID=1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
//        player.stop();
//        player.release();
        //moved to onDestroy()
        return false;
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        //initialize player
        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        stopForeground(true);
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //deprecated method
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(){
//        Log.d("------------songPosn","songPosn = " + songPosn);
//        Log.d("------------getCurrentPosition","getCurrentPosition = " + getCurrentPosition());
        //play a song
        player.reset();
        //get song
        Song playSong = currentPlaylist.get(songPosn);
        //get song path
        String songPath = playSong.getPath();
        //get song title
        songTitle = playSong.getTitle();
        //get id
//        long currSong = playSong.getID();
        //set uri
//        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            //player.setDataSource(getApplicationContext(), trackUri);
            player.setDataSource(songPath);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

//        player.prepareAsync();
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent in=new Intent("custom-event-name");  //you can put anything in it with putExtra
        in.putExtra("songPath",songPath);
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mediaPlayer.start();
        //MainActivity.controller.show();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification.Builder builder = new Notification.Builder(this);
//
//        builder.setContentIntent(pendInt)
//                //.setSmallIcon(R.drawable.play)
//                .setTicker(songTitle)
//                .setOngoing(true)
//                .setContentTitle("Playing")
//                .setContentText(songTitle);
//        Notification not = builder.build();
//
//        startForeground(NOTIFY_ID, not);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public Song getSong(){
        return currentPlaylist.get(songPosn);
    }

    public void setList(ArrayList<Song> theSongs){
        currentPlaylist = theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////MEDIA PLAYER CONTROL/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seekTo(int posn){
        player.seekTo(posn);
    }

    public void startPlayer(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn= currentPlaylist.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        songPosn++;
        if(songPosn>= currentPlaylist.size()) songPosn=0;
        playSong();
    }
}
