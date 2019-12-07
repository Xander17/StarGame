package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.screen.ScreenManager;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class WorldRenderer {
    private SpriteBatch batch;
    private GameController gameController;

    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferRegion;
    private Camera camera;

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.batch = batch;
        this.gameController = gameController;

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, SCREEN_WIDTH, SCREEN_HEIGHT, false);
        frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferRegion.flip(false, true);

        camera = ScreenManager.getInstance().getCamera();
    }

    public void render() {
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gameController.getBackground().render(batch);
        batch.end();
        ScreenManager.getInstance().resetCamera(gameController.getPlayer().getShip().getPosition());
        batch.begin();
        gameController.getPlayer().render(batch);
        gameController.getParticleController().render(batch, ParticleLayouts.SHIP);
        gameController.getBulletController().render(batch);
        gameController.getAsteroidController().render(batch);
        gameController.getDropController().render(batch);
        gameController.getParticleController().render(batch, ParticleLayouts.TOP);
        gameController.getAsteroidController().renderArrows(batch);
        batch.end();
        frameBuffer.end();
        ScreenManager.getInstance().resetCameraToCenter();
        batch.begin();
        batch.draw(frameBufferRegion, 0, 0);
        if (gameController.getGameStatus() != GameController.GameStatus.PAUSED) {
            gameController.getInfoOverlay().render(batch);
        } else gameController.getGamePauseOverlay().render(batch);
        DebugOverlay.render(batch);
        batch.end();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
