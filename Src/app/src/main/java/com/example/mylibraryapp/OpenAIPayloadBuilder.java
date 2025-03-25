package com.example.mylibraryapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenAIPayloadBuilder {

    /**
     * Builds a JSON payload for the OpenAI Chat Completions API.
     *
     * @param ratingsSummary The concatenated ratings summary.
     * @return A JSON string payload.
     * @throws JSONException if any JSON error occurs.
     */
    public static String buildPayload(String ratingsSummary) throws JSONException {
        // Create a prompt that includes the ratings summary.
        String prompt = "Based on the following user ratings:\n" +
                ratingsSummary +
                "\nPlease recommend one book from OpenLibrary that this user might enjoy and return only the search URL, nothing more, for that book in this format: https://openlibrary.org/search.json?title=the+lord+of+the+rings";

        // Create the message object.
        JSONObject messageObj = new JSONObject();
        messageObj.put("role", "user");
        messageObj.put("content", prompt);

        // Create the messages array.
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(messageObj);

        // Build the final payload.
        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o");  // Adjust if needed.
        payload.put("messages", messagesArray);

        return payload.toString();
    }
}
