package com.example.mylibraryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> books;

    public BookAdapter(Context context, ArrayList<String> books) {
        super(context, R.layout.list_item, books);
        this.context = context;
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the custom layout if necessary
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Get the current book title
        String bookTitle = books.get(position);

        // Find the TextView in the custom layout and set the title
        TextView titleTextView = convertView.findViewById(R.id.textViewTitle);
        titleTextView.setText(bookTitle);

        return convertView;
    }
}
