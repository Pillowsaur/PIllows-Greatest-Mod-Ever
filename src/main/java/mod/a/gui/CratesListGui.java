package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.APIHelper;
import mod.a.util.Data;
import mod.a.util.ItemHelper;
import mod.a.util.data.CrateRespData;
import mod.a.util.price.CrateData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CratesListGui extends BaseGui {
    public CratesListGui() {
        super("Choose a Crate", 54);

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

        for (int i = 0; i < Data.crates.size(); i++) {
            inv.setInventorySlotContents(10 + 7 * (i / 7) + i % 7, Data.crates.get(i).getCrateStack());
        }

        ItemStack key = new ItemStack(Items.book);
        key.setStackDisplayName(ChatFormatting.WHITE + "Reward Key");

        ItemHelper.addLore(key, CrateData.getColorFromLevel(4) + "- Legendary");
        ItemHelper.addLore(key, CrateData.getColorFromLevel(3) + "- Epic");
        ItemHelper.addLore(key, CrateData.getColorFromLevel(2) + "- Rare");
        ItemHelper.addLore(key, CrateData.getColorFromLevel(1) + "- Uncommon");
        ItemHelper.addLore(key, CrateData.getColorFromLevel(0) + "- Common");

        inv.setInventorySlotContents(49, key);
    }

    private boolean flag = false;

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (!flag && 9 < slotId && slotId < 44) {
            flag = true;
            int index = 7 * ((slotId - 9) / 9) + slotId % 9 - 1;

            if (index > Data.crates.size()) {
                flag = false;
            } else {
                CrateRespData data = APIHelper.rollCrate(Data.crates.get(index).getCrateId());

                if (data == null) {
                    flag = false;
                } else {
                    Main.getInstance().setGuiToOpen(new CrateGui(data));
                }
            }
        } else if (slotId == 85) {
            Main.getInstance().setGuiToOpen(new GamesGui());
        }
    }
}
