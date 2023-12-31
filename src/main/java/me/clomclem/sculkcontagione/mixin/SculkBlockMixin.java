package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.block.entity.SculkBlockEntity;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("deprecation")
@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin extends ExperienceDroppingBlock implements SculkSpreadable, BlockEntityProvider {
    public SculkBlockMixin(IntProvider experienceDropped, Settings settings) {
        super(experienceDropped, settings);
    }

    @ModifyReturnValue(method = "getExtraBlockState",
            at = @At("RETURN"))
    private BlockState modifyExtraBlockstate(BlockState original, WorldAccess world, BlockPos pos, Random random, boolean allowShrieker) {
        if (random.nextInt(63) == 0) {
            return Blocks.SCULK_CATALYST.getDefaultState();
        } else {
            return original;
        }
    }

    @WrapOperation(method = "spread",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SculkSpreadManager;isWorldGen()Z"))
    private boolean enableAlwaysSpawnWarden(SculkSpreadManager instance, Operation<Boolean> original, SculkSpreadManager.Cursor cursor, WorldAccess worldAccess, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        if (worldAccess instanceof World world && world.getGameRules().getBoolean(SculkContagioneGamerules.SCULK_SPREAD_SPAWN_WARDEN)) {
            return true;
        } else {
            return original.call(instance);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.onSyncedBlockEvent(type, data);
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockWithEntity.validateTicker(type, SculkContagione.SCULK_BLOCK_ENTITY, SculkBlockEntity::tick);
    }
}
