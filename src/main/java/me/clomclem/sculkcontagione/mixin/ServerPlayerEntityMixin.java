package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.SculkContagioneAttachmentTypes;
import me.clomclem.sculkcontagione.world.SculkContagioneGamerules;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow
    public abstract ServerWorld getServerWorld();

    @Shadow protected abstract void forgiveMobAnger();

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean isSpectator();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        final int spawnDelay = getServerWorld().getGameRules().getInt(SculkContagioneGamerules.SCULK_CATALYST_SPAWN_DELAY);
        if (!(isCreative() || isSpectator() || isSculk()) && spawnDelay != 0
                && getRandom().nextInt(spawnDelay) == 0) {
            final int range = getServerWorld().getGameRules().getInt(SculkContagioneGamerules.SCULK_CATALYST_SPAWN_RADIUS);
            World world = getWorld();
            BlockPos pos = getBlockPos().add(random.nextBetween(-range, range), random.nextBetween(-range, range), random.nextBetween(-range, range));
            int counter = 0;
            while (!world.getBlockState(pos).isAir() || world.getBlockState(pos.down()).isAir() ||
                    !world.getBlockState(pos.down()).isFullCube(world, pos) || pos == getBlockPos()) {
                pos = getBlockPos().add(random.nextBetween(-range, range), random.nextBetween(-range, range), random.nextBetween(-range, range));
                counter++;
                if (counter > 10) {
                    return;
                }
            }
            world.setBlockState(pos, Blocks.SCULK_CATALYST.getDefaultState());
        }
    }


    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void checkOnDeath(DamageSource damageSource, CallbackInfo ci, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        if (!this.isSculk()) {
            shouldTurnToSculk.set(damageSource.isOf(SculkContagione.SCULK_ATTRITION) && this.random.nextBoolean());
        } else {
            shouldTurnToSculk.set(false);
            this.emitGameEvent(GameEvent.ENTITY_DIE);

            this.dropShoulderEntities();
            if (this.getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
                this.forgiveMobAnger();
            }

            if (!this.isSpectator()) {
                this.drop(getServerWorld(), damageSource);
            }

            this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this, ScoreAccess::incrementScore);
            LivingEntity livingEntity = this.getPrimeAdversary();
            if (livingEntity != null) {
                livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
                this.onKilledBy(livingEntity);
            }

            this.setSculk(true);
            this.disableExperienceDropping();
            this.clearStatusEffects();
            this.setHealth(this.getMaxHealth());
            this.requestRespawn();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, StatusEffectInstance.INFINITE, 0, false, false, false));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, StatusEffectInstance.INFINITE, 0, false, false, false));

            BlockPos closestCatalyst = getClosestCatalyst();

            Random rand = this.getRandom();
            if (closestCatalyst != null) {
                Vec3d pos = closestCatalyst.add(rand.nextBetween(-1, 1), rand.nextBetween(0, 1), rand.nextBetween(-1, 1)).toCenterPos();
                this.teleport(pos.x, pos.y, pos.z, false);
            } else {
                BlockPos spawnPos = getServerWorld().getSpawnPos().add(rand.nextBetween(-1, 1), rand.nextBetween(0, 1), rand.nextBetween(-1, 1));
                Vec3d pos = spawnPos.toCenterPos();
                this.teleport(pos.x, pos.y, pos.z, false);
                if (this.getServerWorld().getBlockState(spawnPos).getBlock() != Blocks.SCULK_CATALYST) {
                    this.getServerWorld().setBlockState(spawnPos, Blocks.SCULK_CATALYST.getDefaultState());
                }
            }

            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.extinguish();
            this.setFrozenTicks(0);
            this.setOnFire(false);
            this.getDamageTracker().update();
            ci.cancel();
        }
    }

    @Nullable
    @Unique
    private BlockPos getClosestCatalyst() {
        List<BlockPos> catalysts = getServerWorld().getAttachedOrCreate(SculkContagioneAttachmentTypes.WORLD_CATALYST_LIST);

        if (catalysts.isEmpty()) {
            return null;
        }

        BlockPos catalyst = catalysts.get(0);

        for (BlockPos pos : catalysts) {
            if (pos.getManhattanDistance(this.getBlockPos()) < catalyst.getManhattanDistance(this.getBlockPos())) {
                catalyst = pos;
            }
        }
        return catalyst;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    private void afterDeath(DamageSource damageSource, CallbackInfo ci, @Share("shouldTurnToSculk") LocalBooleanRef shouldTurnToSculk) {
        if (shouldTurnToSculk.get()) {
            this.setSculk(true);
            this.disableExperienceDropping();
            this.clearStatusEffects();
            this.setHealth(this.getMaxHealth());
            this.requestRespawn();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, StatusEffectInstance.INFINITE, 0, false, false, false));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, StatusEffectInstance.INFINITE, 0, false, false, false));
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * 1.5);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 1.5);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 2.0);
            getServerWorld().playSoundFromEntity(this, this, SoundEvents.ENTITY_WARDEN_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f);
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

    @Inject(method = "consumeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;consumeItem()V"))
    private void onEat(CallbackInfo ci) {
        if (isSculk() && this.activeItemStack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            this.setSculk(false);
            this.clearStatusEffects();
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) / 1.5);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / 1.5);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) / 2.0);
            getServerWorld().playSoundFromEntity(this, this, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
