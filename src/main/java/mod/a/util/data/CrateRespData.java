package mod.a.util.data;

import com.google.gson.JsonArray;
import mod.a.util.ItemHelper;
import mod.a.util.price.CrateData;
import net.minecraft.item.ItemStack;

public class CrateRespData {
    private final ItemStack winningStack;
//    private final String crateName;
//    private final RandomCollection<ItemStack> randomCollection = new RandomCollection<>();
    private final String gameId;
    private final CrateData crateData;

    public CrateRespData(JsonArray winnings, CrateData data, String gameId) {
        winningStack = ItemHelper.parseItem(winnings);
        this.gameId = gameId;
        this.crateData = data;

//        JsonArray arr = (JsonArray) crateData.get("rolls");
//        for (JsonElement elem : arr) {
//            JsonObject obj = (JsonObject) elem;
//
//            randomCollection.add(ItemHelper.parseItem(obj.get("reward").getAsJsonArray()), obj.get("chance").getAsDouble());
//        }
//
//        crateName = crateData.get("name").getAsString();
    }

    public CrateRespData(ItemStack winningStack, String gameId) {
        this.winningStack = winningStack;
        this.gameId = gameId;

        this.crateData = null;
    }

    public ItemStack nextItem() {
        return crateData.nextItem();
    }

    public ItemStack getWinningStack() {
        return winningStack;
    }

    public String getCrateName() {
        return crateData.getCrateName();
    }

    public String getGameId() {
        return gameId;
    }
}
