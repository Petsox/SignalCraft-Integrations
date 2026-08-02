package SCIntegrations.core;

import SCIntegrations.oc.digitalController.BlockDigitalController;
import SCIntegrations.oc.digitalController.ItemBlockDigitalController;
import SCIntegrations.oc.digitalController.TileDigitalController;
import SCIntegrations.oc.digitalUnivController.BlockDigitalUniversalController;
import SCIntegrations.oc.digitalUnivController.ItemBlockDigitalUniversalController;
import SCIntegrations.oc.digitalUnivController.TileDigitalUniversalController;
import SCIntegrations.projred.bundledAdvController.BlockBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.ItemBlockBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.TileBundledAdvancedController;
import SCIntegrations.projred.bundledController.BlockBundledController;
import SCIntegrations.projred.bundledController.ItemBlockBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import SCIntegrations.projred.bundledReceiver.BlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.ItemBlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.TileBundledReceiver;
import SCIntegrations.projred.bundledUnivController.BlockBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.ItemBlockBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.TileBundledUniversalController;
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

            //Bundled Universal Controller
            GameRegistry.registerTileEntity(TileBundledUniversalController.class, "BundledUniversalController");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledUniversalController.class, new TileControllerRenderer(new TileBundledUniversalController()));

            BlockBundledUniversalController bundledUniversalController = new BlockBundledUniversalController("BundledUniversalController");
            GameRegistry.registerBlock(bundledUniversalController, ItemBlockBundledUniversalController.class, bundledUniversalController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledUniversalController), new ControllerItemRenderer(new TileBundledUniversalController()));

            //Bundled Advanced Controller
            GameRegistry.registerTileEntity(TileBundledAdvancedController.class, "BundledAdvancedController");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledAdvancedController.class, new TileControllerRenderer(new TileBundledAdvancedController()));

            BlockBundledAdvancedController bundledAdvancedController = new BlockBundledAdvancedController("BundledAdvancedController");
            GameRegistry.registerBlock(bundledAdvancedController, ItemBlockBundledAdvancedController.class, bundledAdvancedController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledAdvancedController), new ControllerItemRenderer(new TileBundledAdvancedController()));

            //Bundled Receiver
            GameRegistry.registerTileEntity(TileBundledReceiver.class, "BundledReceiver");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledReceiver.class, new TileControllerRenderer(new TileBundledReceiver()));

            BlockBundledReceiver bundledReceiver = new BlockBundledReceiver("BundledReceiver");
            GameRegistry.registerBlock(bundledReceiver, ItemBlockBundledReceiver.class, bundledReceiver.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledReceiver), new ControllerItemRenderer(new TileBundledReceiver()));
        }

        if (Mods.isLoaded(Mods.ModsEnum.OpenComputers)) {
            //Digital Controller
            GameRegistry.registerTileEntity(TileDigitalController.class, "DigitalController");
            ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalController.class, new TileControllerRenderer(new TileDigitalController()));

            BlockDigitalController digitalController = new BlockDigitalController("DigitalController");
            GameRegistry.registerBlock(digitalController, ItemBlockDigitalController.class, digitalController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(digitalController), new ControllerItemRenderer(new TileDigitalController()));

            //Digital Universal Controller
            GameRegistry.registerTileEntity(TileDigitalUniversalController.class, "DigitalUniversalController");
            ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalUniversalController.class, new TileControllerRenderer(new TileDigitalUniversalController()));

            BlockDigitalUniversalController digitalUniversalController = new BlockDigitalUniversalController("DigitalUniversalController");
            GameRegistry.registerBlock(digitalUniversalController, ItemBlockDigitalUniversalController.class, digitalUniversalController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(digitalUniversalController), new ControllerItemRenderer(new TileDigitalUniversalController()));
        }
    }
}
