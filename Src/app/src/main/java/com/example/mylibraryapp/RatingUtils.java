package com.example.mylibraryapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class RatingUtils {

    public static Task<String> getUserRatingsSummary() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return com.google.android.gms.tasks.Tasks.forResult("No user logged in.");
        }
        String uid = user.getUid();
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
        Query query = ratingsRef.orderByChild("userId").equalTo(uid);

        // Fetch the ratings and build the summary string.
        return query.get().continueWith(task -> {
            DataSnapshot snapshot = task.getResult();
            StringBuilder summary = new StringBuilder();
            for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                String title = ratingSnapshot.child("bookTitle").getValue(String.class);
                String author = ratingSnapshot.child("bookAuthor").getValue(String.class);
                Object ratingObj = ratingSnapshot.child("rating").getValue();
                String ratingStr = (ratingObj != null) ? ratingObj.toString() : "0";
                String suggestion = ratingSnapshot.child("suggestion").getValue(String.class);
                summary.append(title)
                        .append(" by ")
                        .append(author)
                        .append(": ")
                        .append(ratingStr)
                        .append(" stars; Suggestion: ")
                        .append(suggestion)
                        .append("\n");
            }
            return summary.toString();
        });
    }
}
