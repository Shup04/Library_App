package com.example.mylibraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookRatingAdapter extends RecyclerView.Adapter<BookRatingAdapter.RatingViewHolder> {

    private List<BookRating> ratingList;

    public BookRatingAdapter(List<BookRating> ratingList) {
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rating_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        BookRating rating = ratingList.get(position);
        holder.bookTitleTv.setText(rating.getBookTitle());
        holder.bookAuthorTv.setText(rating.getBookAuthor());
        holder.ratingTv.setText("Rating: " + rating.getRating());
        holder.suggestionTv.setText(rating.getSuggestion());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTv, bookAuthorTv, ratingTv, suggestionTv;
        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTv = itemView.findViewById(R.id.book_title_tv);
            bookAuthorTv = itemView.findViewById(R.id.book_author_tv);
            ratingTv = itemView.findViewById(R.id.rating_tv);
            suggestionTv = itemView.findViewById(R.id.suggestion_tv);
        }
    }
}
