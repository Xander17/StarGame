package com.star.app.game.pilots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;

public class EnemyController extends ObjectPool<Enemy> {
    GameController gameController;

    public EnemyController(GameController gameController) {
        this.gameController = gameController;
    }

    public void createNew() {
        getActive().activate();
    }

    @Override
    public Enemy getNew() {
        return new Enemy(gameController);
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
