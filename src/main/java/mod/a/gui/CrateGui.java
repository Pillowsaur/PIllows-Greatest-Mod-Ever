package mod.a.gui;

import mod.a.Main;
import mod.a.util.data.CrateRespData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CrateGui extends BaseGui {
    private final ItemStack[] slots = new ItemStack[9];
    private final int maxOffset = 60;
    private final CrateRespData crateData;

    private int offset = 0;
    private long lastUpdate = System.currentTimeMillis();

    private boolean rolling = true;
    private boolean celebrate = true;
    private boolean rerollScreen = true;

    private boolean flag = false;

    public CrateGui(CrateRespData crateData) {
        super(crateData.getCrateName(), 27);
        this.crateData = crateData;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        ItemStack redPane = new ItemStack(Blocks.stained_glass_pane, 1, 14);
        redPane.setStackDisplayName("");

        ItemStack greenPane = new ItemStack(Blocks.stained_glass_pane, 1, 5);
        greenPane.setStackDisplayName("");

        // Creating initial config
        for (int i = 0; i < 9; i++) {
            slots[i] = crateData.nextItem();
            inv.setInventorySlotContents(9 + i, slots[i]);

            inv.setInventorySlotContents(i, redPane);
            inv.setInventorySlotContents(18 + i, redPane);
        }

        inv.setInventorySlotContents(4, greenPane);
        inv.setInventorySlotContents(22, greenPane);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (slotId == 58) {
            Main.getInstance().setGuiToOpen(new CratesListGui());
        }
    }

    private boolean resetTitle = true;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (resetTitle) {
            resetTitle = false;
            ((InventoryCustom) upperChestInventory).resetTitle();
        }

        if (rolling && System.currentTimeMillis() - lastUpdate > 50 + decayFunction(offset + 15 - maxOffset)) {
            lastUpdate = System.currentTimeMillis();

            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

            if (offset == maxOffset - 5) {
                slots[offset % 9] = crateData.getWinningStack();
            } else {
                slots[offset % 9] = crateData.nextItem();
            }

            offset++;

            for (int i = 0; i < 9; i++) {
                inv.setInventorySlotContents(9 + i, slots[(i + offset) % 9]);
            }

            Minecraft.getMinecraft().thePlayer.playSound("random.orb", 0.75f, 0.75f);

            if (offset >= maxOffset) {
                offset = 0;
                rolling = false;
            }
        }

        if (!rolling && celebrate && System.currentTimeMillis() - lastUpdate > 250) {
            lastUpdate = System.currentTimeMillis();

            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

            inv.setInventorySlotContents(13, (offset++ % 2) == 0 ? null : crateData.getWinningStack());

            if (offset % 2 == 1) {
                Minecraft.getMinecraft().thePlayer.playSound("random.orb", 0.75f, 0.5f);
            }

            if (offset >= 6) {
                offset = 0;
                celebrate = false;
            }
        }

        if (!celebrate && rerollScreen && System.currentTimeMillis() - lastUpdate > 1000) {
            rerollScreen = false;
            flag = true;
            Main.getInstance().setGuiToOpen(new DONGui(crateData));
        }
    }

    @Override
    public void onGuiClosed() {
        if (!flag) {
            Main.getInstance().setGuiToOpen(new DONGui(crateData));
        }

        super.onGuiClosed();
    }

    private int decayFunction(int x) {
        if (Math.max(x, 0) == 0) return 0;

        switch (x) {
            case 1:
            case 2:
                return 50;
            case 3:
            case 4:
                return 150;
            case 5:
            case 6:
                return 250;
            case 7:
            case 8:
                return 350;
            case 9:
            case 10:
                return 500;
            case 11:
            default:
                return 700;
        }
    }
}
