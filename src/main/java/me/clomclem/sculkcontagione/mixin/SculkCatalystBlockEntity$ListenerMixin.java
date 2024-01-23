package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkCatalystBlockEntity.Listener.class)
public abstract class SculkCatalystBlockEntity$ListenerMixin implements GameEventListener {
    @Unique
    private World world;

    @ModifyReturnValue(method = "getRange", at = @At("RETURN"))
    private int modifyRange(int original) {
        return original * (world != null ? world.getGameRules().getInt(SculkContagioneGamerules.SCULK_HEAR_RADIUS) : 4);
    }

    @Inject(method = "listen", at = @At("HEAD"), cancellable = true)
    private void onDetect(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos, CallbackInfoReturnable<Boolean> cir) {
        if (this.world == null) {
            this.world = world;
        }
        if (emitter.sourceEntity() instanceof LivingEntity livingEntity && livingEntity.isSculk()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "listen", at = @At("TAIL"), cancellable = true)
    private void onListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos, CallbackInfoReturnable<Boolean> cir) {
        if (event == GameEvent.STEP) {
            Entity i = emitter.sourceEntity();
            if (i instanceof LivingEntity livingEntity && !(livingEntity instanceof WardenEntity)) {
                if (world.getRandom().nextInt(200) == 0 && !livingEntity.hasStatusEffect(SculkContagione.SCULK_SPORES)) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(SculkContagione.SCULK_SPORES, 6000));
                }

                cir.setReturnValue(true);
            }
        }
    }
}
