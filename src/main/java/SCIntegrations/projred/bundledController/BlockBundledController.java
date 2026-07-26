package SCIntegrations.projred.bundledController;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import signalcraft.SignalCraft;
import signalcraft.blocks.controllers.BlockController;
import signalcraft.entities.controllers.TileController;
import signalcraft.packet.SPacketEditorOpen;

import java.util.LinkedList;
import java.util.List;

public class BlockBundledController extends BlockController {
    public BlockBundledController(String name) {
        super(name);
        this.setCreativeTab(SCIntegrations.tabIntegrations);
    }
    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block theBlock) {
        final TileEntity tileE = world.getTileEntity(x, y, z);
        if (tileE instanceof TileBundledController & !world.isRemote) {
            ((TileBundledController) tileE).onNeighborUpdate();
        }
    }

    @Override
    public boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tileE = world.getTileEntity(x, y, z);
        if (!(tileE instanceof TileController) || ((TileController) tileE).getGuiId() == null) {
            FMLLog.severe("SCIntegrations: BlockBundledController.openGui found no valid TileBundledController (with a set GuiID) at %d, %d, %d - tile was: %s",
                    x, y, z, tileE);
            return true;
        }
        final SPacketEditorOpen thePacket = new SPacketEditorOpen((TileController) tileE);
        try {
            final List<Object> list = new LinkedList<>();
            SignalCraft.proxy.packetPipeline.encode(thePacket, list);
            final FMLProxyPacket pkt = (FMLProxyPacket) list.get(0);
            SignalCraft.proxy.packetPipeline.sendTo(pkt, (EntityPlayerMP)player);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileBundledController();
    }
}
