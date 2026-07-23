package SCIntegrations.projred.core;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import signalcraft.integration.projectred.GuiBundledController;
import signalcraft.integration.projectred.TileBundledController;
import signalcraft.signalUtils.Consts;

import java.util.function.Function;
import java.util.function.Supplier;

public enum IntegrationGuis {

    BUNDELED_CONTROLLER_GUI(Consts.GuiIDs.BUNDLED_CONTROLLER, TileBundledController::new, (tile) -> new GuiBundledController((TileBundledController) tile));

    public final Consts.GuiIDs id;
    public final Supplier<TileEntity> tileFactory;
    public final Function<TileEntity, GuiScreen> guiFactory;

    IntegrationGuis(Consts.GuiIDs id, Supplier<TileEntity> tileFactory, Function<TileEntity, GuiScreen> guiFactory) {
        this.id = id;
        this.tileFactory = tileFactory;
        this.guiFactory = guiFactory;
    }

    public static GuiScreen handleGuiById(int guiId, TileEntity tile) {
        for (IntegrationGuis scGui : values()) {
            if (scGui.id.getId() == guiId) {
                return scGui.guiFactory.apply(tile);
            }
        }
        return null;
    }
    public static TileEntity getTileByGuiId(int guiId) {
        for (IntegrationGuis scGui : values()) {
            if (scGui.id.getId() == guiId) {
                return scGui.tileFactory.get(); // call Supplier.get()
            }
        }
        return null;
    }
}
