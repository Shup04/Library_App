package com.example.mylibraryapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    // For updating profile imege
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private TextView usernameTextView;
    private Button btnEditProfile, btnRatingHistory, btnBack;
    private EditText editTextUsername;
    private Button buttonSubmit;
    private ImageView profileImageView;

    private DatabaseReference userRef;
    private String currentUsername;
    private String currentProfilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        // 1. Find views
        usernameTextView = findViewById(R.id.textView);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        //btnRatingHistory = findViewById(R.id.btnRatingHistory);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        profileImageView = findViewById(R.id.imageView4);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            // Listen for changes so the UI updates automatically when the data is modified.
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUsername = snapshot.child("username").getValue(String.class);
                        currentProfilePicUrl = snapshot.child("profilePicUrl").getValue(String.class);

                        usernameTextView.setText("Welcome, " + currentUsername + "!");

                        // If you have a local URI, you can use setImageURI. Otherwise, consider using Glide for remote images.
                        if (currentProfilePicUrl != null && !currentProfilePicUrl.equals("default_profile_pic_url")) {
                            // Create a file URI from the stored file path
                            File file = new File(currentProfilePicUrl);
                            if (file.exists()) {
                                profileImageView.setImageURI(Uri.fromFile(file));
                            } else {
                                // Handle missing file (perhaps show a default image)
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
                usernameTextView.setText("Enter your new username below, then tap Submit");
            }
        });
        

        // 5. "Submit" button updates username
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = editTextUsername.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    usernameTextView.setText("Welcome, " + newUsername + "!");
                } else {
                    usernameTextView.setText("Username can't be empty!");
                }
                editTextUsername.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);
            }
        });
    }
}
