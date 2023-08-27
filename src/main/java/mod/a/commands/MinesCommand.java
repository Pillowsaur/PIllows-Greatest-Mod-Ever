package mod.a.commands;

import mod.a.Main;
import mod.a.gui.MinesConfigGui;
import mod.a.gui.MinesGui;
import mod.a.util.APIHelper;
import mod.a.util.data.MinesData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class MinesCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "mines";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            Main.getInstance().setGuiToOpen(new MinesConfigGui());
        } else {
            int wager;
            int mines;

            try {
                wager = Integer.parseInt(args[0]);
                mines = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return;
            }

            MinesData minesData = APIHelper.newMinesGame(wager, mines);
            Main.getInstance().setGuiToOpen(new MinesGui(minesData, wager, mines));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
