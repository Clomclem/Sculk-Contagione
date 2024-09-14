package me.clomclem.sculkcontagione.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChestBlock.class, EnderChestBlock.class, ShulkerBoxBlock.class})
public abstract class StorageBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player.isSculk()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
