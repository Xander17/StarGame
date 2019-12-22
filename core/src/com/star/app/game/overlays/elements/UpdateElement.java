package com.star.app.game.overlays.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.ships.Updates;
import com.star.app.utils.GameButtonStyle;

public class UpdateElement extends Group {

    private Updates.Types type;
    private Label label;
    private TextButton button;

    public UpdateElement(Updates.Types type, BitmapFont font, ChangeListener listener) {
        this.type = type;
        button = new TextButton("", GameButtonStyle.getInstance().getUpdateStyle(font, type.getTexturePrefix()));
        button.getLabel().setAlignment(Align.bottomRight);
        button.addListener(listener);
        setHeight(button.getHeight() + font.getLineHeight());
        button.setY(font.getLineHeight());
        label = new Label("", new Label.LabelStyle(font, Color.WHITE));
        label.setY(0);
        this.addActor(button);
        this.addActor(label);
    }

    public void setCellWidth(float cellWidth) {
        setWidth(cellWidth);
        button.setX((cellWidth - button.getWidth()) / 2);
        label.setX(cellWidth / 2, Align.center);
    }

    public void setActualPrice(Updates updates) {
        int cost = updates.getCost(type);
        if (cost == -1) label.setText("-");
        else label.setText("$ " + cost);
        label.setAlignment(Align.center);
    }

    public void execute(Updates updates, boolean full) {
        int newLevel = updates.improve(type, full);
        updates.applyUpdate(type);
        if (newLevel >= 0) {
            if (!updates.isUpdatable(type)) {
                button.setDisabled(true);
                button.setText("MAX");
            } else {
                button.setText(String.valueOf(newLevel));
            }
            setActualPrice(updates);
        }
    }
}
