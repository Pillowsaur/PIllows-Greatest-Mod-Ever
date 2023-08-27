package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.ItemHelper;
import mod.a.util.data.MysticWellData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MysticWellRollingGui extends GuiChest {
    private final static int[] tierColors = {6, 5, 4, 14};
    private final static ChatFormatting[] tierColorsFormatting = {ChatFormatting.LIGHT_PURPLE, ChatFormatting.GREEN, ChatFormatting.YELLOW, ChatFormatting.RED};

    private final MysticWellData data;

    private final ItemStack grayPane;
    private final ItemStack colorPane;

    private final static int[] paneLocations = {21, 30, 29, 28, 19, 10, 11, 12};

    private int offset = 0;

    private boolean stage1 = true;
    private boolean stage2 = true;
    private boolean stage3 = true;


    public MysticWellRollingGui(MysticWellData data) {
        super(new InventoryCustom(), new InventoryBasic("Mystic Well", false, 45));
        this.data = data;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 15);
        grayPane.setStackDisplayName(ChatFormatting.GRAY + "It's rollin?");

        colorPane = new ItemStack(Blocks.stained_glass_pane, 1, tierColors[data.getItemTier()]);
        colorPane.setStackDisplayName(tierColorsFormatting[data.getItemTier()] + "It's rollin?");

        for (int i = 0; i < 9; i++) {
            inv.setInventorySlotContents(10 + (i / 3) * 9 + i % 3, grayPane);
        }

        ItemStack well = new ItemStack(Blocks.enchanting_table);
        well.setStackDisplayName(ChatFormatting.LIGHT_PURPLE + "It's rollin?");

        inv.setInventorySlotContents(24, well);

        inv.setInventorySlotContents(20, data.getItem());
        inv.setInventorySlotContents(24, well);

        ItemStack blackPane = new ItemStack(Blocks.stained_glass_pane, 1, 15);
        blackPane.setStackDisplayName("");

        for (int i = 0; i < 15; i++) {
            upperChestInventory.setInventorySlotContents(2 + (i / 5) * 9 + i % 5, blackPane);
        }

        upperChestInventory.setInventorySlotContents(12, ItemHelper.createFreshItem("SWORD", 1.0));
        upperChestInventory.setInventorySlotContents(13, ItemHelper.createFreshItem("BOW", 1.0));
        upperChestInventory.setInventorySlotContents(14, ItemHelper.createFreshItem("PANTS", "RED", 1.0));
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (slotId == 76) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    private long lastUpdate = System.currentTimeMillis();

    private int soundOffset = -1;

    private boolean fastSound = false;
    private boolean fastSucc = false;

    private boolean playFireworks = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (stage1 && System.currentTimeMillis() - lastUpdate > 162) {
            lastUpdate = System.currentTimeMillis();
            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

            if (offset++ < 4) {
                if (offset % 2 == 1) {
                    for (int i = 0; i < 9; i++) {
                        inv.setInventorySlotContents(10 + (i / 3) * 9 + i % 3, colorPane);
                    }

                    // (offset / 2) - NOT A BUG
                    Minecraft.getMinecraft().thePlayer.playSound("random.pop", 0.55f + (offset / 2) * 0.15f, 0.8f + (offset / 2) * 0.1f);
                    Minecraft.getMinecraft().thePlayer.playSound("random.pop", 1.11f + (offset / 2) * 0.3f, 0.8f + (offset / 2) * 0.1f);
                } else {
                    for (int i = 0; i < 9; i++) {
                        inv.setInventorySlotContents(10 + (i / 3) * 9 + i % 3, grayPane);
                    }

                    Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.4f, 0.825f);
                    playFireworks = true;
                }
            } else {
                stage1 = false;
                offset = 0;
            }
        }

        if (!stage1 && stage2) {
            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
            ItemStack temp = new ItemStack(Items.dye, 1, offset % 15);
            temp.setStackDisplayName("");
            inv.setInventorySlotContents(20, temp);
        }

        if (!stage1 && stage2 && System.currentTimeMillis() - lastUpdate > 100) {
            lastUpdate = System.currentTimeMillis();

            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
            inv.setInventorySlotContents(paneLocations[offset % 8], grayPane);

            if (offset++ < 28) {
                inv.setInventorySlotContents(paneLocations[offset % 8], colorPane);

                soundOffset++; // Starts 1 behind regular offset (why can't we just use offset?)

                Minecraft.getMinecraft().thePlayer.playSound("random.pop", 0.04f * soundOffset + 1.16f, getSound() + (1 + soundOffset % 4) / 4f);

                if (offset > 14) { // Slow
                    fastSound = true;
                }

                if (offset > 24) {
                    fastSucc = true;
                    Minecraft.getMinecraft().thePlayer.playSound("random.successful_hit", 0.42f, 1.1746032f);
                }
            } else {
                stage2 = false;

                for (int i = 0; i < 9; i++) {
                    inv.setInventorySlotContents(10 + (i / 3) * 9 + i % 3, colorPane);
                }

                inv.setInventorySlotContents(20, data.getItem());
            }
        }

        if (!stage1 && stage2 && (System.currentTimeMillis() - lastUpdate > 50)) {
            if (fastSound) {
                fastSound = false;
                Minecraft.getMinecraft().thePlayer.playSound("random.pop", 0.04f * soundOffset + 1.18f, getSound() + (1.5f + soundOffset % 4) / 4f);
            }

            if (fastSucc) {
                fastSucc = false;
                Minecraft.getMinecraft().thePlayer.playSound("random.successful_hit", 0.42f, 1.4920635f);
            }
        }

        if (!stage2 && stage3 && (System.currentTimeMillis() - lastUpdate > 100)) {
            stage3 = false;
        }

        if (!stage3) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @Override
    public void onGuiClosed() {
        Main.getInstance().setGuiToOpen(new MysticWellBaseGui(data, false, 0, playFireworks));
        super.onGuiClosed();
    }

    // maxSound = 2x minSound
    private float getSound() {
        return 0.03968f * (soundOffset / 4) + 0.55215f;
    }
}