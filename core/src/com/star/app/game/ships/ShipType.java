package com.star.app.game.ships;

import java.util.HashMap;
import java.util.Map;

public enum  ShipType {
    CLASSIC(90f,
            240f,
            120f,
            120f,
            60f,
            0.5f,
            120f,
            0.1f,
            600f);

    public final float ROTATE_SPEED;
    public final float FORWARD_MAX_VELOCITY;
    public final float BACKWARD_MAX_VELOCITY;
    public final float FORWARD_POWER;
    public final float BACKWARD_POWER;
    public final float BOUND_BREAK_FACTOR;
    public final float FRICTION_BREAK;
    public final float SHOOT_DELAY_MIN;
    public final float SHOT_VELOCITY;

    ShipType(float ROTATE_SPEED, float FORWARD_MAX_VELOCITY, float BACKWARD_MAX_VELOCITY, float FORWARD_POWER, float BACKWARD_POWER, float BOUND_BREAK_FACTOR, float FRICTION_BREAK, float SHOOT_DELAY_MIN, float SHOT_VELOCITY) {
        this.ROTATE_SPEED = ROTATE_SPEED;
        this.FORWARD_MAX_VELOCITY = FORWARD_MAX_VELOCITY;
        this.BACKWARD_MAX_VELOCITY = BACKWARD_MAX_VELOCITY;
        this.FORWARD_POWER = FORWARD_POWER;
        this.BACKWARD_POWER = BACKWARD_POWER;
        this.BOUND_BREAK_FACTOR = BOUND_BREAK_FACTOR;
        this.FRICTION_BREAK = FRICTION_BREAK;
        this.SHOOT_DELAY_MIN = SHOOT_DELAY_MIN;
        this.SHOT_VELOCITY = SHOT_VELOCITY;
    }
}
