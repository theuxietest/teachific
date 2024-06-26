package com.so.luotk.listeners;

public class MyBounceInterpolator implements android.view.animation.Interpolator {
    private double amplitude = 1;
    private double frequency = 10;

    public MyBounceInterpolator(double amp, double freq) {
        amplitude = amp;
        frequency = freq;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ amplitude) * Math.cos(frequency * time) + 1);
    }
}