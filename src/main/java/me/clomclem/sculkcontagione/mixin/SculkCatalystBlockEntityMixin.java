package me.clomclem.sculkcontagione.mixin;

import me.clomclem.sculkcontagione.SculkContagioneAttachmentTypes;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SculkCatalystBlockEntity.class)
public abstract class SculkCatalystBlockEntityMixin extends BlockEntity implements GameEventListener.Holder<SculkCatalystBlockEntity.Listener> {

    @Shadow
    @Final
    private SculkCatalystBlockEntity.Listener eventListener;

    public SculkCatalystBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.eventListener.getSpreadManager().setCatalystPos(pos);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        ((AttachmentTarget) world).getAttachedOrCreate(SculkContagioneAttachmentTypes.WORLD_CATALYST_LIST).add(pos);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        List<BlockPos> blockPosList = ((AttachmentTarget) getWorld()).getAttachedOrCreate(SculkContagioneAttachmentTypes.WORLD_CATALYST_LIST);
        if (blockPosList.contains(pos)) {
            blockPosList.remove(pos);
        }
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        List<BlockPos> blockPosList = ((AttachmentTarget) getWorld()).getAttachedOrCreate(SculkContagioneAttachmentTypes.WORLD_CATALYST_LIST);
        if (!blockPosList.contains(pos)) {
            blockPosList.add(pos);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void onTick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity, CallbackInfo ci) {
        Random rand = world.getRandom();
        final int sculkCatalystSpreadTickdelay = world.getGameRules().getInt(SculkContagioneGamerules.SCULK_CATALYST_SPREAD_TICK_DELAY);
        final int sculkCatalystSpreadAmount = world.getGameRules().getInt(SculkContagioneGamerules.SCULK_CATALYST_SPREAD_AMOUNT);
        if ((sculkCatalystSpreadTickdelay == 0 || rand.nextInt(sculkCatalystSpreadTickdelay) == 0) && sculkCatalystSpreadAmount != 0) {
            SculkCatalystBlockEntity.Listener listener = blockEntity.getEventListener();
            listener.getSpreadManager().spread(BlockPos.ofFloored(pos.add(rand.nextBetween(-3, 3), rand.nextBetween(-1, 1), rand.nextBetween(-3, 3)).toCenterPos()), sculkCatalystSpreadAmount);

            listener.getPositionSource().getPos(world).ifPresent(pos1 -> listener.bloom((ServerWorld) world, BlockPos.ofFloored(pos1), listener.state, rand));
        }
    }
}
