package me.clomclem.sculkinfection.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkCatalystBlockEntity.Listener.class)
public abstract class SculkCatalystBlockEntity$ListenerMixin {
    @ModifyReturnValue(method = "getRange", at = @At("RETURN"))
    private int modifyRange(int original) {
        return original * 4;
    }
}
