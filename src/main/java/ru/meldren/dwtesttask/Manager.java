package ru.meldren.dwtesttask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;
import ru.meldren.dwtesttask.commands.SpawnBossCommand;
import ru.meldren.dwtesttask.entities.BossManager;
import ru.meldren.dwtesttask.utils.PersonalHolo;
import ru.meldren.dwtesttask.utils.sql.SQLite;

/**
 * Created by Meldren on 22/06/2021
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager extends JavaPlugin {

    @Getter
    static Manager instance;
    @Getter
    final SQLite sql = new SQLite("data.sqlite");

    @Override
    public void onEnable() {
        instance = this;

        sql.connect();
        setUpTables();
        Localization.init();
        registerCommands();
        BossManager.clearEntities();
        PersonalHolo.clearHolograms();
        BossManager.init();
        new Listeners();

        System.out.println("DWTestTask has already enabled! Have a nice game :3");
    }

    @Override
    public void onDisable() {
        sql.disconnect();

        System.out.println("DWTestTask has disabled! Bye :c");
    }

    private static void registerCommands() {
        new SpawnBossCommand();
    }

    private void setUpTables() {
        sql.executeSyncQuery("CREATE TABLE IF NOT EXISTS bosses " +
                "(id TEXT, time TEXT, killers TEXT);");
    }

}
