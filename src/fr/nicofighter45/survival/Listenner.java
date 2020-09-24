package fr.nicofighter45.survival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicofighter45.entity.TeamFight;
import fr.nicofighter45.entity.Zombie;
import fr.nicofighter45.enumeration.Category;
import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.scoreboard.ScoreboardCustom;

public class Listenner implements Listener {

	private Main instance;

	public Listenner(Main instance) {
		this.instance = instance;
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!instance.configplayer.contains(p.getName())) {
			instance.configplayer.add(p.getName());
			instance.addPlayer(p.getName());
		}
		instance.state.put(p.getName(), State.P);
		e.getPlayer().setCollidable(false);
		p.setHealthScale((double)instance.hearts.get(p.getName()));
		for(Player player : Bukkit.getOnlinePlayers()) {
			instance.team.teams(player);
		}
		e.setJoinMessage("§7(§9i§7) §f>> §f" + p.getName() + "§7 a rejoint");
		//a modifier (!)
		if(p.hasPlayedBefore()) {
			p.sendMessage("§7(§ci§7) §f>> §7Bienvenue sur le server §4survie §7de §6Vanadium §7! Fait /info pour obtenir plus d'informations sur le server !");
			Inventory i = Bukkit.createInventory(null, 27, "Choisi ton métier !");
			i.setItem(9, item(Material.WITHER_SKELETON_SKULL, "§9Combattant",Metier.COMBATTANT.getDesc()));
			i.setItem(11, item(Material.DIAMOND_ORE, "§eMineur",Metier.MINEUR.getDesc()));
			i.setItem(13, item(Material.OAK_SIGN, "§6Info", "§6Tous ses métiers ont des avantages en combat et de nouveaux crafts uniques."));
			i.setItem(15, item(Material.WHEAT, "§aAgriculteur",Metier.AGRICULTEUR.getDesc()));
			i.setItem(17, item(Material.OAK_WOOD, "§4Bucheron",Metier.BUCHERON.getDesc()));
			p.openInventory(i);
		}
		ScoreboardCustom board = new ScoreboardCustom(p);
		board.sendLine();
		board.setScoreboard();
	}
	
	@EventHandler
	public void playerportalevent(PlayerPortalEvent e) {
		Player p = e.getPlayer();
		State state = instance.state.get(p.getName());
		if(Objects.requireNonNull(e.getTo()).getBlock().getWorld().getEnvironment() == Environment.NETHER && (state == State.F || state == State.P || state == State.O)) {
			cancellingBiome(e, p);
		}else if(e.getTo().getBlock().getWorld().getEnvironment() == Environment.THE_END && (state != State.E && state != State.S)) {
			cancellingBiome(e, p);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void moovePlayer(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(instance.deadplayer.containsKey(p.getName())) {
			e.setCancelled(true);
		}
		Biome biome = Objects.requireNonNull(e.getTo()).getBlock().getBiome();
		Biome[] listbiomestone = {Biome.FOREST, Biome.PLAINS, Biome.SUNFLOWER_PLAINS, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS, Biome.TALL_BIRCH_FOREST, Biome.RIVER, Biome.BEACH, Biome.DARK_FOREST, Biome.DARK_FOREST_HILLS};
		Biome[] listbiomefer = {Biome.DESERT, Biome.DESERT_HILLS, Biome.DESERT_LAKES, Biome.ICE_SPIKES, Biome.FROZEN_RIVER, Biome.SWAMP, Biome.SWAMP_HILLS};
		Biome[] listbiomeor = {Biome.MOUNTAINS, Biome.GRAVELLY_MOUNTAINS, Biome.MODIFIED_GRAVELLY_MOUNTAINS, Biome.WOODED_MOUNTAINS};
		Biome[] listbiomenether = {Biome.NETHER_WASTES, Biome.JUNGLE, Biome.JUNGLE_EDGE, Biome.JUNGLE_HILLS, Biome.BAMBOO_JUNGLE, Biome.BAMBOO_JUNGLE_HILLS, Biome.MODIFIED_JUNGLE, Biome.MODIFIED_JUNGLE_EDGE};
		//riche      -> tous l'overworld
		//new-nether -> tous le nether
		Biome[] listbiomeend = {Biome.THE_END, Biome.THE_VOID};
		//superior   -> tous le jeu
		switch(instance.state.get(p.getName())) {
		case P:
			if(!biomes(listbiomestone).contains(biome)) {
				cancellingBiome(e, p);
			}
			break;
		case F:
			if(!biomes(listbiomestone).contains(biome) && !biomes(listbiomefer).contains(biome)) {
				cancellingBiome(e, p);
			}
			break;
		case O:
			if(!biomes(listbiomestone).contains(biome) && !biomes(listbiomefer).contains(biome) && !biomes(listbiomeor).contains(biome)) {
				cancellingBiome(e, p);
			}
			break;
		case N:
			if(!biomes(listbiomestone).contains(biome) && !biomes(listbiomefer).contains(biome) && !biomes(listbiomeor).contains(biome) && !biomes(listbiomenether).contains(biome)) {
				cancellingBiome(e, p);
			}
			break;
		case R:
			if(e.getTo().getBlock().getWorld().getEnvironment() == Environment.NETHER && biome != Biome.NETHER_WASTES) {
				cancellingBiome(e, p);
			}
			break;
		case E:
			if(e.getTo().getBlock().getWorld().getEnvironment() == Environment.THE_END && !biomes(listbiomeend).contains(biome)) {
				cancellingBiome(e, p);
			}
			break;
		}
	}
	
	private List<Biome> biomes(Biome[] biomes) {
		return new ArrayList<>(Arrays.asList(biomes));
	}
	
	@SuppressWarnings("deprecation")
	private void cancellingBiome(PlayerMoveEvent e, Player p) {
		e.setCancelled(true);
		p.sendTitle("§fImpossible d'§faccéder à ce §4biome", "§fBatter le prochain §4boss");
	}

	@EventHandler
	public void damageable(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if(e.getFinalDamage() >= player.getHealth()) {
				player.setHealth(20);
				player.setGameMode(GameMode.SPECTATOR);
				instance.deadplayer.put(player.getName(), player.getLocation());
				instance.deadinventory.put(player.getName(), player.getInventory());
				player.getInventory().clear();
				player.teleport(new Location(Main.main.world, Main.main.spawn.getX(), Main.main.spawn.getY() + 20, Main.main.spawn.getZ()));
				Inventory i = Bukkit.createInventory(null, 27, "§4Tu es mort");
				i.setItem(11, item(Material.GOLDEN_APPLE, "§aRespawn comme en survie", "§9Avec 10% de money en moins"));
				i.setItem(13, item(Material.OAK_SIGN, "§4Tu es mort", "§6Choisi une option de résurection"));
				i.setItem(15, item(Material.TOTEM_OF_UNDYING, "§5Respawn où tu es mort avec ton stuff", "§cavec 10% de money en moins et 10k de money en moins"));
				player.openInventory(i);
			}
		}
	}

	@EventHandler
	public void leave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage("§7(§9i§7) §f>> §f" + p.getName() + " §7 a quitté");
		instance.sc.remove(p);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Bukkit.broadcastMessage("§7" + e.getPlayer().getName() + " >> " + e.getMessage());
	}

	@EventHandler
	public void night(PlayerBedLeaveEvent e) {
		instance.world.setTime(0);
	}

	@EventHandler
	public void eat(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		if(e.getItem().getItemMeta().getDisplayName().equals("§dExtra Heart")) {
			heart(p);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void dead(PlayerDeathEvent e) {
		Player p = e.getEntity();
		double money = instance.money.get(p.getName());
		instance.money.remove(p.getName());
		if (money - 100 > 0) {
			instance.money.put(p.getName(), money - 100);
		}else {
			instance.money.put(p.getName(), 0.0D);
		}
		p.sendMessage("§7(§ee§7) §f>> §7Vous êtes a présent à §4" + Main.main.money.get(p.getName()) + "€");
	}

	//@EventHandler
	public void damage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			int regen = 0;
			switch(instance.state.get(p.getName())){
				case F:
					regen = 2; //20
					break;
				case O:
					regen = 4; //20
					break;
				case N:
					regen = 6; //20
					break;
				case R:
					regen = 8; //25
					break;
				case NN:
					regen = 10; //30
					break;
				case E:
					regen = 15; //35
					break;
				case S:
					regen = 20; //40
					break;
			}
			if(p.getHealth() - e.getFinalDamage() < regen){
				int finalRegen = regen;
				new BukkitRunnable() {
					@Override
					public void run() {
						if(p.getHealth() < finalRegen){
							if(p.getFoodLevel() > 10){
								p.setHealth(p.getHealth() + 1);
							}
						}else{
							cancel();
						}
					}
				}.runTaskTimer(Main.main, 0, 50);
			}
			if(p.getInventory().getLeggings() == null) return;
			if(p.getInventory().getLeggings().getItemMeta() != null) {
				if(e.getCause() == DamageCause.FALL && p.getInventory().getLeggings().getItemMeta().getDisplayName().equals("§2Emerald Leggings")){
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void interactWithEntity(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getCustomName().equals("§6Shop")) {
			e.getPlayer().openInventory(shopInv());
			e.setCancelled(true);
		}else if(e.getRightClicked().getCustomName().equals("§6Boss")) {
			e.getPlayer().openInventory(bossInv(e.getPlayer()));
			e.setCancelled(true);
		}else if(e.getRightClicked().getCustomName().equals("§6Start")) {
			if(instance.teamFight.containsKey(e.getPlayer().getName())) {
				instance.teamFight.get(e.getPlayer().getName()).startFight();
			}
		}
	}

	private Inventory bossInv(Player p) {
		Inventory i = Bukkit.createInventory(null, 27, "§6Boss");
		i.setItem(2, item(Material.STONE, "§7Boss de l'âge de Pierre", "§aDébloqué"));
		i.setItem(10, item(Material.IRON_CHESTPLATE, "§fBoss de l'âge de Fer", accessBoss(1, p)));
		i.setItem(20, item(Material.GOLDEN_CHESTPLATE, "§eBoss de l'âge d'or", accessBoss(2, p)));
		i.setItem(12, item(Material.BLAZE_ROD, "§9Boss de l'âge du nether", accessBoss(3, p)));
		i.setItem(14, item(Material.DIAMOND_CHESTPLATE, "§9Boss de l'âge du riche", accessBoss(4, p)));
		i.setItem(6, item(Material.RESPAWN_ANCHOR, "§4Boss de l'âge du nouveau nether", accessBoss(5, p)));
		i.setItem(16, item(Material.END_ROD, "§dBoss de l'âge de l'end", accessBoss(6, p)));
		i.setItem(24, item(Material.NETHER_STAR, "§fBoss de l'âge de la supériorité", accessBoss(7, p)));
		return i;
	}

	private String accessBoss(int i, Player p) {
		if(instance.state.get(p.getName()).getValue() >= i) {
			return "§aDébloqué";
		}else {
			return "§cIndisponible à ton niveau";
		}
	}

	private Inventory shopInv() {
		Inventory i = Bukkit.createInventory(null, 45, "§6Shop");
		i.setItem(4, item(Material.BRICKS, "§9Construction"));
		i.setItem(20, item(Material.WHEAT, "§aFarm"));
		i.setItem(22, item(Material.REDSTONE_LAMP, "§fAutre"));
		i.setItem(24, item(Material.DIAMOND_ORE, "§dMinéraux"));
		i.setItem(40, item(Material.NETHERITE_SWORD, "§4Combat"));
		return i;
	}

	private Inventory tradeItem(Material type, String name) {
		Inventory n = Bukkit.createInventory(null, 45, "§6Trade");
		String[] tab = name.split(" ");
		double value = Double.parseDouble(tab[1]) * instance.price.getItemPrice(type);
		if(tab[0].equals("Vendre")) {
			value/=10;
		}
		n.setItem(22, item(type, name, Double.toString(value)));
		n.setItem(0, item(Material.GREEN_STAINED_GLASS_PANE, "Vendre 64"));
		n.setItem(9, item(Material.GREEN_STAINED_GLASS_PANE, "Vendre 10"));
		n.setItem(18, item(Material.GREEN_STAINED_GLASS_PANE, "Vendre 1"));
		n.setItem(27, item(Material.RED_STAINED_GLASS_PANE, "Vendre -1"));
		n.setItem(36, item(Material.RED_STAINED_GLASS_PANE, "Vendre -10"));
		n.setItem(8, item(Material.GREEN_STAINED_GLASS_PANE, "Acheter 64"));
		n.setItem(17, item(Material.GREEN_STAINED_GLASS_PANE, "Acheter 10"));
		n.setItem(26, item(Material.GREEN_STAINED_GLASS_PANE, "Acheter 1"));
		n.setItem(35, item(Material.RED_STAINED_GLASS_PANE, "Acheter -1"));
		n.setItem(44, item(Material.RED_STAINED_GLASS_PANE, "Acheter -10"));
		n.setItem(41, item(Material.BARRIER, "§6Retour"));
		n.setItem(39, item(Material.ACACIA_SIGN, "§6Trade"));
		return n;
	}

	private ItemStack item(Material type, String name, String lore) {
		ItemStack s = new ItemStack(type);
		ItemMeta m = s.getItemMeta();
		List<String> list= new ArrayList<>();
		list.add(lore);
		assert m != null;
		m.setLore(list);
		m.setDisplayName(name);
		s.setItemMeta(m);
		return s;
	}

	private ItemStack item(Material type, String name) {
		ItemStack s = new ItemStack(type);
		ItemMeta m = s.getItemMeta();
		assert m != null;
		m.setDisplayName(name);
		s.setItemMeta(m);
		return s;
	}

	@EventHandler
	public void inventoryInteract(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Inventory i = e.getInventory();
		if(item == null) return;
		if(item.getItemMeta() == null)return;
		if(i.contains(item(Material.BRICKS, "§9Construction")) || i.contains(item(Material.WHEAT, "§aFarm"))) {
			Inventory n = Bukkit.createInventory(null, 54, "§6Shop");
			int number = 0;
			for(Material type : instance.price.materialbuy) {
				if(number == 44) {
					break;
				}
				Category cate = instance.price.getItemcategory(type);
				if(cate == Category.CONSTRUCTION && item.getType() == Material.BRICKS || cate == Category.FARM && item.getType() == Material.WHEAT || cate == Category.AUTRE && item.getType() == Material.REDSTONE_LAMP
						|| cate == Category.MINERAUX && item.getType() == Material.DIAMOND_ORE || cate == Category.COMBAT && item.getType() == Material.NETHERITE_SWORD) {
					double v = instance.price.getItemPrice(type);
					ItemStack s = new ItemStack(type);
					ItemMeta m = s.getItemMeta();
					ArrayList<String> list= new ArrayList<>();
					list.add("§4Acheter pour " + v + "€");
					list.add("§4Vendre pour " + v/10 + "€");
					assert m != null;
					m.setLore(list);
					s.setItemMeta(m);
					n.setItem(number, s);
					number++;
				}
			}
			n.setItem(53, item(Material.HOPPER, "§6Revenir au menu du shop"));
			e.setCancelled(true);
			((Player) e.getWhoClicked()).openInventory(n);
		}else if(item.getItemMeta().getDisplayName().equals("§6Revenir au menu du shop") || item.getItemMeta().getDisplayName().equals("§6Retour")) {
			e.setCancelled(true);
			((Player) e.getWhoClicked()).openInventory(shopInv());
		}else if(i.contains(item(Material.HOPPER, "§6Revenir au menu du shop"))) {
			e.setCancelled(true);
			((Player) e.getWhoClicked()).openInventory(tradeItem(item.getType(), "Acheter 0"));
		}else if(i.contains(item(Material.BARRIER, "§6Retour"))) {
			String text = Objects.requireNonNull(Objects.requireNonNull(i.getItem(22)).getItemMeta()).getDisplayName();
			String[] t = text.split(" ");
			if(item.getType() == Material.RED_STAINED_GLASS_PANE || item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
				String name = item.getItemMeta().getDisplayName();
				String[] v = name.split(" ");
				if(v[0].equals("Vendre")) {
					if(text.contains("Acheter")) {
						if(name.contains("-")) {
							e.setCancelled(true);
							return;
						}
						text = name;
					}else {
						if(Double.parseDouble(t[1]) + Double.parseDouble(v[1]) > 0){
							text = "Vendre " + Integer.toString(Integer.parseInt(t[1]) + Integer.parseInt(v[1]));
						}
					}
				}else {
					if(text.contains("Vendre")) {
						if(name.contains("-")) {
							e.setCancelled(true);
							return;
						}
						text = name;
					}else {
						if(Double.parseDouble(t[1]) + Double.parseDouble(v[1]) > 0){
							text = "Acheter " + Integer.toString(Integer.parseInt(t[1]) + Integer.parseInt(v[1]));
						}
					}
				}
			}else if(item.getItemMeta().getDisplayName().equals("§6Trade")){
				System.out.println("t");
				if(!t[1].equals("0")) {
					if(t[0].equals("Acheter")) {
						instance.eco.buy((Player) e.getWhoClicked(), i.getItem(22).getType(), Integer.parseInt(t[1]));
					}else{
						instance.eco.sell((Player) e.getWhoClicked(), i.getItem(22).getType(), Integer.parseInt(t[1]));
					}
					e.setCancelled(true);
					e.getWhoClicked().closeInventory();
					return;
				}
			}
			e.setCancelled(true);
			((Player) e.getWhoClicked()).openInventory(tradeItem(i.getItem(22).getType(), text));
		}else if(i.contains(item(Material.STONE, "§7Boss de l'âge de Pierre", "§aDébloqué"))) {
			if(item.getItemMeta().getLore().get(0).equalsIgnoreCase("§aDébloqué")){
				//a changer
				TeamFight tf = new TeamFight(instance, instance.state.get(e.getWhoClicked().getName()));
				tf.addPlayer((Player) e.getWhoClicked());
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
			}else{
				Player p = (Player) e.getWhoClicked();
				p.sendMessage("§7(§4!§7) §f>> §7Vous n'avez pas encore débloqué ce §4boss");
				p.closeInventory();
				e.setCancelled(true);
			}
		}else if(i.contains(item(Material.OAK_SIGN, "§6Info", "§6Tous ses métiers ont des avantages en combat et de nouveaux crafts uniques."))) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if(item.getType() == Material.OAK_SIGN) {
				return;
			}else if(item.getType() == Material.WITHER_SKELETON_SKULL) {
				instance.eco.setMetier(p, Metier.COMBATTANT);
    			p.sendMessage("§7(§9i§7) §f>> §7Vous êtes a présent §4combattant");
			}else if(item.getType() == Material.DIAMOND_ORE) {
				instance.eco.setMetier(p, Metier.MINEUR);
    			p.sendMessage("§7(§9i§7) §f>> §7Vous êtes a présent §4mineur");
			}else if(item.getType() == Material.WHEAT) {
				instance.eco.setMetier(p, Metier.AGRICULTEUR);
    			p.sendMessage("§7(§9i§7) §f>> §7Vous êtes a présent §4agriculteur");
			}else if(item.getType() == Material.OAK_WOOD) {
				instance.eco.setMetier(p, Metier.BUCHERON);
    			p.sendMessage("§7(§9i§7) §f>> §7Vous êtes a présent §4bucheron");
			}
			p.closeInventory();
		}else if(i.contains(item(Material.OAK_SIGN, "§4Tu es mort", "§6Choisi une option de résurection"))){
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if(item.getType() == Material.GOLDEN_APPLE){
				p.setGameMode(GameMode.SURVIVAL);
				p.teleport(instance.spawn);
				instance.deadplayer.remove(p.getName());
				instance.eco.pay(p, null, instance.money.get(p)/10);
				Objects.requireNonNull(instance.deadplayer.get(p.getName()).getWorld()).dropItem(instance.deadplayer.get(p.getName()), Objects.requireNonNull(allItem(instance.deadinventory.get(p))));
			}else if(item.getType() == Material.TOTEM_OF_UNDYING){
				if(instance.money.get(p.getName()) >= 11112){
					instance.eco.pay(p, null, instance.money.get(p)/10 - 10000);
					p.setGameMode(GameMode.SURVIVAL);
					p.teleport(instance.spawn);
					p.getInventory().addItem(allItem(instance.deadinventory.get(p.getName())));
				}else{
					p.sendMessage("§7(§c!§7) §f>> §7Vous n'avez pas assez d'§4argent §7pour utiliser cette option de réanimation");
				}
			}
		}
	}

	private ItemStack allItem(PlayerInventory itemStacks){
		for (ItemStack it : itemStacks) {
			return it;
		}
		return null;
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		if(e.getItem() == null) return;
		if(e.getItem().getItemMeta() == null) return;
		if(e.getItem().getItemMeta().getDisplayName().equals("§6Boss")) {
			new Zombie(e.getPlayer().getLocation(), 1);
		}
		Player p = e.getPlayer();
		if(e.getItem().getItemMeta().getDisplayName().equals("§dExtra Heart")) {
			heart(p);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void craft(CraftItemEvent e) {
		ItemStack i = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		Metier me = null;
		if(instance.metier.containsKey(p.getName())) {
			me = instance.metier.get(p.getName());
		}
		assert i != null;
		if(Objects.requireNonNull(i.getItemMeta()).getDisplayName().equals("§eFortune Book") && me != Metier.MINEUR) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Mineur §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§eLooting Book") && me != Metier.COMBATTANT) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Combattant §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§eSilk_Touch Book") && me != Metier.BUCHERON) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Bucheron §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§eAutoplanter") && me != Metier.AGRICULTEUR) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Agriculteur §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§2Emerald Helmet") && me != Metier.MINEUR) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Mineur §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§2Emerald Chestplate") && me != Metier.COMBATTANT) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Combattant §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§2Emerald Leggings") && me != Metier.BUCHERON) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Bucheron §7pour effectuer ce craft");
			e.setCancelled(true);
		}else if(i.getItemMeta().getDisplayName().equals("§2Emerald Boots") && me != Metier.AGRICULTEUR) {
			p.sendMessage("§7(§4!§7) §f>> §7Vous devez être §4Agriculteur §7pour effectuer ce craft");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void breakblock(BlockBreakEvent e) {
		Block i = e.getBlock();
		Player p = e.getPlayer();
		Metier me = null;
		Material m = i.getBlockData().getMaterial();
		if(instance.metier.containsKey(p.getName())) {
			me = instance.metier.get(p.getName());
		}
		if(p.getInventory().getItemInMainHand().getItemMeta() == null) return;
		if(me == Metier.AGRICULTEUR && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§eAutoplanter")) {
			if(m == Material.CARROTS || m == Material.POTATOES || m == Material.BEETROOTS || m == Material.WHEAT || m == Material.COCOA_BEANS) {
				Ageable a = (Ageable) i.getBlockData();
				if(a.getMaximumAge() == a.getAge()) {
					if(m == Material.CARROTS) {
						p.getInventory().addItem(new ItemStack(Material.CARROT, 3));
					}else if(m == Material.POTATOES) {
						p.getInventory().addItem(new ItemStack(Material.POTATO, 3));
					}else if(m == Material.BEETROOTS) {
						p.getInventory().addItem(new ItemStack(Material.BEETROOT, 3));
					}else if(m == Material.COCOA_BEANS) {
						p.getInventory().addItem(new ItemStack(Material.COCOA, 3));
					}else {
						p.getInventory().addItem(new ItemStack(Material.WHEAT, 3));
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							i.setType(m);
						}
					}.runTaskLater(instance, 1);
				}
			}
		}
	}

	private void heart(Player p) {
		int heart = instance.hearts.get(p.getName());
		ItemStack extraheart = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
		ItemMeta extraheratmeta = extraheart.getItemMeta();
		assert extraheratmeta != null;
		extraheratmeta.setDisplayName("§dExtra Heart");
		extraheart.setItemMeta(extraheratmeta);
		p.getInventory().removeItem(extraheart);
		if(heart < 39) {
			p.setHealthScale(p.getHealthScale() + 2);
			instance.hearts.remove(p.getName());
			instance.hearts.put(p.getName(), (int) p.getHealthScale());
			p.sendMessage("§7(§9i§7) §f>> §7Vous avez a présent §4" + p.getHealthScale() + " §7coeurs");
		}else {
			p.sendMessage("§7(§c!§7) §f>> §7Vous avez déjà atteint la limite de §4coeur");
			instance.world.dropItem(p.getLocation(), new ItemStack(Material.DIAMOND_BLOCK, 1));
			instance.world.dropItem(p.getLocation(), new ItemStack(Material.EMERALD_ORE, 4));
		}
	}

}