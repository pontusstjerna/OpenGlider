package gr8pefish.openglider.common.effects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface GliderEffect {
    void apply(EntityPlayer player, ItemStack glider);
    boolean enabled();
}
