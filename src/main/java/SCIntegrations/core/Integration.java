package SCIntegrations.core;

import SCIntegrations.projred.bundledController.BlockBundledController;
import SCIntegrations.projred.bundledController.ItemBlockBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import SCIntegrations.projred.bundledReceiver.BlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.ItemBlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.TileBundledReceiver;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import signalcraft.renderers.entities.controllers.TileControllerRenderer;
import signalcraft.renderers.items.controllers.ControllerItemRenderer;

public class Integration {



    public static void registerIntegrations() {

        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)){
            //Bundled Controller
            GameRegistry.registerTileEntity(TileBundledController.class, "BundledController");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledController.class, new TileControllerRenderer(new TileBundledController()));

            BlockBundledController bundledController = new BlockBundledController("BundledController");
            GameRegistry.registerBlock(bundledController, ItemBlockBundledController.class, bundledController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledController), new ControllerItemRenderer(new TileBundledController()));

            //Bundled Receiver
            GameRegistry.registerTileEntity(TileBundledReceiver.class, "BundledReceiver");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledReceiver.class, new TileControllerRenderer(new TileBundledReceiver()));

            BlockBundledReceiver bundledReceiver = new BlockBundledReceiver("BundledReceiver");
            GameRegistry.registerBlock(bundledReceiver, ItemBlockBundledReceiver.class, bundledReceiver.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledReceiver), new ControllerItemRenderer(new TileBundledReceiver()));
        }
    }
}
