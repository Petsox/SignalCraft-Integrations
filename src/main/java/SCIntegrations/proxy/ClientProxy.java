package SCIntegrations.proxy;

import SCIntegrations.core.GuiConsts;
import SCIntegrations.core.Mods;
import SCIntegrations.projred.bundledAdvController.GuiBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.TileBundledAdvancedController;
import SCIntegrations.projred.bundledController.GuiBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import SCIntegrations.projred.bundledReceiver.GuiBundledReceiver;
import SCIntegrations.projred.bundledReceiver.TileBundledReceiver;
import SCIntegrations.projred.bundledUnivController.GuiBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.TileBundledUniversalController;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import signalcraft.gui.SCGuis;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(final FMLInitializationEvent event) {
        super.init(event);

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
}