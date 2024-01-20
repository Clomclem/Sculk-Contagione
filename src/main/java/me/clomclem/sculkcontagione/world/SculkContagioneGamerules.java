package me.clomclem.sculkcontagione.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class SculkContagioneGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> EVERYTHING_TURNS_INTO_SCULK = GameRuleRegistry.register("everythingTurnsIntoSculk", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanRule> SCULK_SPREAD_SPAWN_WARDEN = GameRuleRegistry.register("sculkSpreadSpawnWarden", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> SCULK_SPREAD_REVERTS = GameRuleRegistry.register("sculkSpreadReverts", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> SCULK_SPREAD_GENERATES_CATALYSTS = GameRuleRegistry.register("sculkSpreadGeneratesCatalysts", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPREAD_TICK_DELAY = GameRuleRegistry.register("sculkCatalystSpreadTickdelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(40, 0));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPREAD_AMOUNT = GameRuleRegistry.register("sculkCatalystSpreadAmount", GameRules.Category.MISC, GameRuleFactory.createIntRule(5, 0));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPAWN_DELAY = GameRuleRegistry.register("sculkCatalystSpawnDelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(400, 0));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPAWN_RADIUS = GameRuleRegistry.register("sculkCatalystSpawnRadius", GameRules.Category.MISC, GameRuleFactory.createIntRule(20, 1));

    public static void initialize() {}
}
