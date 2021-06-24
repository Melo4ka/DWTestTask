package ru.meldren.dwtesttask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.meldren.dwtesttask.utils.sql.MySQL;

/**
 * Created by Meldren on 22/06/2021
 */
public class Manager extends JavaPlugin {

    private static Manager instance;
    @Getter
    private final MySQL sql =
            new MySQL("host", "database", "username", "password", 1);

    @Override
    public void onEnable() {
        instance = this;

        sql.connect();
        setUpTables();
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        sql.disconnect();
    }

    protected void setUpTables() {
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS ocelots " +
                "(killerName TEXT, ocelotName TEXT, date TEXT) DEFAULT CHARSET=utf8");
    }

    public static Manager getInstance() {
        return instance;
    }
}
