package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private ListView listView;
    private BookAdapter adapter;
    private ArrayList<String> bookTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_activity);

        listView = findViewById(R.id.listView);
        bookTitles = new ArrayList<>();
        adapter = new BookAdapter(this, bookTitles);
        listView.setAdapter(adapter);

        // Default search query
        searchBooks("android");

        Button loginButton = findViewById(R.id.rate_books_button);
        loginButton.setOnClickListener(this::rateBooks);
    }

    public void searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            // Use HTTPS to avoid cleartext issues:
            String url = "https://openlibrary.org/search.json?q=" + encodedQuery;

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                bookTitles.clear();
                                JSONArray docsArray = response.getJSONArray("docs");
                                for (int i = 0; i < docsArray.length(); i++) {
                                    JSONObject bookObject = docsArray.getJSONObject(i);
                                    String title = bookObject.optString("title");
                                    bookTitles.add(title);
                                }
                                // Notify adapter of the data change
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Log.e("searchBooks", "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("searchBooks", "Error: " + error.toString());
                }
            });

            queue.add(request);
        } catch (UnsupportedEncodingException e) {
            Log.e("searchBooks", "Encoding error: " + e.getMessage());
        }
    }

    // Called via XML onClick attribute or programmatically
    public void performSearch(View view) {
        EditText searchInput = findViewById(R.id.search_input);
        String searchText = searchInput.getText().toString().trim();

        if (!searchText.isEmpty()) {
            Toast.makeText(this, "Searching for: " + searchText, Toast.LENGTH_SHORT).show();
            searchBooks(searchText);
        } else {
            Toast.makeText(this, "Please enter a search term!", Toast.LENGTH_SHORT).show();
        }
    }

    public void rateBooks(View view) {
        Intent intent = new Intent(this, thirdpage.class);
        startActivity(intent);
    }
}

