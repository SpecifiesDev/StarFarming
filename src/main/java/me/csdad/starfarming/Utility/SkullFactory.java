package me.csdad.starfarming.Utility;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.csdad.starfarming.Core;

// refer to me.csdad.starfarming.Utility.ItemFactory for the javadocs on individual methods
public class SkullFactory {
	
	// instance of main class and the skull
    private ItemStack skull;
    private Core plugin;

    public SkullFactory(Player player) {
    	// init itemstack and plugin
        this.skull = new ItemStack(Material.PLAYER_HEAD, 1);
        this.plugin = Core.getInstance();
        
        // set the skull's owner to the inputted player
        this.setOwner(player);
    }



    public ItemStack getSkull() {
        return this.skull;
    }

    public void setOwner(Player player) {
        SkullMeta meta = this.getMeta();
        meta.setOwningPlayer(player);
        this.skull.setItemMeta(meta);
    }

    public void setDisplayName(String displayName) {
        SkullMeta meta = this.getMeta();
        meta.setDisplayName(this.plugin.color(displayName));
        this.skull.setItemMeta(meta);
    }

    public void addToLore(String additionString) {
        SkullMeta meta = this.getMeta();
        ArrayList <String> lore = meta.hasLore() ? new ArrayList <> (meta.getLore()) : new ArrayList <> ();
        for (String s: additionString.split("~")) {
            lore.add(this.plugin.color(s));
        }
        meta.setLore(lore);
        this.skull.setItemMeta(meta);
    }

    public void setLore(String loreString) {
        ArrayList < String > lore = new ArrayList < > ();
        for (String s: loreString.split("~")) {
            lore.add(this.plugin.color(s));
        }
        SkullMeta meta = getMeta();
        meta.setLore(lore);
        this.skull.setItemMeta(meta);
    }

    public void addItemFlag(ItemFlag flag) {
        SkullMeta meta = this.getMeta();
        meta.addItemFlags(flag);
        this.skull.setItemMeta(meta);
    }

    private SkullMeta getMeta() {
        return (SkullMeta) this.skull.getItemMeta();
    }
}