package ru.meldren.dwtesttask;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import ru.meldren.dwtesttask.utils.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Meldren on 23/07/2021
 */
@UtilityClass
public class Localization {

    private static final Map<String, String> localization = new HashMap<>();

    public static void init() {
        FileConfiguration localizationConfig = new Configuration("localization").get();
        localizationConfig.getKeys(false).forEach(s ->
                localization.put(s.toUpperCase(), localizationConfig.getString(s)));
    }

    public static String getLocalization(String id, String... args) {
        String result = localization.get(id.toUpperCase());

        for (int i = 0; i < args.length; i += 2) {
            result = result.replaceAll(args[i], args[i + 1]);
        }

        return ChatColor.translateAlternateColorCodes('&', result);
    }
}
