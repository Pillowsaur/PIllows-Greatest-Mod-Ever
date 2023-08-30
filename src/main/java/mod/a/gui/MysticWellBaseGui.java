package mod.a.gui;


// Timings
// 162 MS waiting
// 100 MS rolling

// Sounds
// FIGURE OUT LATER :)

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.Main;
import mod.a.util.APIHelper;
import mod.a.util.Data;
import mod.a.util.ItemHelper;
import mod.a.util.data.MysticWellData;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class MysticWellBaseGui extends BaseGui {
    private final static int[] tierColors = {6, 5, 4, 14};
    private final static ChatFormatting[] tierColorsFormatting = {ChatFormatting.LIGHT_PURPLE, ChatFormatting.GREEN, ChatFormatting.YELLOW, ChatFormatting.RED};
    private final static String[] tierNumerals = {"", "I", "II", "III"};

    private final static String[] pantsColors = {"RED", "ORANGE", "YELLOW", "GREEN", "BLUE"};

    private final MysticWellData data;

    private final ItemStack grayPane;
    private final ItemStack colorPane;

    private final static int[] paneLocations = {10, 11, 12, 21, 30, 29, 28, 19};
    private int offset;

    public MysticWellBaseGui(MysticWellData data, boolean circle, int offsetStart) {
        this(data, circle, offsetStart, false);
    }

    private boolean playFireworks;

    public MysticWellBaseGui(MysticWellData data, boolean circle, int offsetStart, boolean playFireworks) {
        super("Mystic Well", 45);
        this.data = data;
        this.offset = offsetStart;
        this.playFireworks = playFireworks;

        IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();

//        grayPane = new ItemStack(Blocks.stained_glass_pane, 1, 7);
        grayPane = new ItemStack(Blocks.stained_glass_pane, 1, circle ? 15 : tierColors[data.getItemTier()]);
        grayPane.setStackDisplayName((circle ? ChatFormatting.GRAY : tierColorsFormatting[data.getItemTier()]) + "Item in Well");

        colorPane = new ItemStack(Blocks.stained_glass_pane, 1, tierColors[data.getItemTier()]);
        colorPane.setStackDisplayName(tierColorsFormatting[data.getItemTier()] + "Item in Well");

        for (int i = 0; i < 9; i++) {
            inv.setInventorySlotContents(10 + (i / 3) * 9 + i % 3, grayPane);
        }

        ItemStack well;
        if (data.getItemTier() >= 3) {
            well = new ItemStack(Blocks.stained_hardened_clay, 1, 14);
            well.setStackDisplayName(ChatFormatting.RED + "Mystic Well");

            ItemHelper.setLore(well, new String[]{ChatFormatting.GRAY + "This item cannot be upgraded any", ChatFormatting.GRAY + "further.", "", ChatFormatting.RED + "Maxed out upgrade tier!"});
        } else {
            well = new ItemStack(Blocks.enchanting_table);
            well.setStackDisplayName(ChatFormatting.LIGHT_PURPLE + "Mystic Well");

            ItemHelper.setLore(well, new String[]{ChatFormatting.GRAY + "Upgrade: " + tierColorsFormatting[data.getItemTier() + 1] + "Tier " + tierNumerals[data.getItemTier() + 1], ChatFormatting.GRAY + "Cost: " + ChatFormatting.GOLD + data.getNextCost() + " credits", "", ChatFormatting.YELLOW + "Click to upgrade!"});
        }

        inv.setInventorySlotContents(20, data.getItem());
        inv.setInventorySlotContents(24, well);

        ItemStack blackPane = new ItemStack(Blocks.stained_glass_pane, 1, 15);
        blackPane.setStackDisplayName("");

        for (int i = 0; i < 15; i++) {
            upperChestInventory.setInventorySlotContents(2 + (i / 5) * 9 + i % 5, blackPane);
        }

        upperChestInventory.setInventorySlotContents(12, ItemHelper.createFreshItem("SWORD", Data.wellPrice.getSwordPrice()));
        upperChestInventory.setInventorySlotContents(13, ItemHelper.createFreshItem("BOW", Data.wellPrice.getBowPrice()));
        upperChestInventory.setInventorySlotContents(14, ItemHelper.createFreshItem("PANTS", "RED", Data.wellPrice.getPantsPrice()));
    }

    private boolean flag = false;

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);

        if (flag) {
            System.out.println("Tried to do something while locked");
        } else if (data.getItem() != null && slotId == 24) {
            flag = true;

            String gameId = data.getGameId();

            if (gameId == null) {
                MysticWellData data1 = null;

                if (data.getItem().getItem() == Items.golden_sword) {
                    data1 = APIHelper.freshItem("SWORD");
                } else if (data.getItem().getItem() == Items.bow) {
                    data1 = APIHelper.freshItem("BOW");
                } else if (data.getItem().getItem() == Items.leather_leggings) {
                    data1 = APIHelper.freshItem("PANTS", ItemHelper.getPantsColor(data.getItem()));
                }

                if (data1 != null) {
                    gameId = data1.getGameId();
                }
            }

            if (gameId != null) {
                MysticWellData data1 = APIHelper.enchantItem(gameId);
                Data.numCredits = APIHelper.getNumCredits();
                if (data1 != null) {
                    Main.getInstance().setGuiToOpen(new MysticWellRollingGui(data1));
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } else if (slotId == 57) {
            MysticWellData data1 = new MysticWellData(ItemHelper.createFreshItem("SWORD"), null, 0);
            Main.getInstance().setGuiToOpen(new MysticWellBaseGui(data1, true, offset));
        } else if (slotId == 58) {
            MysticWellData data1 = new MysticWellData(ItemHelper.createFreshItem("BOW"), null, 0);
            Main.getInstance().setGuiToOpen(new MysticWellBaseGui(data1, true, offset));
        } else if (slotId == 59) {
            MysticWellData data1 = new MysticWellData(ItemHelper.createFreshItem("PANTS", pantsColors[(int) (Math.random() * 5)]), null, 0);
            Main.getInstance().setGuiToOpen(new MysticWellBaseGui(data1, true, offset));
        } else if (slotId == 76) {
            Main.getInstance().setGuiToOpen(new GamesGui());
        } else if (slotId == 20) {
            MysticWellData data1 = new MysticWellData(null, null, 0);
            Main.getInstance().setGuiToOpen(new MysticWellBaseGui(data1, true, offset));
        }
    }


    @Override
    public void onGuiClosed() {
        if (!flag && data.getGameId() != null) {
            double reward = APIHelper.cancelMysticWell(data.getGameId());
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You won " + ChatFormatting.GOLD + reward + " credits " + ChatFormatting.WHITE + "from ").appendSibling(data.getItem().getChatComponent()));
            Data.numCredits = APIHelper.getNumCredits();
        }

        super.onGuiClosed();
    }

    private long lastUpdate = 0;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (playFireworks) {
            playFireworks = false;

            Minecraft.getMinecraft().thePlayer.playSound("fireworks.twinkle", 0.9f, 1.7936f);
            Minecraft.getMinecraft().thePlayer.playSound("fireworks.largeBlast", 0.9f, 1.1905f);
        }


        if (System.currentTimeMillis() - lastUpdate > 162) {
            lastUpdate = System.currentTimeMillis();

            IInventory inv = ((ContainerChest) this.inventorySlots).getLowerChestInventory();
            inv.setInventorySlotContents(paneLocations[offset], grayPane);

            offset = (offset + 1) % 8;
            inv.setInventorySlotContents(paneLocations[offset], colorPane);
        }
    }
}
