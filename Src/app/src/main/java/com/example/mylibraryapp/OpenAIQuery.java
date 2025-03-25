package com.example.mylibraryapp;

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

public class OpenAIQuery {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    // You can adjust the model name here if needed.
    private static final String MODEL = "gpt-4o";
    // Get your API key from BuildConfig (set up via local.properties & Gradle)
    private static final String API_KEY = BuildConfig.OPENAI_API_KEY;

    /**
     * Queries the OpenAI API with the provided ratings summary.
     *
     * @param ratingsSummary The concatenated string of all ratings.
     * @param callback       The callback to receive success or failure.
     */
    public static void queryRecommendation(String ratingsSummary, final OpenAIResponseCallback callback) {
        try {
            String jsonPayload = OpenAIPayloadBuilder.buildPayload(ratingsSummary);
            MediaType JSONType = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonPayload, JSONType);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_KEY)
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
                            String recommendation = message.getString("content").trim();
                            callback.onSuccess(recommendation);
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
