package mod.a.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.APIHelper;
import mod.a.util.data.MinesData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MinesConfigGui extends BaseGui {
    private int wager;
    private int numMines;

    private ItemStack wagerStack;
    private ItemStack bombStack;

    public MinesConfigGui() {
        this(15, 3);
    }

    public MinesConfigGui(int wager, int numMines) {
        super("Mines Config", 27);

        this.wager = wager;
        this.numMines = numMines;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

        ItemStack grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        grayPane.setStackDisplayName("");

        for (int i = 0; i < 27; i++) {
            inv.setInventorySlotContents(i, grayPane);
        }

//        for (int i = 0; i < 27; i++) {
//            upperChestInventory.setInventorySlotContents(i, grayPane);
//        }

        ItemStack increaseWager = new ItemStack(Blocks.stained_glass_pane, 1, 5);
        increaseWager.setStackDisplayName(ChatFormatting.GREEN + "Increase Wager (+1)");

        inv.setInventorySlotContents(12, increaseWager);

        ItemStack increaseBombs = new ItemStack(Blocks.stained_glass_pane, 1, 5);
        increaseBombs.setStackDisplayName(ChatFormatting.GREEN + "Increase Bombs (+1)");

        inv.setInventorySlotContents(16, increaseBombs);

        ItemStack decreaseWager = new ItemStack(Blocks.stained_glass_pane, 1, 14);
        decreaseWager.setStackDisplayName(ChatFormatting.RED + "Decrease Wager (-1)");

        inv.setInventorySlotContents(10, decreaseWager);

        ItemStack decreaseBombs = new ItemStack(Blocks.stained_glass_pane, 1, 14);
        decreaseBombs.setStackDisplayName(ChatFormatting.RED + "Decrease Bombs (-1)");

        inv.setInventorySlotContents(14, decreaseBombs);

        wagerStack = new ItemStack(Items.gold_nugget, wager);
        wagerStack.setStackDisplayName(ChatFormatting.GRAY + "Wager: " + ChatFormatting.GOLD + wager + " credit" + (wager > 1 ? "s" : ""));

        inv.setInventorySlotContents(11, wagerStack);

        bombStack = new ItemStack(Items.tnt_minecart, numMines);
        bombStack.setStackDisplayName(ChatFormatting.GRAY + "Bombs: " + ChatFormatting.RED + numMines);

        inv.setInventorySlotContents(15, bombStack);

        ItemStack start = new ItemStack(Items.nether_star);
        start.setStackDisplayName(ChatFormatting.AQUA + "Start!");

        upperChestInventory.setInventorySlotContents(13, start);
    }

    private boolean flag = false;

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (slotId == 12 && wager < 5000) {
            wager += 5;
            wagerStack.setStackDisplayName(ChatFormatting.GRAY + "Wager: " + ChatFormatting.GOLD + wager + " credit" + (wager > 1 ? "s" : ""));
            wagerStack.stackSize = wager;
        } else if (slotId == 10 && wager > 1) {
            wager -= 5;
            wagerStack.setStackDisplayName(ChatFormatting.GRAY + "Wager: " + ChatFormatting.GOLD + wager + " credit" + (wager > 1 ? "s" : ""));
            wagerStack.stackSize = wager;
        } else if (slotId == 14 && numMines > 1) {
            numMines--;
            bombStack.setStackDisplayName(ChatFormatting.GRAY + "Bombs: " + ChatFormatting.RED + numMines);
            bombStack.stackSize = numMines;
        } else if (slotId == 16 && numMines < 24) {
            numMines++;
            bombStack.setStackDisplayName(ChatFormatting.GRAY + "Bombs: " + ChatFormatting.RED + numMines);
            bombStack.stackSize = numMines;
        } else if (slotId == 40 && !flag) {
            flag = true;
            MinesData data = APIHelper.newMinesGame(wager, numMines);
            if (data != null) {
                Minecraft.getMinecraft().displayGuiScreen(new MinesGui(data, wager, numMines));
            } else {
                flag = false;
            }
        } else if (slotId == 58) {
            Main.getInstance().setGuiToOpen(new GamesGui());
        }
    }
}
