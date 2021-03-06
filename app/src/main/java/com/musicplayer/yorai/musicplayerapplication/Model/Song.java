package com.musicplayer.yorai.musicplayerapplication.Model;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.musicplayer.yorai.musicplayerapplication.Logic.DatabaseHelper;
import com.musicplayer.yorai.musicplayerapplication.MainActivity;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.io.IOException;

public class Song {

    Mp3File mp3file;
    ID3v2 id3v2Tag;

    private String path;
//    private long id;
//    private String title;
//    private String artist;

    public Song(String songPath){//, long songID, String songTitle, String songArtist) {
        path = songPath;
//        id = songID;
//        title = songTitle;
//        artist = songArtist;
        mp3file = null;
        try {
            mp3file = new Mp3File(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        if (mp3file != null)
        {
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
            }
            else {
                id3v2Tag = new ID3v24Tag();
                if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    id3v2Tag.setTrack(id3v1Tag.getTrack());
                    id3v2Tag.setArtist(id3v1Tag.getArtist());
                    id3v2Tag.setTitle(id3v1Tag.getTitle());
                    id3v2Tag.setAlbum(id3v1Tag.getAlbum());
                    id3v2Tag.setYear(id3v1Tag.getYear());
                    id3v2Tag.setGenre(id3v1Tag.getGenre());
                    id3v2Tag.setComment(id3v1Tag.getComment());
                }
            }
            mp3file.setId3v2Tag(id3v2Tag);
        }
    }

    public String getPath(){return path;}
    //    public long getID(){return id;}
    public String getTrack(){return id3v2Tag.getTrack();}
    public String getArtist(){
        String artist = id3v2Tag.getArtist();
        if (artist == null)
            artist = "Unknown";
        return artist;
    }
    public String getTitle(){
        String title = id3v2Tag.getTitle();
        if (title == null) {
            title = mp3file.getFilename();
            // instead of the full path take only the file name
            // from the last '/' until before the ".mp3"
            title = title.substring(title.lastIndexOf('/') + 1, title.length() - 4);
        }
        return title;
    }
    public String getAlbum(){return id3v2Tag.getAlbum();}
    public String getYear(){return id3v2Tag.getYear();}
    public void setYear(String year){id3v2Tag.setYear(year);}
    public int getGenre(){return id3v2Tag.getGenre();}
    public String getGenreDescription(){return id3v2Tag.getGenreDescription();}
    public String getComment(){return id3v2Tag.getComment();}
    public String getLyrics(){return id3v2Tag.getLyrics();}
    public String getComposer(){return id3v2Tag.getComposer();}
    public String getPublisher(){return id3v2Tag.getPublisher();}
    public String getOriginalArtist(){return id3v2Tag.getOriginalArtist();}
    public String getAlbumArtist(){return id3v2Tag.getAlbumArtist();}
    public String getCopyright(){return id3v2Tag.getCopyright();}
    public String getUrl(){return id3v2Tag.getUrl();}
    public String getEncoder(){return id3v2Tag.getEncoder();}
    public byte[] getAlbumImage(){return id3v2Tag.getAlbumImage();}
    public Bitmap getAlbumImage(Context context){
        byte[] albumImage = getAlbumImage();
        Bitmap bitmap;
        if (albumImage != null) {
            bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
        }
        else{
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.song_cover_null);
        }
        return bitmap;
    }
    public long getDuration(){return mp3file.getLengthInSeconds();}

    public void save(String path) {
        try {
            mp3file.save(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
    }
}
