package SCIntegrations.projred.bundledUnivController;

import SCIntegrations.SCIntegrations;
import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;

public class ItemBlockBundledUniversalController extends SCItemBlock
{
    public ItemBlockBundledUniversalController(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(SCIntegrations.tabIntegrations);
    }
}
