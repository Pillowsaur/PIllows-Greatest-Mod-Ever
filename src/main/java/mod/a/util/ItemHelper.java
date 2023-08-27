package mod.a.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemHelper {
    private enum PantColor {
        RED(ChatFormatting.RED, 0xFF5555),
        ORANGE(ChatFormatting.GOLD, 0xFFAA00),
        YELLOW(ChatFormatting.YELLOW, 0xFFFF55),
        GREEN(ChatFormatting.GREEN, 0x55FF55),
        BLUE(ChatFormatting.BLUE, 0x5555FF);

        private final ChatFormatting chatFormatting;
        private final int hexCode;

        PantColor(ChatFormatting chatFormatting, int hexCode) {
            this.chatFormatting = chatFormatting;
            this.hexCode = hexCode;
        }

        public ChatFormatting getChatFormatting() {
            return chatFormatting;
        }

        public int getHexCode() {
            return hexCode;
        }
    }

    public static ItemStack parseItem(JsonArray arr) {
        return parseItem(arr.get(0).getAsString(), arr.get(1).getAsInt());
    }

    public static ItemStack parseItem(String itemName, int amount) {
        ItemStack stack = getItemFromName(itemName, amount);
        stack.setStackDisplayName(formatName(itemName, amount));

        return stack;
    }

    private static final HashMap<String, PantColor> pantColors = new HashMap<>();
    private final static ChatFormatting[] tierColorsFormatting = {ChatFormatting.LIGHT_PURPLE, ChatFormatting.GREEN, ChatFormatting.YELLOW, ChatFormatting.RED};
    private final static String[] tierNumerals = {"", "I", "II", "III"};

    public static ItemStack parseMysticItem(JsonObject obj) {
        ItemStack stack;
        int tier = obj.get("tier").getAsInt();

        if (tier == 0) {
            switch (obj.get("itemType").getAsString()) {
                case "SWORD":
                case "BOW":
                    return createFreshItem(obj.get("itemType").getAsString());
                case "PANTS":
                    return createFreshItem("PANTS", obj.get("color").getAsString());
                default:
                    return null;
            }
        } else {
            switch (obj.get("itemType").getAsString()) {
                case "SWORD":
                    stack = new ItemStack(Items.golden_sword);
                    stack.setStackDisplayName(tierColorsFormatting[tier] + getItemPrefix(obj) + "Tier " + tierNumerals[tier] + " Sword");
                    setAttackDamage(stack, 6.5f);
                    break;
                case "BOW":
                    stack = new ItemStack(Items.bow);
                    stack.setStackDisplayName(tierColorsFormatting[tier] + getItemPrefix(obj) + "Tier " + tierNumerals[tier] + " Bow");
                    break;
                case "PANTS":
                    stack = new ItemStack(Items.leather_leggings);

                    String color = obj.get("color").getAsString();
                    ChatFormatting format = pantColors.get(color).getChatFormatting();

                    stack.setStackDisplayName(format + getItemPrefix(obj) + "Tier " + tierNumerals[tier] + " " + color.charAt(0) + color.substring(1).toLowerCase() + " Pants");
                    dyeStack(stack, pantColors.get(color).getHexCode());
                    break;
                default:
                    return null;
            }

            int lives = obj.get("lives").getAsInt();
            setLore(stack, new String[]{ChatFormatting.GRAY + "Lives: " + ChatFormatting.GREEN + lives + ChatFormatting.GRAY + "/" + lives});

            for (JsonElement ench : obj.getAsJsonArray("enchants")) {
                String enchName = ench.getAsJsonArray().get(0).getAsString();
                int enchLevel = ench.getAsJsonArray().get(1).getAsInt();

                addLore(stack, "");
                addLore(stack, enchantments.get(enchName).getName(enchLevel));
                addLore(stack, enchantments.get(enchName).getDescription(enchLevel));
            }

            if ("PANTS".equals(obj.get("itemType").getAsString())) {
                addLore(stack, "");

                String color = obj.get("color").getAsString();
                ChatFormatting format = pantColors.get(color).getChatFormatting();

                addLore(stack, format + "As strong as iron");
            }

            return stack;
        }
    }

    public static ItemStack createFreshItem(String itemType) {
        return createFreshItem(itemType, null);
    }

    public static ItemStack createFreshItem(String itemType, String color) {
        ItemStack stack;

        switch (itemType) {
            case "SWORD":
                stack = new ItemStack(Items.golden_sword);
                stack.setStackDisplayName(ChatFormatting.YELLOW + "Mystic Sword");
                setLore(stack, new String[]{ChatFormatting.GRAY + "Kept on death", "", ChatFormatting.GRAY + "Used in the mystic well"});
                setAttackDamage(stack, 6.5f);
                return stack;
            case "BOW":
                stack = new ItemStack(Items.bow);
                stack.setStackDisplayName(ChatFormatting.AQUA + "Mystic Bow");
                setLore(stack, new String[]{ChatFormatting.GRAY + "Kept on death", "", ChatFormatting.GRAY + "Used in the mystic well"});
                return stack;
            case "PANTS":
                stack = new ItemStack(Items.leather_leggings);
                ChatFormatting format = pantColors.get(color).getChatFormatting();

                stack.setStackDisplayName(format + "Fresh " + color.charAt(0) + color.substring(1).toLowerCase() + " Pants");
                dyeStack(stack, pantColors.get(color).getHexCode());

                setLore(stack, new String[]{ChatFormatting.GRAY + "Kept on death", "", format + "Used in the mystic well", format + "Also, a fashion statement"});
                return stack;
            default:
                return null;
        }
    }

    public static ItemStack createFreshItem(String itemType, double cost) {
        return createFreshItem(itemType, null, cost);
    }

    public static ItemStack createFreshItem(String itemType, String color, double cost) {
        ItemStack stack;

        switch (itemType) {
            case "SWORD":
                stack = new ItemStack(Items.golden_sword);
                stack.setStackDisplayName(ChatFormatting.YELLOW + "Mystic Sword");
                setLore(stack, new String[]{ChatFormatting.GRAY + "Cost: " + ChatFormatting.GOLD + cost + " credits", ChatFormatting.GRAY + "Kept on death", "", ChatFormatting.GRAY + "Used in the mystic well"});
                setAttackDamage(stack, 6.5f);

                return stack;
            case "BOW":
                stack = new ItemStack(Items.bow);
                stack.setStackDisplayName(ChatFormatting.AQUA + "Mystic Bow");
                setLore(stack, new String[]{ChatFormatting.GRAY + "Cost: " + ChatFormatting.GOLD + cost + " credits", ChatFormatting.GRAY + "Kept on death", "", ChatFormatting.GRAY + "Used in the mystic well"});
                return stack;
            case "PANTS":
                stack = new ItemStack(Items.leather_leggings);
                ChatFormatting format = pantColors.get(color).getChatFormatting();

                stack.setStackDisplayName(format + "Fresh " + color.charAt(0) + color.substring(1).toLowerCase() + " Pants");
                dyeStack(stack, pantColors.get(color).getHexCode());

                setLore(stack, new String[]{ChatFormatting.GRAY + "Cost: " + ChatFormatting.GOLD + cost + " credits", ChatFormatting.GRAY + "Kept on death", "", format + "Used in the mystic well", format + "Also, a fashion statement"});
                return stack;
            default:
                return null;
        }
    }

    public static String getItemPrefix(JsonObject obj) {
        int numRares = 0;
        int numTokens = 0;

        for (JsonElement ench : obj.getAsJsonArray("enchants")) {
            String enchName = ench.getAsJsonArray().get(0).getAsString();
            int enchLevel = ench.getAsJsonArray().get(1).getAsInt();

            numTokens += enchLevel;

            if (enchantments.get(enchName).getName(enchLevel).contains("RARE")) {
                numRares++;
            }
        }

        int numLives = obj.get("lives").getAsInt();

        if (numRares == 3 && numLives > 100) {
            return "One in a million ";
        } else if (numRares == 3) {
            return "Unthinkable ";
        } else if (numTokens >= 7 && numLives > 100) {
            return "Overpowered ";
        } else if (numRares == 2 && numLives > 100) {
            return "Miraculous ";
        } else if (numLives >= 100) {
            return "Artefact ";
        } else if (numRares == 2) {
            return "Extraordinary ";
        } else if (numTokens == 8) {
            return "Legendary ";
        } else {
            return "";
        }
    }

    public static ItemStack getItemFromName(String itemName, int amount) {
        switch (itemName) {
            case "sewer_rubbish":
                return new ItemStack(Items.dye, amount, 15);
            case "cactus":
                return new ItemStack(Blocks.cactus, amount);
            case "pants_bundle":
                return new ItemStack(Items.chest_minecart, amount);
            case "feather":
                return new ItemStack(Items.feather, amount);
            case "vile":
                return new ItemStack(Items.coal, amount);
            case "gem":
                ItemStack item = new ItemStack(Items.emerald, amount);
                item.addEnchantment(Enchantment.fortune, 1);
                item.getTagCompound().setInteger("HideFlags", 1);
                return item;
            case "jewel_sword":
                return new ItemStack(Blocks.ender_chest, amount);
        }

        return new ItemStack(Blocks.barrier, amount);
    }

    private static final Pattern pattern = Pattern.compile("Fresh (\\w+) Pants");

    public static String getPantsColor(ItemStack stack) {
        Matcher matcher = pattern.matcher(stack.getDisplayName());
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        return null;
    }

    public static String formatName(String itemName, int amount) {
        switch (itemName) {
            case "sewer_rubbish":
                return ChatFormatting.DARK_AQUA + (amount + "x Sewer Rubbish");
            case "cactus":
                return ChatFormatting.GREEN + (amount + "x Philosopher's Cactus");
            case "pants_bundle":
                return ChatFormatting.BLUE + (amount + "x Pants Bundle");
            case "feather":
                return ChatFormatting.AQUA + (amount + "x Funky Feather");
            case "vile":
                return ChatFormatting.DARK_PURPLE + (amount + "x Chunk of Vile");
            case "gem":
                return ChatFormatting.GREEN + (amount + "x Totally Legit Gem");
            case "jewel_sword":
                return ChatFormatting.LIGHT_PURPLE + (amount + "x Hidden Jewel Sword");
        }

        return amount + "x " + itemName;
    }

    public static void setLore(ItemStack stack, String[] lore) {
        NBTTagCompound displayTag;
        if (stack.getTagCompound().hasKey("display")) {
            displayTag = stack.getTagCompound().getCompoundTag("display");
        } else {
            displayTag = new NBTTagCompound();
            stack.getTagCompound().setTag("display", displayTag);
        }

        NBTTagList loreTag = new NBTTagList();
        displayTag.setTag("Lore", loreTag);

        for (String line : lore) {
            loreTag.appendTag(new NBTTagString(ChatFormatting.RESET + line));
        }
    }

    public static void addLore(ItemStack stack, ArrayList<String> lore) {
        NBTTagCompound displayTag;
        if (stack.getTagCompound().hasKey("display")) {
            displayTag = stack.getTagCompound().getCompoundTag("display");
        } else {
            displayTag = new NBTTagCompound();
            stack.getTagCompound().setTag("display", displayTag);
        }

        NBTTagList loreTag;
        if (displayTag.hasKey("Lore")) {
            loreTag = displayTag.getTagList("Lore", 8);
        } else {
            loreTag = new NBTTagList();
            displayTag.setTag("Lore", loreTag);
        }

        for (String line : lore) {
            loreTag.appendTag(new NBTTagString(ChatFormatting.RESET + line));
        }
    }

    public static void addLore(ItemStack stack, String lore) {
        NBTTagCompound displayTag;
        if (stack.getTagCompound().hasKey("display")) {
            displayTag = stack.getTagCompound().getCompoundTag("display");
        } else {
            displayTag = new NBTTagCompound();
            stack.getTagCompound().setTag("display", displayTag);
        }

        NBTTagList loreTag;
        if (displayTag.hasKey("Lore")) {
            loreTag = displayTag.getTagList("Lore", 8);
        } else {
            loreTag = new NBTTagList();
            displayTag.setTag("Lore", loreTag);
        }

        loreTag.appendTag(new NBTTagString(ChatFormatting.RESET + lore));
    }

    public static void setAttackDamage(ItemStack stack, float damage) {
        NBTTagList attributeModifiers;
        if (stack.getTagCompound().hasKey("AttributeModifiers")) {
            attributeModifiers = stack.getTagCompound().getTagList("AttributeModifiers", 10);
        } else {
            attributeModifiers = new NBTTagList();
            stack.getTagCompound().setTag("AttributeModifiers", attributeModifiers);
        }


        NBTTagCompound attackDamage = new NBTTagCompound();

        attackDamage.setString("AttributeName", "generic.attackDamage");
        attackDamage.setString("Name", "Damage");
        attackDamage.setInteger("UUIDLeast", 1);
        attackDamage.setInteger("UUIDMost", 1);
        attackDamage.setInteger("Operation", 0);
        attackDamage.setFloat("Amount", damage);

        attributeModifiers.appendTag(attackDamage);
    }

    public static void dyeStack(ItemStack stack, int color) {
        NBTTagCompound displayTag;
        if (stack.getTagCompound().hasKey("display")) {
            displayTag = stack.getTagCompound().getCompoundTag("display");
        } else {
            displayTag = new NBTTagCompound();
            stack.getTagCompound().setTag("display", displayTag);
        }

        displayTag.setInteger("color", color);
    }

    private static class EnchantmentData {
        private final ArrayList<ArrayList<String>> descriptions = new ArrayList<>();
        private final String name;

        public EnchantmentData(JsonObject obj) {
            name = obj.get("Name").getAsString();

            for (JsonElement e : obj.getAsJsonArray("Descriptions")) {
                JsonArray arr = e.getAsJsonArray();

                ArrayList<String> desc = new ArrayList<>();
                for (JsonElement e1 : arr) {
                    desc.add(e1.getAsString());
                }

                descriptions.add(desc);
            }
        }

        public String getName(int tier) {
            return name + " " + tierNumerals[tier];
        }

        public ArrayList<String> getDescription(int tier) {
            return descriptions.get(tier - 1);
        }
    }

    private static final HashMap<String, EnchantmentData> enchantments = new HashMap<>();

    static {
        pantColors.put("RED", PantColor.RED);
        pantColors.put("ORANGE", PantColor.ORANGE);
        pantColors.put("YELLOW", PantColor.YELLOW);
        pantColors.put("GREEN", PantColor.GREEN);
        pantColors.put("BLUE", PantColor.BLUE);

        boolean flag = true;

        while (flag) {
            ResourceLocation loc = new ResourceLocation("testmod:enchantmentDisplay.json");
            try {
                JsonObject obj = new JsonParser().parse(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream())).getAsJsonObject();
                flag = false;

                for (Map.Entry<String, JsonElement> elem : obj.entrySet()) {
                    enchantments.put(elem.getKey(), new EnchantmentData(elem.getValue().getAsJsonObject()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
