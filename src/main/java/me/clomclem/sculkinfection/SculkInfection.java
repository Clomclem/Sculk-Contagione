package me.clomclem.sculkinfection;

import me.clomclem.sculkinfection.block.entity.SculkBlockEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SculkInfection implements ModInitializer {
	public static final String ID = "sculkinfection";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static BlockEntityType<SculkBlockEntity> SCULK_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		SCULK_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				new Identifier(ID, "sculk_block_entity"),
				FabricBlockEntityTypeBuilder.create(SculkBlockEntity::new, Blocks.SCULK).build()
		);
	}
}