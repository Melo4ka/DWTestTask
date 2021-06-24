package ru.meldren.dwtesttask;

import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Meldren on 22/06/2021
 */
public class Listeners implements org.bukkit.event.Listener {

    private final Set<Integer> leathers = new HashSet<>();

    public Listeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(Manager.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Server.SPAWN_ENTITY) {
                    @Override
                    public void onPacketSending(PacketEvent e) {
                        WrapperPlayServerSpawnEntity packet =
                                new WrapperPlayServerSpawnEntity(e.getPacket());

                        if (!leathers.contains(packet.getEntityID()))
                            return;

                        final Item item = (Item) packet.getEntity(e);
                        item.setCustomName(e.getPlayer().getName());
                    }
                });
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        leathers.remove(e.getEntity().getEntityId());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        final LivingEntity entity = e.getEntity();

        if (entity.getKiller() == null)
            return;

        if (entity instanceof Zombie) {
            new FighterOcelot(entity.getLocation());
            return;
        }

        if (!(((CraftEntity) entity).getHandle() instanceof FighterOcelot))
            return;
        e.getDrops().clear();

        final Location loc = entity.getLocation();
        final EntityItem item = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(),
                loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(new ItemStack(Material.LEATHER)));
        item.pickupDelay = 10;
        item.setCustomNameVisible(true);
        leathers.add(item.getId());
        ((CraftWorld) loc.getWorld()).getHandle()
                .addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);

        ((FighterOcelot) ((CraftEntity) entity).getHandle()).saveOcelotDeathInfo(
                entity.getKiller().getName(), entity.getCustomName(),
                System.currentTimeMillis()
        );
    }

}
