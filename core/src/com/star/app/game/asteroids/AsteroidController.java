package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class AsteroidController extends ObjectPool<Asteroid> {
    @Override
    public Asteroid getNew() {
        return new Asteroid();
    }

    public void createNew(float x, float y, float scale, float velocityX, float velocityY) {
        getActive().activate(x, y, scale, velocityX, velocityY);
    }

    public void createNew() {
        getActive().activate();
    }

    void update(float dt) {
        if (activeList.size()==0) createNew();
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid b = activeList.get(i);
            b.update(dt);
        }
        checkFreeObjects();
    }

    void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
