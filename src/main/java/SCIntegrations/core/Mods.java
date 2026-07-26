package SCIntegrations.core;

import cpw.mods.fml.common.Loader;

public class Mods {

    //Mods used for integration
    public enum ModsEnum {
        ProjectRed("ProjRed|Core"),
        OpenComputers("OpenComputers");

        public final String modId;

        ModsEnum(String modId) {
            this.modId = modId;
        }
    }


    public static boolean isLoaded(ModsEnum mod) {
        return Loader.isModLoaded(mod.modId);
    }

    public static boolean integrationCreativeTabNeeded(){
        for (ModsEnum value : ModsEnum.values()) {
            if (isLoaded(value)) {
                return true;
            }
        }
        return false;
    }
}