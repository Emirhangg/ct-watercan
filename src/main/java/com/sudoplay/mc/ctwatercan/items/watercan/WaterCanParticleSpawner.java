package com.sudoplay.mc.ctwatercan.items.watercan;

import com.sudoplay.mc.ctwatercan.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;

/* package */ class WaterCanParticleSpawner implements
    IWaterCanParticleSpawner {

  public static final IWaterCanParticleSpawner INSTANCE = new WaterCanParticleSpawner();

  @Override
  public void spawnParticles(
      World world,
      double x,
      double y,
      double z,
      int range
  ) {

    double rx;
    double rz;
    double xCoord;
    double yCoord;
    double zCoord;
    BlockPos pos;

    int numParticles = 1 + range * 4;

    for (int j = 0; j < numParticles; j++) {
      rx = Util.RANDOM.nextGaussian();
      rz = Util.RANDOM.nextGaussian();
      xCoord = x + rx * 0.6 * range;
      zCoord = z + rz * 0.6 * range;

      pos = this.getParticleSpawnBlockPos(world, xCoord, y, zCoord);

      if (pos == null) {
        continue;
      }

      yCoord = pos.getY() + 0.25;

      for (int i = 0; i < 4; i++) {
        world.spawnParticle(
            EnumParticleTypes.WATER_SPLASH,
            xCoord,
            yCoord,
            zCoord,
            0.0,
            0.0,
            0.0
        );
      }
    }
  }

  /**
   * Returns suitable spawn position for the water particles by checking blocks in a column.
   * Will return null if no suitable spawn position is found.
   *
   * @param world world
   * @param x     x
   * @param y     y
   * @param z     z
   * @return spawn position or null
   */
  @Nullable
  private BlockPos getParticleSpawnBlockPos(
      World world,
      double x,
      double y,
      double z
  ) {

    int blockX = (int) Math.floor(x);
    int blockY = (int) Math.floor(y);
    int blockZ = (int) Math.floor(z);

    BlockPos pos = new BlockPos(blockX, blockY + 5, blockZ);
    BlockPos nextPos;

    for (int y2 = pos.getY(); y2 >= blockY - 5; y2--) {
      nextPos = pos.down();

      if (this.canSpawnParticlesAtBlockPos(world, pos, nextPos)) {
        return pos;
      }
      pos = nextPos;
    }

    return null;
  }

  /**
   * Uses a block and the block below it to evaluate if pos is a suitable particle spawn position.
   *
   * @param world   world
   * @param pos     candidate pos
   * @param nextPos pos below pos
   * @return true if suitable particle spawn location
   */
  private boolean canSpawnParticlesAtBlockPos(
      World world,
      BlockPos pos,
      BlockPos nextPos
  ) {

    Block block = world.getBlockState(pos).getBlock();
    Block nextBlock = world.getBlockState(nextPos).getBlock();

    if (world.isAirBlock(pos)
        && !world.isAirBlock(nextPos)) {

      if (world.isBlockNormalCube(nextPos, true)
          || nextBlock == Blocks.FARMLAND) {
        return true;
      }
    }

    if (block instanceof IGrowable
        || block instanceof IPlantable) {

      if (world.isBlockNormalCube(nextPos, true)
          || nextBlock == Blocks.FARMLAND) {
        return true;
      }
    }

    return false;
  }

}