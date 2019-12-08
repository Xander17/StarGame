package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;

public class BulletController extends ObjectPool<Bullet> {
    private final float BULLET_MAX_DISTANCE = 1000;
    private GameController gameController;

    public BulletController(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public Bullet getNew() {
        return new Bullet();
    }

    public void createNew(float x, float y, float angle, float velocityX, float velocityY, float damage,boolean playerOwner) {
        getActive().activate(x, y, angle, velocityX, velocityY, damage, BULLET_MAX_DISTANCE,playerOwner);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt, gameController);
        }
        checkFreeObjects();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
