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

import com.musicplayer.yorai.musicplayerapplication.Adapters.ArtistListAdapter;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ArtistsFragmentTab extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<String> artistList;
    private ListView artistView;

    public ArtistsFragmentTab() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ArtistsFragmentTab newInstance(int sectionNumber) {
        ArtistsFragmentTab fragment = new ArtistsFragmentTab();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistView = (ListView)rootView.findViewById(R.id.song_list);
        //artistList = MainActivity.currentPlaylist;
        getArtistList();

//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Collections.sort(artistList, new Comparator<String>(){
            public int compare(String a, String b){
                return a.compareTo(b);
            }
        });

        ArtistListAdapter songAdt = new ArtistListAdapter(getActivity(), artistList);
        artistView.setAdapter(songAdt);

        return rootView;
    }

    public void getArtistList() {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                artistList.add(thisArtist);
            }
            while (musicCursor.moveToNext());
        }
    }
}
