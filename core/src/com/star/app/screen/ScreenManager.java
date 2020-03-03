package com.star.app.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.star.app.utils.Assets;

public class ScreenManager {
    public static final int SCREEN_WIDTH = Gdx.graphics.getBackBufferWidth();
    public static final float SCREEN_HALF_WIDTH = SCREEN_WIDTH / 2;
    public static final int SCREEN_HEIGHT = Gdx.graphics.getBackBufferHeight();
    public static final float SCREEN_HALF_HEIGHT = SCREEN_HEIGHT / 2;
    private static ScreenManager instance = new ScreenManager();
    private SpriteBatch batch;
    private Game game;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private GameOverScreen gameOverScreen;
    private Screen targetScreen;
    private ScreenType targetScreenType;
    private Viewport viewport;
    private Camera camera;

    public static ScreenManager getInstance() {
        return instance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void init(SpriteBatch batch, Game game) {
        this.batch = batch;
        this.game = game;
        loadingScreen = new LoadingScreen(batch);
        gameScreen = new GameScreen(batch);
        menuScreen = new MenuScreen(batch);
        gameOverScreen = new GameOverScreen(batch);
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCameraToCenter() {
        resetCamera(SCREEN_HALF_WIDTH, SCREEN_HALF_HEIGHT);
    }

    public void resetCamera(Vector2 position) {
        resetCamera(position.x, position.y);
    }

    public void resetCamera(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        targetScreenType = type;
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) screen.dispose();
        resetCameraToCenter();
        game.setScreen(loadingScreen);
        switch (type) {
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case GAME_OVER:
                targetScreen = gameOverScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME_OVER);
                break;
        }
    }

    public ScreenType getTargetScreenType() {
        return targetScreenType;
    }

    public void goToTargetScreen() {
        game.setScreen(targetScreen);
    }

    public GameOverScreen getGameOverScreen() {
        return gameOverScreen;
    }

    public Camera getCamera() {
        return camera;
    }

    public enum ScreenType {GAME, MENU, GAME_OVER}
}
