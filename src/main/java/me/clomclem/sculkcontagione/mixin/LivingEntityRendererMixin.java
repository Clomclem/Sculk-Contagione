/*
    Code taken from Sculkhunt (https://github.com/Ladysnake/Sculkhunt/blob/main/src/main/java/ladysnake/sculkhunt/mixin/client/LivingEntityRendererMixin.java),
    licensed under GPLv3.
    All credits go to doctor4t, Pyrofab and the Ladysnake team.
 */

package me.clomclem.sculkcontagione.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.clomclem.sculkcontagione.SculkContagione;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    private Identifier texture;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyExpressionValue(method = "getRenderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier changeTexture(Identifier originalTexture, T entity, boolean showBody, boolean translucent, boolean showOutline) throws IOException {
        if (((LivingEntity) entity).isSculk()) {
            if (this.texture == null) {
                String textureSize = "64x64";
                NativeImage image = NativeImage.read(MinecraftClient.getInstance().getResourceManager().open(originalTexture));
                int width = image.getWidth();
                int height = image.getHeight();
                if (width == 32 && height == 32) {
                    textureSize = "32x32";
                } else if (width == 64 && height == 32) {
                    textureSize = "64x32";
                } else if (width == 64 && height == 64) {
                    textureSize = "64x64";
                } else if (width == 128 && height == 64) {
                    textureSize = "128x64";
                }

                this.texture = new Identifier(SculkContagione.ID, "textures/entity/sculk_" + textureSize + ".png");
            }

            return this.texture;
        } else {
            return originalTexture;
        }
    }
}
