package com.example.mylibraryapp;

public class BookRating {
    private String bookTitle;
    private String bookAuthor;
    private float rating;
    private String suggestion;
    private String userId;

    public BookRating() {}

    public BookRating(String bookTitle, String bookAuthor, float rating, String suggestion, String userId) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.rating = rating;
        this.suggestion = suggestion;
        this.userId = userId;
    }

    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public float getRating() { return rating; }
    public String getSuggestion() { return suggestion; }
    public String getUserId() { return userId; }
}
