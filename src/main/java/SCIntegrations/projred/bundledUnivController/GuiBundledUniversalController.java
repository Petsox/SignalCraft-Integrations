package SCIntegrations.projred.bundledUnivController;

import SCIntegrations.projred.GuiBundledRowScreen;
import net.minecraft.client.resources.I18n;
import signalcraft.signalUtils.Utils;

public class GuiBundledUniversalController extends GuiBundledRowScreen {
    private static final int FIELD_WIDTH = 80;
    private static final int FIELD_MAX_LENGTH = 12;

    private final TileBundledUniversalController thisTileE;

    public GuiBundledUniversalController(final TileBundledUniversalController thisTileE) {
        super(thisTileE);
        this.thisTileE = thisTileE;
    }

    protected int getFieldCount() {
        return 1;
    }

    protected int getFieldWidth(int fieldIndex) {
        return FIELD_WIDTH;
    }

    protected int getFieldMaxLength(int fieldIndex) {
        return FIELD_MAX_LENGTH;
    }

    protected String getFieldLabel(int fieldIndex) {
        return null;
    }

    protected String getInitialFieldText(int row, int fieldIndex) {
        String existingName = thisTileE.getReceiverNames()[row];
        return existingName != null ? existingName : "";
    }

    protected String[] getGuideTextLines() {
        return new String[]{
                I18n.format("gui.lightSignalUniversalBundledCont.use"),
                I18n.format("gui.lightSignalUniversalBundledCont.use2"),
                I18n.format("gui.lightSignalUniversalBundledCont.use3")
        };
    }

    protected void onDone(String[][] fieldValues) {
        String[] names = new String[ROWS];
        for (int i = 0; i < ROWS; i++) {
            names[i] = fieldValues[i][0].trim();
        }

        thisTileE.setReceiverNames(names);

        Utils.addLocalizedChatMessage(this.mc.thePlayer, "message.controllerUpdatedWithNames");
        Utils.addChatMessage(this.mc.thePlayer, java.util.Arrays.toString(names));
    }
}
