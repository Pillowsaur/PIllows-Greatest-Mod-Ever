package mod.a.util.data;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.util.ItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class DONData {
    private final ItemStack winningStack;
    private final String gameId;
    private final boolean win;
    private final int maxHeight;

    public DONData(JsonObject data) {
        ItemStack stack = ItemHelper.parseItem(data.getAsJsonArray("reward"));

        win = stack.stackSize != 0;
        gameId = data.get("gameId").getAsString();

        if (win) {
            maxHeight = 5;
            winningStack = stack;
        } else {
            maxHeight = (int) (Math.random() * 5) + 1;
            winningStack = new ItemStack(Items.dye, 1, 3);
            winningStack.setStackDisplayName(ChatFormatting.GRAY + "Trash");
        }
    }

    public ItemStack getWinningStack() {
        return winningStack;
    }

    public boolean isWin() {
        return win;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public String getGameId() {
        return gameId;
    }
}
