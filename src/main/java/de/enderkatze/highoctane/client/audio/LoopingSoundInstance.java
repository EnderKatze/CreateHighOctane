package de.enderkatze.highoctane.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class LoopingSoundInstance extends AbstractTickableSoundInstance {

    private final Level level;
    private final BlockPos pos;

    LoopingSoundInstance(BlockPos pos, SoundEvent sound, Level level) {
        super(sound, SoundSource.BLOCKS, RandomSource.create());
        this.pos = pos;

        this.looping = true;
        this.volume = 1.0f;

        this.level = level;

        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public void tick() {
        if (!level.isLoaded(pos) || level.getBlockState(pos).isAir()) {
            stop();
        }
    }
}
