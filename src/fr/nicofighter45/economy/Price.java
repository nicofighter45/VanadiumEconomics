package fr.nicofighter45.economy;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import fr.nicofighter45.enumeration.Category;

public class Price {
	
	public ArrayList<Material> materialbuy = new ArrayList<>();
	private HashMap<Material, Double> itemprice = new HashMap<>();
	private HashMap<Material, Category> itemcategory = new HashMap<>();
	private FileConfiguration config;
	
	public Price(FileConfiguration conf) {
		this.config = conf;
		for(String str : config.getConfigurationSection("item").getKeys(false)) {
            Material m = Material.getMaterial(str.toUpperCase());
            if(m != null) {
            	materialbuy.add(m);
            	itemprice.put(m, config.getDouble("item." + str + ".money"));
            	itemcategory.put(m, Category.getValuefromInt(config.getInt("item." + str + ".category")));
            }
        }
	}
	
	public Double getItemPrice(Material t) {
		return this.itemprice.get(t);
	}
	
	public void setItemPrice(Material t, double d) {
		if(this.itemprice.containsKey(t)) {
			this.itemprice.remove(t);
		}
		this.itemprice.put(t, d);
		if(this.materialbuy.contains(t)) {
			this.materialbuy.remove(t);
		}
		this.materialbuy.add(t);
	}
	
	public void removeItem(Material t) {
		if(this.itemprice.containsKey(t)) {
			this.itemprice.remove(t);
		}
		if(this.materialbuy.contains(t)) {
			this.materialbuy.remove(t);
		}
	}

	public Category getItemcategory(Material type) {
		return itemcategory.get(type);
	}

	public void setItemcategory(Material type, Category category) {
		if(itemcategory.containsKey(type)) {
			itemcategory.remove(type);
		}
		itemcategory.put(type, category);
	}
}