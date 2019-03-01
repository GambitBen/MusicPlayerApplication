package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.musicplayer.yorai.musicplayerapplication.MainActivity;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

public class CreateSongSQLDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
    DatabaseHelper myDB;
    MainActivity activity;

    public CreateSongSQLDatabaseAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        myDB = new DatabaseHelper(activity);
        //retrieve song info
        ContentResolver contentResolver = activity.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            int location = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                String thisPath = musicCursor.getString(location);
                if (!thisPath.equals("/storage/sdcard/Notifications/Calendar Notification.ogg")) {
                    if (!myDB.exists(thisPath)) {
                        Song song = new Song(thisPath);
                        myDB.add(song.getTitle(), song.getArtist(), song.getAlbum(), thisPath);
                        Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: thisPath=" + thisPath);
                    }
                }
            }
            while (musicCursor.moveToNext());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        activity.populateActivity();
    }
}
