package de.enderkatze.highoctane.content.vsix_engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import de.enderkatze.highoctane.HighOctanePartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;

public class CarEngineRenderer extends ShaftRenderer<CarEngineBlockEntity> {
    public CarEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    ArrayList<PartialModel> cylinders = new ArrayList<>(
            Arrays.asList(
                    HighOctanePartialModels.V6_CYLINDERS_1,
                    HighOctanePartialModels.V6_CYLINDERS_2,
                    HighOctanePartialModels.V6_CYLINDERS_3,
                    HighOctanePartialModels.V6_CYLINDERS_4,
                    HighOctanePartialModels.V6_CYLINDERS_5,
                    HighOctanePartialModels.V6_CYLINDERS_6)
    );

    ArrayList<PartialModel> bays = new ArrayList<>(
            Arrays.asList(
                    HighOctanePartialModels.V6_BAY_1,
                    HighOctanePartialModels.V6_BAY_2)
    );

    float[] cylinderOffsets = {
            0.0f,
            (float) Math.toRadians(60f),
            (float) Math.toRadians(120f),
            (float) Math.toRadians(210f), // 180f would cancel out bay wobble
            (float) Math.toRadians(240f),
            (float) Math.toRadians(300f)
    };

    HashMap<CarEngineBlockEntity, Float> blockEntityMap = new HashMap<>();

    @Override
    public void renderSafe(
            CarEngineBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay) {
        float animationPhase = blockEntityMap.getOrDefault(be, 0f);
        animationPhase+=be.getSpeed()/700.0f;
        animationPhase %= (float)(Math.PI * 2);
        blockEntityMap.put(be, animationPhase);

        int angle = (int) (Math.abs(KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), KineticBlockEntityRenderer.getRotationAxisOf(be))*180/Math.PI) * 3 % 360)/36;

        for (int i = 0; i < 2; i++) {
            float swell = (float) (((1.0f + Math.sin(animationPhase)) / 2)*0.02f);
            CachedBuffers.partial(bays.get(i), be.getBlockState())
                    .center()
                    .scale(1+swell)
                    .rotateYDegrees(be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot())
                    .uncenter()
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }


        for (int i = 0; i < 6; i++) {
            float swellOffset = (float) (((1.0f + Math.sin(animationPhase+cylinderOffsets[i])) / 2)*0.07f);
            float swell = (float) (((1.0f + Math.sin(animationPhase)) / 2)*0.02f);
            CachedBuffers.partial(cylinders.get(i), be.getBlockState())
                    .center()
                    .scale(1+(swellOffset/3)+swell)
                    .rotateYDegrees(be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot())
                    .uncenter()
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }


        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
    }
}
