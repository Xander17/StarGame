package com.star.app.game.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.GameController;
import com.star.app.game.overlays.elements.UpdateGroup;
import com.star.app.game.ships.updates.Updates;
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

    public GamePauseOverlay(GameController gameController, SpriteBatch batch) {
        this.gameController = gameController;
        this.fontButton = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        this.fontMainLabel = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        setAlphaTexture(0.5f);
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        setStage();
    }

    private void setAlphaTexture(float alpha) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.Alpha);
        pixmap.setColor(0, 0, 0, alpha);
        pixmap.fill();
        textureDark = new Texture(pixmap);
        pixmap.dispose();
    }

    private void setStage() {
        Gdx.input.setInputProcessor(stage);
        setControlStage();
        setUpdatesStage();
    }

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

    private void setUpdatesStage() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(fontMainLabel, Color.WHITE);
        Label mainLabel = new Label("Updates", labelStyle);
        mainLabel.setPosition(SCREEN_WIDTH / 4f, SCREEN_HEIGHT * 0.9f, Align.center);
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((UpdateGroup) actor.getParent()).execute(gameController.getPlayer().getUpdates());
            }
        };
        Updates updates = gameController.getPlayer().getUpdates();
        UpdateGroup updateGroup1 = new UpdateGroup(Updates.Types.MAX_HEALTH, fontButton, "healupdate", listener, updates.getCost(Updates.Types.MAX_HEALTH));
        updateGroup1.setPosition(SCREEN_WIDTH * 0.1f, SCREEN_HEIGHT * 0.7f);
        UpdateGroup updateGroup2 = new UpdateGroup(Updates.Types.DAMAGE, fontButton, "damageupdate", listener, updates.getCost(Updates.Types.DAMAGE));
        updateGroup2.setPosition(SCREEN_WIDTH * 0.2f, SCREEN_HEIGHT * 0.7f);
        UpdateGroup updateGroup3 = new UpdateGroup(Updates.Types.ROTATION_SPEED, fontButton, "rotationspeed", listener, updates.getCost(Updates.Types.ROTATION_SPEED));
        updateGroup3.setPosition(SCREEN_WIDTH * 0.1f, SCREEN_HEIGHT * 0.5f);
        UpdateGroup updateGroup4 = new UpdateGroup(Updates.Types.FORWARD_SPEED, fontButton, "forwardspeed", listener, updates.getCost(Updates.Types.FORWARD_SPEED));
        updateGroup4.setPosition(SCREEN_WIDTH * 0.2f, SCREEN_HEIGHT * 0.5f);
        stage.addActor(mainLabel);
        stage.addActor(updateGroup1);
        stage.addActor(updateGroup2);
        stage.addActor(updateGroup3);
        stage.addActor(updateGroup4);
    }

    public void show() {
        lastStatus = gameController.getGameStatus();
        gameController.setGameStatus(GameController.GameStatus.PAUSED);
        updateAvailablePrices();
    }

    private void updateAvailablePrices() {
    }

    public void update() {
        stage.act();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) resumeGame();
    }

    private void resumeGame() {
        gameController.setGameStatus(lastStatus);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureDark, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.end();
        stage.draw();
        batch.begin();
    }
}
