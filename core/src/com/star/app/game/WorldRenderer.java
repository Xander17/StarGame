package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
    private SpriteBatch batch;
    private GameController gameController;

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.batch = batch;
        this.gameController = gameController;
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gameController.getBackground().render(batch);
        //gameController.getHero().render(batch);
        gameController.getPlayer().render(batch);
        gameController.getBulletController().render(batch);
        gameController.getAsteroidController().render(batch);
        batch.end();
    }
}
