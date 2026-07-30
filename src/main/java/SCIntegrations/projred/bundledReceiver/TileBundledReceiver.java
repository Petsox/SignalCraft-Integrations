package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.SCIntegrations;
import SCIntegrations.core.GuiConsts;
import mrtjp.projectred.api.IBundledTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.signals.ISignalReceiver;
import signalcraft.signalUtils.SignalState;

import java.util.Arrays;

public class TileBundledReceiver extends TileReceiver implements ISignalReceiver, IBundledTile {

    private byte[] outputSignal = new byte[16];
    private int[] signalStates = new int[16];
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

    public int[] getSignalStates() {
        return signalStates;
    }

    public void setSignalStates(int[] states) {
        if (states.length == 16) {
            this.signalStates = states;
        }
    }


    private void updateOutputSignal() {
        byte[] output = new byte[16];
        for (int i = 0; i < 16; i++) {
            if (signalStates[i] == currentState.ordinal()) {
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
    public boolean canConnectBundled(int side) {
        return true;
    }

    @Override
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
        tag.setIntArray("signalStates", signalStates);
        tag.setByteArray("outputSignal", outputSignal);
        tag.setInteger("currentState", currentState.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setName(tag.getString("Name"));
        if (tag.hasKey("signalStates")) {
            int[] stored = tag.getIntArray("signalStates");
            if (stored != null) {
                if (stored.length == 16) {
                    signalStates = stored;
                } else {
                    signalStates = Arrays.copyOf(stored, 16);
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
