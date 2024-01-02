package me.clomclem.sculkcontagione.mixin;

import me.clomclem.sculkcontagione.accessor.ISculkSpreadManagerAccessor;
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

    @Unique
    private int blockAmount = 0;

    @Nullable
    @Override
    public BlockPos getCatalystPos() {
        return catalystPos;
    }

    @Override
    public void setCatalystPos(BlockPos catalystPos) {
        this.catalystPos = catalystPos;
    }

    @Override
    public int getBlockAmount() {
        return blockAmount;
    }

    @Override
    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        if (catalystPos != null) {
            nbt.putIntArray("catalyst_pos", List.of(catalystPos.getX(), catalystPos.getY(), catalystPos.getZ()));
        }
        nbt.putInt("block_amount", blockAmount);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("catalyst_pos")) {
            int[] pos = nbt.getIntArray("catalyst_pos");
            catalystPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
        blockAmount = nbt.getInt("block_amount");
    }
}
