package fr.nicofighter45.entity;

import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.EntityGhast;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

import java.util.Objects;

public class Ghast extends EntityGhast {
    public Ghast(Location loc){
        super(EntityTypes.GHAST, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.setCustomName(new ChatComponentText("§4Boss N-Nether"));
        this.setInvisible(true);
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(50.0D);
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(100.0D);
        Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(1D);
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
    }
}
