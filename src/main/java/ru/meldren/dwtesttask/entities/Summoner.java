package ru.meldren.dwtesttask.entities;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.meldren.dwtesttask.Manager;
import ru.meldren.dwtesttask.utils.GlobalUtils;
import ru.meldren.dwtesttask.utils.Item;

/**
 * Created by Meldren on 21/07/2021
 */
public class Summoner extends DWBoss {

    public Summoner(Location loc, String name, int cooldownInSecs, double health, double damage) {
        super(EntityTypes.ZOMBIE, loc, name, cooldownInSecs, health, damage);

        this.summonCreatures();
        this.putOnArmor();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(
                this, EntityHuman.class, true));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1, false));
    }

    private void summonCreatures() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dead) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < GlobalUtils.randomInt(3) + 1; ++i)
                    new SummonerCreature(Summoner.this.getBukkitEntity().getLocation());
            }
        }.runTaskTimer(Manager.getInstance(), 20 * 60, 20 * 60);
    }

    private void putOnArmor() {
        this.getEquipment().setItemInMainHand(new Item(Material.STONE_SWORD)
                .enchantments(Enchantment.DAMAGE_ALL, 3));
        this.getEquipment().setHelmet(new Item(Material.LEATHER_HELMET)
                .enchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 3));
        this.getEquipment().setChestplate(new Item(Material.LEATHER_CHESTPLATE)
                .enchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 3));
        this.getEquipment().setLeggings(new Item(Material.LEATHER_LEGGINGS)
                .enchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 3));
        this.getEquipment().setBoots(new Item(Material.LEATHER_BOOTS)
                .enchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 3));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!dead) getEquipment().clear();
            }
        }.runTaskLater(Manager.getInstance(), 20 * 30);
    }

    private class SummonerCreature extends EntityZombie {

        public SummonerCreature(Location loc) {
            super(Summoner.this.world);

            this.setBaby(true);
            this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

            world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }

        @Override
        protected void initPathfinder() {
            this.goalSelector.a(0, new PathfinderGoalFloat(this));
            this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
            this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8));
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(
                    this, EntityHuman.class, true));
            this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1, false));
        }
    }

}
