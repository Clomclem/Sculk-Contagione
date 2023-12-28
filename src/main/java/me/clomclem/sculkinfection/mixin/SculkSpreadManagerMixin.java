package me.clomclem.sculkinfection.mixin;

import me.clomclem.sculkinfection.accessor.ISculkSpreadManagerAccessor;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SculkSpreadManager.class)
public abstract class SculkSpreadManagerMixin implements ISculkSpreadManagerAccessor {
    @Unique
    private BlockPos catalystPos;

    @Nullable
    @Override
    public BlockPos getCatalystPos() {
        return catalystPos;
    }

    @Override
    public void setCatalystPos(BlockPos catalystPos) {
        this.catalystPos = catalystPos;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        if (catalystPos != null) {
            nbt.putIntArray("catalyst_pos", List.of(catalystPos.getX(), catalystPos.getY(), catalystPos.getZ()));
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("catalyst_pos")) {
            int[] pos = nbt.getIntArray("catalyst_pos");
            catalystPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }
}
