package fr.nicofighter45.entity;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EntityVillager;
import net.minecraft.server.v1_16_R1.EnumItemSlot;
import net.minecraft.server.v1_16_R1.MerchantRecipe;
import net.minecraft.server.v1_16_R1.VillagerData;
import net.minecraft.server.v1_16_R1.VillagerProfession;
import net.minecraft.server.v1_16_R1.VillagerType;

public class Villager extends EntityVillager{

	public Villager(Player player, String name, int type) {
		super(EntityTypes.VILLAGER, ((CraftWorld) player.getWorld()).getHandle());
		this.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
		this.setInvulnerable(true);
		this.setPersistent();
		this.setCustomName(new ChatComponentText(name));
		this.setNoAI(true);
		this.setInvisible(false);
		this.collides = false;
		switch(type) {
		case 1:
			this.setVillagerData(new VillagerData(VillagerType.PLAINS, VillagerProfession.LIBRARIAN, 10));
			ItemStack mendingbook = new ItemStack(Material.ENCHANTED_BOOK, 1);
			EnchantmentStorageMeta mendingmeta = (EnchantmentStorageMeta)mendingbook.getItemMeta();
			mendingmeta.addStoredEnchant(Enchantment.MENDING, 1, true);
			mendingbook.setItemMeta(mendingmeta);
			MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(new ItemStack(Material.BOOK)), CraftItemStack.asNMSCopy(new ItemStack(Material.EMERALD_BLOCK)), CraftItemStack.asNMSCopy(mendingbook), 0, Integer.MAX_VALUE, 5, 0);
			this.getOffers().add(recipe);
			break;
		case 2:
			this.setEquipment(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_HELMET)));
			this.setEquipment(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_CHESTPLATE)));
			this.setEquipment(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_LEGGINGS)));
			this.setEquipment(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_BOOTS)));
			this.setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_SWORD)));
			break;
		}
		((CraftWorld) player.getWorld()).getHandle().addEntity(this);
	}

}