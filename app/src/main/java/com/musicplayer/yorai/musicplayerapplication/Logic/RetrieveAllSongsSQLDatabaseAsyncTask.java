package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.database.Cursor;
import android.os.AsyncTask;

import com.musicplayer.yorai.musicplayerapplication.Fragments.SongsTabFragment;

public class RetrieveAllSongsSQLDatabaseAsyncTask extends AsyncTask<Void, String, Void> {
    DatabaseHelper myDB;
    SongsTabFragment fragment;
//    ArrayList<Song> songPlaylist;

    public RetrieveAllSongsSQLDatabaseAsyncTask(SongsTabFragment fragment) {
        this.fragment = fragment;
//        this.songPlaylist = new ArrayList<Song>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        myDB = new DatabaseHelper(fragment.getContext());

        Cursor cursor = myDB.getAll();
        if(cursor!=null && cursor.moveToFirst()){
            do {
                String songPath = cursor.getString(0);
                publishProgress(songPath);
//                songPlaylist.add(new Song(songPath));
            }
            while (cursor.moveToNext());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... strings) {
        fragment.updateSongList(strings[0]);
    }

}
