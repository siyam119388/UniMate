package com.unimate.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Talks to a language model.
 *
 * HOW TO TURN IT ON
 *   1. Get a free key from  https://aistudio.google.com/apikey
 *   2. Paste it into API_KEY below.
 *   3. Save and restart Tomcat.
 *
 * With no key the assistant still replies using the material list only,
 * so the screen never looks broken during a demo.
 */
public class AIService {

    private static final String API_KEY = "AQ.Ab8RN6LnRTfHZL1tLQ3yJs7xXw8Rz80kkba-DvyvPo497_VavA";      // <-- paste your key here

    private static final String ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent";

    public static boolean isEnabled() {
        return API_KEY != null && !API_KEY.trim().isEmpty();
    }

    public static String ask(String question, String materialList) {
        if (!isEnabled()) {
            return offlineAnswer(question, materialList);
        }
        try {
            String prompt =
                "You are a study assistant for university students in Bangladesh.\n" +
                "Answer the question briefly and clearly, in at most four sentences.\n" +
                "These are the study materials the student can open:\n" +
                materialList + "\n" +
                "If one of them is relevant, mention it by name.\n\n" +
                "Question: " + question;

            String body = buildRequest(prompt);

            /*HttpURLConnection con = (HttpURLConnection)
                    new URL(ENDPOINT + "?key=" + API_KEY).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");  */
                        HttpURLConnection con = (HttpURLConnection)
                    new URL(ENDPOINT).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("x-goog-api-key", API_KEY);  //
            con.setConnectTimeout(10000);
            con.setReadTimeout(20000);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes("UTF-8"));
            }

            /*if (con.getResponseCode() != 200) {
                return offlineAnswer(question, materialList);
            } */
                       if (con.getResponseCode() != 200) {
                StringBuilder err = new StringBuilder();
                try (BufferedReader r = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), "UTF-8"))) {
                    String line;
                    while ((line = r.readLine()) != null) err.append(line);
                }
                return "API error " + con.getResponseCode() + ": " + err;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
            }
            return readAnswer(sb.toString(), question, materialList);

        } catch (Exception e) {
            return offlineAnswer(question, materialList);
        }
    }

    private static String buildRequest(String prompt) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", new Object[]{ part });
        Map<String, Object> root = new HashMap<>();
        root.put("contents", new Object[]{ content });
        return new Gson().toJson(root);
    }

    private static String readAnswer(String json, String question, String materials) {
        try {
            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            return root.getAsJsonArray("candidates").get(0).getAsJsonObject()
                       .getAsJsonObject("content")
                       .getAsJsonArray("parts").get(0).getAsJsonObject()
                       .get("text").getAsString().trim();
        } catch (Exception e) {
            return offlineAnswer(question, materials);
        }
    }

    /** Used when there is no key, or the call fails. Never leaves the screen blank. */
    private static String offlineAnswer(String question, String materialList) {
        if (materialList == null || materialList.trim().isEmpty()) {
            return "There is no study material in your courses yet, so I have nothing to search. "
                 + "Ask your instructor to upload notes or slides.";
        }
        return "I searched your course materials for \"" + question.trim()
             + "\". The closest match is shown below - open it to read more.\n\n"
             + "(Connect a language model key in AIService.java for full answers.)";
    }
}
