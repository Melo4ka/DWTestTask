package ru.meldren.dwtesttask.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;
import ru.meldren.dwtesttask.Localization;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by Meldren on 22/07/2021
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Command extends org.bukkit.command.Command {

    private static CommandMap commandMap;
    final String localization;
    @Getter
    boolean canBeUsedFromConsole;

    static {
        try {
            final Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Command(String name, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));

        this.canBeUsedFromConsole = true;

        StringBuilder sb = new StringBuilder();
        String[] split = this.getClass().getSimpleName().split("(?=[A-Z])");

        for (int i = 0; i < split.length - 1; ++i)
            sb.append(split[i]).append('_');

        sb.append(split[split.length - 1]).append("_ARGUMENTS");
        this.localization = sb.toString();

        commandMap.register("dw", this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        boolean console = !(sender instanceof Player);

        if (console && !this.canBeUsedFromConsole) {
            System.out.println("This command can't be used from console.");
            return true;
        }

        this.handle(sender, args);
        return true;
    }

    public abstract void handle(CommandSender sender, String[] args);

    protected void unavailableFromConsole() {
        this.canBeUsedFromConsole = false;
    }

    protected void notEnoughArguments(CommandSender sender) {
        sender.sendMessage(Localization.getLocalization(this.localization));
    }

}