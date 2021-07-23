package ru.meldren.dwtesttask.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.meldren.dwtesttask.Manager;

import java.io.File;
import java.io.IOException;

/**
 * Created by Meldren on 21/07/2021
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Configuration {

    File configfile;
    FileConfiguration config;

    public Configuration(String name) {
        this.configfile = new File(Manager.getInstance().getDataFolder(), name + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.configfile);
    }

    public FileConfiguration get() {
        return this.config;
    }

    public void save() {
        if (this.config == null || this.configfile == null) {
            return;
        }
        try {
            this.get().save(this.configfile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
