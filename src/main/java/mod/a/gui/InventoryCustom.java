package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.util.Data;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventoryCustom extends InventoryBasic {
    public InventoryCustom() {
        super("Credits: " + String.valueOf(Data.numCredits).substring(0, String.valueOf(Data.numCredits).length() - 2) + "." + String.valueOf(Data.numCredits).substring(String.valueOf(Data.numCredits).length() - 2), false, 36);

        ItemStack realGrayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        realGrayPane.setStackDisplayName("");

        for (int i = 0; i < 9; i++) {
            setInventorySlotContents(27 + i, realGrayPane);
        }

        ItemStack backButton = new ItemStack(Blocks.barrier);
        backButton.setStackDisplayName(ChatFormatting.RED + "Back");

        setInventorySlotContents(31, backButton);
    }

    public void resetTitle() {
        setCustomName("Credits: " + String.valueOf(Data.numCredits).substring(0, String.valueOf(Data.numCredits).length() - 2) + "." + String.valueOf(Data.numCredits).substring(String.valueOf(Data.numCredits).length() - 2));
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        index += 9;
        if (index >= getSizeInventory()) {
            index -= getSizeInventory();
        }

        super.setInventorySlotContents(index, stack);
    }
}
