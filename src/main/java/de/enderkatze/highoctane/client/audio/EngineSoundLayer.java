package de.enderkatze.highoctane.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public class EngineSoundLayer {

    private final float start;
    private final float end;
    private final SoundEvent off;
    private final SoundEvent on;
    private final BlockPos pos;
    private final Level level;
    private final float peakShift;

    private LoopingSoundInstance loopOff;
    private LoopingSoundInstance loopOn;

    private boolean initialised = false;

    public EngineSoundLayer(float start, float end, SoundEvent off, SoundEvent on, BlockPos pos, Level level, float peakShift) {
        this.start = start;
        this.end = end;
        this.off = off;
        this.on = on;
        this.pos = pos;
        this.level = level;
        this.peakShift = peakShift;
    }

    public void tick(float normalRPM, boolean throttle) {



        float newVolume = (float) (-(Math.pow(((normalRPM-peakShift)/(0.35f-(peakShift/7))), 2)) + (0.5f+(peakShift/2)));
        newVolume = Math.max(newVolume, 0.0f);
        System.out.println(

                "Layer: PEAK " + peakShift +
                        " | RPM: " + normalRPM +
                        " | initialised: " + initialised +
                        " | Volume: " + newVolume
        );


        if(!initialised && newVolume > 0) { lazyInit();}


        if(initialised){
            if (throttle) {
                loopOff.setVolume(0f);
                loopOn.setVolume(newVolume);
            } else {
                loopOn.setVolume(0f);
                loopOff.setVolume(newVolume);
            }
        }
    }

    private void lazyInit() {

        loopOff = new LoopingSoundInstance(pos, off, level);
        loopOn = new LoopingSoundInstance(pos, on, level);

        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().getSoundManager().play(loopOff);
            Minecraft.getInstance().getSoundManager().play(loopOn);
            loopOff.setVolume(0f);
            loopOn.setVolume(0f);
            initialised=true;
        });
    }
}
