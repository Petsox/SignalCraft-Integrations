package SCIntegrations.projred;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import signalcraft.entities.controllers.TileContReceBase;
import signalcraft.signalUtils.Consts;
import signalcraft.signalUtils.Network;

import java.awt.*;

// one row per wire color; subclasses declare their fields and persist values in onDone()
public abstract class GuiBundledRowScreen extends GuiScreen {
    protected static final int ROWS = 16;
    protected static final int ROW_HEIGHT = 16;
    protected static final int SWATCH_SIZE = 8;
    protected static final int GAP = 4;
    protected static final int FIELD_X_NUDGE = 8;
    private static final int NAME_FIELD_MAX_LENGTH = 12;

    private final TileContReceBase thisTileE;
    protected GuiButton doneButton;
    private GuiTextField controllerNameField;
    private GuiTextField[][] rowFields; // [row][fieldIndex]
    private String[] fieldLabels;

    private int swatchX, colorNameX;
    private int[] labelX;
    private int[] fieldX;
    protected int firstRowY;

    protected GuiBundledRowScreen(final TileContReceBase thisTileE) {
        Keyboard.enableRepeatEvents(true);
        this.allowUserInput = true;
        this.thisTileE = thisTileE;
    }

    protected abstract int getFieldCount();
    protected abstract int getFieldWidth(int fieldIndex);
    protected abstract int getFieldMaxLength(int fieldIndex);
    protected abstract String getFieldLabel(int fieldIndex);
    protected abstract String getInitialFieldText(int row, int fieldIndex);
    protected abstract String[] getGuideTextLines();
    protected abstract void onDone(String[][] fieldValues);

    protected int getFieldXNudge(int fieldIndex) {
        return FIELD_X_NUDGE;
    }

