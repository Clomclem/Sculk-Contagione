/*
    Code taken from Sculkhunt (https://github.com/Ladysnake/Sculkhunt/blob/main/src/main/java/ladysnake/sculkhunt/mixin/client/PlayerEntityRendererMixin.java),
    licensed under GPLv3.
    All credits go to doctor4t, Pyrofab and the Ladysnake team.
 */

package me.clomclem.sculkcontagione.mixin;

import me.clomclem.sculkcontagione.SculkContagione;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier SCULK_TEXTURE = new Identifier(SculkContagione.ID, "textures/entity/sculk_64x64.png");

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Shadow
    protected abstract void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity_1);

    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    private void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertices, int lightmap, AbstractClientPlayerEntity renderedPlayer, CallbackInfo ci) {
        if (renderedPlayer.isSculk() && renderSculkArm(matrices, vertices, renderedPlayer, lightmap, false)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    private void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertices, int lightmap, AbstractClientPlayerEntity renderedPlayer, CallbackInfo ci) {
        if (renderedPlayer.isSculk() && renderSculkArm(matrices, vertices, renderedPlayer, lightmap, false)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean renderSculkArm(MatrixStack matrices, VertexConsumerProvider vertices, AbstractClientPlayerEntity renderedPlayer, int lightmapCoordinates, boolean rightArm) {
        EntityRenderer<? super LivingEntity> possessedRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(renderedPlayer);
        // If the mob has an arm, render it instead of the player's
        if (possessedRenderer instanceof FeatureRendererContext) {
            Model possessedModel = ((FeatureRendererContext<?, ?>) possessedRenderer).getModel();
            if (possessedModel instanceof BipedEntityModel) {
                @SuppressWarnings("unchecked") BipedEntityModel<LivingEntity> bipedModel = (BipedEntityModel<LivingEntity>) possessedModel;
                PlayerEntityModel<AbstractClientPlayerEntity> playerModel = this.getModel();
                this.setModelPose(renderedPlayer);
                bipedModel.rightArmPose = playerModel.rightArmPose;
                bipedModel.handSwingProgress = 0.0F;
                bipedModel.sneaking = false;
                bipedModel.setAngles(renderedPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                bipedModel.rightArm.pitch = 0.0F;
                bipedModel.rightArm.render(matrices, vertices.getBuffer(possessedModel.getLayer(SCULK_TEXTURE)), lightmapCoordinates, OverlayTexture.DEFAULT_UV);
            }
        }
        return true;
    }

    // no name for sculk players
    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V", at = @At("HEAD"), cancellable = true)
    protected void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        if (abstractClientPlayerEntity.isSculk()) {
            ci.cancel();
        }
    }
}