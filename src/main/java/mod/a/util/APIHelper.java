package mod.a.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.data.CrateRespData;
import mod.a.util.data.DONData;
import mod.a.util.data.MinesData;
import mod.a.util.data.MysticWellData;
import mod.a.util.price.CrateData;
import mod.a.util.price.WellPrice;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APIHelper {
    public static String apiKey = "";
    private static final String baseUrl = "http://127.0.0.1:5000";

    private static final JsonParser jsonParser = new JsonParser();

    public static boolean setApiKey(String key) {
        apiKey = key;

        Main.getInstance().getConfiguration().setValue("apiKey", apiKey);

        Data.numCredits = getNumCredits();

        return Data.numCredits != -100;
    }

    public static CrateRespData rollCrate(String crateName) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("crate", crateName);

        JsonObject resp = postRequest("/api/game/crate", headers, body);
        if (resp == null) return null;

        return new CrateRespData(resp.get("reward").getAsJsonArray(), Data.cratesMap.get(crateName), resp.get("gameId").getAsString());
    }

    // TODO - add credits gained here?
    public static boolean cancelCrate(String gameId) {
        return cancelGame("/api/game/crate_boring", gameId) != -1;
    }

    public static DONData doubleOrNothing(String gameId) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("gameId", gameId);

        JsonObject resp = postRequest("/api/game/crate_fun", headers, body);
        if (resp == null) return null;

        return new DONData(resp);
    }

    public static MysticWellData freshItem(String itemType) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("item", itemType);

        JsonObject resp = postRequest("/api/game/create_fresh", headers, body);
        if (resp == null) return null;

        return new MysticWellData(resp);
    }

    public static MysticWellData freshItem(String itemType, String color) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("item", itemType);
        body.addProperty("color", color);

        JsonObject resp = postRequest("/api/game/create_fresh", headers, body);
        if (resp == null) return null;

        return new MysticWellData(resp);
    }

    public static MysticWellData enchantItem(String gameId) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("gameId", gameId);

        JsonObject resp = postRequest("/api/game/enchant_item", headers, body);
        if (resp == null) return null;

        return new MysticWellData(resp);
    }

    public static double cancelMysticWell(String gameId) {
        return cancelGame("/api/game/finish_item", gameId);
    }

    public static MinesData newMinesGame(int wager, int numMines) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("wager", wager);
        body.addProperty("mines", numMines);

        JsonObject resp = postRequest("/api/game/new_mines", headers, body);
        if (resp == null) {
            return null;
        } else {
            return new MinesData(resp);
        }
    }

    public static String updateMinesData(MinesData minesData, int row, int col) {
        if (apiKey.equals("")) return null;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("row", row);
        body.addProperty("col", col);
        body.addProperty("gameId", minesData.getGameId());

        JsonObject resp = postRequest("/api/game/reveal_mines", headers, body);
        if (resp == null) return null;

        minesData.setMultiplier(resp.get("multiplier").getAsDouble());
        minesData.setGameId(resp.get("gameId").getAsString());

        return resp.get("spot").getAsString();
    }

    public static double endMinesGame(String gameId) {
        return cancelGame("/api/game/finish_mines", gameId);
    }

    private static double cancelGame(String path, String gameId) {
        if (apiKey.equals("")) return -1;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject body = new JsonObject();
        body.addProperty("gameId", gameId);

        JsonObject resp = postRequest(path, headers, body);
        if (resp == null) return -1;
        return resp.get("credits").getAsDouble();
    }

    // Multiplies by 100
    public static int getNumCredits() {
        if (apiKey.equals("")) return -100;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("api-key", apiKey);

        JsonObject resp = getRequest("/api/client/num_credits", headers);
        if (resp != null) {
            return (int) (resp.get("numCredits").getAsDouble() * 100);
        } else {
            return -100;
        }
    }

    public static WellPrice getWellPrices() {
        JsonObject wellData = getRequest("/static/mysticwell/prices.json");
        if (wellData == null) return null;
        return new WellPrice(wellData.get("SWORD").getAsDouble(), wellData.get("BOW").getAsDouble(), wellData.get("PANTS").getAsDouble(), wellData.get("T1").getAsDouble(), wellData.get("T2").getAsDouble(), wellData.get("T3").getAsDouble());
    }

//    public static HashMap<String, CrateData> getCratePrices() {
////        HashMap<String, CrateData> prices = new HashMap<>();
////
////        JsonObject data = getRequest("/static/crates/data.json");
////        if (data == null) return prices;
////
////        for (Map.Entry<String, JsonElement> i : data.entrySet()) {
////            prices.put(i.getKey(), new CrateData(i.getKey(), i.getValue().getAsJsonObject()));
////        }
////
////        return prices;
////    }

    public static ArrayList<CrateData> getCratePrices() {
        ArrayList<CrateData> prices = new ArrayList<>();
//        HashMap<String, CrateData> pricesMap = new HashMap<>();

        JsonObject data = getRequest("/static/crates/data.json");
        if (data == null) return prices;

        for (Map.Entry<String, JsonElement> i : data.entrySet()) {
            CrateData data1 = new CrateData(i.getKey(), i.getValue().getAsJsonObject());

//            pricesMap.put(i.getKey(), data1);
            prices.add(data1);
        }

        return prices;
    }

    private static JsonObject postRequest(String path) {
        return postRequest(path, new HashMap<>(), new JsonObject());
    }

    private static JsonObject postRequest(String path, JsonObject body) {
        return postRequest(path, new HashMap<>(), body);
    }

    private static JsonObject postRequest(String path, HashMap<String, String> headers, JsonObject body) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(baseUrl + path).openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }

            con.setDoOutput(true);

            con.getOutputStream().write(body.toString().getBytes(StandardCharsets.UTF_8));

            if (con.getResponseCode() == 200) {
                return (JsonObject) jsonParser.parse(IOUtils.toString(con.getInputStream()));
            } else {
                JsonObject obj = (JsonObject) jsonParser.parse(IOUtils.toString(con.getErrorStream()));
                if (obj.has("error")) {

                    if (Minecraft.getMinecraft().thePlayer != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Error: " + obj.get("error").getAsString()));
                    } else {
                        System.out.println("Error: " + obj.get("error").getAsString());
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();

            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Error: Generic Error"));
            }
        }

        return null;
    }

    private static JsonObject getRequest(String path) {
        return getRequest(path, new HashMap<>());
    }

    private static JsonObject getRequest(String path, HashMap<String, String> headers) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(baseUrl + path).openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "*/*");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (con.getResponseCode() == 200) {
                return (JsonObject) jsonParser.parse(IOUtils.toString(con.getInputStream()));
            } else {
                JsonObject obj = (JsonObject) jsonParser.parse(IOUtils.toString(con.getErrorStream()));
                if (obj.has("error")) {

                    if (Minecraft.getMinecraft().thePlayer != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Error: " + obj.get("error").getAsString()));
                    } else {
                        System.out.println("Error: " + obj.get("error").getAsString());
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();

            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Error: Generic Error"));
            }
        }

        return null;
    }
}
