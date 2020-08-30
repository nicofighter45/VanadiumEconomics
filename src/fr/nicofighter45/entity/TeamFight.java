package fr.nicofighter45.entity;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.survival.Main;

public class TeamFight {
	
	public Main instance;
	private ArrayList<Player> players = new ArrayList<>();
	public State state;
	
	public TeamFight(Main instance, State state) {
		this.instance = instance;
		this.state = state;
	}
	
	public void addPlayer(Player player) {
		Metier metier = instance.metier.get(player.getName());
		for(Player p : players) {
			if(instance.metier.get(p.getName()) == metier) {
				player.sendMessage("§7(§c!§7) §f>> §7Cette équipe comporte déjà votre métier");
				return;
			}
			p.sendMessage("§7(§9i§7) §f>> §4" + player.getName() + " §7a rejoint votre équipe");
		}
		players.add(player);
		player.sendMessage("§7(§9i§7) §f>> §7Tu as rejoins l'équipe");
		instance.teamFight.put(player.getName(), this);
	}
	
	public void startFight() {
		instance.boss.boss(this);
		for(Player p : players) {
			p.sendMessage("§7(§c!§7) §f>> §7Préparer vos §4armes§7, le §4boss§7 a spawn !");
		}
	}
	
	public ArrayList<Player> getPlayers(){
		return players;
	}
	
}