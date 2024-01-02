package me.clomclem.sculkinfection.entity.effect;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SculkSporesStatusEffect extends StatusEffect {
    public SculkSporesStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 6441);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof WardenEntity) {
            return;
        }
        entity.getWorld().setBlockState(entity.getBlockPos(), Blocks.SCULK_CATALYST.getDefaultState());
        if (!(entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))) {
            entity.kill();
        }
    }

}
