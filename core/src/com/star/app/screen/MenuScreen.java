package com.star.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.Background;
import com.star.app.utils.Assets;
import com.star.app.utils.GameButtonStyle;
import com.star.app.utils.Options;

import java.util.Properties;

import static com.star.app.screen.ScreenManager.*;

public class MenuScreen extends AbstractScreen {
    private BitmapFont font64;
    private BitmapFont font24;
    private BitmapFont font18;
    private Background background;
    private Stage stage;
    private Stage settingsStage;
    private boolean settingsMode;
    private ButtonGroup<TextButton> settingsButtons;
    private int lastCheckedIndex;
    private Properties properties;

    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf", BitmapFont.class);
        font18 = Assets.getInstance().getAssetManager().get("fonts/font18.ttf", BitmapFont.class);
        font64 = Assets.getInstance().getAssetManager().get("fonts/font64.ttf", BitmapFont.class);
        background = new Background(null);
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        settingsStage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        settingsMode = false;
        lastCheckedIndex = -1;
        properties = Options.loadProperties();
        Gdx.input.setInputProcessor(stage);
        setMenuButtons();
        setSettingsButtons();
    }

    private void setMenuButtons() {
        TextButton.TextButtonStyle textButtonStyle = GameButtonStyle.getInstance().getDefaultStyle(font24);

        float textureW = textButtonStyle.up.getMinWidth();
        float textureH = textButtonStyle.up.getMinHeight();
        TextButton btnNewGame = new TextButton("New Game", textButtonStyle);
        TextButton btnSettings = new TextButton("Settings", textButtonStyle);
        TextButton btnExitGame = new TextButton("Exit Game", textButtonStyle);
        btnNewGame.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HALF_HEIGHT - 0.5f * textureH);
        btnSettings.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HALF_HEIGHT - 2.0f * textureH);
        btnExitGame.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HALF_HEIGHT - 5f * textureH);

        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switchSettingsMode();
            }
        });
        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        stage.addActor(btnNewGame);
        stage.addActor(btnSettings);
        stage.addActor(btnExitGame);
    }

    private void setSettingsButtons() {
        TextButton.TextButtonStyle textButtonStyle = GameButtonStyle.getInstance().getDefaultStyle(font24);

        float textureW = textButtonStyle.up.getMinWidth();
        float textureH = textButtonStyle.up.getMinHeight();
        TextButton btnBack = new TextButton("Back", textButtonStyle);
        btnBack.setPosition((SCREEN_WIDTH - textureW) / 2f, SCREEN_HALF_HEIGHT - 5f * textureH);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font24, Color.WHITE);
        Label[] labels = new Label[]{
                new Label("FORWARD", labelStyle),
                new Label("REVERSE", labelStyle),
                new Label("RIGHT", labelStyle),
                new Label("LEFT", labelStyle),
                new Label("FIRE", labelStyle)
        };

        textButtonStyle = GameButtonStyle.getInstance().getKeyButtonStyle(font18);
        textureH = textButtonStyle.up.getMinHeight();
        settingsButtons = new ButtonGroup<>(
                getPropertyKeyButton("PLAYER1_FORWARD", textButtonStyle),
                getPropertyKeyButton("PLAYER1_REVERSE", textButtonStyle),
                getPropertyKeyButton("PLAYER1_RIGHT", textButtonStyle),
                getPropertyKeyButton("PLAYER1_LEFT", textButtonStyle),
                getPropertyKeyButton("PLAYER1_FIRE", textButtonStyle)
        );
        settingsButtons.setMaxCheckCount(1);
        settingsButtons.setMinCheckCount(0);
        settingsButtons.setUncheckLast(true);
        ClickListener listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (lastCheckedIndex != -1) {
                    TextButton textButton = settingsButtons.getButtons().get(lastCheckedIndex);
                    textButton.setText(Input.Keys.toString(Integer.parseInt(properties.getProperty(textButton.getName()))));
                }
                if (settingsButtons.getChecked() != null) {
                    settingsButtons.getChecked().setText("");
                    lastCheckedIndex = settingsButtons.getCheckedIndex();
                }
            }
        };

        for (int i = 0; i < settingsButtons.getButtons().size; i++) {
            labels[i].setPosition(SCREEN_WIDTH * 0.3f, SCREEN_HALF_HEIGHT + (1.8f - 1.2f * i) * textureH);
            settingsButtons.getButtons().get(i).setPosition(SCREEN_WIDTH * 0.7f, SCREEN_HALF_HEIGHT + (2.0f - 1.2f * i) * textureH, Align.right);
            settingsButtons.getButtons().get(i).addListener(listener);
            settingsStage.addActor(settingsButtons.getButtons().get(i));
            settingsStage.addActor(labels[i]);
        }

        settingsStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (settingsButtons.getChecked() != null) {
                    saveNewProperty(settingsButtons.getChecked().getName(), String.valueOf(keycode));
                    settingsButtons.getChecked().setText(Input.Keys.toString(keycode));
                    settingsButtons.getChecked().setChecked(false);
                }
                return true;
            }
        });

        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Options.saveProperties(properties);
                settingsButtons.uncheckAll();
                switchSettingsMode();
            }
        });
        settingsStage.addActor(btnBack);
    }

    private TextButton getPropertyKeyButton(String property, TextButton.TextButtonStyle style) {
        TextButton textButton = new TextButton(Input.Keys.toString(Integer.parseInt(properties.getProperty(property))), style);
        textButton.setName(property);
        return textButton;
    }

    private void saveNewProperty(String property, String key) {
        properties.put(property, key);
    }

    private void switchSettingsMode() {
        if (!settingsMode) {
            settingsMode = true;
            Gdx.input.setInputProcessor(settingsStage);
        } else {
            settingsMode = false;
            Gdx.input.setInputProcessor(stage);
        }
    }

    private void update(float dt) {
        background.update(dt);
        if (settingsMode) settingsStage.act();
        else stage.act();
    }

    @Override
    public void render(float dt) {
        update(dt);
        batch.begin();
        background.render(batch);
        font64.draw(batch, "STAR GAME 2019", 0, SCREEN_HEIGHT * 0.9f, SCREEN_WIDTH, Align.center, false);
        batch.end();
        if (settingsMode) settingsStage.draw();
        else stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
