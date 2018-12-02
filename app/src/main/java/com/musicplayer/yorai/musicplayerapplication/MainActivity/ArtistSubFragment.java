package com.musicplayer.yorai.musicplayerapplication.MainActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.musicplayer.yorai.musicplayerapplication.DataProcessing.Song;
import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ArtistSubFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private static final String ARG_SELECTED_ARTIST = "selected_artist";

    private ArrayList<Song> songList;
    private ListView songView;
    private String artist;

    public ArtistSubFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    //public static ArtistSubFragment newInstance(int sectionNumber, String artist) {
    public ArtistSubFragment newInstance(int sectionNumber, String artist) {
        ArtistSubFragment fragment = new ArtistSubFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.artist = artist;
        //args.putString(ARG_SELECTED_ARTIST, artist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        songView = (ListView)rootView.findViewById(R.id.song_list);
        songList = MainActivity.currentPlaylist;
//        getSongList();

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getArtist().compareTo(b.getArtist());
            }
        });

        SongListAdapter songAdt = new SongListAdapter(getActivity(), R.layout.item_song, songList);
        songView.setAdapter(songAdt);

        return rootView;
    }

//    public void getSongList() {
//        //retrieve song info
//        ContentResolver musicResolver = getActivity().getContentResolver();
//        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//
//        if(musicCursor!=null && musicCursor.moveToFirst()){
//            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.ARTIST);
//            //add songs to list
//            do {
//                long thisId = musicCursor.getLong(idColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
//                if (thisArtist.equals(artist))
//                    songList.add(new Song(thisId, thisTitle, thisArtist));
//            }
//            while (musicCursor.moveToNext());
//        }
//    }
}
