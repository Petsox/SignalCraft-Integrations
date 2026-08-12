package SCIntegrations.proxy;

import SCIntegrations.core.GuiConsts;
import SCIntegrations.core.Integration;
import SCIntegrations.core.Mods;
import SCIntegrations.oc.digitalController.TileDigitalController;
import SCIntegrations.oc.digitalCrossController.TileDigitalCrossingController;
import SCIntegrations.oc.digitalUnivController.TileDigitalUniversalController;
import SCIntegrations.projred.bundledAdvController.GuiBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.TileBundledAdvancedController;
import SCIntegrations.projred.bundledController.GuiBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import SCIntegrations.projred.bundledReceiver.GuiBundledReceiver;
import SCIntegrations.projred.bundledReceiver.TileBundledReceiver;
import SCIntegrations.projred.bundledUnivController.GuiBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.TileBundledUniversalController;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import signalcraft.gui.SCGuis;
import signalcraft.renderers.entities.controllers.TileControllerRenderer;
import signalcraft.renderers.items.controllers.ControllerItemRenderer;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(final FMLInitializationEvent event) {
        super.init(event);

        registerRenderers();

        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)) {
            SCGuis.register(
                    "BUNDLED_CONTROLLER_GUI",
                    GuiConsts.BUNDLED_CONTROLLER.guiId,
                    TileBundledController::new,
                    tile -> new GuiBundledController((TileBundledController) tile)
            );
            SCGuis.register(
                    "BUNDLED_RECEIVER_GUI",
                    GuiConsts.BUNDLED_RECEIVER.guiId,
                    TileBundledReceiver::new,
                    tile -> new GuiBundledReceiver((TileBundledReceiver) tile)
            );
            SCGuis.register(
                    "BUNDLED_UNIVERSAL_CONTROLLER_GUI",
                    GuiConsts.BUNDLED_UNIVERSAL_CONTROLLER.guiId,
                    TileBundledUniversalController::new,
                    tile -> new GuiBundledUniversalController((TileBundledUniversalController) tile)
            );
            SCGuis.register(
                    "BUNDLED_ADVANCED_CONTROLLER_GUI",
                    GuiConsts.BUNDLED_ADVANCED_CONTROLLER.guiId,
                    TileBundledAdvancedController::new,
                    tile -> new GuiBundledAdvancedController((TileBundledAdvancedController) tile)
            );
        }
    }

    private void registerRenderers() {
        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledController.class, new TileControllerRenderer(new TileBundledController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getBundledController()), new ControllerItemRenderer(new TileBundledController()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledUniversalController.class, new TileControllerRenderer(new TileBundledUniversalController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getBundledUniversalController()), new ControllerItemRenderer(new TileBundledUniversalController()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledAdvancedController.class, new TileControllerRenderer(new TileBundledAdvancedController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getBundledAdvancedController()), new ControllerItemRenderer(new TileBundledAdvancedController()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledReceiver.class, new TileControllerRenderer(new TileBundledReceiver()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getBundledReceiver()), new ControllerItemRenderer(new TileBundledReceiver()));
        }

        if (Mods.isLoaded(Mods.ModsEnum.OpenComputers)) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalController.class, new TileControllerRenderer(new TileDigitalController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getDigitalController()), new ControllerItemRenderer(new TileDigitalController()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalUniversalController.class, new TileControllerRenderer(new TileDigitalUniversalController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getDigitalUniversalController()), new ControllerItemRenderer(new TileDigitalUniversalController()));

            ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalCrossingController.class, new TileControllerRenderer(new TileDigitalCrossingController()));
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Integration.getDigitalCrossingController()), new ControllerItemRenderer(new TileDigitalCrossingController()));
        }
    }
}