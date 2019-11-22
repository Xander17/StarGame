package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class BulletController extends ObjectPool<Bullet> {
    private final int BASE_DAMAGE=1;

    TextureRegion texture;
    GameController gameController;

    @Override
    public Bullet getNew() {
        return new Bullet(texture);
    }

    public BulletController(GameController gameController) {
        texture = Assets.getInstance().getTextureAtlas().findRegion("bullet");
        this.gameController = gameController;
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

    public int getBASE_DAMAGE() {
        return BASE_DAMAGE;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }
}
