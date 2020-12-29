package org.savan.LibraryUsingSqlite3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.meicode.LibraryUsingSqlite3.R;

public class BookActivity extends AppCompatActivity {
    private static final String TAG = "BookActivity";

    private TextView name, author, pages, language,  shortDesc, longDesc;
    private ImageView emptyStar, filledStar, bookImage;

    private Book incomingBook;

    private boolean hasChanged = false;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        initViews();

        Intent intent = getIntent();
        try {
            int bookId = intent.getIntExtra("bookId", -1);
            if (bookId != -1) {
                GetBookByIdAsyncTask getBookByIdAsyncTask = new GetBookByIdAsyncTask();
                getBookByIdAsyncTask.execute(bookId);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private class GetBookByIdAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            databaseHelper = new DatabaseHelper(BookActivity.this);
            database = databaseHelper.getReadableDatabase();
            incomingBook = new Book();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            cursor = database.query("books", null, "_id=?", new String[] {String.valueOf(integers[0])},
                    null, null, null);
            if (cursor.moveToFirst()) {
                for (int i=0; i<cursor.getColumnCount(); i++) {
                    switch (cursor.getColumnName(i)) {
                        case "_id":
                            incomingBook.set_id(cursor.getInt(i));
                            break;
                        case "name":
                            incomingBook.setName(cursor.getString(i));
                            break;
                        case "author":
                            incomingBook.setAuthor(cursor.getString(i));
                            break;
                        case "language":
                            incomingBook.setLanguage(cursor.getString(i));
                            break;
                        case "pages":
                            incomingBook.setPages(cursor.getInt(i));
                            break;
                        case "short_desc":
                            incomingBook.setShort_desc(cursor.getString(i));
                            break;
                        case "long_desc":
                            incomingBook.setLong_desc(cursor.getString(i));
                            break;
                        case "image_url":
                            incomingBook.setImage_url(cursor.getString(i));
                            break;
                        case "isFavorite":
                            int isFavorite = cursor.getInt(i);
                            if (isFavorite == 1) {
                                incomingBook.setFavorite(true);
                            }else {
                                incomingBook.setFavorite(false);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            name.setText(incomingBook.getName());
            author.setText("Author: " + incomingBook.getAuthor());
            pages.setText("Pages: " + String.valueOf(incomingBook.getPages()));
            language.setText("Language: " + incomingBook.getLanguage());
            shortDesc.setText(incomingBook.getShort_desc());
            longDesc.setText(incomingBook.getLong_desc());
            Glide.with(BookActivity.this)
                    .asBitmap()
                    .load(incomingBook.getImage_url())
                    .into(bookImage);
            if (incomingBook.isFavorite()) {
                emptyStar.setVisibility(View.GONE);
                filledStar.setVisibility(View.VISIBLE);
                filledStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateLikedSituationAsyncTask updateLikedSituationAsyncTask = new UpdateLikedSituationAsyncTask();
                        updateLikedSituationAsyncTask.execute(false);
                    }
                });
            }else {
                filledStar.setVisibility(View.GONE);
                emptyStar.setVisibility(View.VISIBLE);
                emptyStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateLikedSituationAsyncTask updateLikedSituationAsyncTask = new UpdateLikedSituationAsyncTask();
                        updateLikedSituationAsyncTask.execute(true);
                    }
                });
            }
        }
    }

    private class UpdateLikedSituationAsyncTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            ContentValues contentValues = new ContentValues();
            if (booleans[0]) {
                contentValues.put("isFavorite", 1);
            }else {
                contentValues.put("isFavorite", -1);
            }

            try {
                int rowAffected = database.update("books", contentValues, "_id=?", new String[] {String.valueOf(incomingBook.get_id())});
                if (rowAffected>0) {
                    hasChanged = true;
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (hasChanged) {
                GetBookByIdAsyncTask getBookByIdAsyncTask = new GetBookByIdAsyncTask();
                getBookByIdAsyncTask.execute(incomingBook.get_id());
            }
        }
    }

    private void initViews () {
        Log.d(TAG, "initViews: started");
        name = (TextView) findViewById(R.id.bookName);
        author = (TextView) findViewById(R.id.author);
        pages = (TextView) findViewById(R.id.pages);
        language = (TextView) findViewById(R.id.language);
        shortDesc = (TextView) findViewById(R.id.shortDesc);
        longDesc = (TextView) findViewById(R.id.longDesc);

        emptyStar = (ImageView) findViewById(R.id.emptyStar);
        filledStar = (ImageView) findViewById(R.id.filledStar);
        bookImage = (ImageView) findViewById(R.id.bookImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        database.close();
    }
}
