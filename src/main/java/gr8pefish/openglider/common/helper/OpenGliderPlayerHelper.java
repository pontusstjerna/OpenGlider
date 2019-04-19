package gr8pefish.openglider.common.helper;

import gr8pefish.openglider.api.item.IGlider;
import gr8pefish.openglider.api.helper.GliderHelper;
import gr8pefish.openglider.common.config.ConfigHandler;
import gr8pefish.openglider.common.effects.GliderEffect;
import gr8pefish.openglider.common.network.PacketHandler;
import gr8pefish.openglider.common.network.PacketUpdateGliderDamage;
import gr8pefish.openglider.common.effects.ThermalEffect;
import gr8pefish.openglider.common.effects.TurbulenceEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class OpenGliderPlayerHelper {

    private static GliderEffect turbulence = new TurbulenceEffect();
    private static GliderEffect thermals = new ThermalEffect();

    private static List<GliderEffect> effects = null;

    /**
     * Updates the position of the player when gliding.
     * Glider is assumed to be deployed already.
     *
     * @param player - the player gliding
     */
    public static void updatePosition(EntityPlayer player) {
        if (shouldBeGliding(player)) {
            ItemStack glider = GliderHelper.getGlider(player);
            if (isValidGlider(glider)) {
                if (!player.onGround) { //if flying

                    // Init variables
                    final double horizontalSpeed;
                    final double verticalSpeed;
                    IGlider iGlider = (IGlider) glider.getItem();

                    // Get speed depending on glider and if player is sneaking
                    if (!player.isSneaking()) {
                        horizontalSpeed = iGlider.getHorizontalFlightSpeed();

                        // TODO: Maybe this should be calculated elsewhere. This allows for different formulas to allow stall speed etc
                        verticalSpeed = horizontalSpeed / iGlider.getGlideRatio();
                    } else {
                        horizontalSpeed = iGlider.getShiftHorizontalFlightSpeed();

                        // TODO: Maybe this should be calculated elsewhere. This allows for different formulas to allow stall speed etc
                        verticalSpeed = horizontalSpeed / iGlider.getShiftGlideRatio();
                    }

                    // Apply falling motion
                    player.motionY = -verticalSpeed;

                    // Apply all configured effects
                    for (GliderEffect effect : effects) {
                        if (effect.enabled()) {
                            effect.apply(player, glider);
                        }
                    }

                    final double horizontalAcceleration = iGlider.getHorizontalAcceleration();

                    // Apply forward motion
                    double maxSpeedX = Math.cos(Math.toRadians(player.rotationYaw + 90)) * horizontalSpeed;
                    double maxSpeedZ = Math.sin(Math.toRadians(player.rotationYaw + 90)) * horizontalSpeed;

                    double accelerationX = (Math.cos(Math.toRadians(player.rotationYaw + 90)) * horizontalAcceleration) + player.motionX;
                    double accelerationZ = (Math.sin(Math.toRadians(player.rotationYaw + 90)) * horizontalAcceleration) + player.motionZ;

                    player.motionX = Math.abs(maxSpeedX) <= Math.abs(accelerationX) ? maxSpeedX : accelerationX;
                    player.motionZ = Math.abs(maxSpeedZ) <= Math.abs(accelerationZ) ? maxSpeedZ : accelerationZ;


                    // Stop fall damage
                    player.fallDistance = 0.0F;

//                    playWindSound(player); //ToDo: sounds
                }

                //no wild arm swinging while flying
                if (player.world.isRemote) {
                    player.limbSwing = 0;
                    player.limbSwingAmount = 0;
                }

                //damage the hang glider
                if (ConfigHandler.durabilityEnabled) { //durability should be taken away
                    if (!player.world.isRemote) { //server
                        if (player.world.rand.nextInt(ConfigHandler.durabilityTimeframe) == 0) { //damage about once per x ticks
                            PacketHandler.HANDLER.sendTo(new PacketUpdateGliderDamage(), (EntityPlayerMP) player); //send to client
                            glider.damageItem(ConfigHandler.durabilityPerUse, player);
                            if (((IGlider) (glider.getItem())).isBroken(glider)) { //broken item
                                GliderHelper.setIsGliderDeployed(player, false);
                            }
                        }
                    }
                }

                //SetPositionAndUpdate on server only

            } else { //Invalid item (likely changed selected item slot, update)
                GliderHelper.setIsGliderDeployed(player, false);
            }
        }

    }

    public static void initEffects() {
        effects = new ArrayList<>();
        effects.add(new TurbulenceEffect());
        effects.add(new ThermalEffect());
    }

    //Currently sounds awful with crazy reverb, need to fix it somehow (probably custom ElytraSound without the check for isElytraFlying)
    private static void playWindSound(EntityPlayer player) {

        float volume;
        float pitch;

        float f = MathHelper.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ + player.motionY * player.motionY);
        float f1 = f / 2.0F;

        if ((double)f >= 0.01D) {
            volume = MathHelper.clamp(f1 * f1, 0.0F, 1.0F);
        }
        else {
            volume = 0.0F;
        }

        if (volume > 0.8F) {
            pitch = 1.0F + (volume - 0.8F);
        }
        else {
            pitch = 1.0F;
        }

        player.playSound(SoundEvents.ITEM_ELYTRA_FLYING, volume, pitch);

//        Minecraft.getMinecraft().getSoundHandler().playSound(new ElytraSound((EntityPlayerSP) player)); //doesn't work b/c hardcoded to isElytraFlying?
    }

    /**
     * Check if the player should be gliding.
     * Checks if the player is alive, and not on the ground or in water.
     *
     * @param player - the player to check
     * @return - true if the conditions are met, false otherwise
     */
    public static boolean shouldBeGliding(EntityPlayer player){
        if (player == null || player.isDead) return false;
        if (player.onGround || player.isInWater()) return false;
        return true;
    }

    /**
     * Check if the itemStack is an unbroken HangGlider.
     *
     * @param stack - the itemstack to check
     * @return - true if the item is an unbroken glider, false otherwise
     */
    private static boolean isValidGlider(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (stack.getItem() instanceof IGlider && (!((IGlider)(stack.getItem())).isBroken(stack))) { //hang glider, not broken
                return true;
            }
        }
        return false;
    }

    /**
     * Loop through player's inventory to get their hang glider.
     *
     * @param player - the player to search
     * @return - the first glider found (as an itemstack), null otherwise
     */
    public static ItemStack getGlider(EntityPlayer player) {
//        if (ConfigHandler.holdingGliderEnforced)
              return player.getHeldItemMainhand();
//        if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ItemHangglider) {
//            return player.getHeldItemOffhand();
//        }
//        for (ItemStack stack : player.inventory.mainInventory) {
//            if (stack != null) {
//                if (stack.getItem() instanceof ItemHangglider) {
//                    return stack;
//                }
//            }
//        }
//        return null;
    }

}
