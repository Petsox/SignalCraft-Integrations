package SCIntegrations.proxy;

import SCIntegrations.core.ConstsInt;
import SCIntegrations.core.Mods;
import SCIntegrations.projred.bundledController.GuiBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import signalcraft.gui.SCGuis;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(final FMLInitializationEvent event) {
        super.init(event);

        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)) {
            SCGuis.register(
                    "BUNDLED_CONTROLLER_GUI",
                    ConstsInt.BUNDLED_CONTROLLER.guiId,
                    TileBundledController::new,
                    tile -> new GuiBundledController((TileBundledController) tile)
            );
        }
    }
}