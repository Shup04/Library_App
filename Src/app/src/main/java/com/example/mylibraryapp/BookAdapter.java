package com.example.mylibraryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private Context context;
    private List<Book> books;
    private DatabaseReference ratingsRef;

    public BookAdapter(Context context, List<Book> books) {
        super(context, R.layout.list_item, books);
        this.context = context;
        this.books = books;
        // Reference to "ratings" node in your Firebase Realtime Database
        ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.list_item, parent, false);
        }

        Book book = books.get(position);

        // Bind views
        TextView titleTextView = itemView.findViewById(R.id.textViewTitle);
        TextView authorTextView = itemView.findViewById(R.id.textViewAuthor);
        ImageView coverImageView = itemView.findViewById(R.id.imageViewCover);
        RatingBar ratingBar = itemView.findViewById(R.id.ratingBar);

        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());

        // Load cover image if coverId is valid, otherwise use a placeholder
        if (book.getCoverId() != 0) {
            String coverUrl = "https://covers.openlibrary.org/b/id/" + book.getCoverId() + "-M.jpg";
            Picasso.get().load(coverUrl).into(coverImageView);
        } else {
            coverImageView.setImageResource(R.drawable.placeholder_cover); // Add a placeholder image in drawable
        }

        // Set the current rating (initially 0) before updating from Firebase
        ratingBar.setRating((float) book.getRating());

        // Query Firebase for the rating (assumes ratings are stored under "ratings/<bookTitle>")
        ratingsRef.child(book.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the whole BookRating object
                    BookRating ratingObject = snapshot.getValue(BookRating.class);
                    if (ratingObject != null) {
                        float ratingValue = ratingObject.getRating();
                        book.setRating(ratingValue);
                        ratingBar.setRating(ratingValue);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });


        return itemView;
    }
}
