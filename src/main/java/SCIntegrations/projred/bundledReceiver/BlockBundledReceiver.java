package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.SCIntegrations;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import signalcraft.SignalCraft;
import signalcraft.blocks.controllers.BlockReceiver;
import signalcraft.entities.controllers.TileReceiver;
import signalcraft.packet.SPacketEditorOpen;

import java.util.LinkedList;
import java.util.List;

public class BlockBundledReceiver extends BlockReceiver {
    public BlockBundledReceiver(String name) {
        super(name);
        this.setCreativeTab(SCIntegrations.tabIntegrations);
    }
    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block theBlock) {
        final TileEntity tileE = world.getTileEntity(x, y, z);
        if (tileE instanceof TileBundledReceiver & !world.isRemote) {
            ((TileBundledReceiver) tileE).onNeighborUpdate();
        }
    }

    @Override
    public boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            return true;
        }
        final TileEntity tileE = world.getTileEntity(x, y, z);
        if (!(tileE instanceof TileReceiver) || ((TileReceiver) tileE).getGuiId() == null) {
            FMLLog.severe("SCIntegrations: BlockBundledReceiver.openGui found no valid TileBundledReceiver (with a set GuiID) at %d, %d, %d - tile was: %s",
                    x, y, z, tileE);
            return true;
        }
        final SPacketEditorOpen thePacket = new SPacketEditorOpen((TileReceiver) tileE);
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
        return new TileBundledReceiver();
    }
}
