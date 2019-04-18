package gr8pefish.openglider.common;

import gr8pefish.openglider.api.upgrade.UpgradeItems;
import gr8pefish.openglider.common.config.ConfigHandler;
import gr8pefish.openglider.common.event.ServerEventHandler;
import gr8pefish.openglider.common.helper.OpenGliderPlayerHelper;
import gr8pefish.openglider.common.lib.ModInfo;
import gr8pefish.openglider.common.network.PacketHandler;
import gr8pefish.openglider.common.proxy.IProxy;
import gr8pefish.openglider.common.registry.CapabilityRegistry;
import gr8pefish.openglider.common.registry.ItemRegistry;
import gr8pefish.openglider.common.effects.TurbulenceEffect;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static gr8pefish.openglider.api.OpenGliderInfo.*;

@Mod(modid = MODID, name = MOD_NAME, version = ModInfo.VERSION, guiFactory = ModInfo.GUI_FACTORY, acceptedMinecraftVersions = "[1.12,1.13)")//, updateJSON = FORGE_UPDATE_JSON_URL) //disabled version update checker for now, as it was giving false positives
public class OpenGlider {

    //Proxies
    @SidedProxy(clientSide = ModInfo.CLIENT_PROXY, serverSide = ModInfo.COMMON_PROXY)
    public static IProxy proxy;

    //Creative Tab
    public static final CreativeTabs creativeTab = new CreativeTabs(MODID) {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemRegistry.GLIDER_BASIC);
        }
    };

    //Mod Instance
    @Mod.Instance
    public static OpenGlider instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        //config
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        // Init effects
        OpenGliderPlayerHelper.initEffects();

        //register capabilities
        CapabilityRegistry.registerAllCapabilities();

        //packets
        PacketHandler.init();

        //init renderers and client event handlers
        proxy.preInit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        //upgrades
        UpgradeItems.initUpgradesList();

        //register server events
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        //register config changed event
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

        proxy.init(event);
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(event);
    }
}
