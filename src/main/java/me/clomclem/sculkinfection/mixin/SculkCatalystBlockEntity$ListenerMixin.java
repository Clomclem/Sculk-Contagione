package me.clomclem.sculkinfection.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.clomclem.sculkinfection.SculkInfection;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkCatalystBlockEntity.Listener.class)
public abstract class SculkCatalystBlockEntity$ListenerMixin {
    @ModifyReturnValue(method = "getRange", at = @At("RETURN"))
    private int modifyRange(int original) {
        return original * 4;
    }

    @Inject(method = "listen", at = @At("TAIL"), cancellable = true)
    private void onListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos, CallbackInfoReturnable<Boolean> cir) {
        if (event == GameEvent.STEP) {
            Entity i = emitter.sourceEntity();
            if (i instanceof LivingEntity livingEntity) {
                if (world.getRandom().nextInt(200) == 0 && !livingEntity.hasStatusEffect(SculkInfection.SCULK_SPORES)) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(SculkInfection.SCULK_SPORES, 12000));
                }

                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
