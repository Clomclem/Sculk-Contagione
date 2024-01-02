package me.clomclem.sculkinfection.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.clomclem.sculkinfection.SculkInfection;
import me.clomclem.sculkinfection.block.entity.SculkBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static me.clomclem.sculkinfection.SculkInfection.of;

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
    private boolean enableAlwaysSpawnWarden(SculkSpreadManager instance, Operation<Boolean> original, SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        if (((World)world).getGameRules().getBoolean(SculkInfection.SCULK_SPREAD_SPAWN_WARDEN)) {
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
        return BlockWithEntity.validateTicker(type, SculkInfection.SCULK_BLOCK_ENTITY, SculkBlockEntity::tick);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        if (entity instanceof LivingEntity livingEntity && world.getBlockState(pos).getBlock() == Blocks.SCULK && livingEntity.getType() != EntityType.WARDEN) {
            if (livingEntity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
                return;
            }

            boolean hasBoots = false;

            for (ItemStack itemstack : livingEntity.getArmorItems()) {
                if (itemstack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.BOOTS) {
                    hasBoots = true;
                }
            }

            if (!hasBoots) {
                livingEntity.damage(of(world, SculkInfection.SCULK_ATTRITION), 0.5f);
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1, false, false));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 40, 0, false, false));
                world.playSound(livingEntity instanceof PlayerEntity player ? player : null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.BLOCK_SCULK_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                for (int i = 0; i < 20; i++) {
                    world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.SCULK)), livingEntity.getX() + world.random.nextGaussian() / 5f, livingEntity.getY() + world.random.nextGaussian() / 5f, livingEntity.getZ() + world.random.nextGaussian() / 5f, world.random.nextGaussian() / 9f, world.random.nextFloat() / 4f, world.random.nextGaussian() / 9f);
                }
            }

            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, 1, false, false));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 2, false, false));
        }

        if (entity instanceof ItemEntity itemEntity && world.getBlockState(pos).getBlock() == Blocks.SCULK) {
            itemEntity.setItemAge(MathHelper.clamp(itemEntity.getItemAge(), 5900, 6000));
            world.playSound(itemEntity.getOwner() instanceof PlayerEntity player ? player : null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_SCULK_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            for (int i = 0; i < 10; i++) {
                world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.SCULK)), entity.getX() + world.random.nextGaussian() / 10f, entity.getY() + world.random.nextGaussian() / 10f, entity.getZ() + world.random.nextGaussian() / 10f, world.random.nextGaussian() / 10f, world.random.nextFloat() / 5f, world.random.nextGaussian() / 10f);
            }
        }
    }
}
