package ru.meldren.dwtesttask.entities;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import ru.meldren.dwtesttask.utils.Configuration;

/**
 * Created by Meldren on 22/07/2021
 */
@UtilityClass
public class BossManager {

    @Getter
    private static final FileConfiguration bossesConfig = new Configuration("bosses").get();

    public static void init() {
        for (String key : bossesConfig.getKeys(false)) {
            ConfigurationSection bossSection = bossesConfig.getConfigurationSection(key);

            Location loc = new Location(
                    Bukkit.getWorld(bossSection.getString("location.world")),
                    bossSection.getDouble("location.x"),
                    bossSection.getDouble("location.y"),
                    bossSection.getDouble("location.z"),
                    (float) bossSection.getDouble("location.yaw"),
                    (float) bossSection.getDouble("location.pitch")
            );

            spawnBoss(key, loc, bossSection.getString("name"),
                    bossSection.getInt("cooldown"), bossSection.getDouble("health"),
                    bossSection.getDouble("damage"));
        }
    }

    public static void spawnBoss(String script, Location loc, String name, int cooldown, double health, double damage) {
        try {
            Class<?> clazz = Class.forName("ru.meldren.dwtesttask.entities." + script);

            clazz.getConstructor(Location.class, String.class, int.class, double.class, double.class)
                    .newInstance(loc, name, cooldown, health, damage);
        } catch (Exception ex) {
            System.out.println("Error while spawning a boss with ID: " + script + ".");
            ex.printStackTrace();
        }
    }

    public static void clearEntities() {
        Bukkit.getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(entity -> entity.getCustomName() != null).forEach(Entity::remove));
    }

}
