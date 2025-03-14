package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_activity);

        Button login_button = findViewById(R.id.rate_books_button);
        login_button.setOnClickListener(this::rateBooks);
    }

    // Search function
    public void performSearch(View view) {
        EditText searchInput = findViewById(R.id.search_input);
        String searchText = searchInput.getText().toString().trim();

        if (!searchText.isEmpty()) {
            Toast.makeText(this, "Searching for: " + searchText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a search term!", Toast.LENGTH_SHORT).show();
        }
    }

    // Open thirdPage Activity
    public void rateBooks(View view) {
        Intent intent = new Intent(this, thirdpage.class);
        startActivity(intent);
    }
}
