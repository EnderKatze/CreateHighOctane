package de.enderkatze.highoctane;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HighOctaneSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, HighOctane.MODID);


    private static Supplier<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(HighOctane.MODID, name)));
    }

    public static final Supplier<SoundEvent> I6_IDLE     = register("i6_idle");
    public static final Supplier<SoundEvent> I6_STARTUP  = register("i6_startup");
    public static final Supplier<SoundEvent> I6_LOW_OFF  = register("i6_low_off");
    public static final Supplier<SoundEvent> I6_LOW_ON   = register("i6_low_on");
    public static final Supplier<SoundEvent> I6_MED_OFF  = register("i6_med_off");
    public static final Supplier<SoundEvent> I6_MED_ON   = register("i6_med_on");
    public static final Supplier<SoundEvent> I6_HIGH_OFF = register("i6_high_off");
    public static final Supplier<SoundEvent> I6_HIGH_ON  = register("i6_high_on");
    public static final Supplier<SoundEvent> I6_MAX      = register("i6_max");
}

