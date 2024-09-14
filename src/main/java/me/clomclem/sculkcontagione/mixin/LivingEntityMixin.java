package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.clomclem.sculkcontagione.SculkContagione;
import me.clomclem.sculkcontagione.accessor.ILivingEntityAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, ILivingEntityAccessor {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract void disableExperienceDropping();

    @Unique
    private static final TrackedData<Boolean> IS_SCULK = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE = entity -> !entity.isSculk() && !(entity instanceof WardenEntity);

    @Unique
    private boolean shouldDropLoot;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(EntityType<?> entityType, World world, CallbackInfo ci) {
        this.shouldDropLoot = true;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IS_SCULK, false);
    }

    @Override
    public boolean isSculk() {
        return getDataTracker().get(IS_SCULK);
    }

    @Override
    public void setSculk(boolean isSculk) {
        getDataTracker().set(IS_SCULK, isSculk);
    }

    @Override
    public void setShouldDropLoot(boolean shouldDropLoot) {
        this.shouldDropLoot = shouldDropLoot;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("isSculk", isSculk());
        nbt.putBoolean("shouldDropLoot", shouldDropLoot);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        setSculk(nbt.getBoolean("isSculk"));
        shouldDropLoot = nbt.getBoolean("shouldDropLoot");
    }

    @Override
    public boolean isFireImmune() {
        if (isSculk()) {
            return true;
        } else {
            return super.isFireImmune();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || (isSculk() && (damageSource.isOf(DamageTypes.LAVA) || damageSource.isOf(DamageTypes.DROWN) || damageSource.isOf(DamageTypes.FREEZE) || damageSource.isOf(DamageTypes.FALL)
                || damageSource.isOf(DamageTypes.IN_WALL) || damageSource.isOf(DamageTypes.IN_FIRE) || damageSource.isOf(DamageTypes.ON_FIRE) || damageSource.isOf(DamageTypes.SONIC_BOOM)));
    }

    @ModifyReturnValue(
            method = "canBreatheInWater",
            at = @At("RETURN")
    )
    private boolean modifyCanBreathe(boolean original) {
        if (isSculk()) {
            return true;
        } else {
            return original;
        }
    }

    @ModifyReturnValue(
            method = "shouldDropLoot",
            at = @At("RETURN")
    )
    private boolean modifyShouldDropLoot(boolean original) {
        return original && shouldDropLoot;
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void sculkSporesDeath(DamageSource damageSource, CallbackInfo ci) {
        if (!this.getWorld().isClient && this.hasStatusEffect(SculkContagione.SCULK_SPORES)) {
            this.getWorld().setBlockState(this.getBlockPos(), Blocks.SCULK_CATALYST.getDefaultState());
            this.disableExperienceDropping();
            shouldDropLoot = false;
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;update()V"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (!this.getWorld().isClient && damageSource.isOf(SculkContagione.SCULK_ATTRITION) && !this.isSculk() && this.random.nextBoolean() && this.getType() != EntityType.PLAYER) {
            ServerWorld world = (ServerWorld) this.getWorld();
            LivingEntity entity = (LivingEntity) this.getType().create(world);
            entity.setSculk(true);
            entity.setPosition(this.getPos());
            entity.setYaw(this.getYaw());
            entity.setPitch(this.getPitch());
            entity.setPose(this.getPose());
            entity.disableExperienceDropping();
            entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(entity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * 1.5);
            entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(entity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 1.5);
            entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(entity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 2.0);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, StatusEffectInstance.INFINITE, 0, false, false, false));
            this.shouldDropLoot = false;

            if (this.hasCustomName()) {
                entity.setCustomName(this.getCustomName());
            }

            if (entity instanceof MobEntity mobEntity) {
                if (mobEntity instanceof PathAwareEntity pathAwareEntity) {
                    ((MobEntityAccessor)pathAwareEntity).getGoalSelector().add(0, new MeleeAttackGoal(pathAwareEntity, 1.0, false));
                }
                ((MobEntityAccessor)mobEntity).getTargetSelector().add(0, new ActiveTargetGoal<>(mobEntity, LivingEntity.class, false, CAN_ATTACK_PREDICATE));
            }
            world.spawnEntity(entity);
        }
    }
}
