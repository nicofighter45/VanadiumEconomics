package fr.nicofighter45.scoreboard;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.nicofighter45.enumeration.State;
import fr.nicofighter45.survival.Main;

@SuppressWarnings("deprecation")
public class team {
	
	Main instance;

	public team(Main instance) {
		this.instance = instance; 
	}
	
	public void teams(Player player) {
		State state = instance.state.get(player.getName());
		for(Entry<Player, ScoreboardCustom> list : instance.sc.entrySet()) {
			Scoreboard board = list.getValue().getMainScoreboard();
			String name = state.toString();
			String prefix = "§7[" + state.getName() + "§7] ";
			Team team = board.getTeam(name);
			if(team != null) {
				if(team.hasPlayer(player)) {
					team.removePlayer(player);
				}
				team.addPlayer(player);
			}else {
				board.registerNewTeam(name);
				team = board.getTeam(name);
				team.setPrefix(prefix);
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				team.addPlayer(player);
				team.setColor(state.getColor());
			}
		}
	}
	
}