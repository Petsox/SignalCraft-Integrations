package SCIntegrations.projred.bundledAdvController;

import SCIntegrations.SCIntegrations;
import SCIntegrations.core.GuiConsts;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.signals.ISignalReceiver;
import signalcraft.entities.controllers.signals.lightSignals.ILightSignalsController;
import signalcraft.signalUtils.BlockPos;
import signalcraft.signalUtils.SignalState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TileBundledAdvancedController extends TileController implements ILightSignalsController, IBundledTile {

    private final byte[] currentSignal = new byte[16];
    private int[] signalStates = new int[16];
    private String[] receiverNames = new String[16];
    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_signals_advanced_bundled.png");

    public TileBundledAdvancedController() {
        super(TEXTURE);
        this.setGuiId(GuiConsts.BUNDLED_ADVANCED_CONTROLLER.guiId);
        this.setName("Controller");
    }

    public void onNeighborUpdate() {
        checkBundledInput();
    }

    private void checkBundledInput() {
        byte[] merged = new byte[16];
        boolean anyConnected = false;

        for (int side = 0; side < 6; side++) {
            byte[] input = ProjectRedAPI.transmissionAPI
                    .getBundledInput(worldObj, xCoord, yCoord, zCoord, side);

            if (input == null) continue;
            anyConnected = true;

            for (int i = 0; i < 16; i++) {
                int strength = Math.max(merged[i] & 0xFF, input[i] & 0xFF);
                merged[i] = (byte) strength;
            }
        }

        if (anyConnected) {
            onBundledSignalChanged(merged);
        }
    }

    public int[] getSignalStates() {
        return signalStates;
    }

    public String[] getReceiverNames() {
        return receiverNames;
    }

    private void onBundledSignalChanged(byte[] incoming) {
        for (int i = 0; i < incoming.length; i++) {
            if (incoming[i] == currentSignal[i]) {
                continue; // no change for this color, skip
            } else if (incoming[i] == 0) {
                setMostRestrictiveOnNamed(i);
            } else {
                applyStateChange(i);
            }
        }

        // clone to avoid reference bugs
        System.arraycopy(incoming, 0, currentSignal, 0, 16);
    }

    private void applyStateChange(int colorIndex) {
        SignalState state = SignalState.fromInteger(signalStates[colorIndex]);
        forNamedReceivers(colorIndex, receiver -> {
            if (Arrays.asList(receiver.getValidStatesForSignal()).contains(state)) {
                receiver.setState(state);
            }
        });
    }

    private void setMostRestrictiveOnNamed(int colorIndex) {
        forNamedReceivers(colorIndex, ISignalReceiver::setStateToSignalsMostRestrictive);
    }

    private void forNamedReceivers(int colorIndex, Consumer<ISignalReceiver> action) {
        String namesCsv = receiverNames[colorIndex];
        if (namesCsv == null || namesCsv.trim().isEmpty()) return;

        Set<String> names = new HashSet<>();
        for (String name : namesCsv.split(",")) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) names.add(trimmed);
        }
        if (names.isEmpty()) return;

        for (Map.Entry<BlockPos, Integer> entry : this.getPairings().entrySet()) {
            BlockPos pos = entry.getKey();
            TileEntity tile = worldObj.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (tile instanceof TileReceiver && tile instanceof ISignalReceiver && names.contains(((TileReceiver) tile).getName())) {
                action.accept((ISignalReceiver) tile);
            }
        }
    }

    public void setSignalStates(int[] states) {
        if (states.length == 16) {
            this.signalStates = states;
        }
    }

    public void setReceiverNames(String[] names) {
        if (names.length == 16) {
            this.receiverNames = names;
        }
    }

    @Override
    public boolean canConnectBundled(int side) {
        return true;
    }

    @Override
    public byte[] getBundledSignal(int side) {
        // controller does not emit bundled signal (input-only)
        return new byte[16];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("Name", this.getName());
        tag.setIntArray("signalStates", signalStates);

        NBTTagList namesList = new NBTTagList();
        for (String name : receiverNames) {
            namesList.appendTag(new NBTTagString(name == null ? "" : name));
        }
        tag.setTag("receiverNames", namesList);

        tag.setByteArray("currentSignal", currentSignal);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setName(tag.getString("Name"));
        if (tag.hasKey("signalStates")) {
            int[] stored = tag.getIntArray("signalStates");
            if (stored != null) {
                signalStates = stored.length == 16 ? stored : Arrays.copyOf(stored, 16);
            }
        }
        if (tag.hasKey("receiverNames")) {
            NBTTagList namesList = tag.getTagList("receiverNames", 8); // 8 = NBTTagString id
            for (int i = 0; i < namesList.tagCount() && i < 16; i++) {
                receiverNames[i] = namesList.getStringTagAt(i);
            }
        }
        if (tag.hasKey("currentSignal")) {
            byte[] stored = tag.getByteArray("currentSignal");
            if (stored != null && stored.length == 16) {
                System.arraycopy(stored, 0, currentSignal, 0, 16);
            }
        }
    }
}
