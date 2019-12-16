package com.star.app.game.asteroids;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class AsteroidController extends ObjectPool<Asteroid> {

    private TextureRegion[] asteroidTypes = new TextureRegion[]{
            Assets.getInstance().getTextureAtlas().findRegion("asteroid1"),
            Assets.getInstance().getTextureAtlas().findRegion("asteroid2"),
            Assets.getInstance().getTextureAtlas().findRegion("asteroid3"),
            Assets.getInstance().getTextureAtlas().findRegion("asteroid4"),
            Assets.getInstance().getTextureAtlas().findRegion("asteroid5"),
            Assets.getInstance().getTextureAtlas().findRegion("asteroid6")
    };

    private GameController gameController;
    private TextureRegion arrowTexture;

    @Override
    public Asteroid getNew() {
        return new Asteroid(gameController);
    }

    public void createNew(float x, float y, float scale, float velocityX, float velocityY, int health,boolean delayed) {
        getActive().activate(getRandomTexture(), x, y, scale, velocityX, velocityY, health,delayed);
    }

    void createNew(float x, float y, float scale, int health,boolean delayed) {
        getActive().activate(getRandomTexture(), x, y, scale, health,delayed);
    }

    public void createNew() {
        getActive().activate(getRandomTexture(),false);
    }

    public void update(float dt) {
        if (activeList.size() == 0 && gameController.getGameStatus() == GameController.GameStatus.ACTIVE)
            gameController.setGameStatus(GameController.GameStatus.LEVEL_COMPLETE);
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid b = activeList.get(i);
            b.update(dt);
        }
        checkFreeObjects();
    }

    public AsteroidController(GameController gameController) {
        this.gameController = gameController;
        this.arrowTexture = Assets.getInstance().getTextureAtlas().findRegion("arrow_asteroid");
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void renderArrows(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).renderArrow(batch, arrowTexture);
        }
    }

    private TextureRegion getRandomTexture() {
        return asteroidTypes[MathUtils.random(asteroidTypes.length - 1)];
    }
}
