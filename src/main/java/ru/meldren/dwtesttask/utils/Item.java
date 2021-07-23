package ru.meldren.dwtesttask.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Meldren on 21/07/2021
 */
public class Item extends ItemStack {

    public Item(Material material) {
        super(material);
    }

    public Item material(Material material) {
        this.setType(material);
        return this;
    }

    public Item name(String name) {
        ItemMeta m = this.getItemMeta();
        if (m != null) {
            m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            this.setItemMeta(m);
        }
        return this;
    }

    public Item lore(List<String> lore) {
        ItemMeta m = this.getItemMeta();
        if (m != null) {
            m.setLore(lore.stream().map(s ->
                    ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
            this.setItemMeta(m);
        }
        return this;
    }

    public Item lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public Item unbreakable(boolean unbreakable) {
        ItemMeta m = this.getItemMeta();
        if (m != null) {
            m.setUnbreakable(unbreakable);
            this.setItemMeta(m);
        }
        return this;
    }

    public Item amount(int amount) {
        this.setAmount((byte) amount);
        return this;
    }

    public Item durability(int durability) {
        ItemMeta m = this.getItemMeta();
        if (m != null) {
            ((Damageable)m).setDamage(durability);
            this.setItemMeta(m);
        }
        return this;
    }

    public Item enchantments(Map<Enchantment, Integer> enchantments) {
        this.addUnsafeEnchantments(enchantments);
        return this;
    }

    public Item enchantments(Enchantment enchantment, int lvl) {
        this.addUnsafeEnchantment(enchantment, lvl);
        return this;
    }

    public Item itemFlags(ItemFlag... flags) {
        ItemMeta m = this.getItemMeta();
        if (m != null) {
            m.addItemFlags(flags.length == 0 ? ItemFlag.values() : flags);
            this.setItemMeta(m);
        }
        return this;
    }
}

