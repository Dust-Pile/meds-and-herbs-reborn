package com.plank.meds_and_herbs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MedicineData(ResourceLocation typeId, int uses) {
    public static final Codec<MedicineData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(MedicineData::typeId),
                    Codec.INT.fieldOf("uses").forGetter(MedicineData::uses)
            ).apply(instance, MedicineData::new)
    );

    // ---------- StreamCodec ----------
    public static final StreamCodec<ByteBuf, MedicineData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MedicineData::typeId,
            ByteBufCodecs.INT, MedicineData::uses,
            MedicineData::new
    );


    // 创建 DataComponentType
    public static DataComponentType<MedicineData> createComponentType() {
        return DataComponentType.<MedicineData>builder()
                .persistent(CODEC)
                .networkSynchronized(STREAM_CODEC)
                .build();
    }
}

