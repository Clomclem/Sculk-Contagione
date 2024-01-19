package me.clomclem.sculkcontagione.mixin;

import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.accessor.IItemEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Ownable, IItemEntityAccessor {

    @Shadow
    private int itemAge;

    @Shadow
    public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setItemAge(int itemAge) {
        this.itemAge = itemAge;
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (player.isSculk() && !((this.getStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock().getRegistryEntry().isIn(SculkContagione.SCULK)) || this.getStack().isOf(Items.ENCHANTED_GOLDEN_APPLE))) {
            ci.cancel();
        }
    }
}
