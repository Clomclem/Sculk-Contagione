package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {"net/minecraft/block/entity/SculkSensorBlockEntity$VibrationCallback", "net/minecraft/block/entity/CalibratedSculkSensorBlockEntity$Callback", "net/minecraft/block/entity/SculkShriekerBlockEntity$VibrationCallback", "net/minecraft/entity/mob/WardenEntity$VibrationCallback"})
public abstract class SculkSensorBlockEntityVibrationCallbackMixin implements Vibrations.Callback {
    @Unique
    private World world;

    @ModifyReturnValue(method = "getRange", at = @At("RETURN"))
    private int modifyRange(int original) {
        return original * (world != null ? world.getGameRules().getInt(SculkContagioneGamerules.SCULK_HEAR_RADIUS) : 4);
    }

    @ModifyReturnValue(method = "accepts", at = @At("RETURN"))
    private boolean onAccept(boolean original, ServerWorld world, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
        if (this.world == null) {
            this.world = world;
        }
        if ((emitter.sourceEntity() instanceof LivingEntity livingEntity && livingEntity.isSculk()) || (emitter.sourceEntity() instanceof ItemEntity itemEntity && itemEntity.getOwner() instanceof LivingEntity living && living.isSculk())) {
            return false;
        } else {
            return original;
        }
    }
}
