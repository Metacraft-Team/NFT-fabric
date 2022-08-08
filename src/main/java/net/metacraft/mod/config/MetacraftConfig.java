package net.metacraft.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MetacraftConfig {

    private File config;

    private String apiUrl = "http://127.0.0.1:8080";

    private static final Gson GSON =  new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public String getApiUrl() {
        return apiUrl;
    }

    public MetacraftConfig() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve("metacraft").toFile();
        try {
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            this.config = new File(configDir, "metacraft_config.json");
            if (!config.exists()) {
                config.createNewFile();
                save();
            }
        } catch (IOException e) {
            System.out.println("MetacraftConfig IOException " + e.getMessage());
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("api_url", this.apiUrl);

        try {
            FileWriter writer = new FileWriter(this.config);
            GSON.toJson(jsonObject, writer);
            writer.close();
            System.out.println("metacraft config save success");
        } catch (Exception e) {
            System.out.println("config save failed, " + e.getMessage());
        }
    }

    public void load() {
        try {
            FileReader reader = new FileReader(config);
            JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
            reader.close();
            apiUrl = ConfigHandler.fromJson(jsonObject, "api_url", apiUrl);
        } catch (Exception e) {
            System.out.println("MetacraftConfig load exception : " + e);
        }
    }
}
