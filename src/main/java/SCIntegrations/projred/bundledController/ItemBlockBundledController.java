package SCIntegrations.projred.bundledController;

import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;
import signalcraft.integration.Integration;

public class ItemBlockBundledController extends SCItemBlock
{
    public ItemBlockBundledController(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(Integration.tabIntegrations);
    }
}
