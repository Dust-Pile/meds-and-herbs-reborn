package com.plank.meds_and_herbs.procedures;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface VoxelShapeHelper {

    static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        if (from == to) return shape;
        // 计算顺时针旋转次数（0-3）
        int rotations = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        VoxelShape result = shape;
        for (int i = 0; i < rotations; i++) {
            result = rotateClockwise(result);
        }
        return result;
    }

    private static VoxelShape rotateClockwise(VoxelShape shape) {
        var boxes = shape.toAabbs();
        VoxelShape result = Shapes.empty();
        for (var box : boxes) {
            // 顺时针旋转90度：(x, z) -> (z, 1 - x)
            double minX = 1 - box.maxZ;
            double minZ = box.minX;
            double maxX = 1 - box.minZ;
            double maxZ = box.maxX;
            VoxelShape rotatedBox = Shapes.box(minX, box.minY, minZ, maxX, box.maxY, maxZ);
            result = Shapes.or(result, rotatedBox);
        }
        return result;
    }
}
