package SCIntegrations.projred.bundledUnivController;

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
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.universal.IUniversalController;
import signalcraft.entities.controllers.universal.TileReceiverUniversal;
import signalcraft.signalUtils.BlockPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Optional.Interface(iface = "mrtjp.projectred.api.IBundledTile", modid = "ProjRed|Core", striprefs = true)
public class TileBundledUniversalController extends TileController implements IUniversalController, IBundledTile {

    private final byte[] currentSignal = new byte[16];
    private String[] receiverNames = new String[16];
    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_universal_bundled.png");

    public TileBundledUniversalController() {
        super(TEXTURE);
        this.setGuiId(GuiConsts.BUNDLED_UNIVERSAL_CONTROLLER.guiId);
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

            if (incoming[i] == 0) {
                deactivateColor(i);
            } else {
                activateColor(i);
            }
        }

        // clone to avoid reference bugs
        System.arraycopy(incoming, 0, currentSignal, 0, 16);
    }

    private void activateColor(int colorIndex) {
        forNamedReceivers(colorIndex, receiver -> receiver.activate(true));
    }

    private void deactivateColor(int colorIndex) {
        forNamedReceivers(colorIndex, receiver -> receiver.activate(false));
    }

    private void forNamedReceivers(int colorIndex, Consumer<TileReceiverUniversal> action) {
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
            if (tile instanceof TileReceiverUniversal && names.contains(((TileReceiverUniversal) tile).getName())) {
                action.accept((TileReceiverUniversal) tile);
            }
        }
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
    }
}
