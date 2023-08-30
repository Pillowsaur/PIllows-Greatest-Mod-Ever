package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.APIHelper;
import mod.a.util.Data;
import mod.a.util.data.CrateRespData;
import mod.a.util.data.DONData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class DONGui extends BaseGui {
    private final CrateRespData crateData;
    private boolean requestInProgress = false;

    private boolean flag = false;

    public DONGui(CrateRespData crateData) {
        super("Double or Nothing!", 27);
        this.crateData = crateData;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        ItemStack grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        grayPane.setStackDisplayName("");

        for (int i = 0; i < 27; i++) {
            inv.setInventorySlotContents(i, grayPane);
        }

        ItemStack redPane = new ItemStack(Blocks.stained_glass_pane, 1, 14);
        redPane.setStackDisplayName("");

        for (int i = 0; i < 9; i++) {
            inv.setInventorySlotContents(3 + (i / 3) * 9 + i % 3, redPane);
        }

        ItemStack cancel = new ItemStack(Blocks.stained_glass_pane, 1, 14);
        cancel.setStackDisplayName(ChatFormatting.RED + "Cash Out");

        ItemStack roll = new ItemStack(Blocks.stained_glass_pane, 1, 5);
        roll.setStackDisplayName(ChatFormatting.GREEN + "Double or Nothing (45%)");

        inv.setInventorySlotContents(10, cancel);
        inv.setInventorySlotContents(16, roll);

        inv.setInventorySlotContents(13, crateData.getWinningStack());
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (requestInProgress) return;

        if (slotId == 10) {
            requestInProgress = true;

            if (APIHelper.cancelCrate(crateData.getGameId())) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            } else {
                System.out.println("Failed to cancel crate, please try again");
                requestInProgress = false;
            }
        } else if (slotId == 16) {
            requestInProgress = true;

            DONData data = APIHelper.doubleOrNothing(crateData.getGameId());

            if (data != null) {
                flag = true;
                Main.getInstance().setGuiToOpen(new DON2Gui(data, crateData.getWinningStack()));
//                Data.numCredits = APIHelper.getNumCredits();
            } else {
                requestInProgress = false;
            }
        } else if (slotId == 58) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @Override
    public void onGuiClosed() {
        if (!flag) {
            sendWinMessage();
            Data.numCredits = APIHelper.getNumCredits();
        }
        super.onGuiClosed();
        Main.getInstance().setGuiToOpen(new CratesListGui());
    }

    private void sendWinMessage() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You won ").appendSibling(crateData.getWinningStack().getChatComponent()).appendSibling(new ChatComponentText("!")));
    }
}
