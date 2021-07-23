package ru.meldren.dwtesttask.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import ru.meldren.dwtesttask.Localization;
import ru.meldren.dwtesttask.entities.BossManager;

/**
 * Created by Meldren on 22/07/2021
 */
public class SpawnBossCommand extends Command {

    public SpawnBossCommand() {
        super("spawnboss", "sb");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length == 5) {
            try {
                ConfigurationSection section =
                        BossManager.getBossesConfig().getConfigurationSection(args[0]);

                BossManager.spawnBoss(args[0], new Location(Bukkit.getWorld(args[1]),
                        Double.parseDouble(args[2]), Double.parseDouble(args[3]),
                        Double.parseDouble(args[4])), section.getString("name"),
                        section.getInt("cooldown"), section.getDouble("health"),
                        section.getDouble("damage"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        Localization.getLocalization("SPAWN_BOSS_COMMAND_SUCCESS")));
            } catch (Exception ex) {
                this.notEnoughArguments(sender);
            }
        } else {
            this.notEnoughArguments(sender);
        }
    }
}
