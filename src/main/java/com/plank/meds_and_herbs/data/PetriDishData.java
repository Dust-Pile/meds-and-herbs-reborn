package com.plank.meds_and_herbs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PetriDishData(int progress, int maxProgress) {
    public static final Codec<PetriDishData> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("progress").forGetter(PetriDishData::progress),
                    Codec.INT.fieldOf("maxProgress").forGetter(PetriDishData::maxProgress)
            ).apply(inst, PetriDishData::new)
    );

    public static final StreamCodec<ByteBuf, PetriDishData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PetriDishData::progress,
            ByteBufCodecs.INT, PetriDishData::maxProgress,
            PetriDishData::new
    );

    public boolean isComplete() {
        return progress >= maxProgress;
    }

    // 创建 DataComponentType
    public static DataComponentType<PetriDishData> createComponentType() {
        return DataComponentType.<PetriDishData>builder()
                .persistent(CODEC)
                .networkSynchronized(STREAM_CODEC)
                .build();
    }
}