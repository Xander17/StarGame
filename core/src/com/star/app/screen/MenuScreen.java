package com.star.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.Background;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class MenuScreen extends AbstractScreen {
    private BitmapFont font64;
    private BitmapFont font24;
    private TextureRegion buttonTexture;
    private Background background;
    private Stage stage;

    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        font64 = Assets.getInstance().getAssetManager().get("fonts/font64.ttf");
        buttonTexture = Assets.getInstance().getTextureAtlas().findRegion("button");
        background = new Background(null);
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        setButtons();
    }

    private void setButtons() {
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getTextureAtlas());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttonmenuup");
        textButtonStyle.down = skin.getDrawable("buttonmenudown");
        textButtonStyle.over = skin.getDrawable("buttonmenuover");
        textButtonStyle.pressedOffsetX=1f;
        textButtonStyle.pressedOffsetY=-1f;
textButtonStyle.overFontColor= Color.valueOf("c6f5ff");
textButtonStyle.fontColor= Color.WHITE;
        textButtonStyle.font = font24;
        skin.add("buttonSkin", textButtonStyle);

        float textureW = textButtonStyle.up.getMinWidth();
        float textureH = textButtonStyle.up.getMinHeight();
        Button btnNewGame = new TextButton("New Game", textButtonStyle);
        Button btnExitGame = new TextButton("Exit Game", textButtonStyle);
        btnNewGame.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HEIGHT * 0.5f - textureH);
        btnExitGame.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HEIGHT * 0.5f - textureH * 3);

        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        skin.dispose();
    }

    private void update(float dt) {
        background.update(dt);
        stage.act();
    }

    @Override
    public void render(float dt) {
        update(dt);
        batch.begin();
        background.render(batch);
        font64.draw(batch, "STAR GAME 2019", 0, SCREEN_HEIGHT * 0.75f, SCREEN_WIDTH, Align.center, false);
        DebugOverlay.render(batch);
        batch.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
