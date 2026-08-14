package SCIntegrations;

import SCIntegrations.core.Integration;
import SCIntegrations.core.Mods;
import SCIntegrations.proxy.CommonProxy;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import signalcraft.SignalCraft;

@Mod(modid = "SCIntegrations", name = "SignalCraft-Integrations", dependencies = "required-after:signalcraft@[1.7.10-0.99.2-BETA,)")
public class SCIntegrations {
    @Mod.Instance("SCIntegrations")
    public static SCIntegrations instance;
    public static final String name = "SignalCraft-Integrations";
    public static final String MOD_ID = "SCIntegrations";

    @SidedProxy(clientSide = "SCIntegrations.proxy.ClientProxy", serverSide = "SCIntegrations.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final String[] Devs = {"Petsox", "tpeterka1", "Breeko", "hajdam"};

    public static CreativeTabs tabIntegrations;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void Init(final FMLInitializationEvent event) {
        if (Mods.integrationCreativeTabNeeded()) {
            tabIntegrations = new CreativeTabs(SignalCraft.MOD_ID + "_integrations") {
                @Override
                public Item getTabIconItem() {
                    return Item.getItemFromBlock(Blocks.web);
                }
            };
        }

        Integration.registerIntegrations();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
