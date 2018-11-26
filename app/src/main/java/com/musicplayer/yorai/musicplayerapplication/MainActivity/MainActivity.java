package com.musicplayer.yorai.musicplayerapplication.MainActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.musicplayer.yorai.musicplayerapplication.DataProcessing.Song;
import com.musicplayer.yorai.musicplayerapplication.Logic.MusicController;
import com.musicplayer.yorai.musicplayerapplication.Logic.MusicService.MusicBinder;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.Logic.MusicService;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {//implements MediaPlayerControl {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static ArrayList<Song> currentPlaylist;
    public static ArrayList<Song> songDatabase;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    //public static MusicController controller;

    private boolean mediaPlayerPaused = false;
    private boolean playbackPaused = false;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(currentPlaylist);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an app-defined int constant
                return;
            }
        }

        //SQLlite - use another thread
        if (songDatabase == null){
            songDatabase = new ArrayList<Song>();
            getSongList();
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        currentPlaylist = new ArrayList<Song>();

        //setController();
    }

    public void getSongList() {
        //retrieve song info
        ContentResolver contentResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);
        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor.moveToFirst() "+musicCursor.moveToFirst());
        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor!=null "+(musicCursor!=null));
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int location = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                String thisPath = musicCursor.getString(location);
                //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: thiId="+thisPath);
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                if (!thisPath.equals("/storage/sdcard/Notifications/Calendar Notification.ogg"))
                    songDatabase.add(new Song(thisPath, thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(View view){
        //pause();
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            //setController();
            playbackPaused=false;
        }
        //controller.show(0);
    }

    public void artistPicked(View view){
        /*musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);*/

    }

    @Override
    protected void onDestroy() {
        if (musicBound)
            unbindService(musicConnection);
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mediaPlayerPaused =true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mediaPlayerPaused){
            //setController();
            mediaPlayerPaused =false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //controller.hide();
    }

    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////MEDIA PLAYER CONTROL/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

//    private void setController(){
//        //set the controller up
//        if(controller == null)
//            controller = new MusicController(this);
//        else
//            controller.invalidate();
//        controller.setPrevNextListeners(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNext();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPrev();
//            }
//        });
//        controller.setMediaPlayer(this);
//        controller.setAnchorView(findViewById(R.id.container));
//        controller.setEnabled(true);
//    }
//
//    @Override
//    public void start() {
//        musicSrv.go();
//    }
//
//    @Override
//    public void pause() {
//        playbackPaused=true;
//        musicSrv.pausePlayer();
//    }
//
//    @Override
//    public int getDuration() {
//        if(musicSrv!=null && musicBound && musicSrv.isPng())
//            return musicSrv.getDur();
//        else
//            return 0;
//    }
//
//    @Override
//    public int getCurrentPosition() {
//        if(musicSrv!=null && musicBound && musicSrv.isPng())
//            return musicSrv.getPosn();
//        else
//            return 0;
//    }
//
//    @Override
//    public void seekTo(int pos) {
//        musicSrv.seek(pos);
//    }
//
//    @Override
//    public boolean isPlaying() {
//        if(musicSrv!=null && musicBound)
//            return musicSrv.isPng();
//        return false;
//    }
//
//    @Override
//    public int getBufferPercentage() {
//        return 0;
//    }
//
//    @Override
//    public boolean canPause() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekBackward() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekForward() {
//        return true;
//    }
//
//    @Override
//    public int getAudioSessionId() {
//        return 0;
//    }
//
//    private void playNext(){
//        musicSrv.playNext();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }
//
//    private void playPrev(){
//        musicSrv.playPrev();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////ACTION BAR AND TABS//////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return AllSongsFragmentTab.newInstance(1);
                case 1:
                    return AllSongsFragmentTab.newInstance(2);
                case 2:
                    //return ArtistsFragmentTab.newInstance(3);
                    return AllSongsFragmentTab.newInstance(3);
                case 3:
                    return AllSongsFragmentTab.newInstance(4);
                case 4:
                    return AllSongsFragmentTab.newInstance(5);
            }
            return AllSongsFragmentTab.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Favorites";
                case 1:
                    return "Playlists";
                case 2:
                    return "Artists";
                case 3:
                    return "Albums";
                case 4:
                    return "All Songs";
            }
            return null;
        }
    }
}
