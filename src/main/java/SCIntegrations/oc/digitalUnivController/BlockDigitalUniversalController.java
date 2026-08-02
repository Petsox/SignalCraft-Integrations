package SCIntegrations.oc.digitalUnivController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import signalcraft.blocks.controllers.BlockController;
import signalcraft.entities.controllers.TileController;

public class BlockDigitalUniversalController extends BlockController {
    public BlockDigitalUniversalController(String name) {
        super(name);
        this.setCreativeTab(SCIntegrations.tabIntegrations);
    }

    @Override
    public boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tileE = world.getTileEntity(x, y, z);
        if (!(tileE instanceof TileController)) {
            FMLLog.severe("SCIntegrations: BlockDigitalUniversalController.openGui found no valid TileDigitalUniversalController at %d, %d, %d - tile was: %s",
                    x, y, z, tileE);
            return true;
        }
        ((TileController) tileE).listReceivers(player);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileDigitalUniversalController();
    }
}
