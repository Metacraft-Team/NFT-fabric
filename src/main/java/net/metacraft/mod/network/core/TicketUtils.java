package net.metacraft.mod.network.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.metacraft.mod.config.ConfigHandler;
import net.metacraft.mod.renderer.MapRenderer;
import net.metacraft.mod.utils.Constants;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.metacraft.mod.utils.ThreadPoolUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TicketUtils {
    private static Map<String, byte[]> tickImages = new HashMap<>();

    private static Map<String, Boolean> refreshImages = new HashMap<>();

    public static byte[] getActivityImage(String activityId) {
        return tickImages.get(activityId);
    }

    public static boolean isNeedRefreshImage(String activityId) {
        if (refreshImages.get(activityId) == null) {
            return false;
        }
        return refreshImages.get(activityId);
    }

    public static void refreshed(String activityId) {
        refreshImages.put(activityId, false);
    }

    public static void refreshActivityImage(String activityId) {
        ThreadPoolUtils.INSTANCE.execute(() -> {
            System.out.println("refreshActivityImage start");
            StringBuilder builder = new StringBuilder(ConfigHandler.getConfig().getApiUrl())
                    .append(HttpsUtils.API_TICKET_RENDER)
                    .append("?tid=")
                    .append(activityId);
            BufferedImage bufferedImage = MetaCraftUtils.
                    getBufferedImageForUrl(builder.toString());
            if (bufferedImage == null) {
                System.out.println("refreshActivityImage error");
                return;
            }
            byte[] colors = MapRenderer.render(bufferedImage, Constants.FLAT_PIXEL, Constants.FLAT_PIXEL);
            tickImages.put(activityId, colors);
            refreshImages.put(activityId, true);
        });
    }

    public static String ticketCheck(String playerId, String activityId, String nftTokenId, String nftTokenAddress) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", playerId);
        data.put("tid", activityId);
        data.put("token_id", nftTokenId);
        data.put("token_address", nftTokenAddress);
        String param = new Gson().toJson(data);
        System.out.println(param);
        StringBuilder builder = new StringBuilder();
        builder.append(ConfigHandler.getConfig().getApiUrl())
                .append(HttpsUtils.API_TICKET_CHECK);
        String result = HttpsUtils.post(builder.toString(), param);
        System.out.println("ticketCheck result : " + result);
        try {
            JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
            int error = jsonObject.get("error").getAsInt();
            return error == 0 ? "success" : jsonObject.get("errorMessage").getAsString();
        } catch (Exception e) {
            System.out.println("ticketCheck exception " + e.getMessage());
        }
        return "error";
    }
}
