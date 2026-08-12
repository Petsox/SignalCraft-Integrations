package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.SCIntegrations;
import SCIntegrations.core.GuiConsts;
import cpw.mods.fml.common.Optional;
import mrtjp.projectred.api.IBundledTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.signals.ISignalReceiver;
import signalcraft.signalUtils.SignalState;

@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = "ProjRed|Core", striprefs = true)
public class TileBundledReceiver extends TileReceiver implements ISignalReceiver, IBundledTile {

    private byte[] outputSignal = new byte[16];
    private String[] signalStatesText = new String[16];
    private SignalState currentState = SignalState.STUJ;
    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/receiver_signals_bundled.png");

    public TileBundledReceiver() {
        super(TEXTURE);
        this.setGuiId(GuiConsts.BUNDLED_RECEIVER.guiId);
        this.setName("Receiver");
    }

    public void onNeighborUpdate() {
        // re-broadcast the current output so a newly (dis)connected bundled cable re-reads it
        notifyNeighbors();
    }

    public String[] getSignalStatesText() {
        return signalStatesText;
    }

    public void setSignalStatesText(String[] states) {
        if (states.length == 16) {
            this.signalStatesText = states;
        }
    }

    private boolean channelMatchesCurrentState(int channel) {
        String csv = signalStatesText[channel];
        if (csv == null || csv.trim().isEmpty()) return false;

        for (String token : csv.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && SignalState.contains(trimmed) && SignalState.fromString(trimmed) == currentState) {
                return true;
            }
        }
        return false;
    }

    private void updateOutputSignal() {
        byte[] output = new byte[16];
        for (int i = 0; i < 16; i++) {
            if (channelMatchesCurrentState(i)) {
                output[i] = (byte) 255;
            }
        }
        this.outputSignal = output;
    }

    private void notifyNeighbors() {
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            markDirty();
        }
    }

    @Override
    public void setState(SignalState state) {
        this.currentState = state;
        updateOutputSignal();
        notifyNeighbors();
    }

    @Override
    public SignalState getStateOnSignal() {
        return currentState;
    }

    @Override
    public void setStateToSignalsMostRestrictive() {
        setState(SignalState.STUJ);
    }

    @Override
    public SignalState[] getValidStatesForSignal() {
        return SignalState.values();
    }

    @Override
    @Optional.Method(modid = "ProjRed|Core")
    public boolean canConnectBundled(int side) {
        return true;
    }

    @Override
    @Optional.Method(modid = "ProjRed|Core")
    public byte[] getBundledSignal(int side) {
        return outputSignal.clone();
    }

    @Override
    public boolean isControllerValid(TileController controller) {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("Name", this.getName());

        NBTTagList statesList = new NBTTagList();
        for (String text : signalStatesText) {
            statesList.appendTag(new NBTTagString(text == null ? "" : text));
        }
        tag.setTag("signalStatesText", statesList);

        tag.setByteArray("outputSignal", outputSignal);
        tag.setInteger("currentState", currentState.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setName(tag.getString("Name"));
        if (tag.hasKey("signalStatesText")) {
            NBTTagList statesList = tag.getTagList("signalStatesText", 8); // 8 = NBTTagString id
            for (int i = 0; i < statesList.tagCount() && i < 16; i++) {
                signalStatesText[i] = statesList.getStringTagAt(i);
            }
        } else if (tag.hasKey("signalStates")) {
            // migrate the old single-state-per-channel int[] format so existing saves keep their configuration
            int[] stored = tag.getIntArray("signalStates");
            if (stored != null) {
                for (int i = 0; i < stored.length && i < 16; i++) {
                    if (stored[i] != 0) {
                        signalStatesText[i] = SignalState.fromInteger(stored[i]).StateToString();
                    }
                }
            }
        }
        if (tag.hasKey("outputSignal")) {
            byte[] stored = tag.getByteArray("outputSignal");
            if (stored != null && stored.length == 16) {
                System.arraycopy(stored, 0, outputSignal, 0, 16);
            }
        }
        if (tag.hasKey("currentState")) {
            currentState = SignalState.fromInteger(tag.getInteger("currentState"));
        }
    }
}
