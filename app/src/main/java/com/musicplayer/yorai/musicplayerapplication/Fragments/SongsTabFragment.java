package com.musicplayer.yorai.musicplayerapplication.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.musicplayer.yorai.musicplayerapplication.Model.Song;
import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.MainActivity;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SongsTabFragment extends Fragment {

    private View rootView;
    private ArrayList<Song> songList;
    private ListView songView;

    public SongsTabFragment() {
    }

    public static SongsTabFragment newInstance() {
        SongsTabFragment fragment = new SongsTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_song, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //songList = MainActivity.currentPlaylist;
        this.songList= MainActivity.songDatabase;
        //TODO: clone instead
        MainActivity.currentPlaylist=this.songList;

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //set data and list adapter
        SongListAdapter mAdapter = new SongListAdapter(getActivity(), songList);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Song obj, int position) {
                MainActivity.selectSong(position);
            }
        });



//
//        SongListAdapter songAdt = new SongListAdapter(getActivity(), R.layout.item_song, songList);
//        songView.setAdapter(songAdt);

        return rootView;
    }
}
