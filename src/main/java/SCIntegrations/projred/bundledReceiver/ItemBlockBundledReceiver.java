package SCIntegrations.projred.bundledReceiver;

import SCIntegrations.SCIntegrations;
import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;

public class ItemBlockBundledReceiver extends SCItemBlock
{
    public ItemBlockBundledReceiver(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(SCIntegrations.tabIntegrations);
    }
}
