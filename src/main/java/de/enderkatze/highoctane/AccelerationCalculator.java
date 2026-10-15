package de.enderkatze.highoctane;

public class AccelerationCalculator {

    public static float calculateSpeedChange(
            float currentRPM,
            float idleRPM,
            float maxRPM,
            int analogue_throttle,
            float width,
            float peak,
            float enginePower,
            float engineBrake,
            float engineDrag,
            float gearRatio) {

        if(analogue_throttle>0) {
            float delta = (currentRPM - idleRPM) / (maxRPM - idleRPM);

            delta = Math.clamp(delta, 0f, 1f);
            float x = (delta-peak) / width;

            float drag = (currentRPM/maxRPM)*(currentRPM/maxRPM) * engineDrag * gearRatio;

            float throttle = analogue_throttle/15f;

            return applyFlywheel((float) (Math.exp(-x * x) * (throttle) * enginePower) - drag, 1.5f);
        }

        float engineBrakeTemp = engineBrake * gearRatio;
        float normalizedRPM = currentRPM / maxRPM;

        float brake = engineBrakeTemp
                * (0.5f + normalizedRPM);

        if (currentRPM <= idleRPM) {
            return 0.0f;
        }

        return -brake-engineDrag;

    }

    private static float applyFlywheel(float acceleration, float inertia) {
        return acceleration / inertia;
    }
}
