package fr.nicofighter45.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.EntityZombie;
import net.minecraft.server.v1_16_R1.GenericAttributes;

import java.util.Objects;

public class Zombie extends EntityZombie{

	public Zombie(Location loc, int type) {
		super(((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
		this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		switch(type) {
		case 0:
			this.setCustomName(new ChatComponentText("§7Boss Pierre"));
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(20.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(20.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(0.3D);
			break;
		case 1:
			this.setCustomName(new ChatComponentText("§fMini-Boss Fer"));
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(5.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(5.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(0.5D);
			break;
		case 2:
			this.setCustomName(new ChatComponentText("§fBoss Fer"));
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(30.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(30.0D);
			Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(0.3D);
			break;
		}
		((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
	}

}
