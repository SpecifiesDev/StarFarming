package me.csdad.starfarming.DataStructures;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.csdad.starfarming.Core;

public class StarCrop {
	
	// instance of our main class
	private Core plugin;
	
	// pertinent information to a SC object
	private String name; // name of the crop
	private String seedName; // name of the seed
	private Material material; // material of the crop
	
	private int growthTime; // time of crop growth
	
	private int spread; // create a spread for the amount of crop on harvest
	private int base; // base of the amount per harvest of a crop
	
	private Material productMaterial; // material of the harvest item
	private String productName; // name of the harvest item
	
	private int price; // price that the crop will sell per 1 in the shop
	
	private int xp; // experience player will receive from harvesting one crop

	private double dropChance; // value that determines what the dropchance for a crop is
	
	private ArrayList<String> seasons; // arraylist containing growable seasons for the crop
	
	public StarCrop(String name, String seedName, Material material, int growthTime, int spread, int base, Material productMaterial, String productName, int price, int xp, double dropChance, ArrayList<String> seasons) {
		
		this.name = name;
		this.seedName = seedName;
		this.material = material;
		
		this.growthTime = growthTime;
		
		this.spread = spread;
		this.base = base;
		
		this.productMaterial = productMaterial;
		this.productName = productName;
		
		this.price = price;
		
		this.xp = xp;
		
		this.dropChance = dropChance;
		
		this.seasons = seasons;
		
		this.plugin = Core.getInstance();
		
		
	}
	
	// we only need getters as this is static information that is loaded on startup of the server. may add the ability to dynamically edit this, but for now its static
	
	/**
	 * Method to get the name of a crop
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Method to get the seed name of a crop
	 * @return seedName
	 */
	public String getSeedName() {
		return this.seedName;
	}
	
	/**
	 * Method to get the seed material of a crop
	 * @return material
	 */
	public Material getSeedMaterial() {
		return this.material;
	}
	
	/**
	 * Method to take all of the pertinent config information and construct a seed itemstack
	 * @return new ItemStack
	 */
	public ItemStack getSeed() {
		ItemStack seed = new ItemStack(this.material, 1);
		
		ItemMeta sm = seed.getItemMeta();
		sm.setDisplayName(this.plugin.color(this.name));
		
		seed.setItemMeta(sm);
		
		return seed;
	}
	
	/**
	 * Method to retrieve the sell price of the final product
	 * @return price
	 */
	public int getSellPrice() {
		return this.price;
	}
	
	/**
	 * Method to retrieve the experience value
	 * @return xp
	 */
	public int getXp() {
		return this.xp;
	}
	
	/**
	 * Method to retrieve the product item of the crop
	 * @param amount The amount to set the itemstacks internal value to
	 * @return new ItemStack
	 */
	public ItemStack getProduct(int amount) {
		
		ItemStack product = new ItemStack(this.productMaterial, amount);
		ItemMeta pm = product.getItemMeta();
		
		pm.setDisplayName(this.plugin.color(productName));
	
		pm.setLore(Arrays.asList(new String[] {this.plugin.color("&7Value&8: &7" + this.price)}));
		
		product.setItemMeta(pm);
		
		return product;
	}
	
	/**
	 * Method to retrieve the growth time of the crop
	 * @return growthTime
	 */
	public int getGrowthTime() {
		return this.growthTime;
	}
	
	/**
	 * Method to retrieve the drop chance of the crop's seed
	 * @return dropChance
	 */
	public double getDropChance() {
		return this.dropChance;
	}
	
	/**
	 * Method to get the spread value for the crop's drop
	 * @return spread
	 */
	public int getSpread() {
		return this.spread;
	}
	
	/**
	 * Method to get the base value for the crop's drop
	 * @return base
	 */
	public int getBase() {
		return this.base;
	}
	
	/**
	 * Method to get all seasons a crop is permitted to grow in.
	 * @return seasons
	 */
	public ArrayList<String> getGrowableSeasons() {
		return this.seasons;
	}
	

}
