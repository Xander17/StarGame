package com.star.app.game.drops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class DropController extends ObjectPool<Drop> {
    private final float HEAL_BASE_AMOUNT_MIN = 5f;
    private final float HEAL_BASE_AMOUNT_MAX = 10f;
    private final float HEAL_LEVEL_FACTOR = 0.5f;
    private final int AMMO_BASE_AMOUNT_MIN = 50;
    private final int AMMO_BASE_AMOUNT_MAX = 200;
    private final float AMMO_LEVEL_FACTOR = 0.1f;
    private final int CASH_BASE_AMOUNT_MIN = 10;
    private final int CASH_BASE_AMOUNT_MAX = 30;
    private final float CASH_LEVEL_FACTOR = 1f;

    private GameController gameController;

    private TextureRegion[] textures;

    public DropController(GameController gameController) {
        this.gameController = gameController;
        textures = new TextureRegion[]{
                Assets.getInstance().getTextureAtlas().findRegion("healdrop"),
                Assets.getInstance().getTextureAtlas().findRegion("ammodrop"),
                Assets.getInstance().getTextureAtlas().findRegion("cashdrop"),
                Assets.getInstance().getTextureAtlas().findRegion("minesdrop")
        };
    }

    @Override
    public Drop getNew() {
        return new Drop(gameController, this);
    }

    public void getRandom(Vector2 position, float chance) {
        if (Math.random() >= chance) return;
        DropType[] types = DropType.values();
        int i = MathUtils.random(types.length - 1);
        switch (types[i]) {
            case HEAL:
                getActive().activate(position, DropType.HEAL, textures[i], new float[]{1f, 0.5f, 0.5f});
                break;
            case AMMO:
                getActive().activate(position, DropType.AMMO, textures[i], new float[]{0.5f, 0.7f, 1f});
                break;
            case CASH:
                getActive().activate(position, DropType.CASH, textures[i], new float[]{0.5f, 1f, 0.5f});
                break;
            case MINES:
                getActive().activate(position, DropType.MINES, textures[i], new float[]{0.58f, 0.5f, 1f});
                break;
        }
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

    public void getEffect(DropType type) {
        switch (type) {
            case HEAL:
                gameController.getPlayer().getShip().addDurability(getRandomOnLevel(HEAL_BASE_AMOUNT_MIN, HEAL_BASE_AMOUNT_MAX, HEAL_LEVEL_FACTOR));
                break;
            case AMMO:
                gameController.getPlayer().getShip().getWeapon().addBullets((int) getRandomOnLevel(AMMO_BASE_AMOUNT_MIN, AMMO_BASE_AMOUNT_MAX, AMMO_LEVEL_FACTOR));
                break;
            case CASH:
                gameController.getPlayer().addCash((int) getRandomOnLevel(CASH_BASE_AMOUNT_MIN, CASH_BASE_AMOUNT_MAX, CASH_LEVEL_FACTOR));
                break;
            case MINES:
                gameController.getPlayer().getShip().addMines();
                break;
        }
    }

    private float getRandomOnLevel(float min, float max, float factor) {
        int level = gameController.getLevel();
        return MathUtils.random((1 + factor * level) * min, (1 + factor * level) * max);
    }
}
