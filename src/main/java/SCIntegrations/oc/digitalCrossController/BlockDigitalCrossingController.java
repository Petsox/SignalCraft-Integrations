package SCIntegrations.oc.digitalCrossController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import signalcraft.blocks.controllers.BlockController;
import signalcraft.entities.controllers.TileController;

public class BlockDigitalCrossingController extends BlockController {
    public BlockDigitalCrossingController(String name) {
        super(name);
        this.setCreativeTab(SCIntegrations.tabIntegrations);
    }



    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileDigitalCrossingController();
    }

    @Override
    public boolean openGui(World world, int i, int i1, int i2, EntityPlayer entityPlayer) {
        return false;
    }
}
