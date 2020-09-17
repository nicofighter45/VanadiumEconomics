package fr.nicofighter45.survival;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nicofighter45.economy.Economy;
import fr.nicofighter45.economy.Price;
import fr.nicofighter45.entity.Boss;
import fr.nicofighter45.entity.TeamFight;
import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.scoreboard.ScoreboardCustom;
import fr.nicofighter45.scoreboard.ScoreboardRun;
import fr.nicofighter45.scoreboard.team;


public class Main extends JavaPlugin{
	
	public static Main main;
	public Location spawn;
	public Economy eco;
	public Price price;
	public Boss boss;
	public World world;
	public File file;
	public FileConfiguration config;
	public team team;
	public ArrayList<String> configplayer = new ArrayList<>();
	public HashMap<String, Location> deadplayer = new HashMap<>();
	public HashMap<String, Double> money = new HashMap<>();
	public HashMap<String, Location> base = new HashMap<>();
	public HashMap<String, Integer> hearts = new HashMap<>();
	public HashMap<String, Metier> metier = new HashMap<>();
	public HashMap<String, State> state = new HashMap<>();
	public HashMap<Player, ScoreboardCustom> sc= new HashMap<>();
	public HashMap<String, TeamFight> teamFight = new HashMap<>();
	public HashMap<TeamFight, State> stateFight = new HashMap<>();
	
	@Override
	public void onEnable() {
		main = this;
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		file = new File(getDataFolder(), "config.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				config = YamlConfiguration.loadConfiguration(file);
				config.set("spawn", "-1270,64,-120");
				config.set("item.cobblestone.money", 1.0D);
				config.set("item.cobblestone.category", 0);
				config.set("item.ender_pearl.money", 100.0D);
				config.set("item.ender_pearl.category", 2);
				config.set("item.ender_pearl.boss", "0,1,2,3,4,5");
				config.set("player.nicofighter45.money", 0.0D);
				config.set("player.nicofighter45.base", 0);
				config.set("player.nicofighter45.heart", 20);
				config.set("player.nicofighter45.metier", 0);
				config.set("player.nicofighter45.state", 0);
				config.set("boss.0", "-195,64,-199");
				config.set("boss.1", "-195,64,-199");
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		world = Bukkit.getWorld("world");
		eco = new Economy(config);
		price = new Price(config);
		boss = new Boss(this, config);
		team = new team(this);
		String location = config.getString("spawn");
		assert location != null;
		String[] locationdecoup = location.split(",");
		spawn = new Location(world, Integer.parseInt(locationdecoup[0]), Integer.parseInt(locationdecoup[1]), Integer.parseInt(locationdecoup[2]));
		for (String s1 : Arrays.asList("spawn", "setspawn", "pay", "base", "setbase", "money", "eco", "info", "set", "test", "setspawnboss", "bossitemblock")) {
			Objects.requireNonNull(getCommand(s1)).setExecutor(new Command(this));
		}
		Bukkit.getPluginManager().registerEvents(new Listenner(this), this);
		new ScoreboardRun(this).runTaskTimer(this, 0 , 20);
		newcraft();
		System.out.println("Le plugin VanadiumEconomy est lance. Economy du server operationnel");
	}
	
	@Override
	public void onDisable() {
		config.set("spawn", (int)spawn.getX() + "," + (int)spawn.getY() + "," + (int)spawn.getZ());
		for(String p : configplayer) {
			 config.set("player." + p + ".money", money.get(p));
			 if(base.containsKey(p)) {
				 config.set("player." + p + ".base", (int)base.get(p).getX() + "," + (int)base.get(p).getY() + "," + (int)base.get(p).getZ());
			 }else {
				 config.set("player." + p + ".base", 0);
			 }
			 config.set("player." + p + ".heart", hearts.get(p));
			 if(metier.get(p) != null) {
				 config.set("player." + p + ".metier", metier.get(p).getValue());
			 }else {
				 config.set("player." + p + ".metier", 0);
			 }
			 config.set("player." + p + ".state", state.get(p).getValue());
		}
		for(Material m : price.materialbuy) {
			config.set("item." + m.toString().toLowerCase() + ".money", price.getItemPrice(m));
			config.set("item." + m.toString().toLowerCase() + ".category", price.getItemcategory(m).getValue());
		}
		for(Entry<State, Location> entry : boss.bossLoc.entrySet()) {
			config.set("boss." + entry.getKey().getValue(), entry.getValue().getX() + "," + entry.getValue().getY() + "," + entry.getValue().getZ());
		}
		for(Entry<Material, List<State>> entry : boss.itemBlock.entrySet()) {
			StringBuilder states = new StringBuilder();
			for(State s : entry.getValue()) {
				if(s != null) {
					states.append(s.getValue()).append(",");
				}
			}
			config.set("item." + entry.getKey().toString().toLowerCase() + ".boss", states.toString());
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Le plugin Vanadium Economy est eteint. Economy du server down");
	}
	
	public void addPlayer(String name) {
		if(!money.containsKey(name)) {
			money.remove(name);
		}
		money.put(name, 0.0D);
		if(!hearts.containsKey(name)) {
			hearts.remove(name);
		}
		hearts.put(name, 20);
		if(!metier.containsKey(name)) {
			metier.remove(name);
		}
		metier.put(name, null);
		state.put(name, State.P);
	}

	public void reload() {
		configplayer.clear();
		money.clear();
		base.clear();
		hearts.clear();
		metier.clear();
		state.clear();
		eco = new Economy(config);
	}
	
	public void newcraft() {
		ItemStack extraheart = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
		ItemMeta extraheratmeta = extraheart.getItemMeta();
		extraheratmeta.setDisplayName("§dExtra Heart");
		extraheart.setItemMeta(extraheratmeta);
		ShapedRecipe recipeheart = new ShapedRecipe(new NamespacedKey(this, "extra_heart"), extraheart);
		recipeheart.shape(" E ", "EDE", " E ");
		recipeheart.setIngredient('E', Material.EMERALD_ORE);
		recipeheart.setIngredient('D', Material.DIAMOND_BLOCK);
		Bukkit.addRecipe(recipeheart);
		
		ItemStack starhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		ItemMeta shmeta = starhelmet.getItemMeta();
		shmeta.setDisplayName("§dStar Helmet");
		((LeatherArmorMeta) shmeta).setColor(Color.fromRGB(255,255,255));
		shmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("nh-armor", 6, Operation.ADD_NUMBER));
		shmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		shmeta.setUnbreakable(true);
		shmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		starhelmet.setItemMeta(shmeta);
		ShapedRecipe recipenh = new ShapedRecipe(new NamespacedKey(this, "starhelmet"), starhelmet);
		recipenh.shape("DND", "D D", "   ");
		recipenh.setIngredient('N', Material.NETHER_STAR);
		recipenh.setIngredient('D', Material.DIAMOND_BLOCK);
		Bukkit.addRecipe(recipenh);
		
		ItemStack starleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		ItemMeta slmeta = starleggings.getItemMeta();
		slmeta.setDisplayName("§dStar Leggings");
		((LeatherArmorMeta) slmeta).setColor(Color.fromRGB(255,255,255));
		slmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("nl-armor", 8, Operation.ADD_NUMBER));
		slmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		slmeta.setUnbreakable(true);
		slmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		starleggings.setItemMeta(slmeta);
		ShapedRecipe recipenl = new ShapedRecipe(new NamespacedKey(this, "starleggings"), starleggings);
		recipenl.shape("DND", "D D", "D D");
		recipenl.setIngredient('N', Material.NETHER_STAR);
		recipenl.setIngredient('D', Material.DIAMOND_BLOCK);
		Bukkit.addRecipe(recipenl);
		
		ItemStack starboots = new ItemStack(Material.LEATHER_BOOTS, 1);
		ItemMeta sbmeta = starboots.getItemMeta();
		sbmeta.setDisplayName("§dStar Boots");
		((LeatherArmorMeta) sbmeta).setColor(Color.fromRGB(255,255,255));
		sbmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("nb-armor", 6, Operation.ADD_NUMBER));
		sbmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		sbmeta.setUnbreakable(true);
		sbmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		starboots.setItemMeta(sbmeta);
		ShapedRecipe recipenb = new ShapedRecipe(new NamespacedKey(this, "starboots"), starboots);
		recipenb.shape("D D", "D D", "   ");
		recipenb.setIngredient('D', Material.DIAMOND_BLOCK);
		Bukkit.addRecipe(recipenb);
		
