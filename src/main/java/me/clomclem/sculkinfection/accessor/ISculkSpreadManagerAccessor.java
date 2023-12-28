package me.clomclem.sculkinfection.accessor;

import net.minecraft.util.math.BlockPos;

public interface ISculkSpreadManagerAccessor {
    default BlockPos getCatalystPos() {
        return null;
    }

    default void setCatalystPos(BlockPos catalystPos) {}
}
