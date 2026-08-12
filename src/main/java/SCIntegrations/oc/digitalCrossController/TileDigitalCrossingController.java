package SCIntegrations.oc.digitalCrossController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.crossings.TileCrossingController;
import signalcraft.entities.controllers.crossings.TileCrossingReceiver;
import signalcraft.signalUtils.Consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Optional.InterfaceList({
        @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers", striprefs = true),
        @Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = "OpenComputers", striprefs = true)
})
public class TileDigitalCrossingController extends TileCrossingController implements SimpleComponent, DeviceInfo {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_crossing_digital.png");

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

    private Object[] activate(String name, boolean state) {
        boolean any = false;
        for (TileReceiver receiver : this.getReceivers()) {
            if (!(receiver instanceof TileCrossingReceiver) || !name.equals(receiver.getName())) {
                continue;
            }
            TileCrossingReceiver crossingReceiver = (TileCrossingReceiver) receiver;
            if (state) {
                // lowering always drops the barriers, matching setBarrierState(true)
                crossingReceiver.setCrossingState(true);
            } else if (!crossingHasBarriers() || crossingReceiver.signalHasBarriers()) {
                // raising only affects receivers that actually have barriers, unless none of them do
                crossingReceiver.setCrossingState(false);
            }
            any = true;
        }
        return new Object[]{any};
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
}
