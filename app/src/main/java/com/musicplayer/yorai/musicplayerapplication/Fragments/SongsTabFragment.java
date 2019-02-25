package com.musicplayer.yorai.musicplayerapplication.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.musicplayer.yorai.musicplayerapplication.Logic.RetrieveAllSongsSQLDatabaseAsyncTask;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;
import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.MainActivity;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;


public class SongsTabFragment extends Fragment {

    private View rootView;
    private ArrayList<Song> songList;
    private SongListAdapter mAdapter;

    public SongsTabFragment() {
        songList = new ArrayList<Song>();
        // set data adapter
        mAdapter = new SongListAdapter(getActivity(), songList );
        // on item list clicked
        mAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Song obj, int position) {
                // tell the music service what the current playlist is
                MainActivity.currentPlaylist=songList; // change the whole currentplaylist thing
                MainActivity.musicSrv.setList(songList);
                // select the song that was clicked from the current playlist
                MainActivity.selectSong(position);
            }
        });
    }

    public static SongsTabFragment newInstance() {
        SongsTabFragment fragment = new SongsTabFragment();
        return fragment;
    }

    public void updateSongList(String songPath) {
        songList.add(new Song(songPath));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_song, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        new RetrieveAllSongsSQLDatabaseAsyncTask(this).execute();

        // set list adapter
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

}
