package de.enderkatze.highoctane;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.checkerframework.checker.nullness.qual.NonNull;

import static de.enderkatze.highoctane.HighOctane.REGISTRATE;

public class HighOctaneFluids {

    public static final FluidEntry<BaseFlowingFluid.Flowing> HIGH_OCTANE_GAS = REGISTRATE.fluid("high_octane_gasoline",
            HighOctane.rl("block/high_octane_gas_still"),
            HighOctane.rl("block/high_octane_gas_flow"))
            .properties(b -> b.viscosity(1500).density(500))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .source(BaseFlowingFluid.Source::new)
            .block()
            .lang("High-Octane Gasoline")
            .build()
            .bucket()
            .onRegister(HighOctaneFluids::registerFluidDispenseBehavior)
            .lang("High-Octane Gasoline Bucket")
            .build()
            .lang("High-Octane Gasoline")
            .register();

    public static void register() {}
    // from Create

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior(){
        @Override
        protected @NonNull ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.pos().relative(pSource.state().getValue(DispenserBlock.FACING));
            Level level = pSource.level();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null, pStack)) {
                return new ItemStack(Items.BUCKET);
            }
            return DEFAULT.dispense(pSource, pStack);
        }
    };

    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
    }
}
