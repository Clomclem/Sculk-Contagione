package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkSpreadManager.Cursor.class)
public abstract class SculkSpreadManagerCursorMixin {
    @WrapOperation(method = "canSpread(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSideSolidFullSquare(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
    private static boolean modifyFullBlockRequirement(BlockState instance, BlockView blockView, BlockPos blockPos, Direction direction, Operation<Boolean> original) {
        return true;
    }
}
