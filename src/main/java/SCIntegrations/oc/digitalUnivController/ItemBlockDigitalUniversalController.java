package SCIntegrations.oc.digitalUnivController;

import SCIntegrations.SCIntegrations;
import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;

public class ItemBlockDigitalUniversalController extends SCItemBlock
{
    public ItemBlockDigitalUniversalController(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(SCIntegrations.tabIntegrations);
    }
}