		ItemStack fortune = new ItemStack(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta fortunemeta = (EnchantmentStorageMeta)fortune.getItemMeta();
		fortunemeta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
		fortunemeta.setDisplayName("§eFortune Book");
		fortune.setItemMeta(fortunemeta);
		ShapedRecipe recipefort = new ShapedRecipe(new NamespacedKey(this, "fortunebook"), fortune);
		recipefort.shape(" G ", "GBG", " G ");
		recipefort.setIngredient('G', Material.GOLD_BLOCK);
		recipefort.setIngredient('B', Material.BOOK);
		Bukkit.addRecipe(recipefort);
		
		ItemStack looting = new ItemStack(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta lootingmeta = (EnchantmentStorageMeta)looting.getItemMeta();
		lootingmeta.addStoredEnchant(Enchantment.LOOT_BONUS_MOBS, 3, true);
		lootingmeta.setDisplayName("§eLooting Book");
		looting.setItemMeta(lootingmeta);
		ShapedRecipe recipeloot = new ShapedRecipe(new NamespacedKey(this, "lootingbook"), looting);
		recipeloot.shape(" E ", "RBG", " H ");
		recipeloot.setIngredient('E', Material.ENDER_PEARL);
		recipeloot.setIngredient('R', Material.BLAZE_ROD);
		recipeloot.setIngredient('B', Material.BOOK);
		recipeloot.setIngredient('G', Material.GHAST_TEAR);
		recipeloot.setIngredient('H', Material.HONEYCOMB);
		Bukkit.addRecipe(recipeloot);
		
		ItemStack silk_touch = new ItemStack(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta silk_touchmeta = (EnchantmentStorageMeta)silk_touch.getItemMeta();
		silk_touchmeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, true);
		silk_touchmeta.setDisplayName("§eSilk_Touch Book");
		silk_touch.setItemMeta(silk_touchmeta);
		ShapedRecipe recipelootsilk_touch = new ShapedRecipe(new NamespacedKey(this, "silktouchbook"), silk_touch);
		recipelootsilk_touch.shape(" O ", "JBS", " D ");
		recipelootsilk_touch.setIngredient('O', Material.OAK_LOG);
		recipelootsilk_touch.setIngredient('J', Material.JUNGLE_LOG);
		recipelootsilk_touch.setIngredient('B', Material.BOOK);
		recipelootsilk_touch.setIngredient('S', Material.SPRUCE_LOG);
		recipelootsilk_touch.setIngredient('D', Material.DARK_OAK_LOG);
		Bukkit.addRecipe(recipelootsilk_touch);
		
		ItemStack autoplanter = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta autoplantermeta = autoplanter.getItemMeta();
		autoplantermeta.setDisplayName("§eAutoplanter");
		autoplanter.setItemMeta(autoplantermeta);
		ShapedRecipe recipeautoplanter = new ShapedRecipe(new NamespacedKey(this, "autoplanter"), autoplanter);
		recipeautoplanter.shape(" PD", " SM", "S  ");
		recipeautoplanter.setIngredient('S', Material.STICK);
		recipeautoplanter.setIngredient('D', Material.DIAMOND_BLOCK);
		recipeautoplanter.setIngredient('P', Material.PUMPKIN);
		recipeautoplanter.setIngredient('M', Material.MELON);
		Bukkit.addRecipe(recipeautoplanter);
		
		ItemStack emeraldhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		ItemMeta ehmeta = emeraldhelmet.getItemMeta();
		ehmeta.setDisplayName("§2Emerald Helmet");
		((LeatherArmorMeta) ehmeta).setColor(Color.fromRGB(32,213,68));
		ehmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("eh-armor", 4, Operation.ADD_NUMBER));
		ehmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ehmeta.setUnbreakable(true);
		ehmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		emeraldhelmet.setItemMeta(ehmeta);
		ShapedRecipe recipeeh = new ShapedRecipe(new NamespacedKey(this, "emeraldhelmet"), emeraldhelmet);
		recipeeh.shape("EEE", "E E", "   ");
		recipeeh.setIngredient('E', Material.EMERALD_BLOCK);
		Bukkit.addRecipe(recipeeh);
		
		ItemStack emeraldchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		ItemMeta ecmeta = emeraldchestplate.getItemMeta();
		ecmeta.setDisplayName("§2Emerald Chestplate");
		((LeatherArmorMeta) ecmeta).setColor(Color.fromRGB(32,213,68));
		ecmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("ec-armor", 5, Operation.ADD_NUMBER));
		ecmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ecmeta.setUnbreakable(true);
		ecmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		emeraldchestplate.setItemMeta(ecmeta);
		ShapedRecipe recipeec = new ShapedRecipe(new NamespacedKey(this, "emeraldchestplate"), emeraldchestplate);
		recipeec.shape("E E", "EEE", "EEE");
		recipeec.setIngredient('E', Material.EMERALD_BLOCK);
		Bukkit.addRecipe(recipeec);
		
