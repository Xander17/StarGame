package com.star.app.game.helpers;

import com.badlogic.gdx.math.Vector2;

public interface Destroyable {
    void destroy();

    Vector2 getPosition();

    float getHitBoxRadius();
}
