package gr8pefish.openglider.common.effects;

import gr8pefish.openglider.api.item.IGlider;
import gr8pefish.openglider.common.config.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermalEffect implements GliderEffect {

    /**
     * Apply thermal effect, making the player go up or down depending on up or down lift
     * Affected by a variety of things, from player height to glider durability to weather, etc.
     *
     * @param player - the player to move around
     * @param glider - the hang glider item
     */
    public void apply(EntityPlayer player, ItemStack glider) {
        // TODO: Make configurable?
        applyStaticThermals(player, glider);
    }

    public boolean enabled() {
        return ConfigHandler.thermalsEnabled;
    }

    // This applies a static uplift from a heat source on the ground
    private void applyStaticThermals(EntityPlayer player, ItemStack glider) {
        BlockPos pos = player.getPosition();
        World worldIn = player.getEntityWorld();

        int maxSearchDown = 30;
        for (int i = 0; i < maxSearchDown; i++) {
            BlockPos scanpos = pos.down(i);
            Block scanned = worldIn.getBlockState(scanpos).getBlock();
            if (scanned.equals(Blocks.FIRE) ||
                    scanned.equals(Blocks.LAVA) ||
                    scanned.equals(Blocks.FLOWING_LAVA) ||
                    scanned.equals(Blocks.SAND) ||
                    scanned.equals(Blocks.GRASS)
                    ) { //ToDo: configurable

                // make uplift relative to configured fall speed (TODO: Maybe change)
                double upliftAirSpeedMt = 0.2;

                // Add uplift to player yVel
                player.motionY += upliftAirSpeedMt;
//                Logger.info("AFTER: "+player.motionY);


//                double boostAmt = 1 + (0.5 * (maxSearchDown - i));
//                double calculated = (player.motionY - (player.motionY * boostAmt));
//                Logger.info(calculated);
//                Logger.info("BEFORE: "+player.motionY);
//                player.motionY += calculated;
//                Logger.info("AFTER: "+player.motionY);


//                Vec3d vec3d = player.getLookVec();
//                double d0 = 1.5D;
//                double d1 = 0.1D;
////                player.motionX += vec3d.x * d1 + (vec3d.x * d0 - player.motionX) * 0.2D;
////                player.motionZ += vec3d.z * d1 + (vec3d.z * d0 - player.motionZ) * 0.2D;
//                double up_boost;
//                if (i > 0) {
//                    up_boost = -0.07 * i + 0.6;
//                } else {
//                    up_boost = 0.07;
//                }
//                if (up_boost > 0) {
//                    player.addVelocity(0, up_boost, 0);
//
//                    if (ConfigHandler.airResistanceEnabled) {
//                        player.motionX *= glider.getAirResistance();
//                        player.motionZ *= glider.getAirResistance();
//                    }
//                }

                break;
            } else if (!scanned.equals(Blocks.AIR)) {
                break;
            }
        }
    }
}
