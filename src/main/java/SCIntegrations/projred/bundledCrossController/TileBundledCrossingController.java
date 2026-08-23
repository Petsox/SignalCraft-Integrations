package SCIntegrations.projred.bundledCrossController;

import SCIntegrations.SCIntegrations;
import SCIntegrations.core.GuiConsts;
import cpw.mods.fml.common.Optional;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.crossings.TileCrossingController;
import signalcraft.entities.controllers.crossings.TileCrossingReceiver;
import signalcraft.entities.levelCrossings.ILevelCrossing;

import java.util.HashSet;
import java.util.Set;

@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = "ProjRed|Core", striprefs = true)
public class TileBundledCrossingController extends TileCrossingController implements IBundledTile {

    private final byte[] currentSignal = new byte[16];
    private String[] receiverNames = new String[16];
    // names currently commanded active (barriers down); used to avoid raising a barrier
    // that's shared with another receiver whose name is still active - see activate()
    private final Set<String> activeNames = new HashSet<>();
    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_crossing_bundled.png");

    public TileBundledCrossingController() {
        super(TEXTURE);
        this.setGuiId(GuiConsts.BUNDLED_CROSSING_CONTROLLER.guiId);
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

    public String[] getReceiverNames() {
        return receiverNames;
    }

    private void onBundledSignalChanged(byte[] incoming) {
        for (int i = 0; i < incoming.length; i++) {
            if (incoming[i] == currentSignal[i]) continue; // no change for this color, skip

            activateColor(i, incoming[i] != 0);
        }

        // clone to avoid reference bugs
        System.arraycopy(incoming, 0, currentSignal, 0, 16);
    }

    private void activateColor(int colorIndex, boolean state) {
        String namesCsv = receiverNames[colorIndex];
        if (namesCsv == null || namesCsv.trim().isEmpty()) return;

        Set<String> names = new HashSet<>();
        for (String name : namesCsv.split(",")) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) names.add(trimmed);
        }

        for (String name : names) {
            activate(name, state);
        }
    }

    private boolean nameHasBarriers(String name) {
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver instanceof TileCrossingReceiver && name.equals(receiver.getName())
                    && ((TileCrossingReceiver) receiver).signalHasBarriers()) {
                return true;
            }
        }
        return false;
    }

    private void activate(String name, boolean state) {
        if (state) {
            activeNames.add(name);
        } else {
            activeNames.remove(name);
        }
        boolean nameHasBarriers = !state && nameHasBarriers(name);
        for (TileReceiver receiver : this.getReceivers()) {
            if (!(receiver instanceof TileCrossingReceiver) || !name.equals(receiver.getName())) {
                continue;
            }
            TileCrossingReceiver crossingReceiver = (TileCrossingReceiver) receiver;
            if (state) {
                // lowering always drops the barriers, matching setBarrierState(true)
                crossingReceiver.setCrossingState(true);
            } else if ((!nameHasBarriers || crossingReceiver.signalHasBarriers())
                    && !isCrossingHeldByOtherActiveName(crossingReceiver, name)) {
                // raising only affects receivers that actually have barriers, unless none of the receivers with this name do,
                // and never raises a physical barrier that another still-active name is also relying on
                crossingReceiver.setCrossingState(false);
            }
        }
    }

    // true if the physical crossing found above `receiver` is also reachable from a receiver
    // belonging to a different, still-active name - i.e. two named groups share one barrier
    private boolean isCrossingHeldByOtherActiveName(TileCrossingReceiver receiver, String excludeName) {
        TileEntity crossing = findCrossingTile(receiver);
        if (crossing == null) return false;

        for (TileReceiver other : this.getReceivers()) {
            if (!(other instanceof TileCrossingReceiver) || other == receiver) continue;
            String otherName = other.getName();
            if (otherName == null || otherName.equals(excludeName) || !activeNames.contains(otherName)) continue;
            if (findCrossingTile((TileCrossingReceiver) other) == crossing) return true;
        }
        return false;
    }

    // mirrors TileCrossingReceiver's private findCrossing() scan, using only public API
    private static TileEntity findCrossingTile(TileCrossingReceiver receiver) {
        for (int i = 1; i <= 10; i++) {
            TileEntity tile = receiver.getWorldObj().getTileEntity(receiver.xCoord, receiver.yCoord + i, receiver.zCoord);
            if (tile instanceof ILevelCrossing) {
                return tile;
            }
        }
        return null;
    }

    public void setReceiverNames(String[] names) {
        if (names.length == 16) {
            this.receiverNames = names;
        }
    }

    @Override
    @Optional.Method(modid = "ProjRed|Core")
    public boolean canConnectBundled(int side) {
        return true;
    }

    @Override
    @Optional.Method(modid = "ProjRed|Core")
    public byte[] getBundledSignal(int side) {
        // controller does not emit bundled signal (input-only)
        return new byte[16];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("Name", this.getName());

        NBTTagList namesList = new NBTTagList();
        for (String name : receiverNames) {
            namesList.appendTag(new NBTTagString(name == null ? "" : name));
        }
        tag.setTag("receiverNames", namesList);

        tag.setByteArray("currentSignal", currentSignal);

        NBTTagList activeNamesList = new NBTTagList();
        for (String name : activeNames) {
            activeNamesList.appendTag(new NBTTagString(name));
        }
        tag.setTag("activeNames", activeNamesList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setName(tag.getString("Name"));
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
        activeNames.clear();
        if (tag.hasKey("activeNames")) {
            NBTTagList activeNamesList = tag.getTagList("activeNames", 8); // 8 = NBTTagString id
            for (int i = 0; i < activeNamesList.tagCount(); i++) {
                activeNames.add(activeNamesList.getStringTagAt(i));
            }
        }
    }
}
