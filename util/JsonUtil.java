package com.unimate.util;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static final Gson GSON = new Gson();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static String message(String text) {
        Map<String, String> m = new HashMap<>();
        m.put("message", text);
        return GSON.toJson(m);
    }

    public static String error(String text) {
        Map<String, String> m = new HashMap<>();
        m.put("error", text);
        return GSON.toJson(m);
    }
}
