package com.star.app.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GameButtonStyle {
    private static GameButtonStyle instance;

    static {
        instance = new GameButtonStyle();
    }

    public static GameButtonStyle getInstance() {
        return instance;
    }

    private TextButton.TextButtonStyle defaultButtonStyle;
    private TextButton.TextButtonStyle keyButtonStyle;

    public GameButtonStyle() {
        defaultButtonStyle = new TextButton.TextButtonStyle();
        keyButtonStyle = new TextButton.TextButtonStyle();
    }

    private void setStyle(TextButton.TextButtonStyle style, BitmapFont font, String prefix, boolean checkable) {
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getTextureAtlas());
        style.up = skin.getDrawable(prefix + "up");
        style.down = skin.getDrawable(prefix + "down");
        style.over = skin.getDrawable(prefix + "over");
        style.pressedOffsetX = 1f;
        style.pressedOffsetY = -1f;
        style.overFontColor = Color.valueOf("c6f5ff");
        style.fontColor = Color.WHITE;
        style.font = font;
        if (checkable) {
            style.checked = skin.getDrawable(prefix + "check");
            style.checkedOffsetX = 1f;
            style.checkedOffsetY = -1f;
            style.checkedFontColor = Color.valueOf("9e8600");
        }
        skin.dispose();
    }

    public TextButton.TextButtonStyle getDefaultStyle(BitmapFont font) {
        setStyle(defaultButtonStyle, font, "buttonmenu", false);
        return defaultButtonStyle;
    }

    public TextButton.TextButtonStyle getKeyButtonStyle(BitmapFont font) {
        setStyle(keyButtonStyle, font, "buttonkey", true);
        return keyButtonStyle;
    }
}