package ru.meldren.dwtesttask;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.meldren.dwtesttask.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by Meldren on 22/06/2021
 */
public class FighterOcelot extends EntityOcelot {

    public FighterOcelot(Location loc) {
        super(((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());

        this.setCustomName(new ChatComponentText(StringUtils.getRandomString(5)));
        this.setCustomNameVisible(true);
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        this.targetSelector.a(2, new PathfinderGoalTargetNearestPlayer(this));

        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    /*
    Занесем необходимую информацию об убийстве оцелота в базу данных
     */
    public void saveOcelotDeathInfo(String killerName, String ocelotName, long time) {
        Manager.getInstance().getSql().executeUpdate(String.format("INSERT INTO ocelots " +
                        "(killerName, ocelotName, date) VALUES (%s, %s, %s)",
                killerName, ocelotName, new SimpleDateFormat().format(time)));
    }

    /*
    Оцелотик более не будет убегать от игрока
     */
    @Override
    protected void dz() {

    }
}
