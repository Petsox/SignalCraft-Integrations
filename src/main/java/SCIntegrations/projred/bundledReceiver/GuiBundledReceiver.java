package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.projred.GuiBundledRowScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import signalcraft.signalUtils.Utils;

public class GuiBundledReceiver extends GuiBundledRowScreen {
    private static final int FIELD_WIDTH = 110;
    private static final int FIELD_MAX_LENGTH = 48;

    private final TileBundledReceiver thisTileE;

    public GuiBundledReceiver(final TileBundledReceiver thisTileE) {
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
        String existing = thisTileE.getSignalStatesText()[row];
        return existing != null ? existing : "";
    }

    protected String[] getGuideTextLines() {
        return new String[]{
                I18n.format("gui.lightSignalBundledRece.use"),
                I18n.format("gui.lightSignalBundledRece.use2"),
                I18n.format("gui.lightSignalBundledRece.use3")
        };
    }

    protected void onDone(String[][] fieldValues) {
        String[] states = new String[ROWS];
        for (int i = 0; i < ROWS; i++) {
            states[i] = fieldValues[i][0].trim();
        }

        thisTileE.setSignalStatesText(states);

        Utils.addChatMessage(this.mc.thePlayer, new ChatComponentTranslation("message.controllerUpdatedWithStates"));
        Utils.addChatMessage(this.mc.thePlayer, java.util.Arrays.toString(states));
    }
}
