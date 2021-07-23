package ru.meldren.dwtesttask.entities;

import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PathfinderGoalCrossbowAttack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.meldren.dwtesttask.Manager;
import ru.meldren.dwtesttask.utils.Item;

/**
 * Created by Meldren on 21/07/2021
 */
public class Robber extends DWBoss implements ICrossbow {

    public Robber(Location loc, String name, int cooldownInSecs, double health, double damage) {
        super(EntityTypes.PILLAGER, loc, name, cooldownInSecs, health, damage);

        this.getEquipment().setItemInMainHand(new Item(Material.CROSSBOW)
                .enchantments(Enchantment.PIERCING, 4)
                .enchantments(Enchantment.MULTISHOT, 1));
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(
                this, EntityHuman.class, true));
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, 1, false));
        this.goalSelector.a(2, new PathfinderGoalCrossbowAttack<>(this, 1, 8));
    }

    @Override
    protected void onDamage(Player damager) {
        if (this.getHealth() <= this.getMaxHealth() * .5 &&
                this.getEquipment().getItemInMainHand().getType() == Material.CROSSBOW) {
            this.getEquipment().setItemInMainHand(new Item(Material.IRON_AXE));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (dead) {
                        this.cancel();
                        return;
                    }
                    LivingEntity le = getLivingEntity();
                    le.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
                    if (Robber.this.getGoalTarget() != null) {
                        Location targetLoc = getGoalTarget().getBukkitEntity().getLocation();
                        Vector leapVector = new Vector(targetLoc.getX() - locX(), 1.5, targetLoc.getZ() - locZ()).normalize();
                        le.setVelocity(leapVector);
                    }
                }
            }.runTaskTimer(Manager.getInstance(), 0, 20 * 60);
        }
    }

    @Override
    public void b(boolean flag) {
    }

    @Override
    public void a(EntityLiving entityLiving, ItemStack itemStack, IProjectile iProjectile, float v) {
        this.a(this, entityLiving, iProjectile, f, 1.6F);
    }

    @Override
    public void U_() {
        this.ticksFarFromPlayer = 0;
    }

    @Override
    public void a(EntityLiving entityLiving, float v) {
        this.b(this, 1.6F);
    }
}