    public void initGui() {
        int fieldCount = getFieldCount();
        rowFields = new GuiTextField[ROWS][fieldCount];
        labelX = new int[fieldCount];
        fieldX = new int[fieldCount];
        fieldLabels = new String[fieldCount];

        int colorNameWidth = 0;
        for (Consts.GameColor color : Consts.GameColor.values()) {
            colorNameWidth = Math.max(colorNameWidth, this.fontRendererObj.getStringWidth(I18n.format(color.getLangKey())));
        }

        int[] labelWidths = new int[fieldCount];
        for (int f = 0; f < fieldCount; f++) {
            fieldLabels[f] = getFieldLabel(f);
            labelWidths[f] = fieldLabels[f] != null ? this.fontRendererObj.getStringWidth(fieldLabels[f]) : 0;
        }

        int rowWidth = SWATCH_SIZE + GAP + colorNameWidth + GAP * 2;
        for (int f = 0; f < fieldCount; f++) {
            rowWidth += labelWidths[f] + (labelWidths[f] > 0 ? GAP : 0) + getFieldWidth(f) + GAP;
        }
        int rowX = this.width / 2 - rowWidth / 2;

        swatchX = rowX;
        colorNameX = swatchX + SWATCH_SIZE + GAP;
        int cursorX = colorNameX + colorNameWidth + GAP * 2;
        for (int f = 0; f < fieldCount; f++) {
            labelX[f] = cursorX;
            cursorX += labelWidths[f] + (labelWidths[f] > 0 ? GAP : 0);
            fieldX[f] = cursorX - getFieldXNudge(f);
            cursorX = fieldX[f] + getFieldWidth(f) + GAP; // next label follows the field's actual (nudged) position
        }

        firstRowY = this.height / 4 - 30;

        this.buttonList.clear();
        this.buttonList.add(this.doneButton = new GuiButton(0, this.width / 2 - 110, firstRowY + ROWS * ROW_HEIGHT + 20, I18n.format("gui.done")));

        controllerNameField = new GuiTextField(this.fontRendererObj, this.width / 2 - 40, this.height / 4 - 57, 80, 15);
        controllerNameField.setText(thisTileE.getName());

        for (int row = 0; row < ROWS; row++) {
            int rowY = firstRowY + row * ROW_HEIGHT;
            int colorHex = Consts.GameColor.values()[row].getHexValue();
            for (int f = 0; f < fieldCount; f++) {
                GuiTextField field = new GuiTextField(this.fontRendererObj, fieldX[f], rowY, getFieldWidth(f), 12);
                field.setTextColor(colorHex);
                String initial = getInitialFieldText(row, f);
                field.setText(initial != null ? initial : "");
                rowFields[row][f] = field;
            }
        }
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (!this.mc.gameSettings.forceUnicodeFont) {
            this.fontRendererObj.setUnicodeFlag(true);
            this.fontRendererObj.setBidiFlag(true);
        }
        this.drawDefaultBackground();
        this.drawHorizontalLine(0, this.width, this.height / 32 * 4 + 4, new Color(255, 255, 255, 128).getRGB());
        controllerNameField.drawTextBox();

        int fieldCount = getFieldCount();
        for (int row = 0; row < ROWS; row++) {
            int rowY = firstRowY + row * ROW_HEIGHT;
            int colorHex = Consts.GameColor.values()[row].getHexValue();

            this.drawRect(swatchX, rowY + 2, swatchX + SWATCH_SIZE, rowY + 2 + SWATCH_SIZE, 0xFF000000 | colorHex);
            this.fontRendererObj.drawStringWithShadow(I18n.format(Consts.GameColor.values()[row].getLangKey()), colorNameX, rowY, colorHex);

            for (int f = 0; f < fieldCount; f++) {
                if (fieldLabels[f] != null) {
                    this.fontRendererObj.drawStringWithShadow(fieldLabels[f], labelX[f], rowY, colorHex);
                }
                rowFields[row][f].drawTextBox();
            }
        }

        drawGuideText();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawGuideText() {
        String[] lines = getGuideTextLines();
        int lineHeight = 10;
        int startY = firstRowY + ROWS * ROW_HEIGHT + 40;

        for (int i = 0; i < lines.length; i++) {
            int lineWidth = this.fontRendererObj.getStringWidth(lines[i]);
            this.fontRendererObj.drawStringWithShadow(lines[i], this.width / 2 - lineWidth / 2, startY + i * lineHeight, 0xAAAAAA);
        }
    }

    protected void actionPerformed(final GuiButton button) {
        if (button.id == 0) {
            this.thisTileE.markDirty();
            this.mc.displayGuiScreen(null);
        }
    }

    protected void mouseClicked(final int x, final int y, final int buttonClicked) {
        this.controllerNameField.mouseClicked(x, y, buttonClicked);

        for (GuiTextField[] row : rowFields) {
            for (GuiTextField field : row) {
                field.mouseClicked(x, y, buttonClicked);
            }
        }

        super.mouseClicked(x, y, buttonClicked);
    }

    protected void keyTyped(final char character, final int code) {
        if (this.controllerNameField.getText().length() <= NAME_FIELD_MAX_LENGTH || code == 14) {
            this.controllerNameField.textboxKeyTyped(character, code);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int f = 0; f < getFieldCount(); f++) {
                GuiTextField field = rowFields[row][f];
                if (field.getText().length() <= getFieldMaxLength(f) || code == 14) {
                    field.textboxKeyTyped(character, code);
                }
            }
        }

        if (code == 1) {
            this.actionPerformed(this.doneButton);
        }
    }

    public void updateScreen() {
        this.controllerNameField.updateCursorCounter();
    }

    public void onGuiClosed() {
        thisTileE.setName(this.controllerNameField.getText());

        String[][] values = new String[ROWS][getFieldCount()];
        for (int row = 0; row < ROWS; row++) {
            for (int f = 0; f < getFieldCount(); f++) {
                values[row][f] = rowFields[row][f].getText();
            }
        }

        onDone(values);

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
