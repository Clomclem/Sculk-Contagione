package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BlockWithEntity implements Waterloggable {
    protected SculkSensorBlockMixin(Settings settings) {
        super(settings);
    }

    /*@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSculk() && SculkSensorBlock.isInactive(state)) {
            if (!world.isClient) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof SculkSensorBlockEntity sculkSensorBlockEntity
                        && world instanceof ServerWorld serverWorld
                        && sculkSensorBlockEntity.getVibrationCallback().accepts(serverWorld, pos, GameEvent.STEP, GameEvent.Emitter.of(state))) {
                    sculkSensorBlockEntity.getEventListener().forceListen(serverWorld, GameEvent.STEP, GameEvent.Emitter.of(player), player.getPos());
                }
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }*/

    @ModifyExpressionValue(
            method = "onSteppedOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SculkSensorBlock;isInactive(Lnet/minecraft/block/BlockState;)Z") // Random target, it's just to add an extra condition
    )
    private boolean modifyOnSteppedOn(boolean original, World world, BlockPos pos, BlockState state, Entity entity) {
        return original && (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isSculk());
    }
}
