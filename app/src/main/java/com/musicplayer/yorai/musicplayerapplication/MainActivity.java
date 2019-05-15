package com.musicplayer.yorai.musicplayerapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.musicplayer.yorai.musicplayerapplication.Fragments.*;

import com.musicplayer.yorai.musicplayerapplication.Logic.CreateSongSQLDatabaseAsyncTask;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;
import com.musicplayer.yorai.musicplayerapplication.Logic.MusicService.MusicBinder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.musicplayer.yorai.musicplayerapplication.Logic.MusicService;

import java.util.ArrayList;
import java.util.List;

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
    private TabLayout tabLayout;

    private static ImageView song_album_cover;
    private static ImageView bt_back;
    private static ImageView bt_play_pause;
    private static ImageView bt_next;
    private static TextView song_title;
    private static TextView song_artist;

    // todo: check if i can create a method in the main activity to communicate with the current playlist in the music service instead of saving 2 instances of currentplaylist
//    public static ArrayList<Song> currentPlaylist;
    public static ArrayList<Song> playlistToSearch;

    public static MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    //public static MusicController controller;

    private static boolean mediaPlayerPaused = true;
    //private static boolean playbackPaused = false;

    //connect to the service
    private ServiceConnection musicConnection;

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
//        if (songArrayList == null){
//            songArrayList = new ArrayList<Song>();
//            getSongList();
//        }
        musicConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicBinder binder = (MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
//                musicSrv.setCurrentPlaylist(currentPlaylist);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };


        new CreateSongSQLDatabaseAsyncTask(this).execute();
        initComponent();
        actionHandle();
    }

    public void populateActivity() {
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

//        currentPlaylist = new ArrayList<Song>(); //maybe i should comment this out

        tabLayout.getTabAt(0).setText("Songs");
        tabLayout.getTabAt(1).setText("Artists");
        tabLayout.getTabAt(2).setText("Albums");
    }

    private void initComponent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        song_album_cover = (ImageView) findViewById(R.id.song_album_cover);
        bt_back = (ImageView) findViewById(R.id.bt_back);
        bt_play_pause = (ImageView) findViewById(R.id.bt_play_pause);
        bt_next = (ImageView) findViewById(R.id.bt_next);
        song_title = (TextView) findViewById(R.id.song_title);
        song_artist = (TextView) findViewById(R.id.song_artist);

        IntentFilter iff= new IntentFilter("custom-event-name");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, iff);
    }

    private void actionHandle() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // setup music component
        bt_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mediaPlayerPaused) {
                    mediaPlayerPaused = false;
                    musicSrv.startPlayer();
                    bt_play_pause.setImageResource(R.drawable.ic_pause);
                } else {
                    mediaPlayerPaused = true;
                    musicSrv.pausePlayer();
                    bt_play_pause.setImageResource(R.drawable.ic_play);
                }
            }
        });

        song_album_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv.getSong() != null) {
                    Intent i = new Intent(getApplicationContext(), PlayerDetailActivity.class);
                    startActivity(i);
                }
            }
        });
    }

//    private void getSongList() {
//        //retrieve song info
//        ContentResolver contentResolver = getContentResolver();
//        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);
//        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor.moveToFirst() "+musicCursor.moveToFirst());
//        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor!=null "+(musicCursor!=null));
//        if(musicCursor!=null && musicCursor.moveToFirst()){
//            //get columns
////            int titleColumn = musicCursor.getColumnIndex
////                    (MediaStore.Audio.Media.TITLE);
////            int idColumn = musicCursor.getColumnIndex
////                    (MediaStore.Audio.Media._ID);
////            int artistColumn = musicCursor.getColumnIndex
////                    (MediaStore.Audio.Media.ARTIST);
//            int location = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.DATA);
//            //add songs to list
//            do {
//                String thisPath = musicCursor.getString(location);
//                //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: thiId="+thisPath);
////                long thisId = musicCursor.getLong(idColumn);
////                String thisTitle = musicCursor.getString(titleColumn);
////                String thisArtist = musicCursor.getString(artistColumn);
//                if (!thisPath.equals("/storage/sdcard/Notifications/Calendar Notification.ogg"))
//                    songArrayList.add(new Song(thisPath));//, thisId, thisTitle, thisArtist));
//            }
//            while (musicCursor.moveToNext());
//        }
//    }

    public static boolean isMediaPlayerPaused() {
        return mediaPlayerPaused;
        //musicSrv.isPlaying();
    }

    public static void setMediaPlayerPaused(boolean mediaPlayerPaused) {
        MainActivity.mediaPlayerPaused = mediaPlayerPaused;
    }

    //    public static void selectPlaylist(Playlist playlist){
//    }

    public static void selectSong(int position){
        musicSrv.setSong(position);
        musicSrv.resetPlayer();
        musicSrv.playSong();
        if(mediaPlayerPaused) {
            mediaPlayerPaused = false;
            bt_play_pause.setImageResource(R.drawable.ic_pause);
        }
//        updateSong(currentPlaylist.get(position));
        updateSong(musicSrv.getSong());
    }
    private static void updateSong(String songPath) {
        updateSong(new Song(songPath));
    }

    private static void updateSong(Song song) {
        song_title.setText(song.getTitle());
        song_artist.setText(song.getArtist());
//        song_album_cover.setImageBitmap(song.getAlbumImage(this));
        byte[] albumImage = song.getAlbumImage();
        if (albumImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            song_album_cover.setImageBitmap(bitmap);
        }
        else{
            song_album_cover.setImageResource(R.drawable.song_cover_null);
        }
    }


//    public static void nextSong() {
//        musicSrv.playNext();
//        updateSong(musicSrv.getSong());
//    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////ACTIVITY STATES//////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicBound)
            unbindService(musicConnection);
        stopService(playIntent);
        musicSrv=null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (MainActivity.isMediaPlayerPaused()) {
            bt_play_pause.setImageResource(R.drawable.ic_play);
        } else {
            bt_play_pause.setImageResource(R.drawable.ic_pause);
        }
        IntentFilter iff= new IntentFilter("custom-event-name");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, iff);
        //get current song
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

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String songPath = intent.getStringExtra("songPath");
            if (songPath != null) {
                updateSong(songPath);
            }

        }
    };



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

    private void setupViewPager(ViewPager viewPager) {
        //while (songArrayList == null);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(SongsTabFragment.newInstance(), "Songs");
        mSectionsPagerAdapter.addFragment(SongsTabFragment.newInstance(), "Albums");
        mSectionsPagerAdapter.addFragment(SongsTabFragment.newInstance(), "Artists");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the options menu from XML
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
                Log.d("SearchView", "ENTER HERE ------------------------------------------------------");
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("Query", query);
                startActivity(i);
                mSearch.collapseActionView();
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
//
//        mSearchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
//                Log.i("SearchView", "onClick: ");
////                Intent i = new Intent(getApplicationContext(), PlayerDetailActivity.class);
////                populateActivity(i);
//            }
//        });

//        mSearchView.setQueryHint("Search");
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });

//        return super.onCreateOptionsMenu(menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tags) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public String getTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
