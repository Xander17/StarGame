package com.star.app.game.mines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class MineController extends ObjectPool<Mine> {
    private final float BASE_DAMAGE = 3f;
    private final float BASE_BLAST_POWER = 200f;
    private final float BASE_BLAST_RADIUS = 300f;

    GameController gameController;
    private TextureRegion[][] texture;

    public MineController(GameController gameController) {
        this.gameController = gameController;
        texture = Assets.getInstance().getTextureAtlas().findRegion("mine").split(32, 32);
    }

    @Override
    public Mine getNew() {
        return new Mine(gameController, texture);
    }

    public void createNew(float x, float y) {
        getActive().activate(x, y, BASE_DAMAGE, BASE_BLAST_POWER, BASE_BLAST_RADIUS);
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
