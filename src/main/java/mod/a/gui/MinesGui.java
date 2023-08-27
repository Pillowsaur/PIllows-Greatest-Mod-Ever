package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.APIHelper;
import mod.a.util.Data;
import mod.a.util.data.MinesData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class MinesGui extends BaseGui {
    private final int wager;
    private final int numNumes;

    private final MinesData minesData;
    private final ItemStack rewardStack;
    private final ItemStack emptyStack;

    public MinesGui(MinesData minesData, int wager, int numMines) {
        super("Mines!", 54);

        this.wager = wager;
        this.numNumes = numMines;

        this.minesData = minesData;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        ItemStack grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        grayPane.setStackDisplayName("");

        for (int i = 0; i < 54; i++) {
            inv.setInventorySlotContents(i, grayPane);
        }

        emptyStack = new ItemStack(Items.dye, 1, 8);
        emptyStack.setStackDisplayName(ChatFormatting.GREEN + "Uncover Me");

        for (int i = 0; i < 25; i++) {
            inv.setInventorySlotContents((i / 5) * 9 + i % 5 + 2, emptyStack);
        }

//        rewardStack = new ItemStack(Items.dye, 1, 10);
        rewardStack = new ItemStack(Items.emerald);

        ItemStack cashOut = new ItemStack(Blocks.redstone_torch);
        cashOut.setStackDisplayName(ChatFormatting.RED + "Cash Out :(");

        upperChestInventory.setInventorySlotContents(26, cashOut);
    }

    private boolean running = true;
    private int explodingSlot = -1;

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        int row = slotId / 9;
        int col = slotId % 9 - 2;

        if (running && row < 5 && 0 <= col && col <= 4 && slotIn != null && slotIn.getStack() == emptyStack) {
            String resp = APIHelper.updateMinesData(minesData, row, col);

            Minecraft.getMinecraft().thePlayer.playSound("random.pop", 0.8f, (float) (0.7f + Math.random() * 0.1));

            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

            if ("o".equals(resp)) {
                inv.setInventorySlotContents(row * 9 + col + 2, rewardStack);
                rewardStack.setStackDisplayName(ChatFormatting.GOLD + minesData.getMultiplier() + "x");
            } else if ("x".equals(resp)) {
                running = false;

//                ItemStack tnt = new ItemStack(Items.dye, 1, 1);
                ItemStack tnt = new ItemStack(Blocks.tnt);
                tnt.setStackDisplayName(ChatFormatting.RED + "Primed TNT");

                explodingSlot = row * 9 + col + 2;
                inv.setInventorySlotContents(explodingSlot, tnt);

                rewardStack.setStackDisplayName(ChatFormatting.RED + "0x");
//                rewardStack.setItem(Items.rotten_flesh);
//                rewardStack.setItemDamage(1);
            }
        }

        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if ((slotId == 80 && running) || (slotId == 67 && !running) || slotId == 85) {
            Minecraft.getMinecraft().displayGuiScreen(new MinesConfigGui(wager, numNumes));
        }
    }

    private long lastUpdate = System.currentTimeMillis();
    private long lastUpdate2 = System.currentTimeMillis();


    private int offset = 0;

    private boolean flag1 = true;
    private boolean flag2 = true;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!running) {
            if (flag1) {
                flag1 = false;
                lastUpdate = System.currentTimeMillis();
                lastUpdate2 = System.currentTimeMillis();

                Minecraft.getMinecraft().thePlayer.playSound("creeper.primed", 1.0f, 0.49f);

                upperChestInventory.setInventorySlotContents(26, null);

                ItemStack retry = new ItemStack(Items.nether_star);
                retry.setStackDisplayName(ChatFormatting.GREEN + "Play Again");
                upperChestInventory.setInventorySlotContents(13, retry);
            }

            if (!flag1 && flag2 && System.currentTimeMillis() - lastUpdate2 > 500) {
                lastUpdate2 = System.currentTimeMillis();

                ItemStack stack = new ItemStack(offset++ % 2 == 0 ? Blocks.quartz_block : Blocks.tnt);
                stack.setStackDisplayName(ChatFormatting.RED + "Primed TNT");

                IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
                inv.setInventorySlotContents(explodingSlot, stack);
            }

            if (!flag1 && flag2 && System.currentTimeMillis() - lastUpdate > 3000) {
                lastUpdate = System.currentTimeMillis();
                flag2 = false;

                Minecraft.getMinecraft().thePlayer.playSound("random.explode", 4.0f, 0.6825f);

                IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

                ItemStack grassBlock = new ItemStack(Items.gunpowder);
                grassBlock.setStackDisplayName(ChatFormatting.GRAY + "Dust");

                for (int i = 0; i < 25; i++) {
                    inv.setInventorySlotContents((i / 5) * 9 + i % 5 + 2, grassBlock);
                }

                ItemStack stack = new ItemStack(Blocks.tnt);
                stack.setStackDisplayName(ChatFormatting.RED + "BOOM!");

                inv.setInventorySlotContents(explodingSlot, stack);
            }
        }
    }


    // game.tnt.primed 1 1
    // creeper.primed 1 0.49
    // random.explode 4.0 0.68

    private boolean flag = true;

    @Override
    public void onGuiClosed() {
        if (flag) {
            double reward = APIHelper.endMinesGame(minesData.getGameId());
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.WHITE + "You won " + ChatFormatting.GOLD + reward + " credits"));
            Data.numCredits = APIHelper.getNumCredits();
        }

        super.onGuiClosed();
    }

    // game.tnt.primed 1 1
// creeper.primed 1 0.49
// random.explode 4.0 0.68
}
