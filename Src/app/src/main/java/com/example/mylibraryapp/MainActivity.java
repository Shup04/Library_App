package com.example.mylibraryapp;

import android.content.Intent;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your main layout with login_button

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::loginMsg);
    }

    public void loginMsg(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(MainActivity.this, "Login was successful!", Toast.LENGTH_SHORT).show();
                // Show the profile setup dialog
                showProfileDialog();
            } else {
                Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileDialog() {
        // Inflate the custom layout for profile setup
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_profile_setup, null);
        EditText usernameEt = dialogView.findViewById(R.id.username_et);
        ImageView profileImageView = dialogView.findViewById(R.id.profile_image);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);

        // When the user taps "Select Profile Image", launch an image picker
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Set up your profile")
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = usernameEt.getText().toString().trim();
                        if (username.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Use the selected image URI if one was chosen; otherwise, use a default placeholder value.
                        String profilePicUrl = (selectedImageUri != null) ? selectedImageUri.toString() : "default_profile_pic_url";

                        // Save the profile info to Firebase Realtime Database
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("profilePicUrl", profilePicUrl);
                            FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(userData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();
                                            // Proceed to next activity
                                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Optionally, you can update an ImageView in the dialog to show the selected image.
        }
    }
}
