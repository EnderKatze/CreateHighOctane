package de.enderkatze.highoctane.content.transmission;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import de.enderkatze.highoctane.HighOctaneBlockEntityTypes;
import de.enderkatze.highoctane.content.vsix_engine.CarEngineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransmissionBlock extends HorizontalKineticBlock implements IBE<TransmissionBlockEntity> {

    public TransmissionBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<TransmissionBlockEntity> getBlockEntityClass() {
        return TransmissionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransmissionBlockEntity> getBlockEntityType() {
        return HighOctaneBlockEntityTypes.TRANSMISSION.get();
    }

    public static final VoxelShape SHAPE_NORTH_SOUTH = Shapes.or(
            CarEngineBlock.box(1.0, 0.0, 0.0, 15.0, 2.0, 16.0),
            CarEngineBlock.box(3.0, 2.0, 0.0, 13.0, 14.0, 16.0)
    );

    public static final VoxelShape SHAPE_EAST_WEST = Shapes.or(
            CarEngineBlock.box(0.0, 0.0, 1.0, 16.0, 2.0, 15.0),
            CarEngineBlock.box(0.0, 2.0, 3.0, 16.0, 14.0, 13.0)
    );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(HORIZONTAL_FACING);

        if (facing == Direction.EAST || facing == Direction.WEST) {
            return SHAPE_EAST_WEST;
        }
        return SHAPE_NORTH_SOUTH;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.SOUTH;
    }


}
