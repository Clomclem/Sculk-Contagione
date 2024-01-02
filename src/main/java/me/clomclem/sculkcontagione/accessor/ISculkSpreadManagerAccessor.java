package me.clomclem.sculkcontagione.accessor;

import net.minecraft.util.math.BlockPos;

public interface ISculkSpreadManagerAccessor {
    default BlockPos getCatalystPos() {
        return null;
    }

    default void setCatalystPos(BlockPos catalystPos) {}

    default int getBlockAmount() {return 0;}

    default void setBlockAmount(int amount) {}
}
