package com.example.mylibraryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    // Global reference for the ImageView inside the profile setup dialog
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::loginMsg);
    }

    public void loginMsg(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(MainActivity.this, "Login was successful!", Toast.LENGTH_SHORT).show();
                // Check if the profile exists in the database.
                String uid = user.getUid();
                FirebaseDatabase.getInstance().getReference("users").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Profile exists; go directly to the next activity.
                                    startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                    finish();
                                } else {
                                    // Profile doesn't exist; show the profile setup dialog.
                                    showProfileDialog();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Database error.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File file = new File(getFilesDir(), "profile_image.jpg"); // For simplicity, using a fixed name
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void showProfileDialog() {
        // Inflate your dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_profile_setup, null);

        // Get the references from the dialog layout
        EditText usernameEt = dialogView.findViewById(R.id.username_et);
        profileImageView = dialogView.findViewById(R.id.profile_image);  // Save the reference globally
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);

        // Set up button for image selection
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Build and show the dialog
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

                        // If an image was selected, copy it to internal storage.
                        String profilePicPath = "default_profile_pic_path"; // Use a default if needed.
                        if (selectedImageUri != null) {
                            String localPath = copyImageToInternalStorage(selectedImageUri);
                            if (localPath != null) {
                                profilePicPath = localPath;
                            }
                        }
                        // Save the profile info to Firebase Realtime Database
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("profilePicUrl", profilePicPath);
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
            // Update the dialog's ImageView preview if available
            if (profileImageView != null && selectedImageUri != null) {
                profileImageView.setImageURI(selectedImageUri);
            }
        }
    }
}
