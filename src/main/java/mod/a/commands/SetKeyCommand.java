package mod.a.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.a.util.APIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class SetKeyCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "setkey";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            if (APIHelper.setApiKey("")) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN + "Removed API Key"));
            }
        } else {
            if (APIHelper.setApiKey(args[0])) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN + "Set API Key to " + args[0]));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Failed to set API Key"));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
