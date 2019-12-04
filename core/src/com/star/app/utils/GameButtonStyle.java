package com.star.app.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

    private TextButton.TextButtonStyle tmpStyle;

    public GameButtonStyle() {
    }

    private void setStyle(BitmapFont font, String prefix, boolean over, boolean pressed, boolean checkable, boolean disabled) {
        tmpStyle = new TextButton.TextButtonStyle();
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getTextureAtlas());
        tmpStyle.up = skin.getDrawable(prefix + "up");
        if (font != null) {
            tmpStyle.fontColor = Color.WHITE;
            tmpStyle.font = font;
        }
        if (pressed) {
            tmpStyle.down = skin.getDrawable(prefix + "down");
            tmpStyle.pressedOffsetX = 1f;
            tmpStyle.pressedOffsetY = -1f;
        }
        if (over) {
            tmpStyle.over = skin.getDrawable(prefix + "over");
            tmpStyle.overFontColor = Color.valueOf("c6f5ff");
        }
        if (checkable) {
            tmpStyle.checked = skin.getDrawable(prefix + "check");
            tmpStyle.checkedOffsetX = 1f;
            tmpStyle.checkedOffsetY = -1f;
            tmpStyle.checkedFontColor = Color.valueOf("9e8600");
        }
        if (disabled) {
            tmpStyle.disabled = skin.getDrawable(prefix + "disabled");
            tmpStyle.disabledFontColor = Color.valueOf("ececec");
        }
        skin.dispose();
    }

    public TextButton.TextButtonStyle getDefaultStyle(BitmapFont font) {
        setStyle(font, "buttonmenu", true, true, false,false);
        return new TextButton.TextButtonStyle(tmpStyle);
    }

    public TextButton.TextButtonStyle getKeyButtonStyle(BitmapFont font) {
        setStyle(font, "buttonkey", true, true, true,false);
        return new TextButton.TextButtonStyle(tmpStyle);
    }

    public TextButton.TextButtonStyle getUpdateStyle(BitmapFont font, String filePrefix) {
        setStyle(font, filePrefix, false, false, false,true);
        float w = tmpStyle.up.getMinWidth();
        float h = tmpStyle.up.getMinHeight();
        tmpStyle.unpressedOffsetX = -w / 10;
        tmpStyle.unpressedOffsetY = h / 20;
        tmpStyle.pressedOffsetX = -w / 10;
        tmpStyle.pressedOffsetY = h / 20;
        tmpStyle.checkedOffsetX = -w / 10;
        tmpStyle.checkedOffsetY = h / 20;
        return new TextButton.TextButtonStyle(tmpStyle);
    }
}