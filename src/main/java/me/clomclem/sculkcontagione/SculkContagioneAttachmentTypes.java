package me.clomclem.sculkcontagione;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SculkContagioneAttachmentTypes {
    public static final AttachmentType<List<BlockPos>> WORLD_CATALYST_LIST = AttachmentRegistry.<List<BlockPos>>builder()
            .persistent(Codec.list(BlockPos.CODEC))
            .initializer(ArrayList::new)
            .buildAndRegister(Identifier.of(SculkContagione.ID, "world_catalyst_list"));
}
