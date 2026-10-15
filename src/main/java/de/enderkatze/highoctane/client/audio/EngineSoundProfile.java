package de.enderkatze.highoctane.client.audio;

import net.minecraft.sounds.SoundEvent;

public record EngineSoundProfile(
        SoundEvent startup,
        SoundEvent idle,
        SoundEvent low_off,
        SoundEvent low_on,
        SoundEvent med_off,
        SoundEvent med_on,
        SoundEvent high_off,
        SoundEvent high_on,
        SoundEvent max
) {
}
