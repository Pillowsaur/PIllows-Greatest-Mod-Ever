package mod.a.util.data;

import com.google.gson.JsonObject;
import mod.a.util.ItemHelper;
import net.minecraft.item.ItemStack;

public class MysticWellData {
    private final String gameId;
    private final ItemStack item;
    private final double nextCost;
    private final int itemTier;

    public MysticWellData(JsonObject resp) {
        item = ItemHelper.parseMysticItem(resp.getAsJsonObject("item"));
        gameId = resp.get("gameId").getAsString();
        nextCost = resp.get("nextCost").getAsDouble();
        itemTier = resp.getAsJsonObject("item").get("tier").getAsInt();
    }

    public MysticWellData(ItemStack item, String gameId, double nextCost, int itemTier) {
        this.item = item;
        this.gameId = gameId;
        this.nextCost = nextCost;
        this.itemTier = itemTier;
    }

    public String getGameId() {
        return gameId;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getNextCost() {
        return nextCost;
    }

    public int getItemTier() {
        return itemTier;
    }
}
