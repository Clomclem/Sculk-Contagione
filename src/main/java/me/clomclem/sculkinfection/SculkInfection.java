package me.clomclem.sculkinfection;

import me.clomclem.sculkinfection.block.entity.SculkBlockEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SculkInfection implements ModInitializer {
	public static final String ID = "sculkinfection";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static BlockEntityType<SculkBlockEntity> SCULK_BLOCK_ENTITY;

	public static final RegistryKey<DamageType> SCULK_ATTRITION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(ID, "sculk_attrition"));

	public static List<BlockPos> getNeighbors(BlockPos pos)
	{
		List<BlockPos> neighbors = new ArrayList<>();
		neighbors.add(pos.up());
		neighbors.add(pos.down());
		neighbors.add(pos.west());
		neighbors.add(pos.east());
		neighbors.add(pos.north());
		neighbors.add(pos.south());

		neighbors.add(pos.west().up());
		neighbors.add(pos.east().up());
		neighbors.add(pos.north().up());
		neighbors.add(pos.south().up());

		neighbors.add(pos.west().down());
		neighbors.add(pos.east().down());
		neighbors.add(pos.north().down());
		neighbors.add(pos.south().down());

		neighbors.add(pos.west().north());
		neighbors.add(pos.east().south());
		neighbors.add(pos.north().east());
		neighbors.add(pos.south().west());
		return neighbors;
	}

	public static DamageSource of(World world, RegistryKey<DamageType> key) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
	}

	@Override
	public void onInitialize() {
		SCULK_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				new Identifier(ID, "sculk_block_entity"),
				FabricBlockEntityTypeBuilder.create(SculkBlockEntity::new, Blocks.SCULK).build()
		);
	}
}