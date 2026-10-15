package de.enderkatze.highoctane.content.transmission;

import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TransmissionRenderer extends ShaftRenderer<TransmissionBlockEntity> {
    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}
