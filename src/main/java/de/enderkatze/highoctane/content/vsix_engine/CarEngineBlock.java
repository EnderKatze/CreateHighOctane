package de.enderkatze.highoctane.content.vsix_engine;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import de.enderkatze.highoctane.HighOctaneBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class CarEngineBlock extends HorizontalKineticBlock implements IBE<CarEngineBlockEntity> {
    public CarEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, net.minecraft.core.Direction side) {
        return false;
    }

    @Override
    public Class<CarEngineBlockEntity> getBlockEntityClass() {
        return CarEngineBlockEntity.class;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hit) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        CarEngineBlockEntity be = getBlockEntity(level, pos);

        if (be == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        IFluidHandler tank = level.getCapability(

                Capabilities.FluidHandler.BLOCK,

                pos,

                null

        );

        if (tank == null)

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        FluidStack before = be.tank.getCapability().getFluidInTank(0).copy();

        boolean changed = false;
        SoundEvent sound = null;


        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be)) {

            changed = true;
            sound = FluidHelper.getEmptySound(be.tank.getCapability().getFluidInTank(0));
        }

        else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
            changed = true;
            sound = FluidHelper.getFillSound(be.tank.getCapability().getFluidInTank(0));
        }

        if (!changed) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack)) {
                return ItemInteractionResult.SUCCESS;
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        }

        if (sound != null && !level.isClientSide) {
            FluidStack fluid = be.tank.getCapability().getFluidInTank(0);
            float pitch = Mth.clamp(

                    1 - (fluid.getAmount() / (float) CarEngineBlockEntity.tank_capacity),

                    0.5f, 1f

            );
            pitch += (level.random.nextFloat() - 0.5f) * 0.1f;
            level.playSound(
                    null,
                    pos,
                    sound,
                    SoundSource.BLOCKS,
                    0.5f,
                    pitch
            );

        }


        FluidStack after = be.tank.getCapability().getFluidInTank(0);

        if (!FluidStack.isSameFluidSameComponents(before, after)) {

            be.setChanged();

            be.getLevel().sendBlockUpdated(pos, state, state, 3);

        }

        return ItemInteractionResult.SUCCESS;

    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        if (level.isClientSide) return;
        int signal = level.getBestNeighborSignal(pos);
        withBlockEntityDo(level, pos, be -> {

            be.setThrottleSignal(signal);

            be.setThrottleChanged(true);

        });

    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {

        return face.getAxis() == getRotationAxis(state);

    }

    @Override
    public BlockEntityType<? extends CarEngineBlockEntity> getBlockEntityType() {
        return HighOctaneBlockEntityTypes.CAR_ENGINE.get();
    }

    public static final VoxelShape Base = CarEngineBlock.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

    public static final VoxelShape SHAPE_NORTH_SOUTH = Shapes.or(
            Base,
            CarEngineBlock.box(1.0, 4.0, 0.0, 15.0, 14.0, 16.0)
    );

    public static final VoxelShape SHAPE_EAST_WEST = Shapes.or(
            Base,
            CarEngineBlock.box(0.0, 4.0, 1.0, 16.0, 14.0, 15.0)
    );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(HORIZONTAL_FACING);

        if (facing == Direction.EAST || facing == Direction.WEST) {
            return SHAPE_EAST_WEST;
        }
        return SHAPE_NORTH_SOUTH;
    }
}
