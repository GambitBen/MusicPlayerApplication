package com.musicplayer.yorai.musicplayerapplication.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.DataProcessing.Song;
import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AllSongsFragmentTab extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<Song> songList;
    private ListView songView;

    public AllSongsFragmentTab() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AllSongsFragmentTab newInstance(int sectionNumber) {
        AllSongsFragmentTab fragment = new AllSongsFragmentTab();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        songView = (ListView)rootView.findViewById(R.id.song_list);
        //songList = MainActivity.currentPlaylist;
        this.songList=MainActivity.songDatabase;
        MainActivity.currentPlaylist=this.songList;

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongListAdapter songAdt = new SongListAdapter(getActivity(), R.layout.item_song, songList);
        songView.setAdapter(songAdt);

        return rootView;
    }
}
