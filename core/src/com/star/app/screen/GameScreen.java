package com.star.app.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.WorldRenderer;

public class GameScreen extends AbstractScreen {
    private WorldRenderer worldRenderer;
    private GameController gameController;
    private SpriteBatch batch;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        gameController = new GameController();
        worldRenderer = new WorldRenderer(gameController, batch);
    }

    @Override
    public void render(float dt) {
        gameController.update(dt);
        worldRenderer.render();
    }
}