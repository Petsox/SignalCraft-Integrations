package SCIntegrations.projred.bundledController;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.signals.ISignalReceiver;
import signalcraft.entities.controllers.signals.lightSignals.ILightSignalsController;
import signalcraft.signalUtils.Consts;
import signalcraft.signalUtils.SignalState;

import java.util.Arrays;

public class TileBundledController extends TileController implements ILightSignalsController {

    private final byte[] currentSignal = new byte[16];
    private int[] signalStates = new int[16];
    private static final ResourceLocation TEXTURE = new ResourceLocation("signalcraft:textures/models/controllers/controller_signals_bundeled.png");

    public TileBundledController() {
        super(TEXTURE);
        this.setGuiId(Consts.GuiIDs.BUNDLED_CONTROLLER);
        this.setName("Controller");
    }

    public void onNeighborUpdate() {

    }



    public int[] getSignalStates() {
        return signalStates;
    }

    private void onBundledSignalChanged(byte[] incoming) {
        for (int i = 0; i < incoming.length; i++) {
            if (incoming[i] == currentSignal[i]) {
                continue; // no change for this color, skip
            } else if (incoming[i] == 0) {
                setMostRestrictiveOnAll();
            } else {
                applyStateChange(i);
            }
        }

        // clone to avoid reference bugs
        System.arraycopy(incoming, 0, currentSignal, 0, 16);
    }


    private void applyStateChange(int stateIndex) {
            SignalState state = SignalState.fromInteger(signalStates[stateIndex]);
            changeStateOnAll(state);
    }


    private void changeStateOnAll(SignalState newState) {
        this.getPairings().forEach((pairing, id) -> {
            TileEntity tile = worldObj.getTileEntity(pairing.getX(), pairing.getY(), pairing.getZ());
            if (tile instanceof ISignalReceiver) {
                ISignalReceiver receiver = (ISignalReceiver) tile;
                if (Arrays.asList(receiver.getValidStatesForSignal()).contains(newState)) {
                    receiver.setStateOnSignal(newState);
                }
            }
        });
    }

    private void setMostRestrictiveOnAll() {
        this.getPairings().forEach((pairing, id) -> {
            TileEntity tile = worldObj.getTileEntity(pairing.getX(), pairing.getY(), pairing.getZ());
            if (tile instanceof ISignalReceiver) {
                ((ISignalReceiver) tile).setStateOnSignalToMostRestrictive();
            }
        });
    }

    public void setSignalStates(int[] states) {
        if (states.length == 16) {
            this.signalStates = states;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("Name", this.getName());
        tag.setIntArray("signalStates", signalStates);
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
    }
}