		ItemStack emeraldleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		ItemMeta elmeta = emeraldleggings.getItemMeta();
		elmeta.setDisplayName("§2Emerald Leggings");
		((LeatherArmorMeta) elmeta).setColor(Color.fromRGB(32,213,68));
		elmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("el-armor", 5, Operation.ADD_NUMBER));
		elmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		elmeta.setUnbreakable(true);
		elmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		emeraldleggings.setItemMeta(elmeta);
		ShapedRecipe recipeel = new ShapedRecipe(new NamespacedKey(this, "emeraldleggings"), emeraldleggings);
		recipeel.shape("EEE", "E E", "E E");
		recipeel.setIngredient('E', Material.EMERALD_BLOCK);
		Bukkit.addRecipe(recipeel);
		
		ItemStack emeraldboots = new ItemStack(Material.LEATHER_BOOTS, 1);
		ItemMeta ebmeta = emeraldboots.getItemMeta();
		ebmeta.setDisplayName("§2Emerald Boots");
		((LeatherArmorMeta) ebmeta).setColor(Color.fromRGB(32,213,68));
		ebmeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("eb-armor", 4, Operation.ADD_NUMBER));
		ebmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ebmeta.setUnbreakable(true);
		ebmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		emeraldboots.setItemMeta(ebmeta);
		ShapedRecipe recipeeb = new ShapedRecipe(new NamespacedKey(this, "emeraldboots"), emeraldboots);
		recipeeb.shape("E E", "E E", "   ");
		recipeeb.setIngredient('E', Material.EMERALD_BLOCK);
		Bukkit.addRecipe(recipeeb);
	}
	
	public void addWhPlayer(String name) {
		OfflinePlayer player = getServer().getPlayer(name);
		if(!getServer().getWhitelistedPlayers().contains(player)) {
			getServer().getWhitelistedPlayers().add(player);
		}
	}
	
}