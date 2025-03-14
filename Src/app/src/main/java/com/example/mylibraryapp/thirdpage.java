package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class thirdpage extends AppCompatActivity {

    private ArrayList<TextView> stars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdpage);

        // Add all stars to the list
        stars.add(findViewById(R.id.star1));
        stars.add(findViewById(R.id.star2));
        stars.add(findViewById(R.id.star3));
        stars.add(findViewById(R.id.star4));
        stars.add(findViewById(R.id.star5));
    }

    // Method to handle star rating
    public void rateStar(View view) {
        int selectedStar = stars.indexOf((TextView) view);

        // Change colors for selected stars
        for (int i = 0; i < stars.size(); i++) {
            if (i <= selectedStar) {
                stars.get(i).setTextColor(0xFFFFD700); // Gold color for selected stars
                stars.get(i).setTextSize(40); // Make selected stars bigger
            } else {
                stars.get(i).setTextColor(0xFF888888); // Grey for unselected stars
                stars.get(i).setTextSize(36); // Reset size
            }
        }
    }

    // Ensure back button returns to SecondActivity instead of MainActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SecondActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}