package com.example.mylibraryapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class RatingUtils {

    public static Task<String> getAllRatingsAsString() {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
        return ratingsRef.get().continueWith(task -> {
            DataSnapshot snapshot = task.getResult();
            StringBuilder sb = new StringBuilder();
            // Loop through all ratings in the "ratings" node.
            for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                String bookTitle = ratingSnapshot.child("bookTitle").getValue(String.class);
                String bookAuthor = ratingSnapshot.child("bookAuthor").getValue(String.class);
                Object ratingValue = ratingSnapshot.child("rating").getValue();
                String suggestion = ratingSnapshot.child("suggestion").getValue(String.class);

                sb.append(bookTitle)
                        .append(" by ")
                        .append(bookAuthor)
                        .append(": ")
                        .append(ratingValue != null ? ratingValue.toString() : "N/A")
                        .append(" stars; Suggestion: ")
                        .append(suggestion)
                        .append("\n");
            }
            return sb.toString();
        });
    }
}
