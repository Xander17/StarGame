package com.star.app.game.helpers;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public interface Destroyable {
    void destroy();

    boolean takeDamage(int amount);

    Circle getHitBox();
}
