package me.clomclem.sculkcontagione.mixin;

import me.clomclem.sculkcontagione.SculkContagione;
import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.clomclem.sculkcontagione.SculkContagione.of;

@SuppressWarnings("deprecation")
@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements ItemConvertible, FabricBlock {
    @Shadow public abstract RegistryEntry.Reference<Block> getRegistryEntry();

    public BlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onSteppedOn", at = @At("TAIL"))
    private void whenSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (this.getRegistryEntry().isIn(SculkContagione.SCULK)) {
            if (entity instanceof LivingEntity livingEntity && !(livingEntity instanceof WardenEntity)) {
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
                    livingEntity.damage(of(world, SculkContagione.SCULK_ATTRITION), 0.5f);
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

            if (entity instanceof ItemEntity itemEntity) {
                if (itemEntity.getStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock().getRegistryEntry().isIn(SculkContagione.SCULK)) {
                    return;
                }
                itemEntity.setItemAge(MathHelper.clamp(itemEntity.getItemAge(), 5900, 6000));
                world.playSound(itemEntity.getOwner() instanceof PlayerEntity player ? player : null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_SCULK_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                for (int i = 0; i < 10; i++) {
                    world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.SCULK)), entity.getX() + world.random.nextGaussian() / 10f, entity.getY() + world.random.nextGaussian() / 10f, entity.getZ() + world.random.nextGaussian() / 10f, world.random.nextGaussian() / 10f, world.random.nextFloat() / 5f, world.random.nextGaussian() / 10f);
                }
            }
        }
    }

}
