package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class BulletController extends ObjectPool<Bullet> {

    @Override
    public Bullet getNew() {
        return new Bullet();
    }

    public void createNew(float x, float y, float angle, float velocityX, float velocityY) {
        getActive().activate(x, y, angle, velocityX, velocityY);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkFreeObjects();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
