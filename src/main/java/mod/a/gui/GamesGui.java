package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.data.MysticWellData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GamesGui extends BaseGui{
    public GamesGui() {
        super("Games", 54);

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        ItemStack grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        grayPane.setStackDisplayName("");

        for (int i = 0; i < 9; i++) {
            inv.setInventorySlotContents(i, grayPane);
            inv.setInventorySlotContents(45 + i, grayPane);
        }

        for (int i = 1; i < 5; i++) {
            inv.setInventorySlotContents(i * 9, grayPane);
            inv.setInventorySlotContents(i * 9 + 8, grayPane);
        }

        ItemStack crates = new ItemStack(Blocks.chest);
        crates.setStackDisplayName(ChatFormatting.WHITE + "Crates");

        ItemStack well = new ItemStack(Blocks.enchanting_table);
        well.setStackDisplayName(ChatFormatting.WHITE + "Mystic Well");

        ItemStack mines = new ItemStack(Blocks.tnt);
        mines.setStackDisplayName(ChatFormatting.WHITE + "Mines");

        inv.setInventorySlotContents(10, crates);
        inv.setInventorySlotContents(11, well);
        inv.setInventorySlotContents(12, mines);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (slotId == 10) {
            Main.getInstance().setGuiToOpen(new CratesListGui());
        } else if (slotId == 11) {
            Main.getInstance().setGuiToOpen(new MysticWellBaseGui(new MysticWellData(null, null, 0), true, 0));
        } else if (slotId == 12) {
            Main.getInstance().setGuiToOpen(new MinesConfigGui());
        } else if (slotId == 85) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }
}
