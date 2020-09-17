package fr.nicofighter45.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.survival.Main;

public class Boss {

	public Main instance;
	public HashMap<State, Location> bossLoc = new HashMap<>();
	public HashMap<Material, List<State>> itemBlock = new HashMap<>();

	public Boss(Main instance, FileConfiguration config) {
		this.instance = instance;
		for(String str : config.getConfigurationSection("boss").getKeys(false)) {
			String location = config.getString("boss." + str);
			String[] locationdecoup = location.split(",");
			bossLoc.put(State.getValuefromInt(Integer.parseInt(str)), new Location(instance.world, Double.parseDouble(locationdecoup[0]), Double.parseDouble(locationdecoup[1]), Double.parseDouble(locationdecoup[2])));
		}
		for(String str : config.getConfigurationSection("item").getKeys(false)) {
			Material m = Material.getMaterial(str.toUpperCase());
            if(m != null) {
            	if(config.getString("item." + str + ".boss") != null) {
            		String[] states = config.getString("item." + str + ".boss").split(",");
            		List<State> list = new ArrayList<>();
            		for(String s : states) {
            			State state = State.getValuefromInt(Integer.parseInt(s));
            			if(state != null) {
            				list.add(state);
            			}
            		}
            		itemBlock.put(m, list);
            	}
            }
		}
	}

	public void boss(TeamFight team) {
		removeItem(team);
		switch(team.state) {
		case P:
			new Zombie(bossLoc.get(team.state), 0);
			break;
		case F:
			new Zombie(bossLoc.get(team.state), 1);
			new Zombie(bossLoc.get(team.state), 2);
			new Zombie(bossLoc.get(team.state), 1);
			break;
		case O:
			new Skeleton(bossLoc.get(team.state));
			break;
		case N:
			new Blaze(bossLoc.get(team.state));
			break;
		case R:
			new Wither(bossLoc.get(team.state));
			break;
		case NN:
			//ghast invisible
			break;
		case E:
			//ender dragon
			break;
		case S:
			//vendicateur
			break;
		default:
			break;
		}
	}

	private void removeItem(TeamFight team) {
		for(Player p : team.getPlayers()) {
			Inventory inventory = Bukkit.createInventory(null, 45, "§6Ton stuff");
			for(Entry<Material, List<State>> entry : itemBlock.entrySet()) {
				if(entry.getValue().contains(team.state)) {
					for(ItemStack it : p.getInventory().getContents()) {
						if(it != null && it.getItemMeta() != null) {
							if(it.getType() == entry.getKey()) {
								inventory.addItem(it);
								it.setAmount(0);
							}
						}
					}
				}
			}
			p.openInventory(inventory);
		}
	}

}