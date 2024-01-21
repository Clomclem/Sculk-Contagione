package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BlockWithEntity implements Waterloggable {
    protected SculkShriekerBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSculk()) {
            if (!world.isClient) {
                world.getBlockEntity(pos, BlockEntityType.SCULK_SHRIEKER).ifPresent(blockEntity -> blockEntity.shriek((ServerWorld) world, (ServerPlayerEntity) player));
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @WrapOperation(method = "onSteppedOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/entity/SculkShriekerBlockEntity;findResponsiblePlayerFromEntity(Lnet/minecraft/entity/Entity;)Lnet/minecraft/server/network/ServerPlayerEntity;"
    ))
    private ServerPlayerEntity onSteppedOn(Entity entity, Operation<ServerPlayerEntity> original) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.isSculk()) {
            return null;
        } else {
            return original.call(entity);
        }
    }
}
