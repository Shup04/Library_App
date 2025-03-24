package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView imageViewCover;
    private TextView textViewTitle, textViewAuthor;
    private RatingBar ratingBar;
    private EditText editTextSuggestion;
    private Button buttonSubmit;
    private DatabaseReference ratingsRef;

    // We'll use the book title as the key (you might want to use a unique ID in a real app)
    private String bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        imageViewCover = findViewById(R.id.imageViewCover);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        ratingBar = findViewById(R.id.ratingBar);
        editTextSuggestion = findViewById(R.id.editTextSuggestion);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Retrieve book details from Intent extras
        Intent intent = getIntent();
        bookTitle = intent.getStringExtra("bookTitle");
        String author = intent.getStringExtra("bookAuthor");
        int coverId = intent.getIntExtra("coverId", 0);
        float currentRating = intent.getFloatExtra("currentRating", 0);

        textViewTitle.setText(bookTitle);
        textViewAuthor.setText(author);
        ratingBar.setRating(currentRating);

        // Construct cover image URL from coverId using OpenLibrary service
        if (coverId != 0) {
            String coverUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
            Picasso.get().load(coverUrl).into(imageViewCover);
        } else {
            imageViewCover.setImageResource(R.drawable.placeholder_cover); // Make sure you have this drawable
        }

        // Reference to the "ratings" node; here we use bookTitle as the key (you might consider a unique id)
        ratingsRef = FirebaseDatabase.getInstance().getReference("ratings").child(bookTitle);

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        float newRating = ratingBar.getRating();
        String suggestion = editTextSuggestion.getText().toString().trim();

        if (newRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Create a BookRating object and store it in Firebase
        BookRating rating = new BookRating(bookTitle, textViewAuthor.getText().toString(), newRating, suggestion, userId);
        // Save under the key 'bookTitle' (or use push() to generate a unique key if you want to keep history)
        ratingsRef.setValue(rating)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookDetailActivity.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the detail view
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BookDetailActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show());
    }
}
