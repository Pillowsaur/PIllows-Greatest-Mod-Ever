package mod.a;

import mod.a.commands.*;
import mod.a.util.Configuration;
import mod.a.util.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "testmod")
public class Main {
    private static Main instance;

    private final Configuration configuration = new Configuration();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;

        Data.loadData();

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new SetKeyCommand());
        ClientCommandHandler.instance.registerCommand(new RollCrateCommand());
        ClientCommandHandler.instance.registerCommand(new MysticWellCommand());
        ClientCommandHandler.instance.registerCommand(new MinesCommand());
        ClientCommandHandler.instance.registerCommand(new GamesCommand());
        ClientCommandHandler.instance.registerCommand(new ReloadDataCommand());
    }

    public static Main getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private GuiScreen guiToOpen = null;

    public void setGuiToOpen(GuiScreen guiToOpen) {
        this.guiToOpen = guiToOpen;
    }

    @SubscribeEvent
    public void displayGui(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }
    }

//    @SubscribeEvent
//    public void onSound(PlaySoundEvent event) {
//        System.out.println(event.name + ", " + event.sound.getVolume() + ", " + event.sound.getPitch());
//    }


}
