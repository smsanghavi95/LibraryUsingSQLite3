package org.savan.LibraryUsingSqlite3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.meicode.LibraryUsingSqlite3.R;

import java.util.ArrayList;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> {
    private static final String TAG = "BooksRecViewAdapter";

    public interface DeleteBook {
        void onDeletingResult (int bookId);
    }

    private DeleteBook deleteBook;

    private Context context;
    private ArrayList<Book> books = new ArrayList<>();

    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }

    public BooksRecViewAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_book, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called");
        viewHolder.bookName.setText(books.get(i).getName());
        viewHolder.bookDesc.setText(books.get(i).getShort_desc());
        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, BookActivity.class);
                intent.putExtra("bookId", books.get(i).get_id());
                context.startActivity(intent);
            }
        });

        viewHolder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Deleting a book")
                        .setMessage("Are you sure you want to delete " + books.get(i).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    deleteBook = (DeleteBook) context;
                                    deleteBook.onDeletingResult(books.get(i).get_id());
                                }catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();
                return true;
            }
        });

        Glide.with(context)
                .asBitmap()
                .load(books.get(i).getImage_url())
                .into(viewHolder.bookImage);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;

        notifyDataSetChanged();
    }

    public void clearAdapter () {
        this.books.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView parent;
        private TextView bookName, bookDesc;
        private ImageView bookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = (CardView) itemView.findViewById(R.id.parent);
            bookName = (TextView) itemView.findViewById(R.id.bookName);
            bookDesc = (TextView) itemView.findViewById(R.id.bookDesc);
            bookImage = (ImageView) itemView.findViewById(R.id.bookImage);
        }
    }
}
