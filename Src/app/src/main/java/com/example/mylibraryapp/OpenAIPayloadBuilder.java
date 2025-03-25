package com.example.mylibraryapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenAIPayloadBuilder {

    /**
     * Builds a JSON payload for the OpenAI Chat Completions API using the provided ratings summary.
     *
     * @param ratingsSummary A string containing all the user ratings.
     * @return A JSON string formatted for the OpenAI API.
     * @throws JSONException if any JSON error occurs.
     */
    public static String buildPayload(String ratingsSummary) throws JSONException {
        // Construct the prompt using the ratings summary.
        String prompt = "Based on the following user ratings:\n"
                + ratingsSummary
                + "\nPlease recommend one book from OpenLibrary that this user might enjoy. Provide the open library query URL for that book.";

        // Create the message object.
        JSONObject messageObj = new JSONObject();
        messageObj.put("role", "user");
        messageObj.put("content", prompt);

        // Build the messages array.
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(messageObj);

        // Construct the final payload with the model and messages.
        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o"); // Use the appropriate model; adjust if needed.
        payload.put("messages", messagesArray);

        return payload.toString();
    }
}
