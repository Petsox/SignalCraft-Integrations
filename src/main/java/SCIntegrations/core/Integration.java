package SCIntegrations.core;

import SCIntegrations.projred.bundledController.BlockBundledController;
import SCIntegrations.projred.bundledController.ItemBlockBundledController;
import SCIntegrations.projred.bundledController.TileBundledController;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import signalcraft.renderers.entities.controllers.TileControllerRenderer;
import signalcraft.renderers.items.controllers.ControllerItemRenderer;

public class Integration {



    public static void registerIntegrations() {

        if (Mods.isLoaded(Mods.ModsEnum.ProjectRed)){
            GameRegistry.registerTileEntity(TileBundledController.class, "BundledControllerInt");
            ClientRegistry.bindTileEntitySpecialRenderer(TileBundledController.class, new TileControllerRenderer(new TileBundledController()));

            BlockBundledController bundledController = new BlockBundledController("BundledControllerInt");
            GameRegistry.registerBlock(bundledController, ItemBlockBundledController.class, bundledController.getUnlocalizedName());
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(bundledController), new ControllerItemRenderer(new TileBundledController()));
        }
    }
}
