package com.example.mylibraryapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RecommendationActivity extends AppCompatActivity {

    private TextView tvRecommendation;
    private Button btnRefresh, btnBackRec;
    // Get your API key from BuildConfig (set via local.properties and Gradle)
    private final String apiKey = BuildConfig.OPENAI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        tvRecommendation = findViewById(R.id.tvRecommendation);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBackRec = findViewById(R.id.btnBackRec);

        btnRefresh.setOnClickListener(v -> getRecommendationFromRatings());
        btnBackRec.setOnClickListener(v -> finish());

        // Optionally, fetch a recommendation when the activity starts
        getRecommendationFromRatings();
    }

    private void getRecommendationFromRatings() {
        // Fetch ratings summary from Firebase
        RatingUtils.getUserRatingsSummary().addOnSuccessListener(ratingsSummary -> {
            // Use the fetched summary to call OpenAI API
            getRecommendation(ratingsSummary);
        }).addOnFailureListener(e ->
                Toast.makeText(RecommendationActivity.this, "Failed to fetch ratings: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void getRecommendation(String ratingsSummary) {
        // Build JSON payload using the ratings summary.
        Log.d("Payload", ratingsSummary);
        ratingsSummary = ratingsSummary.replace("\"", "'").trim();

        String prompt = "User Ratings:\n"
                + ratingsSummary
                + "\nBased on these ratings, please recommend one book from OpenLibrary and return the search URL for that book in the following format: https://openlibrary.org/search.json?title=the+lord+of+the+rings";

        String jsonPayload = "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + prompt.replace("\n", "\\n").replace("\"", "\\\"") + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        MediaType JSONType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonPayload, JSONType);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RecommendationActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(RecommendationActivity.this, "Unexpected response: " + response, Toast.LENGTH_LONG).show());
                    return;
                }
                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        final String recommendation = message.getString("content").trim();
                        runOnUiThread(() -> tvRecommendation.setText(recommendation));
                    }
                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(RecommendationActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
