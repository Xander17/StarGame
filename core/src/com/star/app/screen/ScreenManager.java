package com.star.app.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.star.app.utils.Assets;

public class ScreenManager {
    public enum ScreenType {GAME, MENU, SETTINGS, GAMEOVER}

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    private SpriteBatch batch;
    private Game game;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private Screen targetScreen;
    private ScreenType targetScreenType;
    private Viewport viewport;
    private Camera camera;

    private static ScreenManager instance = new ScreenManager();

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
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCamera() {
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        targetScreenType = type;
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) screen.dispose();
        resetCamera();
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
        }
    }

    public ScreenType getTargetScreenType() {
        return targetScreenType;
    }

    public void goToTargetScreen() {
        game.setScreen(targetScreen);
    }
}
