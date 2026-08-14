package SCIntegrations.oc.digitalController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.signals.ISignalReceiver;
import signalcraft.entities.controllers.signals.lightSignals.ILightSignalsController;
import signalcraft.entities.signals.ISignal;
import signalcraft.entities.signals.lightSignals.TileLightSignal;
import signalcraft.signalUtils.Consts;
import signalcraft.signalUtils.SignalState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Optional.InterfaceList({
        @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers", striprefs = true),
        @Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = "OpenComputers", striprefs = true)
})
public class TileDigitalController extends TileController implements ILightSignalsController, SimpleComponent, DeviceInfo {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_signals_digital.png");

    public TileDigitalController() {
        super(TEXTURE);
        this.setName("Controller");
    }

    // OpenComputers network hookup //
    // SimpleComponent is implemented alone on purpose: OC's ASM ClassTransformer auto-injects
    // Environment (node(), onConnect/onDisconnect/onMessage, network join/leave, NBT save/load)
    // into any class implementing SimpleComponent. Implementing Environment by hand here collides
    // with that injector and silently breaks the component (see "Component logic could not be
    // injected" in the log).

    @Override
    @Optional.Method(modid = "OpenComputers")
    public String getComponentName() {
        return "signalcraft_controller";
    }

    @Override
    @Optional.Method(modid = "OpenComputers")
    public Map<String, String> getDeviceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put(DeviceAttribute.Class, DeviceClass.Communication);
        info.put(DeviceAttribute.Description, "Signal controller");
        info.put(DeviceAttribute.Vendor, "SignalCraft-Integrations");
        info.put(DeviceAttribute.Product, "Digitized Signal Sender X3");
        return info;
    }

    // Computer stuff //

    private Object[] getState(String name) {
        TileReceiver receiver = this.getReceiverByName(name);
        String state = "";
        if (receiver != null) {
            for (int i = 1; i <= 10; ++i) {
                final TileEntity tileE = worldObj.getTileEntity(receiver.xCoord, receiver.yCoord + i, receiver.zCoord);
                if (tileE instanceof ISignal) {
                    state = ((ISignal) tileE).getState().StateToString();
                }
            }
        }
        return new Object[]{state};
    }

    private Object[] setState(String name, String state) {
        if (SignalState.contains(state)) {
            TileReceiver receiver = this.getReceiverByName(name);
            if (receiver instanceof ISignalReceiver) {
                ((ISignalReceiver) receiver).setState(SignalState.fromString(state));
                return new Object[]{true};
            }
            return new Object[]{false};
        }
        throw new IllegalArgumentException("invalid state: " + state);
    }

    private Object[] setEveryState(String state) {
        if (SignalState.contains(state)) {
            for (TileReceiver receiver : this.getReceivers()) {
                if (receiver instanceof ISignalReceiver) {
                    ((ISignalReceiver) receiver).setState(SignalState.fromString(state));
                }
            }
            return new Object[]{true};
        }
        throw new IllegalArgumentException("invalid state" + state);
    }

    private Object[] setMostRestrictiveOnAll() {
        boolean any = false;
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver instanceof ISignalReceiver) {
                ((ISignalReceiver) receiver).setStateToSignalsMostRestrictive();
                any = true;
            }
        }
        return new Object[]{any};
    }

    private Object[] getSpeedSignText(String name) {
        TileReceiver receiver = this.getReceiverByName(name);
        if (receiver != null) {
            for (int i = 1; i <= 10; ++i) {
                final TileEntity tileE = worldObj.getTileEntity(receiver.xCoord, receiver.yCoord + i, receiver.zCoord);
                if (tileE instanceof TileLightSignal) {
                    Consts.SpeedSignText speedSignText = ((TileLightSignal) tileE).getSpeedSignText();
                    return new Object[]{speedSignText != Consts.SpeedSignText.NO_SIGN, speedSignText.toString()};
                }
            }
        }
        return new Object[]{false};
    }

    private Object[] getValidStatesForSignal(String name) {
        TileReceiver receiver = this.getReceiverByName(name);
        if (receiver instanceof ISignalReceiver) {
            return new Object[]{((ISignalReceiver) receiver).getValidStatesForSignal()};
        }
        return new Object[]{aspectMap};
    }

    private Object[] getControllerName() {
        String name = this.getName();
        if (name != null) {
            return new Object[]{name};
        }
        throw new IllegalArgumentException("no valid controller found");
    }

    private Object[] getSignalNames() {
        ArrayList<String> list = new ArrayList<>();
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver.getName() != null) {
                list.add(receiver.getName());
            }
        }
        return new Object[]{list};
    }

    private static LinkedHashMap<Object, Object> aspectMap;

    private static Object[] states() {
        if (aspectMap == null) {
            LinkedHashMap<Object, Object> newMap = new LinkedHashMap<Object, Object>();
            for (int i = 0; i <= SignalState.VALUES.length - 1; i++) {
                SignalState state = SignalState.VALUES[i];

                String name = state.toString();
                newMap.put(name, state);
            }
            aspectMap = newMap;
        }
        return new Object[]{aspectMap};
    }

    @Callback(doc = "function(name:string, state:string):boolean; Tries to set the state for any paired signal with the specified name. Returns true on success.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] setState(Context context, Arguments args) {
        return setState(args.checkString(0), args.checkString(1));
    }

    @Callback(doc = "function(name:string):string; Gets the state for the signal with the specified name. Returns the state on success.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getState(Context context, Arguments args) {
        return getState(args.checkString(0));
    }

    @Callback(doc = "function(state:string):boolean; Sets the state for every paired signal to the specified state. Returns true on success.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] setEveryState(Context context, Arguments args) {
        return setEveryState(args.checkString(0));
    }

    @Callback(doc = "function():boolean; Sets every paired signal to its own most restrictive valid state. Unlike setEveryState, this works even for signals that don't have a \"Stuj\" state. Returns true if at least one receiver was affected.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] setMostRestrictiveOnAll(Context context, Arguments args) {
        return setMostRestrictiveOnAll();
    }

    @Callback(doc = "function(name:string):boolean, string; Returns whether the signal with the specified name has a speed sign, and its text (\"30\", \"50\", \"30S\") if so.", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getSpeedSignText(Context context, Arguments args) {
        return getSpeedSignText(args.checkString(0));
    }

    @Callback(doc = "function(name:string):boolean; Returns a list of every valid state of specified signal (by the name of the receiver below the signal)", direct = true, limit = 64)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getValidStatesForSignal(Context context, Arguments args) {
        return getValidStatesForSignal(args.checkString(0));
    }

    @Callback(doc = "function():table; Returns a list containing the name of every paired receiver.", direct = true, limit = 128)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getSignalNames(Context c, Arguments a) {
        return getSignalNames();
    }

    @Callback(doc = "This is a list of every available Signal States in CZSAR", getter = true, direct = true)
    @Optional.Method(modid = "OpenComputers")
    public Object[] states(Context c, Arguments a) {
        return states();
    }

    @Callback(doc = "function():String; Returns RailCraft label name of the controller", direct = true, limit = 32)
    @Optional.Method(modid = "OpenComputers")
    public Object[] getControllerName(Context c, Arguments a) {
        return getControllerName();
    }
}
