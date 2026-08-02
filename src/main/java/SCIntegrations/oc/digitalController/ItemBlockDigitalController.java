package SCIntegrations.oc.digitalController;

import SCIntegrations.SCIntegrations;
import net.minecraft.block.Block;
import signalcraft.ItemBlocks.SCItemBlock;

public class ItemBlockDigitalController extends SCItemBlock
{
    public ItemBlockDigitalController(Block block) {
        super(block);
        setHasSubtypes(false);
        setCreativeTab(SCIntegrations.tabIntegrations);
    }
}
