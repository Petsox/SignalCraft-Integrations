package SCIntegrations.projred.bundledAdvController;

import SCIntegrations.SCIntegrations;
import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;

public class ItemBlockBundledAdvancedController extends SCItemBlock
{
    public ItemBlockBundledAdvancedController(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(SCIntegrations.tabIntegrations);
    }
}
