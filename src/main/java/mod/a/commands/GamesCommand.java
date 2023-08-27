package mod.a.commands;

import mod.a.Main;
import mod.a.gui.GamesGui;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class GamesCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "games";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Main.getInstance().setGuiToOpen(new GamesGui());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
