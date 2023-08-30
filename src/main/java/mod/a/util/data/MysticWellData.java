package mod.a.util.data;

import com.google.gson.JsonObject;
import mod.a.util.Data;
import mod.a.util.ItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MysticWellData {
    private final String gameId;
    private final ItemStack item;
    private final int itemTier;

    public MysticWellData(JsonObject resp) {
        item = ItemHelper.parseMysticItem(resp.getAsJsonObject("item"));
        gameId = resp.get("gameId").getAsString();
        itemTier = resp.getAsJsonObject("item").get("tier").getAsInt();
    }

    public MysticWellData(ItemStack item, String gameId, int itemTier) {
        this.item = item;
        this.gameId = gameId;
        this.itemTier = itemTier;
    }

    public String getGameId() {
        return gameId;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getNextCost() {
        switch (itemTier) {
            case 0:
                return Data.wellPrice.getT1Price();
            case 1:
                return Data.wellPrice.getT2Price();
            case 2:
                return Data.wellPrice.getT3Price();
            default:
                return 0;
        }
    }

    public int getItemTier() {
        return itemTier;
    }
}
