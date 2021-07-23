package ru.meldren.dwtesttask.utils;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Meldren on 22/07/2021
 */
public abstract class PersonalHolo {

    @Getter
    private static final Map<Integer, PersonalHolo> holograms = new HashMap<>();
    private final EntityArmorStand as;

    public PersonalHolo(Location location) {
        as = new EntityArmorStand(EntityTypes.ARMOR_STAND,
                ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle());
        as.setCustomNameVisible(true);
        as.setNoGravity(true);
        as.setMarker(true);
        as.setInvisible(true);
        as.setInvulnerable(true);
        as.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

        as.getWorld().addEntity(as, CreatureSpawnEvent.SpawnReason.CUSTOM);
        holograms.put(as.getId(), this);
    }

    public PersonalHolo(PersonalHolo previous) {
        this(previous.as.getBukkitEntity().getLocation().clone().add(0, 0.25, 0));
    }

    public void send(Player player) {
        if (!player.hasLineOfSight(as.getBukkitEntity()))
            return;

        WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata();
        wrapper.setEntityID(as.getId());
        wrapper.setMetadata(WrappedDataWatcher.getEntityWatcher(as.getBukkitEntity()).getWatchableObjects());
        wrapper.sendPacket(player);
    }

    public void remove() {
        holograms.remove(as.getId());
        as.getBukkitEntity().remove();
    }

    public abstract String getText(Player player);

    public static void clearHolograms() {
        holograms.values().forEach(PersonalHolo::remove);
        Bukkit.getWorlds().forEach(world ->
                world.getEntitiesByClass(ArmorStand.class).stream()
                        .filter(entity -> entity.getCustomName() != null).forEach(Entity::remove));
    }

}
