package SCIntegrations.core;

import signalcraft.signalUtils.Consts;

public enum ConstsInt {

    BUNDLED_CONTROLLER,
    BUNDLED_UNIVERSAL_CONTROLLER,
    BUNDLED_RECEIVER;

    public final Consts.GuiIDs guiId = Consts.GuiIDs.register(name());

}
