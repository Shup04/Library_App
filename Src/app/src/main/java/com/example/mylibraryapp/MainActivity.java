package com.example.mylibraryapp;

import android.content.Intent;

import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set button click listener
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this::LoginMsg);

        // Setup for anonymous auth


    }

    public void LoginMsg(View view) {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(getBaseContext().getString(R.string.default_web_client_id))
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in anonymously.
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d("AUTH", "Anonymous sign-in successful: " + user.getUid());
                        Toast.makeText(this, "Login was successful!", Toast.LENGTH_LONG).show();
                        // You can now access Firebase services.
                    } else {
                        Log.w("AUTH", "Anonymous sign-in failed.", task.getException());
                        Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
                    }
                });


        Toast.makeText(this, "Login was successful!", Toast.LENGTH_LONG).show();

        // Move to the next activity
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
