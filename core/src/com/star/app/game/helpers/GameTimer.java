package com.star.app.game.helpers;

public class GameTimer {
    private float max;
    private float current;

    public GameTimer(float max) {
        this.max = max;
        current = 0;
    }

    public void update(float dt) {
        if (current < max) current += dt;
    }

    public boolean isReady() {
        return current >= max;
    }

    public void reset() {
        current = 0;
    }

    public void reset(float max) {
        current = 0;
        this.max = max;
    }

    public float percent() {
        return current / max;
    }

    public void disable() {
        current = max;
    }
}
