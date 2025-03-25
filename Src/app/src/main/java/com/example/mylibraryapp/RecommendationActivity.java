package com.example.mylibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendationActivity extends AppCompatActivity {

    private LinearLayout recommendationContainer;
    private Button btnRefreshRec, btnBackRec;
    // For demonstration, we'll use a hardcoded ratings summary.
    private final String ratingsSummary = "Catch-22 by Joseph Heller: 1 stars; Suggestion: mid\n" +
            "Ethical Hacking with Android by Unknown Author: 5 stars; Suggestion: banger!\n" +
            "Hacking by Yacoub AL-smadi: 4 stars; Suggestion: hello bro\n" +
            "Skeleton crew by Stephen King: 3 stars; Suggestion: hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        recommendationContainer = findViewById(R.id.recommendation_container);
        btnRefreshRec = findViewById(R.id.btnRefreshRec);
        btnBackRec = findViewById(R.id.btnBackRec);

        btnRefreshRec.setOnClickListener(v -> fetchRecommendation());
        btnBackRec.setOnClickListener(v -> finish());

        // Fetch recommendation when activity starts.
        fetchRecommendation();
    }

    /**
     * Calls the OpenAIQuery to obtain the recommended OpenLibrary URL,
     * then fetches the book details from that URL.
     */
    private void fetchRecommendation() {
        // Call your existing OpenAIQuery function.
        OpenAIQuery.queryRecommendation(ratingsSummary, new OpenAIResponseCallback() {
            @Override
            public void onSuccess(String recommendationUrl) {
                // Now fetch book details using the recommended URL.
                fetchBookDetails(recommendationUrl);
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(RecommendationActivity.this, "Failed to get recommendation: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    /**
     * Fetches book details from OpenLibrary using the recommended URL and displays them in a list item.
     *
     * @param openLibraryUrl The URL returned from the AI API.
     */
    private void fetchBookDetails(String openLibraryUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(openLibraryUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RecommendationActivity.this, "Failed to load book details: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(RecommendationActivity.this, "Unexpected response: " + response, Toast.LENGTH_LONG).show());
                    return;
                }
                String jsonResponse = response.body().string();
                try {
                    JSONObject json = new JSONObject(jsonResponse);
                    JSONArray docs = json.getJSONArray("docs");
                    if (docs.length() > 0) {
                        JSONObject firstDoc = docs.getJSONObject(0);
                        String title = firstDoc.optString("title", "No Title");
                        JSONArray authorNames = firstDoc.optJSONArray("author_name");
                        String author = (authorNames != null && authorNames.length() > 0) ? authorNames.getString(0) : "Unknown Author";
                        int coverId = firstDoc.optInt("cover_i", 0);
                        String coverUrl = (coverId != 0) ? "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg" : null;

                        runOnUiThread(() -> displayBookItem(title, author, coverUrl));
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(RecommendationActivity.this, "No book found", Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(RecommendationActivity.this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    /**
     * Inflates the list_item.xml layout, populates it with book details, and adds it to the container.
     */
    private void displayBookItem(String title, String author, String coverUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View bookItem = inflater.inflate(R.layout.list_item, recommendationContainer, false);

        ImageView imageViewCover = bookItem.findViewById(R.id.imageViewCover);
        TextView textViewTitle = bookItem.findViewById(R.id.textViewTitle);
        TextView textViewAuthor = bookItem.findViewById(R.id.textViewAuthor);
        RatingBar ratingBar = bookItem.findViewById(R.id.ratingBar);

        TextView recommendReason = findViewById(R.id.recommendReason);



        textViewTitle.setText(title);
        textViewAuthor.setText(author);
        // OpenLibrary doesn't provide a rating, so we hide the rating bar.
        ratingBar.setVisibility(View.GONE);

        if (coverUrl != null) {
            Picasso.get().load(coverUrl).into(imageViewCover);
        } else {
            imageViewCover.setImageResource(R.drawable.placeholder_cover); // Ensure default_cover exists.
        }

        queryBookReasoning(title, new OpenAIResponseCallback() {
            @Override
            public void onSuccess(String reasoning) {
                // Update the UI with the reasoning; for instance:
                runOnUiThread(() -> {
                    // Show in a TextView or list item.
                    recommendReason.setText(reasoning);
                    //Toast.makeText(getApplicationContext(), "Reasoning: " + reasoning, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        // Clear any previous views and add this item.
        recommendationContainer.removeAllViews();
        recommendationContainer.addView(bookItem);
    }

    public static void queryBookReasoning(String bookName, OpenAIResponseCallback callback) {
        try {
            // Build the prompt: explain why you would recommend the given book.
            String prompt = "Please explain why you would recommend the book \"" + bookName + "\". " +
                    "Include a brief reasoning statement, about 20-50 words, highlighting its strengths and why a reader might enjoy it.";

            // Create the message JSON object
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", prompt);

            // Create a JSON array for messages and add our message
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(messageObj);

            // Build the complete payload JSON object.
            JSONObject payload = new JSONObject();
            // Use the desired model – ensure "gpt-4o" is valid for your account or use "gpt-4" / "gpt-3.5-turbo" as needed.
            payload.put("model", "gpt-4o");
            payload.put("messages", messagesArray);

            String jsonPayload = payload.toString();

            MediaType JSONType = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonPayload, JSONType);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Unexpected response: " + response));
                        return;
                    }
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        if (choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject message = firstChoice.getJSONObject("message");
                            String reasoning = message.getString("content").trim();
                            callback.onSuccess(reasoning);
                        } else {
                            callback.onFailure(new Exception("No choices returned"));
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });

        } catch (JSONException e) {
            callback.onFailure(e);
        }
    }

}

