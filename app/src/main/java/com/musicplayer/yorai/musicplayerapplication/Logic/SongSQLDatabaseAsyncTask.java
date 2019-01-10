package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.musicplayer.yorai.musicplayerapplication.MainActivity;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

import java.util.ArrayList;

public class SongSQLDatabaseAsyncTask extends AsyncTask<Context, Void, Void> {
    DatabaseHelper myDB;
    Context context;
    ArrayList<Song> songDatabase;

    public SongSQLDatabaseAsyncTask(Context context) {
        this.context = context;
        this.songDatabase = new ArrayList<Song>();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        myDB = new DatabaseHelper(context);
        //retrieve song info
        ContentResolver contentResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);
        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor.moveToFirst() "+musicCursor.moveToFirst());
        //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: musicCursor!=null "+(musicCursor!=null));
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.ARTIST);
            int location = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                String thisPath = musicCursor.getString(location);
                //Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: thiId="+thisPath);
//                long thisId = musicCursor.getLong(idColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
                if (!thisPath.equals("/storage/sdcard/Notifications/Calendar Notification.ogg")) {
                    Song song = new Song(thisPath);
                    songDatabase.add(song);//, thisId, thisTitle, thisArtist));
                    myDB.add(song.getTitle(), song.getArtist(), song.getAlbum());
                    Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: thisPath="+thisPath);
                    Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: songDatabase.indexOf="+songDatabase.indexOf(song));
                    Log.d("!!!!!!!!!!!!!!!!!!!!", "getSongList: songDatabase.indexOf="+myDB.get(songDatabase.indexOf(song)).toString());
                }
            }
            while (musicCursor.moveToNext());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        MainActivity.songDatabase = (ArrayList<Song>) this.songDatabase.clone();
    }
}
