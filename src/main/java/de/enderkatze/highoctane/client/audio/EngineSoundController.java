package de.enderkatze.highoctane.client.audio;

import com.jcraft.jorbis.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;

public class EngineSoundController {

    public final EngineSoundProfile profile;
    public final float maxRPM;
    public final float idleRPM;
    public final float idleTolerance;
    public final BlockPos pos;

    private EngineSoundLayer idle;
    private EngineSoundLayer low;
    private EngineSoundLayer med;
    private EngineSoundLayer high;
    private EngineSoundLayer max;

    private EngineSoundLayer[] layers;

    private boolean initialised = false;



    public EngineSoundController(EngineSoundProfile profile, float maxRPM, float idleRPM, BlockPos pos) {
        this.profile = profile;
        this.maxRPM = maxRPM;
        this.idleRPM = idleRPM;
        this.pos = pos;
        idleTolerance = Math.min(4f, maxRPM / 15f);

    }

    public void startEngine(Level level) {
        level.playSound(null, pos, profile.startup(),
                SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void tick(boolean throttle, float rpm, Level level) {

        if(!initialised) {
            idle = new EngineSoundLayer(0.0f, 0.15f, profile.idle(), profile.idle(), pos, level, 0.0f);
            low = new EngineSoundLayer(0.06f, 0.4f, profile.low_off(), profile.low_on(), pos, level, 0.2f);
            med = new EngineSoundLayer(0.30f, 0.7f, profile.med_off(), profile.med_on(), pos, level, 0.5f);
            high = new EngineSoundLayer(0.6f, 0.97f, profile.high_off(), profile.high_on(), pos, level, 0.8f);
            max = new EngineSoundLayer(0.95f, 1.0f, profile.max(), profile.max(), pos, level, 1.0f);



            layers = new EngineSoundLayer[]{
                    idle, low, med, high, max
            };

            initialised = true;
        }

        float rpmNormal = rpm / maxRPM;

        for(EngineSoundLayer layer : layers) {
            layer.tick(rpmNormal, throttle);
        }



    }
}
