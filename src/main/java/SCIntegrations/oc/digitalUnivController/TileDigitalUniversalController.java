package SCIntegrations.oc.digitalUnivController;

import SCIntegrations.SCIntegrations;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.util.ResourceLocation;
import signalcraft.entities.controllers.TileController;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.entities.controllers.universal.IUniversalController;
import signalcraft.entities.controllers.universal.TileReceiverUniversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileDigitalUniversalController extends TileController implements IUniversalController, SimpleComponent, DeviceInfo {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SCIntegrations.MOD_ID + ":textures/models/controllers/controller_signals_digital.png");

    public TileDigitalUniversalController() {
        super(TEXTURE);
        this.setName("Controller");
    }

    // OpenComputers network hookup //
    // SimpleComponent is implemented alone on purpose: OC's ASM ClassTransformer auto-injects
    // Environment (node(), onConnect/onDisconnect/onMessage, network join/leave, NBT save/load)
    // into any class implementing SimpleComponent. Implementing Environment by hand collides
    // with that injector and silently breaks the component.

    @Override
    public String getComponentName() {
        return "signalcraft_universal_controller";
    }

    @Override
    public Map<String, String> getDeviceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put(DeviceAttribute.Class, DeviceClass.Communication);
        info.put(DeviceAttribute.Description, "Universal signal controller");
        info.put(DeviceAttribute.Vendor, "SignalCraft-Integrations");
        info.put(DeviceAttribute.Product, "Digitized Signal Sender X3");
        return info;
    }

    // Computer stuff //

    private Object[] setActive(String name, boolean active) {
        TileReceiver receiver = this.getReceiverByName(name);
        if (receiver instanceof TileReceiverUniversal) {
            ((TileReceiverUniversal) receiver).activate(active);
            return new Object[]{true};
        }
        return new Object[]{false};
    }

    private Object[] setActiveEverything(boolean active) {
        boolean any = false;
        for (TileReceiver receiver : this.getReceivers()) {
            if (receiver instanceof TileReceiverUniversal) {
                ((TileReceiverUniversal) receiver).activate(active);
                any = true;
            }
        }
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

    @Callback(doc = "function(name:string, active:boolean):boolean; Activates or deactivates the paired receiver with the specified name. Returns true on success.", direct = true, limit = 32)
    public Object[] setActive(Context context, Arguments args) {
        return setActive(args.checkString(0), args.checkBoolean(1));
    }

    @Callback(doc = "function(active:boolean):boolean; Activates or deactivates every paired receiver. Returns true if at least one receiver was affected.", direct = true, limit = 32)
    public Object[] setActiveEverything(Context context, Arguments args) {
        return setActiveEverything(args.checkBoolean(0));
    }

    @Callback(doc = "function():table; Returns a list containing the name of every paired receiver.", direct = true, limit = 128)
    public Object[] getReceiverNames(Context c, Arguments a) {
        return getReceiverNames();
    }

    @Callback(doc = "function():String; Returns the name of the controller", direct = true, limit = 32)
    public Object[] getControllerName(Context c, Arguments a) {
        return getControllerName();
    }
}
