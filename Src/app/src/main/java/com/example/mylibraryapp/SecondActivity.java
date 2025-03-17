package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    private ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);

        // Initialize the ListView and adapter
        listView = findViewById(R.id.listView);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        listView.setAdapter(adapter);

        // Start with a default query, e.g. "android"
        searchBooks("android");

        // Button to move to another activity
        Button rateBooksButton = findViewById(R.id.rate_books_button);
        rateBooksButton.setOnClickListener(this::rateBooks);
    }

    public void searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            // Use HTTPS to avoid cleartext traffic issues:
            String url = "https://openlibrary.org/search.json?q=" + encodedQuery;

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Clear previous results
                                bookList.clear();
                                JSONArray docsArray = response.getJSONArray("docs");
                                for (int i = 0; i < docsArray.length(); i++) {
                                    JSONObject bookObject = docsArray.getJSONObject(i);
                                    String title = bookObject.optString("title");

                                    // Get the first author if available
                                    String author = "Unknown Author";
                                    JSONArray authorArray = bookObject.optJSONArray("author_name");
                                    if (authorArray != null && authorArray.length() > 0) {
                                        author = authorArray.getString(0);
                                    }

                                    // Get cover id if available; if not, it remains 0
                                    int coverId = bookObject.has("cover_i") ? bookObject.optInt("cover_i") : 0;

                                    // Create a new Book object and add to the list
                                    Book book = new Book(title, author, coverId);
                                    bookList.add(book);
                                }
                                // Refresh the adapter
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

    // This method is linked via XML or can be set programmatically
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
