package org.savan.LibraryUsingSqlite3;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.meicode.LibraryUsingSqlite3.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewBookDialog.AddNewBook, BooksRecViewAdapter.DeleteBook {
    private static final String TAG = "MainActivity";

    @Override
    public void onDeletingResult(int bookId) {
        Log.d(TAG, "onDeletingResult: trying to delete book with an id of: " + bookId);
        try {
            databaseHelper.delete(database, bookId);
            DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
            databaseAsyncTask.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAddingNewBookResult(Book book) {
        Log.d(TAG, "onAddingNewBookResult: new Book: " + book.toString());
        try {
            databaseHelper.insert(database, book);
            DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
            databaseAsyncTask.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private RecyclerView recyclerView;
    private BooksRecViewAdapter adapter;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private ArrayList<Book> allBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.booksRecView);
        adapter = new BooksRecViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allBooks = new ArrayList<>();

        DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
        databaseAsyncTask.execute();
    }

    private class DatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            adapter.clearAdapter();
            databaseHelper = new DatabaseHelper(MainActivity.this);
            database = databaseHelper.getReadableDatabase();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                cursor = database.query("books", null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    for (int i=0; i<cursor.getCount(); i++) {
                        Book book = new Book();
                        for (int j=0; j<cursor.getColumnCount(); j++) {
                            switch (cursor.getColumnName(j)) {
                                case "_id":
                                    book.set_id(cursor.getInt(j));
                                    break;
                                case "name":
                                    book.setName(cursor.getString(j));
                                    break;
                                case "author":
                                    book.setAuthor(cursor.getString(j));
                                    break;
                                case "language":
                                    book.setLanguage(cursor.getString(j));
                                    break;
                                case "pages":
                                    book.setPages(cursor.getInt(j));
                                    break;
                                case "short_desc":
                                    book.setShort_desc(cursor.getString(j));
                                    break;
                                case "long_desc":
                                    book.setLong_desc(cursor.getString(j));
                                    break;
                                case "image_url":
                                    book.setImage_url(cursor.getString(j));
                                    break;
                                case "isFavorite":
                                    int isFavorite = cursor.getInt(j);
                                    if (isFavorite == 1) {
                                        book.setFavorite(true);
                                    }else {
                                        book.setFavorite(false);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        allBooks.add(book);
                        cursor.moveToNext();
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter.setBooks(allBooks);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        database.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addNewBook) {
            AddNewBookDialog addNewBookDialog = new AddNewBookDialog();
            addNewBookDialog.show(getSupportFragmentManager(), "add new book");
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
