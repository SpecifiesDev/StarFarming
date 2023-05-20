package me.csdad.StarFarming.Utility;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.csdad.StarFarming.Main;

/**
 * Utility class designed to make creating mass amounts of item stacks much simpler
 * @author speci
 *
 */
public class ItemFactory {
	
	// local instance of the constructing stack
    private ItemStack stack;
    
    // instance of our main class
    private Main plugin;

    public ItemFactory(Material m, int amount) {
        this.stack = new ItemStack(m, amount);
        this.plugin = Main.getInstance();
    }

    /**
     * Method to return constructed item
     * @return stack
     */
    public ItemStack getItem() {
        return this.stack;
    }

    /**
     * Method to set the constructed items material
     * @param mat Material to set
     */
    public void setType(Material mat) {
        this.stack.setType(mat);
    }

    
    /**
     * Method to change constructed items display name.
     * Automatically colorizes using util methods
     * @param displayName New displayname
     */
    public void setDisplayName(String displayName) {
        ItemMeta im = getMeta();
        im.setDisplayName(this.plugin.color(displayName));
        this.stack.setItemMeta(im);
    }

    /**
     * Method to add lore to the constructed item
     * String format is line1~line2~line3 so on
     * @param additionString The string to add
     */
    public void addToLore(String additionString) {
        ItemMeta im = getMeta();
        ArrayList < String > construct = new ArrayList < > (im.getLore());
        for (String s: additionString.split("~"))
            construct.add(this.plugin.color(s));
        im.setLore(construct);
    }

    /**
     * Method to set the lore of the constructed item
     * String format is line1~line2~line3 so on
     * @param loreString The string to set
     */
    public void setLore(String loreString) {
        ArrayList < String > array = new ArrayList < > ();
        String[] lines = loreString.split("~");
        for (String s: lines)
            array.add(this.plugin.color(s));
        ItemMeta im = getMeta();
        im.setLore(array);
        this.stack.setItemMeta(im);
    }

    /**
     * Method to add an item flag to the constructed item
     * @param flag The flag to set
     */
    public void addItemFlag(ItemFlag flag) {
        ItemMeta im = getMeta();
        im.addItemFlags(new ItemFlag[] {
            flag
        });
        this.stack.setItemMeta(im);
    }

    /**
     * Method to "flush" the constructed item. Basically created a new blank item
     * @param mat the new material
     * @param amount the amount in the new stack
     */
    public void flush(Material mat, int amount) {
        this.stack = new ItemStack(mat, amount);
    }

    /**
     * Private utility method to retrieve the items internal metadata
     * @return Metadata
     */
    private ItemMeta getMeta() {
        return this.stack.getItemMeta();
    }
    
}
