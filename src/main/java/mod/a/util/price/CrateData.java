package mod.a.util.price;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.util.ItemHelper;
import mod.a.util.RandomCollection;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CrateData {
    private static final double[] chanceFromLevel = {0.3, 0.1, 0.05, 0.01, 0.005};
    private static final ChatFormatting[] colorFromLevel = {ChatFormatting.WHITE, ChatFormatting.GREEN, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD};

    private final String crateId;
    private final String crateName;
    private final RandomCollection<ItemStack> randomCollection = new RandomCollection<>();
    private final double price;

    private ItemStack crateStack;

    private static class RewardData {
        private final String reward;
        private final int amount;
        private final int level;
        private final ItemStack rewardStack;

        public RewardData(String reward, int amount, int level) {
            this.reward = reward;
            this.amount = amount;
            this.level = level;
            rewardStack = ItemHelper.parseItem(reward, amount);
        }

        public int getAmount() {
            return amount;
        }

        public int getLevel() {
            return level;
        }

        public String getReward() {
            return reward;
        }

        public ItemStack getRewardStack() {
            return rewardStack;
        }

        @Override
        public String toString() {
            return "RewardData(" + amount + "x" + reward + "," + level + ")";
        }
    }

    public CrateData(String crateId, JsonObject crateData) {
        this.crateId = crateId;
        crateName = crateData.get("name").getAsString();
        price = crateData.get("price").getAsDouble();

        ArrayList<RewardData> rewards = new ArrayList<>();
        for (JsonElement e : crateData.getAsJsonArray("rewards")) {
            JsonArray temp = e.getAsJsonArray();
            JsonArray temp1 = temp.get(0).getAsJsonArray();

            RewardData temp2 = new RewardData(temp1.get(0).getAsString(), temp1.get(1).getAsInt(), temp.get(1).getAsInt());
            rewards.add(temp2);

            randomCollection.add(temp2.getRewardStack(), chanceFromLevel[temp2.getLevel()]);
        }

        rewards.sort(Comparator.comparingInt(RewardData::getLevel));
        Collections.reverse(rewards);

//        System.out.println(rewards);

        crateStack = new ItemStack(Blocks.chest);
        crateStack.setStackDisplayName(getColor() + crateName);

        String temp = String.valueOf((int) (price * 100));
        ItemHelper.addLore(crateStack, ChatFormatting.GRAY + "Price: " + ChatFormatting.GOLD + temp.substring(0, temp.length() - 2) + "." + temp.substring(temp.length() - 2) + " credits");
        ItemHelper.addLore(crateStack, "");
        ItemHelper.addLore(crateStack, ChatFormatting.GRAY + "Rewards: ");

        boolean flag = rewards.size() < 6;

        for (int i = 0; i < (flag ? rewards.size() : 5); i++) {
//        for (int i = 0; i < rewards.size(); i++) {
            RewardData data = rewards.get(i);
            ItemHelper.addLore(crateStack, colorFromLevel[data.getLevel()] + " - " + ChatFormatting.stripFormatting(data.getRewardStack().getDisplayName()));
        }

        if (!flag) {
            ItemHelper.addLore(crateStack, ChatFormatting.WHITE + "And More!");
        }
    }

    private ChatFormatting getColor() {
        return ChatFormatting.GREEN;
    }

    public ItemStack nextItem() {
        return randomCollection.next();
    }

    public String getCrateName() {
        return crateName;
    }

    public double getPrice() {
        return price;
    }

    public ItemStack getCrateStack() {
        return crateStack;
    }

    public String getCrateId() {
        return crateId;
    }

    public static ChatFormatting getColorFromLevel(int level) {
        return colorFromLevel[level];
    }
}
