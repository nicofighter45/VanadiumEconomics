package fr.nicofighter45.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.EntityZombie;
import net.minecraft.server.v1_16_R1.GenericAttributes;

public class Zombie extends EntityZombie{

	public Zombie(Location loc, int type) {
		super(((CraftWorld) loc.getWorld()).getHandle());
		this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		switch(type) {
		case 0:
			this.setCustomName(new ChatComponentText("§7Boss Pierre"));
			this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(20.0D);
			this.getAttributeInstance(GenericAttributes.ARMOR).setValue(20.0D);
			this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3D);
			break;
		case 1:
			break;
		}
		((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
	}

}
