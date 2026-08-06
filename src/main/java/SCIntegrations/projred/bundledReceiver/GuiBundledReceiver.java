package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.projred.GuiBundledRowScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import signalcraft.signalUtils.SignalState;
import signalcraft.signalUtils.Utils;

public class GuiBundledReceiver extends GuiBundledRowScreen {
    private static final int FIELD_WIDTH = 80;
    private static final int FIELD_MAX_LENGTH = 12;

    private final TileBundledReceiver thisTileE;
    private final int[] signalStates = new int[ROWS];

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
        int state = thisTileE.getSignalStates()[row];
        return state != 0 ? SignalState.values()[state].StateToString() : "";
    }

    protected String[] getGuideTextLines() {
        return new String[]{
                I18n.format("gui.lightSignalBundledRece.use"),
                I18n.format("gui.lightSignalBundledRece.use2"),
                I18n.format("gui.lightSignalBundledRece.use3")
        };
    }

    protected void onDone(String[][] fieldValues) {
        String[] colorNames = new String[ROWS];

        for (int i = 0; i < ROWS; i++) {
            String fieldText = fieldValues[i][0];
            SignalState state = SignalState.ZHAS;

            if (SignalState.contains(fieldText)) {
                state = SignalState.fromString(fieldText);
                int value = state.ordinal();
                if (value > SignalState.values().length - 1 || value == 1) {
                    value = 0; // reset to default if out of bounds
                }
                signalStates[i] = fieldText.isEmpty() ? 0 : value;
            }

            colorNames[i] = signalStates[i] == 0 ? "0" : state.StateToString();
        }

        thisTileE.setSignalStates(signalStates);

        Utils.addChatMessage(this.mc.thePlayer, new ChatComponentTranslation("message.controllerUpdatedWithStates"));
        Utils.addChatMessage(this.mc.thePlayer, java.util.Arrays.toString(colorNames));
    }
}
