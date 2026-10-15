package de.enderkatze.highoctane;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import de.enderkatze.highoctane.content.vsix_engine.CarEngineBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;


public class HighOctaneBlocks {

    private static final CreateRegistrate REGISTRATE = HighOctane.REGISTRATE;

    public static final BlockEntry<CarEngineBlock> CAR_ENGINE_BLOCK =
            REGISTRATE.block("v6_engine", CarEngineBlock::new)
                    .initialProperties(() -> Blocks.IRON_BLOCK)
                    .blockstate((c, p) ->
                            p.getVariantBuilder(c.getEntry())
                                    .forAllStates(bs ->
                                            ConfiguredModel.builder()
                                                    .modelFile(p.models().getExistingFile(
                                                            p.modLoc("block/v6_engine/v6_engine")
                                                    ))
                                                    .rotationY(
                                                            bs.getValue(CarEngineBlock.HORIZONTAL_FACING).getAxis() == Direction.Axis.X
                                                                    ? 90
                                                                    : 0
                                                    )
                                                    .build()

                                    )

                    )
                    .item()
                    .model((c, p) -> p.blockItem(c, "/item"))
                    .build()
                    .register();


    public static void register() {

    }
}
