package net.metacraft.mod.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ConfigHandler {
    private static MetacraftConfig config;

    public static MetacraftConfig getConfig() {
        return config;
    }

    public static void loadConfig() {
        config = new MetacraftConfig();
        config.load();
    }

    public static int fromJson(JsonObject obj, String key, int fallback) {
        try {
            return obj.get(key).getAsInt();
        } catch (NullPointerException | UnsupportedOperationException e) {
            return fallback;
        }
    }

    public static boolean fromJson(JsonObject obj, String key, boolean fallback) {
        try {
            return obj.get(key).getAsBoolean();
        } catch (NullPointerException | UnsupportedOperationException e) {
            return fallback;
        }
    }

    public static String fromJson(JsonObject obj, String key, String fallback) {
        try {
            return obj.get(key).getAsString();
        } catch (NullPointerException | UnsupportedOperationException e) {
            return fallback;
        }
    }

    public static JsonObject fromJson(JsonObject obj, String key) {
        try {
            return obj.get(key).getAsJsonObject();
        } catch (NullPointerException | UnsupportedOperationException e) {
            return new JsonObject();
        }
    }

    public static JsonArray arrayFromJson(JsonObject obj, String key) {
        try {
            return obj.get(key).getAsJsonArray();
        } catch (NullPointerException | UnsupportedOperationException e) {
            return new JsonArray();
        }
    }
}
