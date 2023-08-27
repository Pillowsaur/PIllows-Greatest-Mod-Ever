package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.data.CrateRespData;
import mod.a.util.data.DONData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class DON2Gui extends BaseGui {
    private final DONData donData;

    public DON2Gui(DONData donData, ItemStack initialStack) {
        super("Double or Nothing!", 27);
        this.donData = donData;

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
        cancel.setStackDisplayName(ChatFormatting.RED + "Lose (55%)");

        ItemStack roll = new ItemStack(Blocks.stained_glass_pane, 1, 5);
        roll.setStackDisplayName(ChatFormatting.GREEN + "Win (45%)");

        inv.setInventorySlotContents(10, cancel);
        inv.setInventorySlotContents(16, roll);

        inv.setInventorySlotContents(13, initialStack);
    }

    private int offset = -1;
    private long lastUpdate = System.currentTimeMillis();

    private boolean rollAnim = true;
    private boolean winLoseAnim = true;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (rollAnim && System.currentTimeMillis() - lastUpdate > 750) {
            lastUpdate = System.currentTimeMillis();

            if (++offset < donData.getMaxHeight()) {
                IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
                ItemStack stack = new ItemStack(Blocks.stained_glass_pane, 1, 3);
                stack.setStackDisplayName(ChatFormatting.BLUE + "Rolling...");

                switch (offset) {
                    case 0:
                        inv.setInventorySlotContents(22, stack);
                        break;
                    case 1:
                        inv.setInventorySlotContents(21, stack);
                        inv.setInventorySlotContents(23, stack);
                        break;
                    case 2:
                        inv.setInventorySlotContents(12, stack);
                        inv.setInventorySlotContents(14, stack);
                        break;
                    case 3:
                        inv.setInventorySlotContents(3, stack);
                        inv.setInventorySlotContents(5, stack);
                        break;
                    case 4:
                        inv.setInventorySlotContents(4, stack);
                        break;
                }

                Minecraft.getMinecraft().thePlayer.playSound("random.orb", 0.75f, (float) (0.6f + offset * 0.1));
            } else {
                offset = 0;
                rollAnim = false;
            }
        }

        if (!rollAnim && winLoseAnim && System.currentTimeMillis() - lastUpdate > 250) {
            lastUpdate = System.currentTimeMillis();

            if (offset++ <= 6) {
                IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
                ItemStack thePane = new ItemStack(Blocks.stained_glass_pane, 1, donData.isWin() ? 5 : 14);
                thePane.setStackDisplayName("");

                for (int i = 0; i < 9; i++) {
                    inv.setInventorySlotContents(3 + (i / 3) * 9 + i % 3, (offset % 2 == 0) ? null : thePane);
                }

                if (offset % 2 == 1) {
                    Minecraft.getMinecraft().thePlayer.playSound("random.orb", 0.75f, 0.5f);
                }

                inv.setInventorySlotContents(13, donData.getWinningStack());
            } else {
                winLoseAnim = false;
                Main.getInstance().setGuiToOpen(new DONGui(new CrateRespData(donData.getWinningStack(), donData.getGameId())));

//                if (donData.isWin()) {
//                    flag = true;
//                    Main.getInstance().setGuiToOpen(new DONGui(new CrateRespData(donData.getWinningStack(), donData.getGameId())));
//                }
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (slotId == 58) {
            Main.getInstance().setGuiToOpen(new CratesListGui());
        }
    }

    @Override
    public void onGuiClosed() {
        Main.getInstance().setGuiToOpen(new DONGui(new CrateRespData(donData.getWinningStack(), donData.getGameId())));
        super.onGuiClosed();
    }

    private void sendWinMessage() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You won ").appendSibling(donData.getWinningStack().getChatComponent()).appendSibling(new ChatComponentText("!")));
    }
}
