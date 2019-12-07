package com.star.app.game.asteroids;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class AsteroidController extends ObjectPool<Asteroid> {

    @Override
    public Asteroid getNew() {
        return new Asteroid(this);
    }

    public void createNew(float x, float y, float scale, float velocityX, float velocityY, int health) {
        getActive().activate(x, y, scale, velocityX, velocityY, health);
    }

    public void createNew(float x, float y, float scale, int health) {
        getActive().activate(x, y, scale, health);
    }

    public void createNew() {
        getActive().activate();
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid b = activeList.get(i);
            b.update(dt);
        }
        checkFreeObjects();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
