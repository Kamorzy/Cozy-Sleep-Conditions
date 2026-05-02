package kamorzy.cozy_sleep;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SleepConditionChecks {
    private static final int CAMPFIRE_RADIUS = 16;
    private static final int CAMPFIRE_RADIUS_SQUARED = CAMPFIRE_RADIUS * CAMPFIRE_RADIUS;

    private SleepConditionChecks() {
    }

    public static boolean hasLitCampfireNearby(ServerLevel level, BlockPos bedPos) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int xOffset = -CAMPFIRE_RADIUS; xOffset <= CAMPFIRE_RADIUS; xOffset++) {
            for (int yOffset = -CAMPFIRE_RADIUS; yOffset <= CAMPFIRE_RADIUS; yOffset++) {
                for (int zOffset = -CAMPFIRE_RADIUS; zOffset <= CAMPFIRE_RADIUS; zOffset++) {
                    checkPos.set(
                            bedPos.getX() + xOffset,
                            bedPos.getY() + yOffset,
                            bedPos.getZ() + zOffset
                    );

                    if (checkPos.distSqr(bedPos) > CAMPFIRE_RADIUS_SQUARED) {
                        continue;
                    }

                    BlockState state = level.getBlockState(checkPos);

                    if (state.is(BlockTags.CAMPFIRES)
                            && state.hasProperty(CampfireBlock.LIT)
                            && state.getValue(CampfireBlock.LIT)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean hasRoofCoverageAboveBed(ServerLevel level, BlockPos bedPos) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int y = bedPos.getY() + 1; y < level.getMaxY(); y++) {
            checkPos.set(bedPos.getX(), y, bedPos.getZ());

            BlockState state = level.getBlockState(checkPos);

            /*
             * Intentionally counts any non-air block as roof coverage, even string.
             */
            if (!state.isAir()) {
                return true;
            }
        }

        return false;
    }
}