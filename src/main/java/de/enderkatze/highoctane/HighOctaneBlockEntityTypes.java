package de.enderkatze.highoctane;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import de.enderkatze.highoctane.content.vsix_engine.CarEngineBlockEntity;
import de.enderkatze.highoctane.content.vsix_engine.CarEngineRenderer;

public class HighOctaneBlockEntityTypes {

    private static final CreateRegistrate REGISTRATE = HighOctane.REGISTRATE;

    public static final BlockEntityEntry<CarEngineBlockEntity> CAR_ENGINE = REGISTRATE.blockEntity("car_engine", CarEngineBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .validBlock(HighOctaneBlocks.CAR_ENGINE_BLOCK)
            .renderer(() -> CarEngineRenderer::new)
            .register();

    public static void register() {

    }
}
