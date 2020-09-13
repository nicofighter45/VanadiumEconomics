package fr.nicofighter45.entity;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

import java.util.Objects;

public class Skeleton  extends EntitySkeleton {
    public Skeleton(Location loc, int type) {
        super(EntityTypes.SKELETON, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        switch(type){
            case 0:
                this.setCustomName(new ChatComponentText("§eBoss or"));
                Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE)).setValue(20.0D);
                Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.ARMOR)).setValue(60.0D);
                Objects.requireNonNull(this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)).setValue(0.5D);
                break;
        }
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this);
    }
}
