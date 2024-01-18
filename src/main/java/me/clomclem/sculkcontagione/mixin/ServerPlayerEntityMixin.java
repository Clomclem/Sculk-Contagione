package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import me.clomclem.sculkcontagione.SculkContagione;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }


    @Inject(method = "onDeath", at = @At("HEAD"))
    private void checkOnDeath(DamageSource damageSource, CallbackInfo ci, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        shouldTurnToSculk.set(damageSource.isOf(SculkContagione.SCULK_ATTRITION) && !this.isSculk() && this.random.nextBoolean());
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"), cancellable = true)
    private void afterDeath(DamageSource damageSource, CallbackInfo ci, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        if (shouldTurnToSculk.get()) {
            this.setSculk(true);
            this.disableExperienceDropping();
            this.clearStatusEffects();
            this.setHealth(this.getMaxHealth());
            this.requestRespawn();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, StatusEffectInstance.INFINITE, 0, false, false, false));
            getServerWorld().playSoundFromEntity(this, this, SoundEvents.ENTITY_WARDEN_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            ci.cancel();
        }
    }

    @WrapWithCondition(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")
    )
    private boolean onSendPacket(ServerPlayNetworkHandler instance, Packet<?> packet, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        return !shouldTurnToSculk.get();
    }

    @WrapWithCondition(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V")
    )
    private boolean onSend(ServerPlayNetworkHandler instance, Packet<?> packet, PacketCallbacks packetCallbacks, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        return !shouldTurnToSculk.get();
    }
}
