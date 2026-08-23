package SCIntegrations.core;

import signalcraft.signalUtils.Consts;

public enum GuiConsts {

    BUNDLED_CONTROLLER,
    BUNDLED_UNIVERSAL_CONTROLLER,
    BUNDLED_ADVANCED_CONTROLLER,
    BUNDLED_CROSSING_CONTROLLER,
    DIGITAL_CONTROLLER,
    DIGITAL_UNIVERSAL_CONTROLLER,
    BUNDLED_RECEIVER;

    public final Consts.GuiIDs guiId = Consts.GuiIDs.register(name());

}
