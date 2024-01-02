package me.clomclem.sculkinfection.mixin;

import me.clomclem.sculkinfection.SculkInfection;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkCatalystBlockEntity.class)
public abstract class SculkCatalystBlockEntityMixin extends BlockEntity implements GameEventListener.Holder<SculkCatalystBlockEntity.Listener> {

    @Shadow @Final private SculkCatalystBlockEntity.Listener eventListener;

    public SculkCatalystBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.eventListener.getSpreadManager().setCatalystPos(pos);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void onTick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity, CallbackInfo ci) {
        Random rand = world.getRandom();
        final int sculkCatalystSpreadTickdelay = world.getGameRules().getInt(SculkInfection.SCULK_CATALYST_SPREAD_TICK_DELAY);
        final int sculkCatalystSpreadAmount = world.getGameRules().getInt(SculkInfection.SCULK_CATALYST_SPREAD_AMOUNT);
        if ((sculkCatalystSpreadTickdelay == 0 || rand.nextInt(sculkCatalystSpreadTickdelay) == 0) && sculkCatalystSpreadAmount != 0) {
            SculkCatalystBlockEntity.Listener listener = blockEntity.getEventListener();
            listener.getSpreadManager().spread(BlockPos.ofFloored(pos.add(rand.nextBetween(-3, 3), rand.nextBetween(-1, 1), rand.nextBetween(-3, 3)).toCenterPos()), sculkCatalystSpreadAmount);

            listener.getPositionSource().getPos(world).ifPresent(pos1 -> listener.bloom((ServerWorld) world, BlockPos.ofFloored(pos1), listener.state, rand));
        }
    }
}
