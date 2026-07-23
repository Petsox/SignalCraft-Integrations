package SCIntegrations;

import SCIntegrations.projred.core.Integration;
import SCIntegrations.proxy.CommonProxy;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import signalcraft.SignalCraft;

@Mod(modid = "SCIntegrations", version = "1.7.10-0.1-ALPHA", name = "SignalCraft-Integrations", dependencies = "required-after:signalcraft@[1.7.10-0.1-ALPHA,)")
public class SCIntegrations
{
    @Mod.Instance("SCIntegrations")
    public static SCIntegrations instance;
    public static final String name = "SignalCraft-Integrations";
    public static final String MOD_ID = "SCIntegrations";
    //I need to find out if I can use Proxy from the main mod, or if I need to make a new one for this mod. I think I can use the main mod's proxy, but I need to check.
    @SidedProxy(clientSide = "SCIntegrations.proxy.ClientProxy", serverSide = "SCIntegrations.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final SimpleNetworkWrapper SCIntegrationsNet;
    public static final String[] Devs = {"Petsox", "tpeterka1", "Breeko", "hajdam"};

    public static CreativeTabs tabIntegrations;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void Init(final FMLInitializationEvent event) {
        if (signalcraft.integration.Mods.integrationCreativeTabNeeded()) {
            tabIntegrations = new CreativeTabs(SignalCraft.MOD_ID + "_integrations") {
                @Override
                public Item getTabIconItem() { return Item.getItemFromBlock(Blocks.web); }
            };
        }

        Integration.registerIntegrations();
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {

    }

    static {
        SCIntegrationsNet = NetworkRegistry.INSTANCE.newSimpleChannel("SCIntegrationsNet");
    }
}
