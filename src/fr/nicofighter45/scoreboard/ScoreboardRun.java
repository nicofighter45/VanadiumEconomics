package fr.nicofighter45.scoreboard;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicofighter45.survival.Main;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_16_R1.PlayerConnection;

public class ScoreboardRun extends BukkitRunnable{
	
	Main instance;
	IChatBaseComponent title;
	IChatBaseComponent foot;
	PacketPlayOutPlayerListHeaderFooter headerPacket;
	
	public ScoreboardRun(Main instance){
		this.instance = instance;
		this.title = ChatSerializer.a("{\"text\": \"" + "§6Vanadium Economics\n" + "\"}");
		this.headerPacket = new PacketPlayOutPlayerListHeaderFooter();
	}
	
	@Override
	public void run() {
		for(Entry<Player, ScoreboardCustom> scoreboard : Main.main.sc.entrySet()) {
			ScoreboardCustom board = scoreboard.getValue();
			Player p = scoreboard.getKey();
			board.refresh(p);
			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			this.foot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "\n§3Plugin par §fnicofighter45 \n §eMort : " + p.getStatistic(Statistic.DEATHS) + "\"}");
			try {
				Field fieldu = headerPacket.getClass().getDeclaredField("header");
				fieldu.setAccessible(true);
				fieldu.set(headerPacket, title);
				Field fieldd = headerPacket.getClass().getDeclaredField("footer");
				fieldd.setAccessible(true);
				fieldd.set(headerPacket, foot);
			}catch (Exception e){
				e.printStackTrace();
			}finally {
				connection.sendPacket(headerPacket);
			}
			PlayerInventory i = p.getInventory();
			if(i.getHelmet() != null) {
				if(i.getHelmet().getItemMeta().getDisplayName().equals("§2Emerald Helmet")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2));
				}
			}
			if(i.getChestplate() != null) {
				if(i.getChestplate().getItemMeta().getDisplayName().equals("§2Emerald Chestplate")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 1));
				}
			}
			if(i.getLeggings() != null) {
				if(i.getLeggings().getItemMeta().getDisplayName().equals("§2Emerald Leggings")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 3));
				}
			}
			if(i.getBoots() != null) {
				if(i.getBoots().getItemMeta().getDisplayName().equals("§2Emerald Boots")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
				}
			}
		}
	}
	
}