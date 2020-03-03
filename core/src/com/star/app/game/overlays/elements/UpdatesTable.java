package com.star.app.game.overlays.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.GameController;
import com.star.app.game.ships.Updates;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;

public class UpdatesTable extends Group {
    private UpdateElement[] updateElements;
    private GameController gameController;

    public UpdatesTable(final GameController gameController, int columns, float cellWidth, float verticalSpace, BitmapFont labelFont, BitmapFont textFont) {
        this.gameController = gameController;
        Label.LabelStyle labelStyle = new Label.LabelStyle(labelFont, Color.WHITE);
        Label mainLabel = new Label("Updates", labelStyle);
        mainLabel.setPosition(columns / 2f * cellWidth, SCREEN_HEIGHT * 0.9f, Align.center);
        ChangeListener listener = getButtonListener();
        updateElements = new UpdateElement[]{
                new UpdateElement(Updates.Types.MAX_HEALTH, textFont, listener),
                new UpdateElement(Updates.Types.DAMAGE, textFont, listener),
                new UpdateElement(Updates.Types.ROTATION_SPEED, textFont, listener),
                new UpdateElement(Updates.Types.FORWARD_SPEED, textFont, listener)
        };
        for (int i = 0; i < updateElements.length; i++) {
            updateElements[i].setCellWidth(cellWidth);
            updateElements[i].setPosition(cellWidth * (i % columns), SCREEN_HEIGHT * 0.7f - verticalSpace * (i / columns));
            addActor(updateElements[i]);
        }
        addActor(mainLabel);
    }

    private ChangeListener getButtonListener() {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean full = false;
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
                    full = true;
                ((UpdateElement) actor.getParent()).execute(gameController.getPlayer().getUpdates(), full);
            }
        };
    }

    public void updateAvailablePrices() {
        Updates updates = gameController.getPlayer().getUpdates();
        for (int i = 0; i < updateElements.length; i++) {
            updateElements[i].setActualPrice(updates);
        }
    }

}
