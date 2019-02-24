package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.musicplayer.yorai.musicplayerapplication.Model.Song;

import java.util.ArrayList;

public class RetrievePlaylistSQLDatabaseAsyncTask extends AsyncTask<String, Song, Void> {
    DatabaseHelper myDB;
    Fragment fragment;
    ArrayList<Song> songPlaylist;

    public RetrievePlaylistSQLDatabaseAsyncTask(Fragment fragment) {
        this.fragment = fragment;
        this.songPlaylist = new ArrayList<Song>();
    }

    @Override
    protected Void doInBackground(String... strings) {
        myDB = new DatabaseHelper(fragment.getContext());

        Cursor cursor = myDB.getAll();
        if(cursor!=null && cursor.moveToFirst()){
            do {
                String songPath = cursor.getString(0);
                publishProgress(new Song(songPath));
//                songPlaylist.add(new Song(songPath));
            }
            while (cursor.moveToNext());
        }
        return null;
    }

    //on progress update~ update arrayList and refresh listView
    //onPost = void
    @Override
    protected void onProgressUpdate(Song... song) {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.getActivity().
    }

    @Override
    protected void onPostExecute(ArrayList<Song> result) {
        super.onPostExecute(result);
        //MainActivity.songPlaylist = (ArrayList<Song>) this.songPlaylist.clone();
        fragment.setSongArraylist(result);
        //refresh listView
    }
}
