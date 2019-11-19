package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

import java.util.List;

public class BulletController extends ObjectPool<Bullet> {
    Texture texture;
    GameController gameController;

    @Override
    public Bullet getNew() {
        return new Bullet(texture);
    }

    BulletController(GameController gameController) {
        texture = new Texture("bullets/bullet.png");
        this.gameController = gameController;
    }

    public void createNew(float x, float y,float angle, float velocityX, float velocityY) {
        getActive().activate(x, y,angle, velocityX, velocityY);
    }

    void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            Bullet b = activeList.get(i);
            b.update(dt);
            List<Asteroid> list = gameController.getAsteroidController().getActiveList();
            for (int j = 0; j < list.size(); j++) {
                if (b.checkHit(list.get(j))) {
                    list.get(j).destroy();
                    b.deactivate();
                    break;
                }
            }
        }
        checkFreeObjects();
    }

    void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
