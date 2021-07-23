package ru.meldren.dwtesttask.entities;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;
import ru.meldren.dwtesttask.Localization;
import ru.meldren.dwtesttask.Manager;
import ru.meldren.dwtesttask.utils.GlobalUtils;
import ru.meldren.dwtesttask.utils.PersonalHolo;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Meldren on 21/07/2021
 */
public class DWBoss extends EntityMonster {

    @Getter Location spawnLocation;
    String id;
    @Getter String name;
    @Getter int cooldown;
    double health, damage;
    @Getter Map<String, Double> damagers;

    public DWBoss(EntityTypes<? extends EntityMonster> types, Location loc, String name, int cooldownInSecs, double health, double damage) {
        super(types, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.id = this.getClass().getSimpleName();
        this.spawnLocation = loc;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.cooldown = cooldownInSecs;
        this.health = health;
        this.damage = damage;
        this.damagers = new HashMap<>();

        this.setCustomName(new ChatComponentText(this.name));
        this.setCustomNameVisible(true);
        this.setPersistent();

        this.setAttribute(Attribute.GENERIC_MAX_HEALTH, health);
        this.setHealth((float) health);
        this.setAttribute(Attribute.GENERIC_ATTACK_DAMAGE, damage);

        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        this.getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    protected void initPathfinder() {
    }

    protected void onAttack(LivingEntity entity) {
    }

    protected void onDamage(Player damager) {
    }

    protected void onDeath() {
    }

    public LivingEntity getLivingEntity() {
        return (LivingEntity) this.getBukkitEntity();
    }

    public EntityEquipment getEquipment() {
        return getLivingEntity().getEquipment();
    }

    public void setAttribute(Attribute attribute, double value) {
        Objects.requireNonNull(getLivingEntity().getAttribute(attribute)).setBaseValue(value);
    }

    @Override
    public final boolean attackEntity(Entity entity) {
        CraftEntity e = entity.getBukkitEntity();
        if (e instanceof LivingEntity)
            this.onAttack((LivingEntity) e);

        return super.attackEntity(entity);
    }

    @Override
    public final boolean damageEntity(DamageSource damagesource, float f) {
        if (!super.damageEntity(damagesource, f))
            return false;

        if (damagesource.getEntity() != null &&
                damagesource.getEntity().getBukkitEntity() instanceof Player) {
            CraftEntity e = damagesource.getEntity().getBukkitEntity();
            String name = e.getName();

            if (damagers.containsKey(name)) {
                if (damagers.get(name) < getMaxHealth())
                    this.damagers.put(name, damagers.get(name) + f);
            }
            else {
                this.damagers.put(name, (double) f);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    ((Player) e).spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(Localization.getLocalization(
                                    "BOSS_HEALTH_INFO", "%health%", String.valueOf(Math.round(getHealth())),
                                    "%maxHealth%", String.valueOf(Math.round(getMaxHealth())))));
                }
            }.runTaskLater(Manager.getInstance(), 1);

            this.onDamage((Player) e);
        }

        return true;
    }

    @Override
    public final void die() {
        super.die();
        if (this.killer == null) return;

        List<String> top = new ArrayList<>(damagers.keySet());
        top.sort((o1, o2) -> {
            if (damagers.get(o1) > damagers.get(o2))
                return 1;
            else if (damagers.get(o1).equals(damagers.get(o2)))
                return 0;
            else
                return -1;
        });

        Bukkit.broadcastMessage(Localization.getLocalization("BOSS_DEATH", "%name%", this.name));
        Bukkit.broadcastMessage(Localization.getLocalization("TOP_BOSS_DAMAGERS_HEADER"));

        StringBuilder killers = new StringBuilder();
        killers.append("[");
        for (int i = 0; i < 3 && i < top.size(); ++i) {
            killers.append("{player: ")
                    .append(top.get(i))
                    .append(", damage: ")
                    .append(Math.round(damagers.get(top.get(i))))
                    .append("}");
            if (i != top.size() - 1)
                killers.append(", ");

            Bukkit.broadcastMessage(Localization.getLocalization("TOP_BOSS_DAMAGERS",
                    "%number%", String.valueOf(i + 1), "%name%", top.get(i), "%damage%",
                    String.valueOf(Math.round(damagers.get(top.get(i))))));
        }
        killers.append("]");
        Manager.getInstance().getSql().executeAsyncQuery(String.format("INSERT INTO bosses " +
                        "(id, time, killers) VALUES ('%s', '%s', '%s');",
                this.id, new SimpleDateFormat().format(System.currentTimeMillis()), killers.toString()));

        if (cooldown > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    BossManager.spawnBoss(id, spawnLocation, name, cooldown, health, damage);
                }
            }.runTaskLater(Manager.getInstance(), cooldown * 20);
        }

        this.spawnHolograms();

        this.onDeath();
    }

    private void spawnHolograms() {
        new BukkitRunnable() {

            int time = cooldown;
            final PersonalHolo down = new PersonalHolo(spawnLocation.clone().add(0, 2, 0)) {

                @Override
                public String getText(Player player) {
                    return Localization.getLocalization("BOSS_HOLOGRAM_COOLDOWN",
                            "%name%", name, "%time%", GlobalUtils.getRemainingTime(time--));
                }
            };
            final PersonalHolo up = new PersonalHolo(down) {
                @Override
                public String getText(Player player) {
                    return Localization.getLocalization("BOSS_HOLOGRAM_GREETINGS", "%name%", player.getName());
                }
            };

            @Override
            public void run() {
                if (time < 0) {
                    up.remove();
                    down.remove();
                    this.cancel();
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(down::send);
            }

        }.runTaskTimer(Manager.getInstance(), 0, 20);
    }
}
