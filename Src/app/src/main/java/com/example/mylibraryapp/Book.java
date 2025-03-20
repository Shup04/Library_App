package com.example.mylibraryapp;

public class Book {
    private String title;
    private String author;
    private int coverId; // If not available, 0 can be used
    private float rating; // Initial rating (will be updated from Firebase)

    public Book(String title, String author, int coverId) {
        this.title = title;
        this.author = author;
        this.coverId = coverId;
        this.rating = 0f;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCoverId() { return coverId; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating;
    }
}
