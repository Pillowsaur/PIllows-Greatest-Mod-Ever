package mod.a.commands;

import mod.a.Main;
import mod.a.gui.CrateGui;
import mod.a.gui.CratesListGui;
import mod.a.util.APIHelper;
import mod.a.util.data.CrateRespData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class RollCrateCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "crates";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            Main.getInstance().setGuiToOpen(new CratesListGui());
        } else {
            CrateRespData data = APIHelper.rollCrate(args[0]);

            if (data != null) {
                Main.getInstance().setGuiToOpen(new CrateGui(data));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
