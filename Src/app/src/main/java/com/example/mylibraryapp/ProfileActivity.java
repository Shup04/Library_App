package com.example.mylibraryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView textView;
    private Button btnEditProfile, btnRatingHistory, btnBack;
    private EditText editTextUsername;
    private Button buttonSubmit;
    private ImageView imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        // 1. Find views
        textView = findViewById(R.id.textView);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnRatingHistory = findViewById(R.id.btnRatingHistory);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        imageView4 = findViewById(R.id.imageView4);

        // 2. The new Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the previous page (SecondActivity)
                finish();
            }
        });

        // 3. "Edit Profile" logic: show EditText + Submit button
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextUsername.setVisibility(View.VISIBLE);
                buttonSubmit.setVisibility(View.VISIBLE);
                textView.setText("Enter your new username below, then tap Submit");
            }
        });

        // 4. "Rating History" button sets the TextView
        btnRatingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Put history here");
            }
        });

        // 5. "Submit" button updates username
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = editTextUsername.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    textView.setText("Welcome, " + newUsername + "!");
                } else {
                    textView.setText("Username can't be empty!");
                }
                editTextUsername.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);
            }
        });
    }
}
