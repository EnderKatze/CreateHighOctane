package de.enderkatze.highoctane.content.car_engine;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import de.enderkatze.highoctane.AccelerationCalculator;
import de.enderkatze.highoctane.HighOctaneBlockEntityTypes;
import de.enderkatze.highoctane.HighOctaneSounds;
import de.enderkatze.highoctane.client.audio.EngineSoundController;
import de.enderkatze.highoctane.client.audio.EngineSoundProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CarEngineBlockEntity extends GeneratingKineticBlockEntity implements MenuProvider {

    SmartFluidTankBehaviour tank;
    private int throttleSignal = 0;
    private boolean throttleChanged = false;
    private boolean running = false;
    public static float idle_rpm = 16f;
    public static int tank_capacity = 4000;
    private int startupTicks = 21;
    private int startupProgress = 0;
    private boolean initateStartup = false;
    private int maxRPM = 255;

    public float currentRPM = 0f;
    private int ticksSinceUpdate = 0;
    private int updateRate = 10;
    private int throttleLock = 0;
    private boolean firstTick = true;

    private int soundUpdate = 0;

    private final EngineSoundProfile soundProfile;
    private final EngineSoundController soundController;

    public static final List<TagKey<Fluid>> acceptedFluids = new ArrayList<>(
            Arrays.asList(
                    TagKey.create(
                            Registries.FLUID,
                            ResourceLocation.fromNamespaceAndPath("c", "diesel")
                    ),
                    TagKey.create(
                            Registries.FLUID,
                            ResourceLocation.fromNamespaceAndPath("c", "gasoline")
                    ),
                    TagKey.create(
                            Registries.FLUID,
                            ResourceLocation.fromNamespaceAndPath("c", "high_octane_gasoline")
                    )
            )
    );

    public CarEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);

        soundProfile = new EngineSoundProfile(
                HighOctaneSounds.I6_STARTUP.get(),
                HighOctaneSounds.I6_IDLE.get(),
                HighOctaneSounds.I6_LOW_OFF.get(),
                HighOctaneSounds.I6_LOW_ON.get(),
                HighOctaneSounds.I6_MED_OFF.get(),
                HighOctaneSounds.I6_MED_ON.get(),
                HighOctaneSounds.I6_HIGH_OFF.get(),
                HighOctaneSounds.I6_HIGH_ON.get(),
                HighOctaneSounds.I6_MAX.get()
        );

        soundController = new EngineSoundController(soundProfile, maxRPM, idle_rpm, pos);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, tank_capacity);

        tank.getPrimaryHandler().setValidator(fluidStack ->
                acceptedFluids.stream().anyMatch(tag ->
                        fluidStack.getFluid().is(tag)
                )
        );
        behaviours.add(tank);

    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(

                Capabilities.FluidHandler.BLOCK,
                HighOctaneBlockEntityTypes.CAR_ENGINE.get(),

                (be, side) -> {
                    if (side == null)
                        return be.tank.getCapability();
                    if (side == Direction.DOWN)
                        return be.tank.getCapability();
                    return null;
                }
        );
    }

    @Override
    public void tick() {
        if (firstTick) {
            firstTick = false;
            currentRPM = 0;
            running = false;
            updateGeneratedRotation();
            throttleSignal = level.getBestNeighborSignal(getBlockPos());
        }

        if ((!running && throttleSignal > 0) || initateStartup) {
            initateStartup = true;
            if (startupProgress < startupTicks) {
                if (startupProgress <= 0) {
                    soundController.startEngine(level);
                }
                startupProgress++;
            } else {
                running = true;
                currentRPM = idle_rpm;
                updateGeneratedRotation();
                initateStartup = false;
                throttleLock = 5;
            }
        } else if (running) {

            int tempThrottle = throttleSignal;

            if (throttleLock > 0) {
                tempThrottle = 0;
                throttleLock--;
            }

            soundController.tick(throttleSignal != 0, currentRPM, level);

            if (!level.isClientSide) {
                float acceleration = AccelerationCalculator.calculateSpeedChange(
                        currentRPM,
                        idle_rpm,
                        maxRPM,
                        tempThrottle,
                        0.35f,
                        0.55f,
                        2.5f,
                        1.0f,
                        0.5f,
                        1.0f
                );
                float oldRPM = currentRPM;

                currentRPM += acceleration;

                currentRPM = Math.clamp(currentRPM, 0, maxRPM);


                ticksSinceUpdate++;
                if (ticksSinceUpdate >= updateRate) {
                    updateGeneratedRotation();
                    ticksSinceUpdate = 0;
                }
            }

        } else if (throttleSignal <= 0 && !running) {
            startupProgress = 0;
        }
    }

    public void setThrottleSignal(int throttleSignal) {
        this.throttleSignal = throttleSignal;
    }

    public void setThrottleChanged(boolean throttleChanged) {
        this.throttleChanged = throttleChanged;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    @Override
    public float getGeneratedSpeed() {


        return convertToDirection(Math.abs(currentRPM), getBlockState().getValue(HORIZONTAL_FACING));
    }


    @Override
    public float calculateAddedStressCapacity() {
        return 64f;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getGeneratedSpeed() != 0)
            super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        containedFluidTooltip(tooltip, isPlayerSneaking, tank.getCapability());
        return true;
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("CurrentRPM", currentRPM);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
