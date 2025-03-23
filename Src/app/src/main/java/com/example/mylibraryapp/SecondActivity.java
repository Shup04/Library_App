package com.example.mylibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;            // ADDED
import android.view.MenuItem;      // ADDED
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;  // ADDED FOR TOOLBAR

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
    private BookAdapter adapter;    // from your existing code
    private ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);

        // 1) Use Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2) Setup your ListView + BookAdapter (unchanged)
        listView = findViewById(R.id.listView);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        listView.setAdapter(adapter);

        // 3) Default query
        searchBooks("android");

        // 4) OnItemClick logic for your list
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Book selectedBook = bookList.get(position);
            Intent intent = new Intent(SecondActivity.this, BookDetailActivity.class);
            intent.putExtra("bookTitle", selectedBook.getTitle());
            intent.putExtra("bookAuthor", selectedBook.getAuthor());
            intent.putExtra("coverId", selectedBook.getCoverId());
            intent.putExtra("currentRating", (float) selectedBook.getRating());
            startActivity(intent);
        });
    }

    // 5) The search logic
    public void searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = "https://openlibrary.org/search.json?q=" + encodedQuery;

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                bookList.clear();
                                JSONArray docsArray = response.getJSONArray("docs");
                                for (int i = 0; i < docsArray.length(); i++) {
                                    JSONObject bookObject = docsArray.getJSONObject(i);
                                    String title = bookObject.optString("title");

                                    String author = "Unknown Author";
                                    JSONArray authorArray = bookObject.optJSONArray("author_name");
                                    if (authorArray != null && authorArray.length() > 0) {
                                        author = authorArray.getString(0);
                                    }

                                    int coverId = bookObject.has("cover_i") ? bookObject.optInt("cover_i") : 0;
                                    Book book = new Book(title, author, coverId);
                                    bookList.add(book);
                                }
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

    // 6) Menu code: inflate + handle item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // main_menu.xml
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            // e.g., open a Profile page
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
