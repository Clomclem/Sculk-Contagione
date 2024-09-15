package me.clomclem.sculkcontagione.potion;

import me.clomclem.sculkcontagione.SculkContagione;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SculkContagionePotions {
    public static final Potion SCULK_SPORES = register("sculk_spores", new Potion(new StatusEffectInstance(SculkContagione.SCULK_SPORES, 1200)));

    public static Potion register(String name, Potion potion) {
        return Registry.register(Registries.POTION, Identifier.of(SculkContagione.ID, name), potion);
    }

    public static void register() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Registries.POTION.getEntry(Potions.MUNDANE.value()), //todo: remove ghast tear from mundane potion, make ghastly potion. no effect.
                    // Ingredient
                    Items.SCULK_CATALYST,
                    // Output potion.
                    Registries.POTION.getEntry(SCULK_SPORES)
            );
        });
    }
}
