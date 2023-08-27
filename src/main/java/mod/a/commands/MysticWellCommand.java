package mod.a.commands;

import mod.a.Main;
import mod.a.gui.MysticWellBaseGui;
import mod.a.util.data.MysticWellData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class MysticWellCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "well";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Main.getInstance().setGuiToOpen(new MysticWellBaseGui(new MysticWellData(null, null, 5, 0), true, 0));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
