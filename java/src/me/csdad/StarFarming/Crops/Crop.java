package me.csdad.StarFarming.Crops;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.csdad.StarFarming.Main;

public class Crop {
	
	// instance of our main class
    private Main plugin;
    
    // name of the crop
    private String name;
    
    // name of the crop's seed
    private String seedName;
    
    // material of the crop, generally used for seeds
    private Material material;
    
    // time it takes for the crop to grow
    private int growthTime;
    
    // spread is a value in which we randomize harvest, creates an element of "profitability" for certain crops
    private int spread;
    
    // base is the base harvest value of a crop
    private int base;
    
    // material of the harvest
    private Material productMaterial;
    
    // name of the crop's produce
    private String productName;
    
    // end price of the crop
    private int productPrice;
    
    // xp value a player receives from harvesting the crop
    private int xp;
    
    // value determining the chance of dropping a crop's seed from grass.
    private double dropChance;
    
    // an arraylist containing growable seasons for the crop
    private ArrayList < String > seasons;

    public Crop(String name, String seedName, String material, int growthTime, int spread, int base, ArrayList < String > seasons, Material productMaterial, String productName, int productPrice, int xp, double dropChance) {
        this.name = name;
        this.material = Material.getMaterial(material);
        this.growthTime = growthTime;
        this.spread = spread;
        this.base = base;
        this.seedName = seedName;
        this.seasons = seasons;
        this.productMaterial = productMaterial;
        this.productName = productName;
        this.productPrice = productPrice;
        this.xp = xp;
        this.dropChance = dropChance;
        this.plugin = Main.getInstance();
    }

    public String getName() {
        return this.name;
    }

    public String getSeedName() {
        return this.seedName;
    }

    public Material getSeedMaterial() {
        return this.material;
    }

    public ItemStack getSeed() {
        ItemStack seed = new ItemStack(this.material, 1);
        ItemMeta sm = seed.getItemMeta();
        sm.setDisplayName(this.plugin.color(this.name));
        seed.setItemMeta(sm);
        return seed;
    }

    public int getSellPrice() {
        return this.productPrice;
    }

    public int getXp() {
        return this.xp;
    }

    public ItemStack getProduct(int amount) {
        ItemStack product = new ItemStack(this.productMaterial, amount);
        ItemMeta pm = product.getItemMeta();
        pm.setDisplayName(this.plugin.color(this.productName));
        ArrayList < String > construct = new ArrayList < > ();
        construct.add(this.plugin.color("&7Value&8: &7" + this.productPrice));
        pm.setLore(construct);
        product.setItemMeta(pm);
        return product;
    }

    public int getGrowthTime() {
        return this.growthTime;
    }
    
    public double getDropChance() {
    	return this.dropChance;
    }

    public int getSpread() {
        return this.spread;
    }

    public int getBase() {
        return this.base;
    }

    public ArrayList < String > getGrowableSeasons() {
        return this.seasons;
    }
}