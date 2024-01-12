package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/block/entity/SculkSensorBlockEntity$VibrationCallback")
public abstract class SculkSensorBlockEntityVibrationCallbackMixin implements Vibrations.Callback {
    @ModifyReturnValue(method = "accepts", at = @At("RETURN"))
    private boolean onAccept(boolean original, ServerWorld world, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
        if (emitter.sourceEntity() instanceof LivingEntity livingEntity && livingEntity.isSculk()) {
            return false;
        } else {
            return original;
        }
    }
}
