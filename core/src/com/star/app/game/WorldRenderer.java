package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.overlays.DebugOverlay;

public class WorldRenderer {
    private SpriteBatch batch;
    private GameController gameController;

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.batch = batch;
        this.gameController = gameController;
    }

    public void render() {
        batch.begin();
        gameController.getBackground().render(batch);
        gameController.getPlayer().render(batch);
        gameController.getBulletController().render(batch);
        gameController.getAsteroidController().render(batch);
        gameController.getDropController().render(batch);
        gameController.getParticleController().render(batch);
        gameController.getInfoOverlay().render(batch);
        DebugOverlay.render(batch);
        batch.end();
    }
}
