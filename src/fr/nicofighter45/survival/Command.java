package fr.nicofighter45.survival;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicofighter45.entity.Villager;
import fr.nicofighter45.enumeration.Category;
import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.enumeration.State;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String msg, String[] arg) {
        if(cmd.getName().equalsIgnoreCase("pay")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if(arg.length != 2) {
                    p.sendMessage("§7(§c!§7) §f>> §7Mauvaise utilisation de la §4commande§7: §f/pay <pseudo> <montant>");
                }else {
                    if(Bukkit.getPlayer(arg[0]) == null) {
                        p.sendMessage("§7(§c!§7) §f>> §7Le joueur §4" + arg[0] + " §7n'éxiste pas ou n'est pas connecté");
                    }else {
                        int value = 0;
                        try {
                            value = Integer.parseInt(arg[1]);
                        } catch(NumberFormatException e) {
                            p.sendMessage("§7(§c!§7) §f>> §7Le montant doit être un entier");
                            return false;
                        } catch(NullPointerException e) {
                            p.sendMessage("§7(§c!§7) §f>> §7Le montant doit être un entier");
                            return false;
                        }
                        if(value != 0) {
                        	if (value > 0) {
                        		if(p == Bukkit.getPlayer(arg[0])) {
                                    p.sendMessage("§7(§c!§7) §f>> §7Vous ne pouvez pas vous payer vous même");
                                }else {
                                    Main.main.eco.pay(p, Bukkit.getPlayer(arg[0]), value);
                                }
                        	}else {
                        		p.sendMessage("§7(§c!§7) §f>> §7Vous ne pouvez pas payez avec valeure négative");
                        	}
                        }
                    }
                }
            }else {
                if(arg.length != 2) {
                    System.out.println("§7(§c!§7) §f>> §7Mauvaise utilisation de la §4commande§7: §f/pay <pseudo> <montant>");
                }else {
                    if(Bukkit.getPlayer(arg[0]) == null) {
                        System.out.println("§7(§c!§7) §f>> §7Le joueur §4" + arg[0] + " §7n'existe pas ou n'est pas connecte");
                    }else {
                        int value = 0;
                        try {
                            value = Integer.parseInt(arg[1]);
                        } catch(NumberFormatException e) {
                            System.out.println("§7(§c!§7) §f>> §7Le montant doit être un entier");
                        } catch(NullPointerException e) {
                            System.out.println("§7(§c!§7) §f>> §7Le montant doit être un entier");
                        }
                        if(value != 0) {
                            Main.main.eco.pay(null, Bukkit.getPlayer(arg[0]), value);
                        }
                    }
                }
            }
            if(arg.length != 2) {
            }
        }else if(cmd.getName().equalsIgnoreCase("money")) {
            if(arg.length == 0) {
                if(sender instanceof Player) {
                    Main.main.eco.getMoney((Player) sender, (Player) sender);
                }else {
                    System.out.println("Préciser le joueur: /money <player>");
                }
            }else if(arg.length == 1) {
                Player t = Bukkit.getPlayer(arg[0]);
                if(t == null) {
                    sender.sendMessage("§7(§c!§7) §f>> §7Ce joueur n'existe pas ou n'est pas connecté");
                }else {
                    if (sender instanceof Player) {
                        Main.main.eco.getMoney(t, (Player) sender);
                    }else {
                        Main.main.eco.getMoney(t, null);
                    }
                }
            }else {
                sender.sendMessage("§7(§c!§7) §f>> §7Correct utilisation de la commande: §4/money <player>");
            }
        }else if(cmd.getName().equalsIgnoreCase("eco")) {
            if(arg.length == 1 && arg[0].equals("rl")) {
                Main.main.reload();
                sender.sendMessage("§7(§4CONSOLE§7) §f>> §7Economie du server reload");
            }else if(arg.length == 3) {
            	Material m = Material.getMaterial(arg[0].toUpperCase());
            	if(m != null) {
            		double value = 0;
                    try {
                        value = Double.parseDouble(arg[1]);
                    } catch(NumberFormatException e) {return false; } catch(NullPointerException e) {return false;}
                    if(value == 0) {
                    	Main.main.price.removeItem(m);
                    	sender.sendMessage("§7(§c!§7) §f>> §7L'item §4" + m.toString().toLowerCase() + "§7 a été retiré des items en vente");
                    }else if(value < 0) {
                    	sender.sendMessage("§7(§c!§7) §f>> §7Le montant doit être positif");
                    }else {
                    	int v2 = 0;
                    	try {
                    		v2 = Integer.parseInt(arg[2]);
                    	}catch(NumberFormatException e) {return false; } catch(NullPointerException e) {return false;}
                    	Category cate = Category.getValuefromInt(v2);
                    	if(cate != null) {
                    		if(Main.main.price.materialbuy.contains(m)) {
                    			Main.main.price.materialbuy.remove(m);
                    		}
                    		Main.main.price.materialbuy.add(m);
                    		Main.main.price.setItemPrice(m, value);
                    		Main.main.price.setItemcategory(m, cate);
                    	}else {
                    		sender.sendMessage("§7(§c!§7) §f>> §7Cette catégory n'existe pas (0 à 4)");
                    	}
                    	sender.sendMessage("§7[§cCONSOLE§7] §f>> §7L'item§4 " + arg[0] + " §7est maintenant a §f" + value + "€");
                    }
            	}else {
            		sender.sendMessage("§7(§c!§7) §f>> §7Ce Material n'existe pas");
            	}
            }else{
            	sender.sendMessage("§7(§c!§7) §f>> Mauvaise utilisation de la commande :");
            	sender.sendMessage("§f/eco rl §7: reload l'économie du server");
            	sender.sendMessage("§f/eco <item> <price> <category> §7: définir le prix pour un item");
            }
        }else if(sender instanceof Player) {
            Player p = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("spawn")) {
                new BukkitRunnable() {
                    int timer = 5;
                    @Override
                    public void run() {
                        if(timer == 0) {
                            p.sendMessage("§7(§9i§7) §f>> §7Vous avez été tp au §4spawn");
                            p.teleport(Main.main.spawn);
                            cancel();
                        }else  if (timer == 5){
                            p.sendMessage("§7(§9i§7) §f>> §7Vous allez être tp au §4spawn§7 dans " + timer);
                        }
                        timer--;
                    }
                }.runTaskTimer(Main.main, 0, 20);
            }else if(cmd.getName().equalsIgnoreCase("setspawn")) {
                Main.main.spawn = new Location(Main.main.world, (int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ());
                p.sendMessage("§7[§cCONSOLE§7] §f>> §7Le §4spawn§7 a bien été redéfinis en " + (int)p.getLocation().getX() + "," + (int)p.getLocation().getY() + "," + (int)p.getLocation().getZ());
            }else if(cmd.getName().equalsIgnoreCase("base")) {
                Main.main.eco.tpBase(p);
            }else if(cmd.getName().equalsIgnoreCase("setbase")) {
                Main.main.eco.setBase(p);
            }else if(cmd.getName().equalsIgnoreCase("info")) {
            	p.sendMessage("§7[§cCONSOLE§7] §fINFORMATION CONCERNENT LE SERVER");
            	p.sendMessage("");
            	p.sendMessage("§9Commandes:");
            	p.sendMessage("§f/pay <player> : §7permet de donner de l'argent a un joueur");
            	p.sendMessage("§f/spawn : §7permet de se tp au spawn");
            	p.sendMessage("§f/setbase : §7permet de définir l'emplacement de sa base");
            	p.sendMessage("§f/base permet de se teleporter a sa base");
            	p.sendMessage("§f/money <player> : §7permet de connaitre l'argent d'un joueur");
            	p.sendMessage("§f/info : §7permet d'obtenir les informations concernant le server");
            	p.sendMessage("§f/shop : §7affiche la liste des items et leur prix");
            	p.sendMessage("§f/shop buy/sell <object> <number> : §7permet d'acheter ou de vendre des items");
            	p.sendMessage("§f/metier : §7permet de changer de metier");
            	p.sendMessage("§9Commandes Admin:");
            	p.sendMessage("§f/setspawn : §7permet de redéfinir le spawn");
            	p.sendMessage("§f/eco rl : §7permet de reload l'économie du server");
            	p.sendMessage("§f/eco <block> <value> : §7permet de redéfinir la valeur d'un item");
            	p.sendMessage("§f/reduc <block> <metier> : §7permet de définir la réduction d'un item à un métier");
            	p.sendMessage("§9Metiers:");
            	p.sendMessage("§fBucheron : §7" + Metier.BUCHERON.getDesc());
    			p.sendMessage("§fMineur : §7" + Metier.MINEUR.getDesc());
    			p.sendMessage("§fAgriculteur : §7" + Metier.AGRICULTEUR.getDesc());
    			p.sendMessage("§fCombattant : §7" + Metier.COMBATTANT.getDesc());
            }else if(cmd.getName().equalsIgnoreCase("set")) {
            	if(arg.length == 3 || arg.length == 2) {
            		Player t = Bukkit.getPlayer(arg[0]);
            		if(t == null) {
            			p.sendMessage("§7(§c!§7) §f>> §7Le joueur n'éxiste pas ou n'est pas connecté");
            		}else {
            			if(arg[1].equalsIgnoreCase("money")) {
            				set(1, p, t, arg);
            			}else if(arg[1].equalsIgnoreCase("base")) {
            				if(Main.main.base.containsKey(t.getName())) {
                    			Main.main.base.remove(t.getName());
                    		}
                    		Main.main.base.put(t.getName(), p.getLocation());
                    		p.sendMessage("§7(§9i§7) §f>> §7La base de §4" + t.getName() + " §7a été redéfinis en §4" + (int)p.getLocation().getX() + "," + (int)p.getLocation().getY() + "," + (int)p.getLocation().getZ());
                    		t.sendMessage("§7(§9i§7) §f>> §7Votre base a été redéfinis en §4" + (int)p.getLocation().getX() + "," + (int)p.getLocation().getY() + "," + (int)p.getLocation().getZ() + " §7(par un admin)");
            			}else if(arg[1].equalsIgnoreCase("heart")) {
            				set(2, p, t, arg);
            			}else if(arg[1].equalsIgnoreCase("metier")) {
            				set(3, p, t, arg);
            			}else if(arg[1].equalsIgnoreCase("state")) {
            				set(4, p, t, arg);
            			}else {
            				p.sendMessage("§7(§c!§7) §f>> §7Les attributs disponibles sont:");
            				p.sendMessage("§fmoney, base, heart, metier, state");
            			}
            		}
            	}else {
            		p.sendMessage("§7(§c!§7) §f>> §7La commande est: /set <player> <attribut> <value>");
            	}
            }else if(cmd.getName().equalsIgnoreCase("test")) {
            	if(arg.length == 1) {
            		switch(arg[0]) {
            		case "stick":
            			ItemStack item = new ItemStack(Material.STICK, 1);
            			ItemMeta meta = item.getItemMeta();
            			meta.setDisplayName("§6Boss");
            			item.setItemMeta(meta);
            			p.getInventory().addItem(item);
            			break;
            		case "shop":
            			new Villager(p, "§6Shop", 0);
            			break;
            		case "boss":
            			new Villager(p, "§6Boss", 0);
            			break;
            		case "tp":
            			new Villager(p, "§6Tp", 0);
            			break;
            		case "info":
            			new Villager(p, "§9Info", 0);
            			break;
            		case "start":
            			new Villager(p, "§6Start", 0);
            			break;
            		case "mending":
            			new Villager(p, "§6Mending", 1);
            			break;
            		case "armor":
            			new Villager(p, "§6A", 2);
            			break;
            		}
            	}
            }else if(cmd.getName().equalsIgnoreCase("setspawnboss")) {
            	if(arg.length != 1) {
            		p.sendMessage("§7(§c!§7) §f>> §7La commande est : /setspawnboss <state>");
            	}else {
            		State state = State.getValuefromInt(Integer.parseInt(arg[0]));
            		if(state == null) {
            			p.sendMessage("§7(§c!§7) §f>> §7Ce stade n'existe pas (0 à 7)");
            		}else {
            			Main.main.boss.bossLoc.put(state, p.getLocation());
            			p.sendMessage("§7[§cCONSOLE§7] §f>> §7Le §4spawn§7 du boss du stade " + state.getName() +"§7 a bien été redéfinis en " + (int)p.getLocation().getX() + "," + (int)p.getLocation().getY() + "," + (int)p.getLocation().getZ());
            		}
            	}
            }else if(cmd.getName().equalsIgnoreCase("bossitemblock")) {
            	if(arg.length == 0) {
            		p.sendMessage("§7(§9i§7) §f>> §7Voici les items bloqué en fonction des statdes:");
            		for(Entry<Material, List<State>> entry : Main.main.boss.itemBlock.entrySet()) {
            			String states = "";
            			for(State s : entry.getValue()) {
            				if(s != null) {
            					states+=s.getValue() + ",";
            				}
            			}
            			p.sendMessage(entry.getKey().toString().toLowerCase() + " : " + states);
            		}
            	}else if(arg.length == 2) {
            		Material m = Material.getMaterial(arg[0].toUpperCase());
                	if(m != null) {
                		int value = 0;
                        try {
                            value = Integer.parseInt(arg[1]);
                        } catch(NumberFormatException e) {return false; } catch(NullPointerException e) {return false;}
                        State state = State.getValuefromInt(value);
                        if(state == null) {
                        	p.sendMessage("§7(§c!§7) §f>> §7Ce stade n'existe pas (0 à 7)");
                        }else {
                        	if(Main.main.boss.itemBlock.containsKey(m)) {
                        		Main.main.boss.itemBlock.get(m).add(state);
                        		p.sendMessage("§7(§9i§7) §f>> §7Le matériel a été ajouté");
                        	}else {
                        		List<State> list = new ArrayList<>();
                        		list.add(state);
                        		Main.main.boss.itemBlock.put(m, list);
                        		p.sendMessage("§7(§9i§7) §f>> §7Le matériel a été ajouté");
                        	}
                        }
                	}else {
                		p.sendMessage("§7(§c!§7) §f>> §7Ce Material n'existe pas");
                	}
            	}
            }
        }
        return true;
    }

	private void set(int type, Player p, Player t, String[] arg) {
		if(arg.length == 3) {
			Double value = 0.0D;
			try {
				value = Double.parseDouble(arg[2]);
			} catch(NumberFormatException e) {
				p.sendMessage("§7(§c!§7) §f>> §7Le montant doit être un entier");
				return;
			} catch(NullPointerException e) {
				p.sendMessage("§7(§c!§7) §f>> §7Le montant doit être un entier");
				return;
			}
			if(value >= 0) {
				if(type == 1) {
					Main.main.money.remove(t.getName());
					Main.main.money.put(t.getName(), value);
					t.sendMessage("§7(§ee§7) §f>> §7Votre §4solde §7est a présent de §4" + value + "€ §7(changer par un admin)");
					p.sendMessage("§7(§ee§7) §f>> §7La solde de §f" + t.getName() + " §7est a présent de §4" + value + "€");
				}else if(type == 2) {
					if(value <= 40) {
						Main.main.hearts.remove(t.getName());
						Main.main.hearts.put(t.getName(), (int) Math.round(value));
						t.setHealthScale(value);
						p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent à §4" + value + " §7coeurs (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent à §4" + value + " §7coeurs");
					}else {
						p.sendMessage("§7(§c!§7) §f>> §7Le nombre ne peut pas être supérieur à 40");
					}
				}else if(type == 3) {
					if(value == 0) {
						Main.main.metier.remove(t.getName());
						Main.main.metier.put(t.getName(), null);
						p.sendMessage("§7(§9i§7) §f>> §7Vous n'avez plus de métier (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 n'a plus de metier");
					}else if(value == 1) {
						Main.main.metier.remove(t.getName());
						Main.main.metier.put(t.getName(), Metier.BUCHERON);
						p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent §4" + Metier.BUCHERON.toString().toLowerCase() + "§7 (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent §4" + Metier.BUCHERON.toString().toLowerCase());
					}else if(value == 2) {
						Main.main.metier.remove(t.getName());
						Main.main.metier.put(t.getName(), Metier.MINEUR);
						p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent §4" + Metier.MINEUR.toString().toLowerCase() + "§7 (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent §4" + Metier.MINEUR.toString().toLowerCase());
					}else if(value == 3) {
						Main.main.metier.remove(t.getName());
						Main.main.metier.put(t.getName(), Metier.AGRICULTEUR);
						p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent §4" + Metier.AGRICULTEUR.toString().toLowerCase() + "§7 (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent §4" + Metier.AGRICULTEUR.toString().toLowerCase());
					}else if(value == 4) {
						Main.main.metier.remove(t.getName());
						Main.main.metier.put(t.getName(), Metier.COMBATTANT);
						p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent §4" + Metier.COMBATTANT.toString().toLowerCase() + "§7 (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent §4" + Metier.COMBATTANT.toString().toLowerCase());
					}else {
						p.sendMessage("§7(§c!§7) §f>> §7Le nombre doit être entre 0 et 4");
					}
				}else if (type == 4){
					State state = State.getValuefromInt((int) Math.round(value));
					if(state != null) {
						Main.main.state.remove(t.getName());
						Main.main.state.put(t.getName(), state);
	    				p.sendMessage("§7(§9i§7) §f>> §7Vous êtes à présent §4" + state.getName() + "§7 (changer par un admin)");
						t.sendMessage("§7(§9i§7) §f>> §7Le joueur §4" + t.getName() + "§7 est à présent §4" + state.getName());
						for(Player player : Bukkit.getOnlinePlayers()) {
							Main.main.team.teams(player);
						}
					}else {
						p.sendMessage("§7(§c!§7) §f>> §7Le nombre doit être entre 0 et 7");
					}
				}
			}else {
				p.sendMessage("§7(§c!§7) §f>> §7Le nombre doit être un §4entier §7supérieur à §fzéro");
			}
		}else {
			p.sendMessage("§7(§c!§7) §f>> §7La commande est: /set <player> <attribut> <value>");
		}
		
        
	}
}