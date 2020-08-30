package fr.nicofighter45.scoreboard;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.nicofighter45.enumeration.Metier;
import fr.nicofighter45.survival.Main;

public class ScoreboardCustom implements ScoreboardManager{
	
	public Scoreboard scoreboard;
	public Objective objective1;
	public Objective objective2;
	public String name;
	public Player owner;
	public Random r = new Random();
	
	@SuppressWarnings("deprecation")
	public ScoreboardCustom(Player p) {
		if(Main.main.sc.containsKey(p)) return;
		this.owner = p;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		if(!p.getName().isEmpty() && p.getName().length() < 13) {
			this.name = "sb." + p.getName();
		}else {
			this.name = "sb." + r.nextInt(999999);
		}
		this.objective1 = scoreboard.registerNewObjective(this.name, "VanadiumScoreboard");
		objective1.setDisplayName("§6Vanadium");
		objective1.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective2 = scoreboard.registerNewObjective(this.name + "h", "health");
		objective2.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective2.setDisplayName(ChatColor.RED + "❤");
		Main.main.sc.put(p, this);
	}

	@Override
	public Scoreboard getMainScoreboard() {
		return scoreboard;
	}

	@Override
	public Scoreboard getNewScoreboard() {
		return null;
	}

	public void refresh(Player p) {
		objective2.setDisplayName(ChatColor.RED + "❤");
		for(String ligne : scoreboard.getEntries()) {
			if(ligne.contains("Player") && !ligne.contains(p.getName())) {
				scoreboard.resetScores(ligne);
				objective1.getScore("§9Player : §f" + p.getName()).setScore(6);
			}else if(ligne.contains("Online") && !ligne.contains(Integer.toString(Bukkit.getOnlinePlayers().size()))) {
				scoreboard.resetScores(ligne);
				objective1.getScore("§2Online : §f" + Bukkit.getOnlinePlayers().size()).setScore(5);
			}else if(ligne.contains("Mort") && !ligne.contains(Integer.toString(p.getStatistic(Statistic.DEATHS)))) {
				scoreboard.resetScores(ligne);
				objective1.getScore("§4Mort : §f" + p.getStatistic(Statistic.DEATHS)).setScore(4);
			}else if(ligne.contains("Money") && !ligne.contains(Double.toString(Main.main.money.get(p.getName())))) {
				scoreboard.resetScores(ligne);
				objective1.getScore("§eMoney : §f" + Main.main.money.get(p.getName())).setScore(3);
			}else if(ligne.contains("Stade")) {
				scoreboard.resetScores(ligne);
				objective1.getScore("§6Stade : " + Main.main.state.get(p.getName()).getName()).setScore(2);
			}else if(ligne.contains("Metier")) {
				scoreboard.resetScores(ligne);
				Metier m = Main.main.metier.get(p.getName());
				if(m == null) {
					objective1.getScore("§5Metier : §fAucun").setScore(1);
				}else {
					objective1.getScore("§5Metier : §f" + m.toString().toLowerCase()).setScore(1);
				}
			}
		}
	}
	
	public void sendLine() {
		objective1.getScore("§9Player §f: 0").setScore(6);
		objective1.getScore("§2Online §f: 0").setScore(5);
		objective1.getScore("§4Mort : §f0").setScore(4);
		objective1.getScore("§eMoney §f: 0").setScore(3);
		objective1.getScore("§6Stade : §f0").setScore(2);
		objective1.getScore("§5Metier §f: Aucun").setScore(1);
		objective1.getScore("§n§lvanadium.jeanba.fr").setScore(0);
	}
	
	public void setScoreboard() {
		owner.setScoreboard(scoreboard);
	}
	
}