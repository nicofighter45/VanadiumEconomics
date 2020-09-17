package fr.nicofighter45.entity;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

import java.util.Objects;

public class Blaze extends EntityBlaze {
    public Blaze(Location loc) {
        super(EntityTypes.BLAZE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.setCustomName(new ChatComponentText("§cBoss Nether"));
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(10.0D);
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(100.0D);
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(1D);
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
    }
}