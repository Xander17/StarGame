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
import com.star.app.screen.ScreenManager;
import com.star.app.utils.Assets;
import com.star.app.utils.GameButtonStyle;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class GamePauseOverlay {
    private GameController gameController;
    private Texture textureDark;
    private Stage stage;
    private BitmapFont font;

    public GamePauseOverlay(GameController gameController, SpriteBatch batch) {
        this.gameController = gameController;
        this.font = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        setAlphaTexture(0.5f);
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        setButtons();
    }

    private void setAlphaTexture(float alpha) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.Alpha);
        pixmap.setColor(0, 0, 0, alpha);
        pixmap.fill();
        textureDark = new Texture(pixmap);
        pixmap.dispose();
    }

    private void setButtons() {
        Gdx.input.setInputProcessor(stage);
        TextButton.TextButtonStyle textButtonStyle = GameButtonStyle.getInstance().getDefaultStyle(font);

        float textureW = textButtonStyle.up.getMinWidth();
        float textureH = textButtonStyle.up.getMinHeight();
        TextButton btnResume = new TextButton("Resume", textButtonStyle);
        TextButton btnMenu = new TextButton("Main Menu", textButtonStyle);
        btnResume.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HEIGHT / 2f + textureH);
        btnMenu.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HEIGHT / 2f - textureH);

        btnResume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setPaused(false);
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

    public void update() {
        stage.act();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameController.setPaused(false);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureDark, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.end();
        stage.draw();
        batch.begin();
    }
}
