package gr8pefish.openglider.common.effects;

import gr8pefish.openglider.common.config.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermalEffect implements GliderEffect {

    /**
     * Apply thermal effect, making the player
     * Affected by a variety of things, from player height to glider durability to weather, etc.
     *
     * @param player - the player to move around
     * @param glider - the hang glider item
     */
    public void apply(EntityPlayer player, ItemStack glider) {

        //player.motionY = -3;

        // TODO: Make configurable
        applyStaticThermals(player, glider);
    }

    public boolean enabled() {
        return ConfigHandler.thermalsEnabled;
    }

    private void applyStaticThermals(EntityPlayer player, ItemStack glider) {
        BlockPos pos = player.getPosition();
        World worldIn = player.getEntityWorld();

        int maxSearchDown = 5;
        int maxSquared = (maxSearchDown-1) * (maxSearchDown-1);

        int i = 0;
        while (i <= maxSearchDown) {
            BlockPos scanpos = pos.down(i);
            Block scanned = worldIn.getBlockState(scanpos).getBlock();
            if (scanned.equals(Blocks.FIRE) || scanned.equals(Blocks.LAVA) || scanned.equals(Blocks.FLOWING_LAVA) || scanned.equals(Blocks.SAND)) { //ToDo: configurable

//                get closeness to heat as quadratic (squared)
                double closeness = (maxSearchDown - i) * (maxSearchDown - i);

                //set amount up
                double configMovement = 12.2;
                double upUnnormalized = configMovement * closeness;

//                Logger.info("UN-NORMALIZED: "+upUnnormalized);

                //normalize
                double upNormalized = 1 + (upUnnormalized/(configMovement * maxSquared));

//                Logger.info("NORMALIZED: "+upNormalized);

                //scale amount to player's current motion
                double motion = player.motionY;
                double scaled = motion - (motion * (upNormalized * upNormalized));
//                Logger.info("SCALED: "+scaled);

                //apply final
//                Logger.info("BEFORE: "+player.motionY);
                player.motionY += scaled * 100;
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
            i++;
        }

    }
}
