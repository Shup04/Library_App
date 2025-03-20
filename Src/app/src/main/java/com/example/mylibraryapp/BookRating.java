package com.example.mylibraryapp;

public class BookRating {
    private String bookTitle;
    private String bookAuthor;
    private float rating;
    private String suggestion;

    public BookRating() {}

    public BookRating(String bookTitle, String bookAuthor, float rating, String suggestion) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.rating = rating;
        this.suggestion = suggestion;
    }

    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public float getRating() { return rating; }
    public String getSuggestion() { return suggestion; }
}
