package me.clomclem.sculkinfection.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class SculkInfectionGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> EVERYTHING_TURNS_INTO_SCULK = GameRuleRegistry.register("everythingTurnsIntoSculk", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanRule> SCULK_SPREAD_SPAWN_WARDEN = GameRuleRegistry.register("sculkSpreadSpawnWarden", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPREAD_TICK_DELAY = GameRuleRegistry.register("sculkCatalystSpreadTickdelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(40, 0));

    public static final GameRules.Key<GameRules.IntRule> SCULK_CATALYST_SPREAD_AMOUNT = GameRuleRegistry.register("sculkCatalystSpreadAmount", GameRules.Category.MISC, GameRuleFactory.createIntRule(5, 0));

    public static void initialize() {}
}
