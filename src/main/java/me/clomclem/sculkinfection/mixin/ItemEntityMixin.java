package me.clomclem.sculkinfection.mixin;

import me.clomclem.sculkinfection.accessor.IItemEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Ownable, IItemEntityAccessor {

    @Shadow private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setItemAge(int itemAge) {
        this.itemAge = itemAge;
    }
}
