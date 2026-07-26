package SCIntegrations.projred.bundledController;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import signalcraft.signalUtils.Consts;
import signalcraft.signalUtils.Network;
import signalcraft.signalUtils.SignalState;
import signalcraft.signalUtils.Utils;

import java.awt.*;

public class GuiBundledController extends GuiScreen {
    private final String guiName;
    private final int[] signalStates = new int[16];
    protected GuiButton doneButton;
    private final TileBundledController thisTileE;
    private GuiTextField ControllerName;
    private GuiTextField[] stateFields = new GuiTextField[16];

    public GuiBundledController(final TileBundledController thisTileE) {
        this.guiName = this.getClass().getSimpleName();
        Keyboard.enableRepeatEvents(true);
        this.allowUserInput = true;
        this.thisTileE = thisTileE;
    }

    public void initGui() {
        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 110, this.height / 4 + 160, I18n.format("gui.done")));
        ControllerName = new GuiTextField(this.fontRendererObj, this.width / 2 - 105, this.height / 4 - 57, 80, 15);

        for (int i = 0; i < stateFields.length; i++) {
            int xOffset = (i % 2 == 0) ? -105 : 10; // Alternate between left and right columns
            int yOffset = (i / 2) * 22 - 10; // Adjust vertical spacing
            stateFields[i] = new GuiTextField(this.fontRendererObj, this.width / 2 + xOffset, this.height / 4 + yOffset, 80, 15);
            stateFields[i].setTextColor(Consts.GameColor.values()[i].getHexValue());
            if (thisTileE.getSignalStates()[i] != 0) {
                stateFields[i].setText(SignalState.values()[thisTileE.getSignalStates()[i]].StateToString());
            } else {
                stateFields[i].setText(String.valueOf(I18n.format(Consts.GameColor.values()[i].getLangKey())));
            }
        }

        this.ControllerName.setText(this.thisTileE.getName());
    }

    public void drawScreen(final int mouseX, final int mouseY, final float par3) {
        if (!this.mc.gameSettings.forceUnicodeFont) {
            this.fontRendererObj.setUnicodeFlag(true);
            this.fontRendererObj.setBidiFlag(true);
        }
        this.drawDefaultBackground();
        this.drawHorizontalLine(0, this.width, this.height / 32 * 4 + 4, new Color(255, 255, 255, 128).getRGB());
        ControllerName.drawTextBox();

        for (GuiTextField field : stateFields) {
            field.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, par3);
    }

    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0: {
                this.thisTileE.markDirty();
                this.mc.displayGuiScreen(null);
                break;
            }
        }
    }

    protected void mouseClicked(final int x, final int y, final int buttonClicked) {
        this.ControllerName.mouseClicked(x, y, buttonClicked);

        for (GuiTextField field : stateFields) {
            field.mouseClicked(x, y, buttonClicked);
        }

        super.mouseClicked(x, y, buttonClicked);
    }

    protected void keyTyped(final char character, final int code) {
        if (this.ControllerName.getText().length() <= 12 || code == 14) {
            this.ControllerName.textboxKeyTyped(character, code);
        }

        for (GuiTextField field : stateFields) {
            if (field.getText().length() <= 12 || code == 14) {
                field.textboxKeyTyped(character, code);
            }
        }

        if (code == 1) {
            this.actionPerformed(this.doneButton);
        }
    }

    public void updateScreen() {
        this.ControllerName.updateCursorCounter();
    }

    public void onGuiClosed() {
        thisTileE.setName(this.ControllerName.getText());
        String[] colorNames = new String[signalStates.length];

        for (int i = 0; i < stateFields.length; i++) {
            String FieldText = stateFields[i].getText();
            SignalState State = SignalState.ZHAS;

            if (SignalState.contains(FieldText)) {
                State = SignalState.fromString(FieldText);
                int value = State.ordinal();
                if (value > SignalState.values().length - 1 || value == 1) {
                    value = 0; // reset to default if out of bounds
                }
                signalStates[i] = FieldText.isEmpty() ? 0 : value;
            }

            if (signalStates[i] == 0) {
                colorNames[i] = "0";
            } else {
                colorNames[i] = State.StateToString();
            }
        }

        thisTileE.setSignalStates(signalStates);


        Utils.addLocalizedChatMessage(this.mc.thePlayer, "message.controllerUpdatedWithStates");
        Utils.addChatMessage(this.mc.thePlayer, java.util.Arrays.toString(colorNames));

        Keyboard.enableRepeatEvents(false);
        if (!this.mc.gameSettings.forceUnicodeFont) {
            this.fontRendererObj.setUnicodeFlag(false);
            this.fontRendererObj.setBidiFlag(false);
        }
        Network.updateControllers(this.thisTileE);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}