package com.github.alexthe666.citadel.animation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LegSolver {
   public final LegSolver.Leg[] legs;

   public LegSolver(LegSolver.Leg... legs) {
      this.legs = legs;
   }

   public final void update(LivingEntity entity, float scale) {
      this.update(entity, entity.yBodyRot, scale);
   }

   public final void update(LivingEntity entity, float yaw, float scale) {
      double sideTheta = (double)yaw / (180.0 / Math.PI);
      double sideX = Math.cos(sideTheta) * (double)scale;
      double sideZ = Math.sin(sideTheta) * (double)scale;
      double forwardTheta = sideTheta + (Math.PI / 2);
      double forwardX = Math.cos(forwardTheta) * (double)scale;
      double forwardZ = Math.sin(forwardTheta) * (double)scale;

      for (LegSolver.Leg leg : this.legs) {
         leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
      }
   }

   public static final class Leg {
      public final float forward;
      public final float side;
      private final float range;
      private float height;
      private float prevHeight;
      private boolean isWing;

      public Leg(float forward, float side, float range, boolean isWing) {
         this.forward = forward;
         this.side = side;
         this.range = range;
         this.isWing = isWing;
      }

      public final float getHeight(float delta) {
         return this.prevHeight + (this.height - this.prevHeight) * delta;
      }

      public void update(LivingEntity entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
         this.prevHeight = this.height;
         double posY = entity.getY();
         float settledHeight = this.settle(
            entity,
            entity.getX() + sideX * (double)this.side + forwardX * (double)this.forward,
            posY,
            entity.getZ() + sideZ * (double)this.side + forwardZ * (double)this.forward,
            this.height
         );
         this.height = Mth.clamp(settledHeight, -this.range * scale, this.range * scale);
      }

      private float settle(LivingEntity entity, double x, double y, double z, float height) {
         BlockPos pos = new BlockPos(x, y + 0.001, z);
         float dist = this.getDistance(entity.level, pos);
         if ((double)(1.0F - dist) < 0.001) {
            dist = this.getDistance(entity.level, pos.below()) + (float)y % 1.0F;
         } else {
            dist = (float)((double)dist - (1.0 - y % 1.0));
         }

         if (entity.isOnGround() && height <= dist) {
            return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
         } else {
            return height > 0.0F ? Math.max(height - this.getRiseSpeed(), dist) : height;
         }
      }

      private float getDistance(Level world, BlockPos pos) {
         BlockState state = world.getBlockState(pos);
         AABB aabb = state.getBlockSupportShape(world, pos).getFaceShape(Direction.UP).bounds();
         return aabb == null ? 1.0F : 1.0F - Math.min((float)aabb.maxY, 1.0F);
      }

      protected float getFallSpeed() {
         return 0.25F;
      }

      protected float getRiseSpeed() {
         return 0.25F;
      }
   }
}
