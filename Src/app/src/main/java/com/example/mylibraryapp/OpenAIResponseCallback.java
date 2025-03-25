package com.example.mylibraryapp;

public interface OpenAIResponseCallback {
    void onSuccess(String recommendation);
    void onFailure(Exception e);
}
