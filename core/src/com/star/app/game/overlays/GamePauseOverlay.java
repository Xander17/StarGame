package com.star.app.game.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.star.app.game.GameController;
import com.star.app.game.overlays.elements.UpdatesTable;
import com.star.app.screen.ScreenManager;
import com.star.app.utils.Assets;
import com.star.app.utils.GameButtonStyle;

import static com.star.app.screen.ScreenManager.*;

public class GamePauseOverlay {
    private GameController gameController;
    private Texture textureDark;
    private Stage stage;
    private BitmapFont fontButton, fontMainLabel;
    private GameController.GameStatus lastStatus;
    private UpdatesTable updatesTable;

    public GamePauseOverlay(GameController gameController, SpriteBatch batch) {
        this.gameController = gameController;
        this.fontButton = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        this.fontMainLabel = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        setAlphaTexture(0.5f);
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        setStages();
    }

    private void setAlphaTexture(float alpha) {
        Pixmap pixmap = new Pixmap(SCREEN_WIDTH, SCREEN_HEIGHT, Pixmap.Format.Alpha);
        pixmap.setColor(0, 0, 0, alpha);
        pixmap.fill();
        textureDark = new Texture(pixmap);
        pixmap.dispose();
    }

    private void setStages() {
        Gdx.input.setInputProcessor(stage);
        setControlStage();
        updatesTable = new UpdatesTable(gameController, 3, SCREEN_WIDTH * 0.1f, SCREEN_HEIGHT * 0.2f, fontMainLabel, fontButton);
        updatesTable.setPosition(SCREEN_WIDTH * 0.05f, 0);
        stage.addActor(updatesTable);
    }

    // TODO: 16.12.2019 стоит ли добавить кнопки в отдельную группу?
    private void setControlStage() {
        TextButton.TextButtonStyle textButtonStyle = GameButtonStyle.getInstance().getDefaultStyle(fontButton);
        float textureW = textButtonStyle.up.getMinWidth();
        float textureH = textButtonStyle.up.getMinHeight();
        TextButton btnResume = new TextButton("Resume", textButtonStyle);
        TextButton btnMenu = new TextButton("Main Menu", textButtonStyle);
        btnResume.setPosition(SCREEN_WIDTH * 3 / 4f - textureW / 2f, SCREEN_HALF_HEIGHT + textureH);
        btnMenu.setPosition(SCREEN_WIDTH * 3 / 4f - textureW / 2f, SCREEN_HALF_HEIGHT - textureH);

        btnResume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resumeGame();
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });
        stage.addActor(btnResume);
        stage.addActor(btnMenu);
    }

    public void show() {
        lastStatus = gameController.getGameStatus();
        gameController.setGameStatus(GameController.GameStatus.PAUSED);
        updatesTable.updateAvailablePrices();
    }

    public void update() {
        stage.act();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) resumeGame();
    }

    private void resumeGame() {
        gameController.setGameStatus(lastStatus);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureDark, 0, 0);
        batch.end();
        stage.draw();
        batch.begin();
    }
}
