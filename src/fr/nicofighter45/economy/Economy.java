package fr.nicofighter45.economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.survival.Main;

public class Economy {

    private FileConfiguration config;

    public Economy(FileConfiguration conf) {
        this.config = conf;
        for(String str : config.getConfigurationSection("player").getKeys(false)) {
        	Main.main.configplayer.add(str);
            Main.main.money.put(str, config.getDouble("player." + str + ".money"));
            Main.main.hearts.put(str, config.getInt("player." + str + ".heart"));
            Main.main.state.put(str, State.getValuefromInt(config.getInt("player." + str + ".state")));
            int v = config.getInt("player." + str + ".metier");
            if(v == 0) {
            	Main.main.metier.put(str, null);
            }else if(v == 1) {
            	Main.main.metier.put(str, Metier.BUCHERON);
            }else if(v == 2) {
            	Main.main.metier.put(str, Metier.MINEUR);
            }else if(v == 3) {
            	Main.main.metier.put(str, Metier.AGRICULTEUR);
            }else if(v == 4) {
            	Main.main.metier.put(str, Metier.COMBATTANT);
            }
            int value = 1;
            try {
                value = Integer.parseInt(config.getString("player." + str + ".base"));
            } catch(NumberFormatException e) {} catch(NullPointerException e) {}
            if(value != 0) {
            	String location = config.getString("player." + str + ".base");
        		String[] locationdecoup = location.split(",");
        		Main.main.base.put(str, new Location(Main.main.world, Integer.parseInt(locationdecoup[0]), Integer.parseInt(locationdecoup[1]), Integer.parseInt(locationdecoup[2])));
            }
        }
    }

    public void pay(Player p, Player t, double v) {
        if(p != null) {
            double pm = Main.main.money.get(p.getName());
            if(t == null) {
                Main.main.money.remove(p.getName());
                Main.main.money.put(p.getName(), (pm-v));
                p.sendMessage("§7(§ee§7) §f>> §7Votre §4solde §7est a présent de §4" + (pm-v) + "€");
            }else if((pm - v) < 0) {
                p.sendMessage("§7(§ee§7) §f>> §7L'échange a été anulé car vous n'avez pas assez d'argent");
                t.sendMessage("§7(§ee§7) §f>> §7L'échange a été anulé car " + p.getName() + " n'a pas assez d'argent");
            }else {
                Main.main.money.remove(p.getName());
                Main.main.money.put(p.getName(), (pm-v));
                double tm = Main.main.money.get(t.getName());
                Main.main.money.remove(t.getName());
                Main.main.money.put(t.getName(), (tm+v));
                p.sendMessage("§7(§ee§7) §f>> §7Votre §4solde §7est a présent de §4" + (pm-v) + "€");
                t.sendMessage("§7(§ee§7) §f>> §7Votre §4solde §7est a présent de §4" + (tm+v) + "€");
            }
        }else {
        	double tm = Main.main.money.get(t.getName());
            Main.main.money.remove(t.getName());
            Main.main.money.put(t.getName(), (tm+v));
            t.sendMessage("§7(§ee§7) §f>> §7Votre §4solde §7est a présent de §4" + (tm+v) + "€");
        }
    }

    public void getMoney(Player t, Player s) {
    	double money = Main.main.money.get(t.getName());
        if(s == null) {
            System.out.println("Le joueur " + t.getName() + " a " + money + "€");
        }else {
            s.sendMessage("§7(§ee§7) §f>> §7La solde de §f" + t.getName() + " §7est de §4" + money + "€");
        }
    }

    public void tpBase(Player p) {
        if(Main.main.base.containsKey(p.getName())) {
            new BukkitRunnable() {
                int timer = 5;
                @Override
                public void run() {
                    if(timer == 0) {
                        p.sendMessage("§7(§9i§7) §f>> §7Vous avez été tp à votre §4base");
                        p.teleport(Main.main.base.get(p.getName()));
                        cancel();
                    }else if (timer == 5){
                        p.sendMessage("§7(§9i§7) §f>> §7Vous allez être tp à votre §4base§7 dans " + timer);
                    }
                    timer--;
                }
            }.runTaskTimer(Main.main, 0, 20);
        }else {
            p.sendMessage("§7(§c!§7) §f>> §7Vous n'avez pas définis de base");
        }
    }

    public void setBase(Player p) {
        if(Main.main.base.containsKey(p.getName())) {
            Main.main.base.remove(p.getName());
        }
        Main.main.base.put(p.getName(), p.getLocation());
        p.sendMessage("§7(§9i§7) §f>> §7Votre base a été redéfinis en §4" + (int)p.getLocation().getX() + "," + (int)p.getLocation().getY() + "," + (int)p.getLocation().getZ());
    }

    public void buy(Player p, Material m, int n) {
    	if(Main.main.price.materialbuy.contains(m)) {
    		double price = Main.main.price.getItemPrice(m);
    		double v = price * n;
    		if(Main.main.money.get(p.getName()) >= v) {
    			p.getInventory().addItem(new ItemStack(m, n));
                p.sendMessage("§7(§ee§7) §f>> §7Vous avez acheté §f" + n + " §4" + m.toString().toLowerCase());
                pay(p, null, v);
            }else {
                p.sendMessage("§7(§c!§7) §f>> §7Vous ne pouvez pas vous le permettre");
            }
    	}else {
    		p.sendMessage("§7(§c!§7) §f>> §7Cette item n'est pas a vendre");
    	}
    }

	public void sell(Player p, Material m, int i) {
		if(p.getInventory().containsAtLeast(new ItemStack(m), i)) {
			p.getInventory().removeItem(new ItemStack(m, i));
			double price = Main.main.price.getItemPrice(m);
    		double v = (price/10) * i;
    		if (v == 0) {
    			v = 1;
    		}
			pay(null, p, v);
		}else {
			p.sendMessage("§7(§4!§7) §f>> §7Vous n'avez pas l'item en question dans votre inventaire");
		}
	}
	
	public void setMetier(Player p, Metier m) {
		if(Main.main.metier.containsKey(p.getName())) {
			Main.main.metier.remove(p.getName());
		}
		Main.main.metier.put(p.getName(), m);
	}
}