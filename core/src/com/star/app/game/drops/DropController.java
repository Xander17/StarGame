package com.star.app.game.drops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class DropController extends ObjectPool<Drop> {
    private final float HEAL_AMOUNT_MIN = 10f;
    private final float HEAL_AMOUNT_MAX = 20f;
    private final int AMMO_AMOUNT_MIN = 200;
    private final int AMMO_AMOUNT_MAX = 400;
    private final int CASH_AMOUNT_MIN = 100;
    private final int CASH_AMOUNT_MAX = 200;

    private GameController gameController;

    private TextureRegion[] textures = {
            Assets.getInstance().getTextureAtlas().findRegion("healdrop"),
            Assets.getInstance().getTextureAtlas().findRegion("ammodrop"),
            Assets.getInstance().getTextureAtlas().findRegion("cashdrop")
    };

    public DropController(GameController gameController) {
        this.gameController = gameController;
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
                gameController.getPlayer().getShip().addDurability(MathUtils.random(HEAL_AMOUNT_MIN, HEAL_AMOUNT_MAX));
                break;
            case AMMO:
                gameController.getPlayer().getShip().getWeapon().addBullets(MathUtils.random(AMMO_AMOUNT_MIN, AMMO_AMOUNT_MAX));
                break;
            case CASH:
                gameController.getPlayer().addCash(MathUtils.random(CASH_AMOUNT_MIN, CASH_AMOUNT_MAX));
                break;
        }
    }
}
