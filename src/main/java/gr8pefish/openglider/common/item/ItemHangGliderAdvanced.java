package gr8pefish.openglider.common.item;

import gr8pefish.openglider.api.item.ItemHangGliderBase;
import gr8pefish.openglider.client.model.ModelGlider;
import gr8pefish.openglider.common.OpenGlider;
import gr8pefish.openglider.common.config.ConfigHandler;
import gr8pefish.openglider.common.lib.ModInfo;
import net.minecraft.nbt.NBTTagCompound;

import static gr8pefish.openglider.api.OpenGliderInfo.MODID;

public class ItemHangGliderAdvanced extends ItemHangGliderBase {

    public ItemHangGliderAdvanced() {
        super(ConfigHandler.advancedGliderHorizSpeed, ConfigHandler.advancedGliderGlideRatio, ConfigHandler.advancedGliderShiftHorizSpeed, ConfigHandler.advancedGliderShiftGlideRatio, ConfigHandler.advancedGliderWindModifier, ConfigHandler.advancedGliderAirResistance, ConfigHandler.advancedGliderTotalDurability, ModelGlider.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
        setCreativeTab(OpenGlider.creativeTab);
        setTranslationKey(MODID +":" + ModInfo.ITEM_GLIDER_ADVANCED_NAME);
    }

    //ToDo: Needed?
    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }
}
