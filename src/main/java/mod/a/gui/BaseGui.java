package mod.a.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseGui extends GuiChest {
    private int lastSlot = -1;
    private ItemStack stack = null;
    private long noClickGui = System.currentTimeMillis();

    public BaseGui(String inventoryName, int slotCount) {
        super(new InventoryCustom(), new InventoryBasic(inventoryName, false, slotCount));
    }

    public BaseGui(String inventoryName, int slotCount, boolean resetTitle) {
        super(new InventoryCustom(), new InventoryBasic(inventoryName, false, slotCount));

        this.resetTitle = resetTitle;
    }


    private void resetSlot() {
        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        if (lastSlot == -1) {
            return;
        } else if (lastSlot < inv.getSizeInventory()) {
            if (inv.getStackInSlot(lastSlot) == null) {
                inv.setInventorySlotContents(lastSlot, stack);
            }
        } else {
            int temp = lastSlot - inv.getSizeInventory() + 9;
            if (temp > upperChestInventory.getSizeInventory()) {
                temp -= upperChestInventory.getSizeInventory();
            }

            if (upperChestInventory.getStackInSlot(temp) == null) {
                upperChestInventory.setInventorySlotContents(lastSlot - inv.getSizeInventory(), stack);
            }
        }

        stack = null;
        lastSlot = -1;
    }

    private void handleSlotClick(Slot slotIn, int slotId) {
        resetSlot();

        if (slotIn != null) {
            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

            if (slotId < inv.getSizeInventory() && slotIn.getHasStack()) {
                lastSlot = slotId;
                stack = slotIn.getStack();
                inv.setInventorySlotContents(slotIn.slotNumber, null);

                noClickGui = System.currentTimeMillis();
            }

            if (slotId >= inv.getSizeInventory() && slotIn.getHasStack()) {
                lastSlot = slotId;
                stack = slotIn.getStack();
                upperChestInventory.setInventorySlotContents(lastSlot - inv.getSizeInventory(), null);

                noClickGui = System.currentTimeMillis();
            }
        }
    }

    private boolean resetTitle = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (resetTitle) {
            resetTitle = false;
            ((InventoryCustom) upperChestInventory).resetTitle();
        }

        if (lastSlot != -1 && System.currentTimeMillis() - noClickGui > 75) {
            resetSlot();
        }

        if (lastSlot != -1) {
            drawItemStack(stack, mouseX - 8, mouseY - 8);
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        handleSlotClick(slotIn, slotId);
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;

        RenderHelper.disableStandardItemLighting();
    }
}
