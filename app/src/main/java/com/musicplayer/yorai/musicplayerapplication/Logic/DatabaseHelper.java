package com.musicplayer.yorai.musicplayerapplication.Logic;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "title.db";
    public static final String TABLE_NAME = "song_database";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "ARTIST";
    public static final String COL_4 = "ALBUM";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,TITLE TEXT,ARTIST TEXT,ALBUM TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
    }

    /**
     * Return a cursor object with all rows in the table.
     * @return A cursor suitable for use in a SimpleCursorAdapter
     */
    public Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return null;
        }
        return db.rawQuery("select * from " + TABLE_NAME + " order by title, artist", null);
    }

    /**
     * Return values for a single row with the specified id
     * @param id The unique id of the row to fetch
     * @return All column values are stored as properties in the ContentValues object
     */
    public ContentValues get(long id) {
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return null;
        }
        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("select title, priority from " + TABLE_NAME + " where _id = ?", new String[] { String.valueOf(id) });
        if (cur.moveToNext()) {
            row.put("title", cur.getString(0));
            row.put("artist", cur.getString(1));
            row.put("album", cur.getString(2));
        }
        cur.close();
        db.close();
        return row;
    }

    /**
     * Add a new row to the database table
     * @param title The title value for the new row
     * @param artist The artist value for the new row
     * @param album The album value for the new row
     * @return The unique id of the newly added row
     */
    public long add(String title, String artist, String album) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        row.put("title", title);
        row.put("artist", artist);
        row.put("album", album);
        long id = db.insert("" + TABLE_NAME + "", null, row);
        db.close();
        return id;
    }

    /**
     * Delete the specified row from the database table. For simplicity reasons, nothing happens if
     * this operation fails.
     * @param id The unique id for the row to delete
     */
    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete("" + TABLE_NAME + "", "_id = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    /**
     * Updates a row in the database table with new column values, without changing the unique id of the row.
     * For simplicity reasons, nothing happens if this operation fails.
     * @param id The unique id of the row to update
     * @param title The new title value
     * @param artist The new artist value
     * @param album The new album value
     */
    public void update(long id, String title, String artist, String album) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        row.put("title", title);
        row.put("artist", artist);
        row.put("album", album);
        db.update("" + TABLE_NAME + "", row, "_id = ?", new String[] { String.valueOf(id) } );
        db.close();
    }



//    public boolean insertData(String id,String title, String link, String category){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_1,id);
//        contentValues.put(COL_2,title);
//        contentValues.put(COL_3,link);
//        contentValues.put(COL_4,category);
//        long result = db.insert(TABLE_NAME,null,contentValues);
//
//        if(result==(-1))
//            return false;
//        else
//            return true;
//    }
//
//    public Integer deleteData(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
//    }

}
