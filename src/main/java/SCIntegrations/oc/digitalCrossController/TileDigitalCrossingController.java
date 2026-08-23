package SCIntegrations.oc.digitalCrossController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.crossings.TileCrossingController;
import signalcraft.entities.controllers.crossings.TileCrossingReceiver;
import signalcraft.entities.levelCrossings.ILevelCrossing;
import signalcraft.signalUtils.Consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Optional.InterfaceList({
        @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers", striprefs = true),
        @Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = "OpenComputers", striprefs = true)
})
public class TileDigitalCrossingController extends TileCrossingController implements SimpleComponent, DeviceInfo {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_crossing_digital.png");

    // names currently commanded active (barriers down); used to avoid raising a barrier
    // that's shared with another receiver whose name is still active - see activate()
    private final Set<String> activeNames = new HashSet<>();

    public TileDigitalCrossingController() {
        super(TEXTURE);
        this.setGuiId(Consts.GuiIDs.NOGUI);
    }

    // OpenComputers network hookup //
    // SimpleComponent is implemented alone on purpose: OC's ASM ClassTransformer auto-injects
    // Environment (node(), onConnect/onDisconnect/onMessage, network join/leave, NBT save/load)
    // into any class implementing SimpleComponent. Implementing Environment by hand collides
    // with that injector and silently breaks the component.

    @Override
    @Optional.Method(modid = "OpenComputers")
    public String getComponentName() {
        return "signalcraft_crossing_controller";
    }

    @Override
    @Optional.Method(modid = "OpenComputers")
    public Map<String, String> getDeviceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put(DeviceAttribute.Class, DeviceClass.Communication);
        info.put(DeviceAttribute.Description, "Crossing controller");
        info.put(DeviceAttribute.Vendor, "SignalCraft-Integrations");
        info.put(DeviceAttribute.Product, "Digitized Signal Sender X3");
        return info;
    }

    // Computer stuff //

    private boolean nameHasBarriers(String name) {
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver instanceof TileCrossingReceiver && name.equals(receiver.getName())
                    && ((TileCrossingReceiver) receiver).signalHasBarriers()) {
                return true;
            }
        }
        return false;
    }

    private boolean isArmDownFor(String name) {
        boolean any = false;
        for (TileReceiver receiver : this.getReceivers()) {
            if (!(receiver instanceof TileCrossingReceiver) || !name.equals(receiver.getName())) {
                continue;
            }
            if (!((TileCrossingReceiver) receiver).isArmDown()) {
                return false;
            }
            any = true;
        }
        return any;
    }

    private Object[] activate(String name, boolean state) {
        boolean any = false;
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
            any = true;
        }
        return new Object[]{any};
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

    private Object[] activateAll(boolean state) {
        boolean any = this.getReceivers().length > 0;
        this.setBarrierState(state);
        return new Object[]{any};
    }

    private Object[] getControllerName() {
        String name = this.getName();
        if (name != null) {
            return new Object[]{name};
        }
        throw new IllegalArgumentException("no valid controller found");
    }

    private Object[] getReceiverNames() {
        ArrayList<String> list = new ArrayList<>();
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver.getName() != null) {
                list.add(receiver.getName());
            }
        }
        return new Object[]{list};
    }

    @Callback(doc = "function(name:string, state:boolean):boolean; Activates or deactivates every paired crossing receiver with the specified name. state=true lowers the barriers, state=false raises them (a receiver is only raised if it has barriers, unless none of the paired receivers do). Returns true if at least one receiver matched.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] activate(Context context, Arguments args) {
        return activate(args.checkString(0), args.checkBoolean(1));
    }

    @Callback(doc = "function(state:boolean):boolean; Activates or deactivates every paired crossing receiver, preserving the normal barrier logic (always drops all barriers, but only raises the ones that have barriers unless none of them do). Returns true if at least one receiver was affected.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] activateAll(Context context, Arguments args) {
        return activateAll(args.checkBoolean(0));
    }

    @Callback(doc = "function(name:string):boolean; Returns true if every paired crossing receiver with the specified name has its arm down, false otherwise (including if no receiver matched).", direct = true, limit = 128)
    @Optional.Method(modid = "OpenComputers")
    public Object[] isArmDownFor(Context context, Arguments args) {
        return new Object[]{isArmDownFor(args.checkString(0))};
    }

    @Callback(doc = "function():table; Returns a list containing the name of every paired receiver.", direct = true, limit = 128)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getReceiverNames(Context c, Arguments a) {
        return getReceiverNames();
    }

    @Callback(doc = "function():String; Returns the name of the controller", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getControllerName(Context c, Arguments a) {
        return getControllerName();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList activeNamesList = new NBTTagList();
        for (String name : activeNames) {
            activeNamesList.appendTag(new NBTTagString(name));
        }
        tag.setTag("activeNames", activeNamesList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        activeNames.clear();
        if (tag.hasKey("activeNames")) {
            NBTTagList activeNamesList = tag.getTagList("activeNames", 8); // 8 = NBTTagString id
            for (int i = 0; i < activeNamesList.tagCount(); i++) {
                activeNames.add(activeNamesList.getStringTagAt(i));
            }
        }
    }
}
