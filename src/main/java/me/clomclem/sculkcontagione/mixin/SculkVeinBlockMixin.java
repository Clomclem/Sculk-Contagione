package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.block.entity.SculkBlockEntity;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.minecraft.block.*;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkVeinBlock.class)
public abstract class SculkVeinBlockMixin extends MultifaceGrowthBlock implements SculkSpreadable, Waterloggable {
    public SculkVeinBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "convertToBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean replaceTag(BlockState instance, TagKey tagKey, Operation<Boolean> original, SculkSpreadManager spreadManager, WorldAccess worldAccess, BlockPos pos, Random random) {
        if (worldAccess instanceof World world && world.getGameRules().getBoolean(SculkContagioneGamerules.EVERYTHING_TURNS_INTO_SCULK)) {
            return !instance.isIn(SculkContagione.NON_SCULK_REPLACEABLE);
        } else {
            return original.call(instance, tagKey);
        }
    }

    @Inject(method = "convertToBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER))
    private void setBlockType(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos blockPos, @Local(ordinal = 1) BlockState blockState) {
        SculkBlockEntity blockEntity = ((SculkBlockEntity) world.getBlockEntity(blockPos));
        blockEntity.setPreviousBlock(blockState);
        blockEntity.setCatalystPos(spreadManager.getCatalystPos());
        spreadManager.setBlockAmount(spreadManager.getBlockAmount()+1);
        blockEntity.setBlockAmount(spreadManager.getBlockAmount());
    }

    @Redirect(method = "*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SculkVeinBlock;hasDirection(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
    private boolean removeSpreadLimit(BlockState state, Direction direction) {
        return true;
    }
}
