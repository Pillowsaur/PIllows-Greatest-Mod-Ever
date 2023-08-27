package mod.a.util.data;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class MinesData {
    private String gameId;

//    private ArrayList<ArrayList<String>> board;
    private String multiplier;

    public MinesData(JsonObject object) {
        gameId = object.get("gameId").getAsString();
        setMultiplier(object.get("multiplier").getAsDouble());
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setMultiplier(double m) {
        // 1 -> 001 -> 0.01
        // 11 -> 0011 -> 00.11
        // 111 -> 00111 -> 001.11

        String temp = "00" + (int) (m * 100);
        String temp1 = temp.substring(0, temp.length() - 2);

        if (temp1.length() == 2) {
            temp1 = "0";
        } else if (temp1.length() > 2) {
            temp1 = temp1.substring(2);
        }

        multiplier = temp1 + "." + temp.substring(temp.length() - 2);
    }

    public String getMultiplier() {
        return multiplier;
    }

//    public void
}
