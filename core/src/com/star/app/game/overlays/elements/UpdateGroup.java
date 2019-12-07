package com.star.app.game.overlays.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.ships.updates.Updates;
import com.star.app.utils.GameButtonStyle;

public class UpdateGroup extends Group {
    private Updates.Types type;
    private Label label;
    private TextButton button;

    public UpdateGroup(Updates.Types type, BitmapFont font, String texturePrefix, ChangeListener listener, int initialCost) {
        this.type = type;
        button = new TextButton("", GameButtonStyle.getInstance().getUpdateStyle(font, texturePrefix));
        button.getLabel().setAlignment(Align.bottomRight);
        button.addListener(listener);
        setHeight(button.getHeight() + font.getLineHeight());
        setWidth(button.getWidth());
        button.setPosition(0, font.getLineHeight());
        label = new Label("$ " + initialCost, new Label.LabelStyle(font, Color.WHITE));
        label.setPosition(button.getWidth() / 2, 0, Align.center);
        this.addActor(button);
        this.addActor(label);
    }

    public void execute(Updates updates) {
        int newLevel = updates.improve(type);
        updates.applyUpdate(type);
        if (newLevel >= 0) {
            if (!updates.isUpdatable(type)) {
                button.setDisabled(true);
                button.setText("MAX");
                label.setText("-");
                label.setAlignment(Align.center);
            } else {
                button.setText(String.valueOf(newLevel));
                label.setText("$ " + updates.getCost(type));
            }
        }
    }
}
