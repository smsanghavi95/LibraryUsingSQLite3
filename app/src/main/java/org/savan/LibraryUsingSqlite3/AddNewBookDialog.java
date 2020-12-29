package org.savan.LibraryUsingSqlite3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.meicode.LibraryUsingSqlite3.R;

public class AddNewBookDialog extends DialogFragment {
    private static final String TAG = "AddNewBookDialog";

    interface AddNewBook {
        void onAddingNewBookResult (Book book);
    }

    private AddNewBook addNewBook;

    private EditText name, author, language, pages, shortDesc, longDesc, imageUrl;
    private Button add, cancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_new_book, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Add New Book")
                .setView(view);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName(name.getText().toString());
                book.setAuthor(author.getText().toString());
                book.setLanguage(language.getText().toString());
                book.setPages(Integer.valueOf(pages.getText().toString()));
                book.setShort_desc(shortDesc.getText().toString());
                book.setLong_desc(longDesc.getText().toString());
                book.setImage_url(imageUrl.getText().toString());
                book.setFavorite(false);

                try {
                    addNewBook = (AddNewBook) getActivity();
                    addNewBook.onAddingNewBookResult(book);
                    dismiss();
                }catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void initViews(View view) {
        name = (EditText) view.findViewById(R.id.edtTxtName);
        author = (EditText) view.findViewById(R.id.edtTxtAuthor);
        language = (EditText) view.findViewById(R.id.edtTxtLanguage);
        pages = (EditText) view.findViewById(R.id.edtTxtPages);
        shortDesc = (EditText) view.findViewById(R.id.edtTxtShortDesc);
        longDesc = (EditText) view.findViewById(R.id.edtTxtLongDesc);
        imageUrl = (EditText) view.findViewById(R.id.edtTxtImageUrl);
        add = (Button) view.findViewById(R.id.btnAdd);
        cancel = (Button) view.findViewById(R.id.btnCancel);
    }
}
