package org.savan.LibraryUsingSqlite3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static  final String DATABASE_NAME = "bookStore";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: attempting to create database");
        String sqlStatement = "CREATE TABLE books (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "author TEXT," +
                "language TEXT," +
                "pages INTEGER," +
                "short_desc TEXT," +
                "long_desc TEXT, " +
                "image_url TEXT," +
                "isFavorite INTEGER);";
        db.execSQL(sqlStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert (SQLiteDatabase db, Book book) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", book.getName());
        contentValues.put("author", book.getAuthor());
        contentValues.put("language", book.getLanguage());
        contentValues.put("pages", book.getPages());
        contentValues.put("short_desc", book.getShort_desc());
        contentValues.put("long_desc", book.getLong_desc());
        contentValues.put("image_url", book.getImage_url());

        if (book.isFavorite()) {
            contentValues.put("isFavorite", 1);
        }else {
            contentValues.put("isFavorite", -1);
        }

        db.insert("books", null, contentValues);
    }

    public void delete (SQLiteDatabase database, int bookId) {
        database.delete("books", "_id=?", new String[]{String.valueOf(bookId)});
    }
}
