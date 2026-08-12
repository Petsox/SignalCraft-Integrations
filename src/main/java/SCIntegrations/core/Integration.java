package SCIntegrations.core;

import SCIntegrations.oc.digitalController.BlockDigitalController;
import SCIntegrations.oc.digitalController.ItemBlockDigitalController;
import SCIntegrations.oc.digitalController.TileDigitalController;
import SCIntegrations.oc.digitalCrossController.BlockDigitalCrossingController;
import SCIntegrations.oc.digitalCrossController.ItemBlockDigitalCrossingController;
import SCIntegrations.oc.digitalCrossController.TileDigitalCrossingController;
import SCIntegrations.oc.digitalUnivController.BlockDigitalUniversalController;
import SCIntegrations.oc.digitalUnivController.ItemBlockDigitalUniversalController;
import SCIntegrations.oc.digitalUnivController.TileDigitalUniversalController;
import SCIntegrations.projred.bundledAdvController.BlockBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.ItemBlockBundledAdvancedController;
import SCIntegrations.projred.bundledAdvController.TileBundledAdvancedController;
import SCIntegrations.projred.bundledController.BlockBundledController;
import SCIntegrations.projred.bundledController.ItemBlockBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import SCIntegrations.projred.bundledReceiver.BlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.ItemBlockBundledReceiver;
import SCIntegrations.projred.bundledReceiver.TileBundledReceiver;
import SCIntegrations.projred.bundledUnivController.BlockBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.ItemBlockBundledUniversalController;
import SCIntegrations.projred.bundledUnivController.TileBundledUniversalController;
import cpw.mods.fml.common.registry.GameRegistry;

public class Integration {

    private static BlockBundledController bundledController;
    private static BlockBundledUniversalController bundledUniversalController;
    private static BlockBundledAdvancedController bundledAdvancedController;
    private static BlockBundledReceiver bundledReceiver;
    private static BlockDigitalController digitalController;
    private static BlockDigitalUniversalController digitalUniversalController;
    private static BlockDigitalCrossingController digitalCrossingController;

    public static void registerIntegrations() {

        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)){
            //Bundled Controller
            GameRegistry.registerTileEntity(TileBundledController.class, "BundledController");
            bundledController = new BlockBundledController("BundledController");
            GameRegistry.registerBlock(bundledController, ItemBlockBundledController.class, bundledController.getUnlocalizedName());

            //Bundled Universal Controller
            GameRegistry.registerTileEntity(TileBundledUniversalController.class, "BundledUniversalController");
            bundledUniversalController = new BlockBundledUniversalController("BundledUniversalController");
            GameRegistry.registerBlock(bundledUniversalController, ItemBlockBundledUniversalController.class, bundledUniversalController.getUnlocalizedName());

            //Bundled Advanced Controller
            GameRegistry.registerTileEntity(TileBundledAdvancedController.class, "BundledAdvancedController");
            bundledAdvancedController = new BlockBundledAdvancedController("BundledAdvancedController");
            GameRegistry.registerBlock(bundledAdvancedController, ItemBlockBundledAdvancedController.class, bundledAdvancedController.getUnlocalizedName());

            //Bundled Receiver
            GameRegistry.registerTileEntity(TileBundledReceiver.class, "BundledReceiver");
            bundledReceiver = new BlockBundledReceiver("BundledReceiver");
            GameRegistry.registerBlock(bundledReceiver, ItemBlockBundledReceiver.class, bundledReceiver.getUnlocalizedName());
        }

        if (Mods.isLoaded(Mods.ModsEnum.OpenComputers)) {
            //Digital Controller
            GameRegistry.registerTileEntity(TileDigitalController.class, "DigitalController");
            digitalController = new BlockDigitalController("DigitalController");
            GameRegistry.registerBlock(digitalController, ItemBlockDigitalController.class, digitalController.getUnlocalizedName());

            //Digital Universal Controller
            GameRegistry.registerTileEntity(TileDigitalUniversalController.class, "DigitalUniversalController");
            digitalUniversalController = new BlockDigitalUniversalController("DigitalUniversalController");
            GameRegistry.registerBlock(digitalUniversalController, ItemBlockDigitalUniversalController.class, digitalUniversalController.getUnlocalizedName());

            //Digital Crossing Controller
            GameRegistry.registerTileEntity(TileDigitalCrossingController.class, "DigitalCrossingController");
            digitalCrossingController = new BlockDigitalCrossingController("DigitalCrossingController");
            GameRegistry.registerBlock(digitalCrossingController, ItemBlockDigitalCrossingController.class, digitalCrossingController.getUnlocalizedName());
        }
    }

    public static BlockBundledController getBundledController() {
        return bundledController;
    }

    public static BlockBundledUniversalController getBundledUniversalController() {
        return bundledUniversalController;
    }

    public static BlockBundledAdvancedController getBundledAdvancedController() {
        return bundledAdvancedController;
    }

    public static BlockBundledReceiver getBundledReceiver() {
        return bundledReceiver;
    }

    public static BlockDigitalController getDigitalController() {
        return digitalController;
    }

    public static BlockDigitalUniversalController getDigitalUniversalController() {
        return digitalUniversalController;
    }

    public static BlockDigitalCrossingController getDigitalCrossingController() {
        return digitalCrossingController;
    }
}
