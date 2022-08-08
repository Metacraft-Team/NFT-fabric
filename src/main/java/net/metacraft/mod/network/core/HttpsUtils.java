package net.metacraft.mod.network.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.config.ConfigHandler;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.network.data.NftJsonObject;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.metacraft.mod.utils.ThreadPoolUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpsUtils {

    private static final String API_NFT_LIST = "/nft/list";

    private static final String API_NFT_CHECK = "/nft/check";

    public static final String API_TICKET_CHECK = "/ticket/check";

    public static final String API_TICKET_RENDER = "/ticket/render";

    private interface NetworkCallback {
        void onSuccess(String requestId, String result);
    }

    private static void get(String urlStr, String requestId, NetworkCallback callback) {
        System.out.println("url : " + urlStr);
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            if (callback != null) {
                ThreadPoolUtils.INSTANCE.singleExecute(() -> callback.onSuccess(requestId, result.toString()));
            }
        } catch (IOException e) {
            System.out.println("IOException url : " + urlStr);
            System.out.println("get IOException " + e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                System.out.println("get IOException");
            }
        }
    }

    public static String post(String urlStr, String dataStr) {
        String result = "";

        try {
            byte[] data = dataStr.getBytes("UTF-8");

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.connect();

            OutputStream out = connection.getOutputStream();
            out.write(data);
            out.flush();
            out.close();

            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                System.out.println("post request success");
                InputStream in = connection.getInputStream();
                byte[] data1 = readInputStream(in);
                result = new String(data1);
            } else {
                System.out.println("post request fail");
            }

        } catch (IOException e) {
            System.out.println("sendPost IOException " + e);
        }

        return result;
    }

    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[10240];

        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();

        return outStream.toByteArray();
    }

    public static synchronized void queryNftImages(int page, String search, NftCallback callback) {
        System.out.println("start queryNftImages");
        String requestId = UUID.randomUUID().toString();
        DataHandler.INSTANCE.setRequestId(requestId);
        ThreadPoolUtils.INSTANCE.execute(() -> {
            String playerId = DataHandler.INSTANCE.getPlayerId();
            StringBuilder builder = new StringBuilder();
            builder.append(ConfigHandler.getConfig().getApiUrl())
                    .append(API_NFT_LIST)
                    .append("?uuid=")
                    .append(playerId)
                    .append(appendPageStr(page))
                    .append(appendCollectionStr())
                    .append(appendSearchStr(search));
            get(builder.toString(), requestId, (callId, result) -> {
                if (!callId.equals(DataHandler.INSTANCE.getRequestId())) {
                    System.out.println("requestId is invalid");
                    return;
                }
                NftJsonObject nftJsonObject = convertNftEntities(result);
                if (nftJsonObject == null) {
                    return;
                }
                List<NftEntity> nftEntityList = nftJsonObject.getNftEntityList();
                DataHandler.INSTANCE.updateNftInfo(nftJsonObject);
                if (nftEntityList == null || nftEntityList.isEmpty()) {
                    return;
                }
                nftEntityList.forEach(nftEntity -> nftEntity.setCurrentRequestId(callId));
                if (callback != null && page == DataHandler.INSTANCE.getCurrentPage()) {
                    callback.nftUrlsLoaded();
                }
            });
        });
    }

    private static String appendPageStr(int page) {
        String pageStr = "";
        if (page != 0) {
            pageStr = "&pn=" + page;
        }
        return pageStr;
    }

    private static String appendCollectionStr() {
        String condition = DataHandler.INSTANCE.getCurrentCollection();
        if (!MetaCraftUtils.isEmpty(condition)) {
            condition = "&collection=" + encode(condition);
        } else {
            condition = "";
        }
        return condition;
    }

    private static String appendSearchStr(String search) {
        if (MetaCraftUtils.isEmpty(search)) {
            return "";
        }
        return "&search=" + search;
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("encode UnsupportedEncodingException " + e);
        }
        return null;
    }

    public static boolean checkNftOwner(String imageInfoJson, String uuid) {
        try {
            System.out.println("server uuid : " + uuid);
            NftEntity nftEntity = new Gson().fromJson(imageInfoJson, NftEntity.class);
            StringBuilder builder = new StringBuilder();
            builder.append(ConfigHandler.getConfig().getApiUrl())
                    .append(API_NFT_CHECK)
                    .append("?uuid=")
                    .append(uuid)
                    .append("&contract_address=")
                    .append(nftEntity.getContractAddress())
                    .append("&token_id=")
                    .append(nftEntity.getTokenId());

            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean result = new AtomicBoolean(false);
            get(builder.toString(), UUID.randomUUID().toString(), (callId, jsonStr) -> {
                result.set(jsonStr.contains("true"));
                latch.countDown();
            });
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("queryLoveFood InterruptedException");
            }
            System.out.println("checkNftOwner : " + result.get());
            return result.get();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    private static NftJsonObject convertNftEntities(String gsonStr) {
        NftJsonObject nftEntities = null;
        try {
            nftEntities = new Gson().fromJson(gsonStr, NftJsonObject.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
        return nftEntities;
    }

    public interface NftCallback {
        void nftUrlsLoaded();
    }
}
