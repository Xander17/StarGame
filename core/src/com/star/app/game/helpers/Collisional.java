package com.star.app.game.helpers;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public interface Collisional {
    void destroy();

    boolean takeDamage(float amount);

    boolean takeImpulseDamage(float power, float angle, float amount);

    Circle getHitBox();

    Vector2 getVelocity();

    Vector2 getPosition();

    float getMassFactor();

}
