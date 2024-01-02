package me.clomclem.sculkcontagione.block.entity;

import me.clomclem.sculkcontagione.SculkContagione;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkBlockEntity extends BlockEntity {

    private Block previousBlock;
    private BlockPos catalystPos;
    private int blockAmount;
    private float counter = 0.0f;
    public SculkBlockEntity(BlockPos pos, BlockState state) {
        super(SculkContagione.SCULK_BLOCK_ENTITY, pos, state);
        previousBlock = null;
        catalystPos = null;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkBlockEntity blockEntity) {
        if (blockEntity.previousBlock != null && blockEntity.catalystPos != null) {
            if (world.getBlockState(blockEntity.catalystPos).getBlock() != Blocks.SCULK_CATALYST) {
                blockEntity.counter += 0.05f;
                if (blockEntity.counter >= MathHelper.sqrt(blockEntity.blockAmount)) {
                    world.removeBlockEntity(pos);
                    world.setBlockState(pos, blockEntity.previousBlock.getDefaultState());
                    world.playSound(null, pos, SoundEvents.BLOCK_SCULK_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    List<BlockPos> neighbours = SculkContagione.getNeighbors(pos);
                    for (BlockPos pos1 : neighbours) {
                        Block block = world.getBlockState(pos1).getBlock();
                        if (block == Blocks.SCULK_VEIN || block == Blocks.SCULK_SHRIEKER || block == Blocks.SCULK_SENSOR) {
                            world.setBlockState(pos1, Blocks.AIR.getDefaultState());
                        }
                    }
                }
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

    public int getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (previousBlock != null) {
            nbt.putString("previous_block", Registries.BLOCK.getId(previousBlock).toString());
        }
        if (catalystPos != null) {
            nbt.putIntArray("catalyst_pos", List.of(catalystPos.getX(), catalystPos.getY(), catalystPos.getZ()));
        }
        nbt.putInt("block_amount", blockAmount);
        nbt.putFloat("counter", counter);
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
        blockAmount = nbt.getInt("block_amount");
        counter = nbt.getFloat("counter");
    }

}
