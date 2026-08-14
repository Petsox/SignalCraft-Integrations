package SCIntegrations.projred.bundledAdvController;

import SCIntegrations.projred.GuiBundledRowScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import signalcraft.signalUtils.SignalState;
import signalcraft.signalUtils.Utils;

public class GuiBundledAdvancedController extends GuiBundledRowScreen {
    private static final int STATE_FIELD_WIDTH = 55;
    private static final int NAME_FIELD_WIDTH = 110;
    private static final int STATE_FIELD_MAX_LENGTH = 20;
    private static final int NAME_FIELD_MAX_LENGTH = 48;

    private final TileBundledAdvancedController thisTileE;
    private final int[] signalStates = new int[ROWS];

    public GuiBundledAdvancedController(final TileBundledAdvancedController thisTileE) {
        super(thisTileE);
        this.thisTileE = thisTileE;
    }

    protected int getFieldCount() {
        return 2;
    }

    protected int getFieldWidth(int fieldIndex) {
        return fieldIndex == 0 ? STATE_FIELD_WIDTH : NAME_FIELD_WIDTH;
    }

    protected int getFieldMaxLength(int fieldIndex) {
        return fieldIndex == 0 ? STATE_FIELD_MAX_LENGTH : NAME_FIELD_MAX_LENGTH;
    }

    protected String getFieldLabel(int fieldIndex) {
        return fieldIndex == 0 ? I18n.format("gui.advancedBundledCont.setState") : I18n.format("gui.advancedBundledCont.on");
    }

    protected int getFieldXNudge(int fieldIndex) {
        return fieldIndex == 0 ? FIELD_X_NUDGE : FIELD_X_NUDGE - 6;
    }

    protected String getInitialFieldText(int row, int fieldIndex) {
        if (fieldIndex == 0) {
            int state = thisTileE.getSignalStates()[row];
            return state != 0 ? SignalState.values()[state].StateToString() : "";
        }
        String existingNames = thisTileE.getReceiverNames()[row];
        return existingNames != null ? existingNames : "";
    }

    protected String[] getGuideTextLines() {
        return new String[]{
                I18n.format("gui.lightSignalAdvancedBundledCont.use"),
                I18n.format("gui.lightSignalAdvancedBundledCont.use2"),
                I18n.format("gui.lightSignalAdvancedBundledCont.use3")
        };
    }

    protected void onDone(String[][] fieldValues) {
        String[] receiverNames = new String[ROWS];

        for (int i = 0; i < ROWS; i++) {
            String stateText = fieldValues[i][0];
            SignalState state = SignalState.ZHAS;

            if (SignalState.contains(stateText)) {
                state = SignalState.fromString(stateText);
                int value = state.ordinal();
                if (value > SignalState.values().length - 1 || value == 1) {
                    value = 0; // reset to default if out of bounds
                }
                signalStates[i] = stateText.isEmpty() ? 0 : value;
            }

            receiverNames[i] = fieldValues[i][1].trim();
        }

        thisTileE.setSignalStates(signalStates);
        thisTileE.setReceiverNames(receiverNames);

        Utils.addChatMessage(this.mc.thePlayer, new ChatComponentTranslation("message.controllerUpdatedWithStatesAndNames"));
        Utils.addChatMessage(this.mc.thePlayer, java.util.Arrays.toString(receiverNames));
    }
}
