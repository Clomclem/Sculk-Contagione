package me.clomclem.sculkinfection.block.entity;

import me.clomclem.sculkinfection.SculkInfection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkBlockEntity extends BlockEntity {

    private Block previousBlock;
    private BlockPos catalystPos;
    public SculkBlockEntity(BlockPos pos, BlockState state) {
        super(SculkInfection.SCULK_BLOCK_ENTITY, pos, state);
        previousBlock = null;
        catalystPos = null;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkBlockEntity blockEntity) {
        if (blockEntity.previousBlock != null && blockEntity.catalystPos != null) {
            if (world.getBlockState(blockEntity.catalystPos).getBlock() != Blocks.SCULK_CATALYST) {
                world.removeBlockEntity(pos);
                world.setBlockState(pos, blockEntity.previousBlock.getDefaultState());
                world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
            }
        }
    }

    @Nullable
    public Block getPreviousBlock() {
        return previousBlock;
    }

    public void setPreviousBlock(Block previousBlock) {
        this.previousBlock = previousBlock;
    }

    @Nullable
    public BlockPos getCatalystPos() {
        return catalystPos;
    }

    public void setCatalystPos(BlockPos catalystPos) {
        this.catalystPos = catalystPos;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (previousBlock != null) {
            nbt.putString("previous_block", Registries.BLOCK.getId(previousBlock).toString());
        }
        if (catalystPos != null) {
            nbt.putIntArray("catalyst_pos", List.of(catalystPos.getX(), catalystPos.getY(), catalystPos.getZ()));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("previous_block")) {
            previousBlock = Registries.BLOCK.get(new Identifier(nbt.getString("previous_block")));
        }
        if (nbt.contains("catalyst_pos")) {
            int[] pos = nbt.getIntArray("catalyst_pos");
            catalystPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

}
